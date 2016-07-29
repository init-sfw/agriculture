package ar.com.init.agros.email;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.conf.Configurable;
import ar.com.init.agros.controller.EMailMessageJpaController;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.model.util.EMailMessage;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase EmailManager
 * 
 * 
 * @author gmatheu
 * @version 27/06/2009
 */
public class EmailManager extends Thread implements Configurable {

    private static EmailManager instance;
    private final BlockingQueue<EMailMessage> queue;
    private static final Logger log = Logger.getLogger(EmailManager.class.getName());
    private EMailMessageJpaController emailController;
    private long sleepTime = 400000;
    private String sender;
    private String smtpHost;
    private String smtpPassword;
    private String smtpUser;
    private String port;
    private boolean secureConnection;
    private boolean requiresAuthentication;
    private boolean stop;
    // CLAVES PARA CONFIGURACION
    public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    public static final String EMAIL_USE_DEFAULT_KEY = "EMAIL_USE_DEFAULT";
    public static final String EMAIL_SENDER_KEY = "EMAIL_SENDER";
    public static final String EMAIL_SLEEP_TIME_KEY = "EMAIL_SLEEP_TIME";
    public static final String EMAIL_SMTP_HOST_KEY = "EMAIL_SMTP_HOST";
    public static final String EMAIL_SMTP_USER_KEY = "EMAIL_SMTP_USER";
    public static final String EMAIL_SMTP_PASSWORD_KEY = "EMAIL_SMTP_PASSWORD";
    public static final String EMAIL_REQUIRES_AUTH = "EMAIL_REQUIRES_AUTH";
    public static final String EMAIL_PORT = "EMAIL_PORT";
    public static final String EMAIL_SECURE_CONNECTION = "EMAIL_SECURE_CONNECTION";

    /** Constructor por defecto de EmailManager */
    private EmailManager() {
        super();
        queue = new LinkedBlockingQueue<EMailMessage>();
        emailController = new EMailMessageJpaController();
        stop = false;
        loadEmails();
    }

    public static EmailManager getInstance() {
        if (instance == null) {
            instance = new EmailManager();
        }
        return instance;
    }

    public boolean isStop() {
        return stop;
    }

    public synchronized void setStop(boolean stop) {
        this.stop = stop;

        notifyAll();
    }

    public synchronized void postSystemMail(EMailMessage message) {
        if (message == null) {
            return;
        }        

        post(message);
    }

    public synchronized void postMail(EMailMessage message) {
        if (message == null) {
            return;
        }

        message.setSender(sender);

        String recipients = "";
        List<String> addresses = ConfMgr.getInstance().getController().findValuesByKey(
                EMAIL_ADDRESS);
        if (addresses.isEmpty()) {
            return;
        }
        for (String s : addresses) {
            recipients += s + EMailMessage.RECIPIENT_SEPARATOR;
        }

        message.setRecipients(recipients);

        post(message);
    }

    private synchronized void post(EMailMessage message) throws InvalidStateException, ConstraintViolationException {
        emailController.persist(message);
        queue.add(message);
        log.log(Level.INFO, "Email queued : {0}", message.getSubject());
        log.fine(message.toString());
        notifyAll();
    }

    public synchronized boolean checkConnection(String sender, String smtpHost,
            String port, final String smtpUser, final String smtpPassword,
            final boolean requiresAuthentication, boolean secureConnection) {
        try {
            EMailMessage testMessage = createTestMessage(sender, sender);

            return send(testMessage, smtpHost, port, smtpUser, smtpPassword,
                    requiresAuthentication, secureConnection);
        } catch (Exception ex) {
            log.log(Level.INFO, "Connection Failed", ex);
            return false;
        }
    }

    public void sendTestMessage() {
        postMail(createTestMessage(sender, ""));
    }

    private EMailMessage createTestMessage(String sender, String recipients) {
        EmailHelper helper = new EmailHelper();
        helper.writeLine("Estimado:");
        helper.writeLine("Este es un mensaje de prueba enviado desde Osiris.");
        helper.writeLine(
                "Los valores ingresados son correctos y el sistema está correctamente configurado para enviar los correos de alerta.");
        EMailMessage testMessage = new EMailMessage(
                "OSIRIS - Prueba de Conexión", helper.buildContent());
        testMessage.setSender(sender);
        testMessage.setRecipients(recipients);
        return testMessage;
    }

    private Session createSession(String smtpHost, String port,
            final String smtpUser, final String smtpPassword,
            final boolean requiresAuthentication,
            final boolean useSecureConexion) {
        // Get system properties
        Properties props = System.getProperties();
        // Setup mail server

        props.put("mail.smtp.host", smtpHost);
        if (useSecureConexion) {
            props.put("mail.smtp.starttls.enable", Boolean.toString(useSecureConexion));
        }
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", Boolean.toString(requiresAuthentication));
        props.put("mail.smtp.user", smtpUser);
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");

        Authenticator auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        };

        Session s = Session.getDefaultInstance(props, auth);

        return s;
    }

    private void processQueue() {
        log.log(Level.INFO, "Processing queue: {0} messages", queue.size());
        for (Iterator<EMailMessage> it = queue.iterator(); it.hasNext();) {
            EMailMessage eMailMessage = it.next();

            if (send(eMailMessage, smtpHost, port, smtpUser, smtpPassword,
                    requiresAuthentication, secureConnection)) {
                eMailMessage.setLastSent(new Date());
                eMailMessage.addSent();
                queue.remove(eMailMessage);
            } else {
            }
            try {
                emailController.update(eMailMessage);
            } catch (NonExistentEntityException ex) {
                log.log(Level.SEVERE, null, ex);
            } catch (InvalidStateException ex) {
                log.log(Level.SEVERE, null, ex);
            } catch (ConstraintViolationException ex) {
                log.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        log.log(Level.INFO, "Queue processed: {0} messages to send", queue.size());

    }

    private boolean send(EMailMessage eMailMessage, String smtpHost,
            String port, final String smtpUser, final String smtpPassword,
            final boolean requiresAuthentication, boolean secureConnection) {
        try {
            log.log(Level.INFO, "Sending : {0}", eMailMessage.getSubject());

            // Get session
            Session session = createSession(smtpHost, port, smtpUser,
                    smtpPassword, requiresAuthentication, secureConnection);

            // Define message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(eMailMessage.getSender()));

            String[] recipients = eMailMessage.getRecipients().split(
                    EMailMessage.RECIPIENT_SEPARATOR);
            for (int i = 0; i < recipients.length; i++) {
                String to = recipients[i];
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
            }

            message.setSubject(eMailMessage.getSubject());

            Multipart multipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(eMailMessage.getMessage(), "text/html");
            multipart.addBodyPart(htmlPart);

            // Adding image
            BodyPart imgPart = new MimeBodyPart();

            // Loading the image
            URL ds = getClass().getClassLoader().getResource(
                    "ar/com/init/agros/view/resources/logoEmail.jpg");
            imgPart.setDataHandler(new DataHandler(ds));

            // Setting the header
            imgPart.setHeader("Content-ID", "<logo>");
            multipart.addBodyPart(imgPart);

            // attaching the multi-part to the message
            message.setContent(multipart);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpUser, smtpPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            log.log(Level.INFO, "Message Sent : {0}", eMailMessage.getSubject());
            return true;
        } catch (MessagingException ex) {
            log.log(Level.SEVERE, null, ex);
            log.log(Level.INFO, "Message NOT Sent : {0}", eMailMessage.getSubject());
            return false;
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            log.log(Level.INFO, "Message NOT Sent : {0}", eMailMessage.getSubject());
            return false;
        }
    }

    private void loadEmails() {
        queue.addAll(emailController.findNotSent());
    }

    @Override
    public void run() {
        emailController.purge();

        while (!isStop()) {
            process();
        }

        log.info("Email Manager Stopped");
    }

    private synchronized void process() {
        try {
            processQueue();
            wait(sleepTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(EmailManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    public synchronized void setUp(long sleepTime, String sender,
            String smtpHost, String port, String smtpPassword, String smtpUser,
            boolean requiresAuthentication, boolean secureConnection) {
        this.sleepTime = sleepTime;
        this.sender = sender;
        this.smtpHost = smtpHost;
        this.port = port;
        this.smtpPassword = smtpPassword;
        this.smtpUser = smtpUser;
        this.requiresAuthentication = requiresAuthentication;
        this.secureConnection = secureConnection;
    }

    @Override
    public void loadConfiguration(ConfMgr manager) {
        if (!manager.getController().findBooleanByKey(EMAIL_USE_DEFAULT_KEY,
                true)) {
            long st = 400000; // manager.getController().findLongByKey(EMAIL_SLEEP_TIME_KEY);
            String sen = manager.getController().findValueByKey(
                    EMAIL_SENDER_KEY);
            String host = manager.getController().findValueByKey(
                    EMAIL_SMTP_HOST_KEY);
            String port = manager.getController().findValueByKey(EMAIL_PORT);
            String pass = manager.getController().findValueByKey(
                    EMAIL_SMTP_PASSWORD_KEY);
            String user = manager.getController().findValueByKey(
                    EMAIL_SMTP_USER_KEY);
            boolean auth = manager.getController().findBooleanByKey(
                    EMAIL_REQUIRES_AUTH);
            boolean secure = manager.getController().findBooleanByKey(
                    EMAIL_SECURE_CONNECTION);

            setUp(st, sen, host, port, pass, user, auth, secure);
        } else {
//             setUp(400000,
//             new ObfuscatedString(
//             new long[]{0xABFD59E9151D7B8FL, 0xCB5F0178205CB45DL,
//             0x5E1BE8A81971B45EL, 0xC6400E1BFF376EAEL,
//             0x24223C110E19AC57L}).toString() /* =>
//             "osiris@campoportunidades.com" */,
//             new ObfuscatedString(
//             new long[]{0xBA3FF669C80A04CFL, 0xA645FA6D93DD8F08L,
//             0x7756CB11F7D3867BL, 0x2CC16A590868A06DL,
//             0x7AEE1F33E09393E5L}).toString() /* =>
//             "smtp.campoportunidades.com" */,
//             "25",
//             new ObfuscatedString(new long[]{0x84E82FFFBFF8F4FL,
//             0x928BA110348EEACFL}).toString() /* => "0511" */,
//             new ObfuscatedString(
//             new long[]{0xABFD59E9151D7B8FL, 0xCB5F0178205CB45DL,
//             0x5E1BE8A81971B45EL, 0xC6400E1BFF376EAEL,
//             0x24223C110E19AC57L}).toString() /* =>
//             "osiris@campoportunidades.com" */,
//             true,
//             false);

            // Configuracion alternativa para gmail
            setUp(400000, "info.osiris@campoportunidades.com",
                    "smtp.gmail.com", "587", "osiris0511",
                    "osiris.campoportunidades@gmail.com", true, true);

        }
    }
}
