/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DialogConfiguracion.java
 *
 * Created on 12/07/2009, 12:19:45
 */
package ar.com.init.agros.view.configuracion;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.conf.GuiaFitosanitariaMgr;
import ar.com.init.agros.email.EmailManager;
import ar.com.init.agros.model.util.Configuration;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.RegexInputVerifier;
import ar.com.init.agros.view.Application;
import ar.com.init.agros.view.configuracion.model.AlertasTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.xito.dialog.DialogManager;

/**
 *
 * @author gmatheu
 */
public class DialogConfiguracion extends javax.swing.JDialog
{

    private static final long serialVersionUID = -1L;
    private DefaultListModel direccionesListModel;
    private AlertasTableModel alertasTableModel;
    public static final int TAB_GUIA_FITOSANITARIA = 1;
    private JFileChooser fileChooserGuia;
    private boolean showAutomatically = true;

    /** Creates new form DialogConfiguracion */
    public DialogConfiguracion(java.awt.Frame parent)
    {
        super(parent, true);

        GUIUtility.initWindow(this);
        initComponents();
        jTabbedPane.remove(jPanelCorreo);
        
        addRemovePanel.setBtnRemoveEnabled(false);

        fillComboPuertos();

        ResourceMap resourceMap = Application.getInstance(Application.class).getContext().getResourceMap(
                DialogConfiguracion.class);
        jTextFieldDireccion.setInputVerifier(new RegexInputVerifier(frameNotifier,
                RegexInputVerifier.EMAIL_ADDRESS, resourceMap.getString("validacion.email")));

        addRemovePanel.addActionListener(new AddRemoveEventControl());
        oKCancelCleanPanel.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel.setOwner(this);

        direccionesListModel = new DefaultListModel();
        jXListDirecciones.setModel(direccionesListModel);

        alertasTableModel = new AlertasTableModel();
        jXTableAlertas.setModel(alertasTableModel);

        jXTableAlertas.getModel().addTableModelListener(new TableModelListener()
        {

            @Override
            public void tableChanged(TableModelEvent e)
            {
                if (jXTableAlertas.getRowCount() > 0 && alertasTableModel.isAllSelected()) {
                    jCheckBoxSeleccionarTodasAlertas.setSelected(true);
                }
                else {
                    jCheckBoxSeleccionarTodasAlertas.setSelected(false);
                }
            }
        });

        jXListDirecciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXListDirecciones.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (jXListDirecciones.getSelectedIndex() > -1) {
                    addRemovePanel.setBtnRemoveEnabled(true);
                }
                else {
                    addRemovePanel.setBtnRemoveEnabled(false);
                }
            }
        });

        loadConfiguration();
        jXTableAlertas.packAll();
    }

    private void checkCarpetaGuia(String newValue)
    {
        if (GuiaFitosanitariaMgr.checkCarpetaGuia(newValue)) {
            frameNotifier.showInformationMessage(
                    "La carpeta seleccionada contiene la Guía de Productos Fitosanitaria 2009.");
        }
        else {
            frameNotifier.showErrorMessage(
                    "La carpeta seleccionada NO contiene la Guía de Productos Fitosanitaria 2009.");
        }
    }

    private void clean()
    {
        jTextFieldDireccion.setText("");
        loadConfiguration();
        frameNotifier.showOkMessage();
    }

    private void closeWindow()
    {
        this.dispose();
    }

    private void loadConfiguration()
    {
        alertasTableModel.loadConfiguration();

        //ALERTAS
        List<Configuration> direcciones = ConfMgr.getInstance().getController().findByKey(
                EmailManager.EMAIL_ADDRESS);
        direccionesListModel.clear();
        for (Configuration conf : direcciones) {
            direccionesListModel.addElement(conf);
        }

        //CORREO
        jCheckBoxValoresDefecto.setSelected(ConfMgr.getInstance().getController().findBooleanByKey(
                EmailManager.EMAIL_USE_DEFAULT_KEY, true));
        jTextFieldRemitente.setText(ConfMgr.getInstance().getController().findValueByKey(
                EmailManager.EMAIL_SENDER_KEY));
        jTextFieldServidorSMTP.setText(ConfMgr.getInstance().getController().findValueByKey(
                EmailManager.EMAIL_SMTP_HOST_KEY));
        jTextFieldUsuarioSMTP.setText(ConfMgr.getInstance().getController().findValueByKey(
                EmailManager.EMAIL_SMTP_USER_KEY));
        jPasswordFieldSMTP.setText(ConfMgr.getInstance().getController().findValueByKey(
                EmailManager.EMAIL_SMTP_PASSWORD_KEY));
        jCheckBoxRequiereAutenticacion.setSelected(ConfMgr.getInstance().getController().findBooleanByKey(
                EmailManager.EMAIL_REQUIRES_AUTH));
        jCheckBoxUsarConexionSegura.setSelected(ConfMgr.getInstance().getController().findBooleanByKey(
                EmailManager.EMAIL_SECURE_CONNECTION));
        jComboBoxPuerto.setSelectedItem(ConfMgr.getInstance().getController().findValueByKey(
                EmailManager.EMAIL_PORT));

        //GUIA
        String value = ConfMgr.getInstance().getController().findValueByKey(
                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_FOLDER_KEY);

        if (value == null) {
            jCheckBoxDefaultGuia.setSelected(true);
            jTextFieldCarpetaGuia.setText(GuiaFitosanitariaMgr.INSTALL_PATH);
            jTextPaneGuiaFito.setToolTipText(GuiaFitosanitariaMgr.INSTALL_PATH);
        }
        else {
            jCheckBoxDefaultGuia.setSelected(false);
            jTextFieldCarpetaGuia.setText(value);
            jTextPaneGuiaFito.setToolTipText(value);
        }

        // Mostrar o no el diálogo automáticamente
        String mostrarDialogo = ConfMgr.getInstance().getController().findValueByKey(
                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_AUTOMATIC_SHOW_DISABLED_KEY);

        if (mostrarDialogo != null && mostrarDialogo.equals(Boolean.TRUE.toString()))
        {
            jCheckBoxNoMostrarGuia.setSelected(true);
            showAutomatically = false;
        }
        else
        {
            jCheckBoxNoMostrarGuia.setSelected(false);
            showAutomatically = true;
        }

        jCheckBoxValoresDefectoActionPerformed(null);
        jCheckBoxDefaultGuiaActionPerformed(null);
    }

    public boolean isShowAutomatically()
    {
        return showAutomatically;
    }

    public void setShowAutomatically(boolean showAutomatically)
    {
        this.showAutomatically = showAutomatically;
    }

    private void fillComboPuertos()
    {
        jComboBoxPuerto.removeAllItems();
        jComboBoxPuerto.addItem("25");
        jComboBoxPuerto.addItem("465");
        jComboBoxPuerto.addItem("587");
    }

    private class AddRemoveEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemovePanel.getJButtonAdd()) {
                String address = jTextFieldDireccion.getText();

                if (address.length() > 0) {
                    if (jTextFieldDireccion.getInputVerifier().verify(jTextFieldDireccion)) {
                        boolean exists = false;

                        Object[] addresses = direccionesListModel.toArray();

                        for (Object object : addresses) {
                            Configuration conf = (Configuration) object;
                            if (conf.getConfValue().equals(address)) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            direccionesListModel.addElement(new Configuration(EmailManager.EMAIL_ADDRESS,
                                    address));
                            jTextFieldDireccion.setText("");
                        }
                        else {
                            frameNotifier.showErrorMessage(
                                    "Ya existe la dirección de correo electrónico ingresada.");
                        }
                    }
                }
            }
            else if (e.getSource() == addRemovePanel.getJButtonRemove()) {

                int[] values = jXListDirecciones.getSelectedIndices();
                for (int i = 0; i < values.length; i++) {
                    direccionesListModel.removeElementAt(values[i]);
                }
            }
            else if (e.getSource() == addRemovePanel.getJButtonClean()) {
                DialogConfiguracion.this.clean();
            }
        }
    }

    private class OkCleanCancelEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
                DialogConfiguracion.this.clean();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                DialogConfiguracion.this.closeWindow();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnAceptar()) {
                try {
                    Object[] addresses = direccionesListModel.toArray();
                    ConfMgr.getInstance().getController().replaceConfiguration(Arrays.asList(addresses),
                            EmailManager.EMAIL_ADDRESS);
                    ConfMgr.getInstance().getController().persistOrUpdate(
                            alertasTableModel.getConfigurations());

                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_SENDER_KEY, jTextFieldRemitente.getText()));
                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_SMTP_HOST_KEY, jTextFieldServidorSMTP.getText()));
                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_SMTP_USER_KEY, jTextFieldUsuarioSMTP.getText()));
                    ConfMgr.getInstance().getController().replaceConfiguration(
                            new Configuration(
                            EmailManager.EMAIL_SMTP_PASSWORD_KEY, new String(jPasswordFieldSMTP.getPassword())));
                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_USE_DEFAULT_KEY, Boolean.toString(
                            jCheckBoxValoresDefecto.isSelected())));
                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_REQUIRES_AUTH, Boolean.toString(
                            jCheckBoxRequiereAutenticacion.isSelected())));
                    if (jComboBoxPuerto.getSelectedItem() != null) {
                        ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                                EmailManager.EMAIL_PORT, jComboBoxPuerto.getSelectedItem().toString()));
                    }
                    ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                            EmailManager.EMAIL_SECURE_CONNECTION, Boolean.toString(
                            jCheckBoxUsarConexionSegura.isSelected())));

                    if (jCheckBoxDefaultGuia.isSelected()) {
                        ConfMgr.getInstance().getController().removeConfiguration(
                                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_FOLDER_KEY);
                    }
                    else {
                        ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_FOLDER_KEY,
                                jTextFieldCarpetaGuia.getText()));
                    }
                    //Checkeo de selección para no mostrar el diálogo automáticamente
                    if (jCheckBoxNoMostrarGuia.isSelected())
                    {
                        ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_AUTOMATIC_SHOW_DISABLED_KEY,
                                Boolean.TRUE.toString()));
                    }
                    else
                    {
                        ConfMgr.getInstance().getController().replaceConfiguration(new Configuration(
                                GuiaFitosanitariaMgr.GUIA_FITOSANITARIA_AUTOMATIC_SHOW_DISABLED_KEY,
                                Boolean.FALSE.toString()));
                    }
                    GuiaFitosanitariaMgr.loadConfiguration(ConfMgr.getInstance());

                    EmailManager.getInstance().loadConfiguration(ConfMgr.getInstance());

                    frameNotifier.showInformationMessage("Se guardó la configuración correctamente");

                    DialogConfiguracion.this.clean();
                }
                catch (ConstraintViolationException ex) {
                    frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                }
                catch (InvalidStateException ex) {
                    Logger.getLogger(DialogConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex) {
                    Logger.getLogger(DialogConfiguracion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Action
    public Task verificarConexion()
    {
        return new Task(Application.getApplication())
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                try {
                    if (EmailManager.getInstance().checkConnection(jTextFieldRemitente.getText(),
                            jTextFieldServidorSMTP.getText(),
                            jComboBoxPuerto.getSelectedItem().toString(),
                            jTextFieldUsuarioSMTP.getText(), new String(jPasswordFieldSMTP.getPassword()),
                            jCheckBoxRequiereAutenticacion.isSelected(),
                            jCheckBoxUsarConexionSegura.isSelected())) {
                        DialogManager.showInfoMessage(DialogConfiguracion.this, "Conexión Satisfactoria",
                                "La conexión se ha realizado satisfactoriamente.", DialogManager.OK);
                    }
                    else {
                        DialogManager.showError(DialogConfiguracion.this, "Conexión No Satisfactoria",
                                "No se pudo conectar al servidor.", DialogManager.OK, null);
                    }
                }
                catch (NullPointerException ex) {
                    DialogManager.showError(DialogConfiguracion.this, "Conexión No Satisfactoria",
                            "Debe elegir valores para todos los campos.", DialogManager.OK, null);
                }

                return "";
            }
        };
    }

    @Action
    public void enviarCorreoPrueba()
    {
        try {
            EmailManager.getInstance().sendTestMessage();
            DialogManager.showInfoMessage(this, "Correo de Prueba",
                    "Se ha enviado un correo de prueba a las direcciones configuradas.", DialogManager.OK);
        }
        catch (NullPointerException ex) {
            DialogManager.showError(this, "Correo de Prueba",
                    "Hubo un error al enviar el correo de prueba.", DialogManager.OK, null);
        }
    }

    @Action
    public void examinarGuia()
    {
        if (fileChooserGuia == null) {
            fileChooserGuia = new JFileChooser();
            fileChooserGuia.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        String value = jTextFieldCarpetaGuia.getText();

        if (value.length() > 0) {
            fileChooserGuia.setCurrentDirectory(new File(value));
        }

        while (true) {
            int r = fileChooserGuia.showSaveDialog(this);

            if (r == JFileChooser.APPROVE_OPTION) {
                String newValue = fileChooserGuia.getSelectedFile().getAbsolutePath();
                checkCarpetaGuia(newValue);
                jTextFieldCarpetaGuia.setText(newValue);
                jTextFieldCarpetaGuia.setToolTipText(fileChooserGuia.getSelectedFile().getAbsolutePath());
                break;
            }
            else {
                frameNotifier.showOkMessage();
                break;
            }
        }

    }

    public void setSelectedTab(int idx)
    {
        jTabbedPane.setSelectedIndex(idx);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelAlertas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTableAlertas = new org.jdesktop.swingx.JXTable();
        jPanelDirecciones = new javax.swing.JPanel();
        jButtonSendTestMessage = new javax.swing.JButton();
        addRemovePanel = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jScrollPaneDirecciones = new javax.swing.JScrollPane();
        jXListDirecciones = new org.jdesktop.swingx.JXList();
        jLabelDireccion = new javax.swing.JLabel();
        jTextFieldDireccion = new javax.swing.JTextField();
        jCheckBoxSeleccionarTodasAlertas = new javax.swing.JCheckBox();
        jPanelCorreo = new javax.swing.JPanel();
        jLabelRemitente = new javax.swing.JLabel();
        jLabelServidorSMTP = new javax.swing.JLabel();
        jLabelUsuario = new javax.swing.JLabel();
        jLabelPassword = new javax.swing.JLabel();
        jTextFieldRemitente = new javax.swing.JTextField();
        jTextFieldServidorSMTP = new javax.swing.JTextField();
        jTextFieldUsuarioSMTP = new javax.swing.JTextField();
        jCheckBoxValoresDefecto = new javax.swing.JCheckBox();
        jPasswordFieldSMTP = new javax.swing.JPasswordField();
        jButtonVerificarSMTP = new javax.swing.JButton();
        jCheckBoxRequiereAutenticacion = new javax.swing.JCheckBox();
        jCheckBoxUsarConexionSegura = new javax.swing.JCheckBox();
        jLabelPuerto = new javax.swing.JLabel();
        jComboBoxPuerto = new javax.swing.JComboBox();
        jPanelGuia = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneGuiaFito = new javax.swing.JTextPane();
        jLabelCarpetaGuia = new javax.swing.JLabel();
        jTextFieldCarpetaGuia = new javax.swing.JTextField();
        jButtonExaminarGuia = new javax.swing.JButton();
        jCheckBoxDefaultGuia = new javax.swing.JCheckBox();
        jLabelLogoCasafe = new javax.swing.JLabel();
        jXImagePanelLogoCasafe = new org.jdesktop.swingx.JXImagePanel();
        jCheckBoxNoMostrarGuia = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogConfiguracion.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        frameNotifier.setName("frameNotifier"); // NOI18N

        jTabbedPane.setName("jTabbedPane"); // NOI18N
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

        jPanelAlertas.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelAlertas.border.title"))); // NOI18N
        jPanelAlertas.setName("jPanelAlertas"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jXTableAlertas.setName("jXTableAlertas"); // NOI18N
        jScrollPane2.setViewportView(jXTableAlertas);

        jPanelDirecciones.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelDirecciones.border.title"))); // NOI18N
        jPanelDirecciones.setName("jPanelDirecciones"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(DialogConfiguracion.class, this);
        jButtonSendTestMessage.setAction(actionMap.get("enviarCorreoPrueba")); // NOI18N
        jButtonSendTestMessage.setText(resourceMap.getString("jButtonSendTestMessage.text")); // NOI18N
        jButtonSendTestMessage.setName("jButtonSendTestMessage"); // NOI18N

        addRemovePanel.setName("addRemovePanel"); // NOI18N

        jScrollPaneDirecciones.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPaneDirecciones.border.title"))); // NOI18N
        jScrollPaneDirecciones.setName("jScrollPaneDirecciones"); // NOI18N

        jXListDirecciones.setName("jXListDirecciones"); // NOI18N
        jScrollPaneDirecciones.setViewportView(jXListDirecciones);

        jLabelDireccion.setText(resourceMap.getString("jLabelDireccion.text")); // NOI18N
        jLabelDireccion.setName("jLabelDireccion"); // NOI18N

        jTextFieldDireccion.setText(resourceMap.getString("jTextFieldDireccion.text")); // NOI18N
        jTextFieldDireccion.setName("jTextFieldDireccion"); // NOI18N

        javax.swing.GroupLayout jPanelDireccionesLayout = new javax.swing.GroupLayout(jPanelDirecciones);
        jPanelDirecciones.setLayout(jPanelDireccionesLayout);
        jPanelDireccionesLayout.setHorizontalGroup(
            jPanelDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDireccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDireccion)
                    .addComponent(addRemovePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSendTestMessage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanelDireccionesLayout.setVerticalGroup(
            jPanelDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDireccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelDireccionesLayout.createSequentialGroup()
                        .addComponent(jLabelDireccion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRemovePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSendTestMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jCheckBoxSeleccionarTodasAlertas.setText(resourceMap.getString("jCheckBoxSeleccionarTodasAlertas.text")); // NOI18N
        jCheckBoxSeleccionarTodasAlertas.setName("jCheckBoxSeleccionarTodasAlertas"); // NOI18N
        jCheckBoxSeleccionarTodasAlertas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSeleccionarTodasAlertasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAlertasLayout = new javax.swing.GroupLayout(jPanelAlertas);
        jPanelAlertas.setLayout(jPanelAlertasLayout);
        jPanelAlertasLayout.setHorizontalGroup(
            jPanelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAlertasLayout.createSequentialGroup()
                .addGroup(jPanelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                    .addComponent(jPanelDirecciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelAlertasLayout.createSequentialGroup()
                        .addContainerGap(615, Short.MAX_VALUE)
                        .addComponent(jCheckBoxSeleccionarTodasAlertas)))
                .addContainerGap())
        );
        jPanelAlertasLayout.setVerticalGroup(
            jPanelAlertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAlertasLayout.createSequentialGroup()
                .addComponent(jCheckBoxSeleccionarTodasAlertas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane.addTab(resourceMap.getString("jPanelAlertas.TabConstraints.tabTitle"), jPanelAlertas); // NOI18N

        jPanelCorreo.setName("jPanelCorreo"); // NOI18N

        jLabelRemitente.setText(resourceMap.getString("jLabelRemitente.text")); // NOI18N
        jLabelRemitente.setName("jLabelRemitente"); // NOI18N

        jLabelServidorSMTP.setText(resourceMap.getString("jLabelServidorSMTP.text")); // NOI18N
        jLabelServidorSMTP.setName("jLabelServidorSMTP"); // NOI18N

        jLabelUsuario.setText(resourceMap.getString("jLabelUsuario.text")); // NOI18N
        jLabelUsuario.setName("jLabelUsuario"); // NOI18N

        jLabelPassword.setText(resourceMap.getString("jLabelPassword.text")); // NOI18N
        jLabelPassword.setName("jLabelPassword"); // NOI18N

        jTextFieldRemitente.setText(resourceMap.getString("jTextFieldRemitente.text")); // NOI18N
        jTextFieldRemitente.setName("jTextFieldRemitente"); // NOI18N

        jTextFieldServidorSMTP.setText(resourceMap.getString("jTextFieldServidorSMTP.text")); // NOI18N
        jTextFieldServidorSMTP.setName("jTextFieldServidorSMTP"); // NOI18N

        jTextFieldUsuarioSMTP.setText(resourceMap.getString("jTextFieldUsuarioSMTP.text")); // NOI18N
        jTextFieldUsuarioSMTP.setName("jTextFieldUsuarioSMTP"); // NOI18N

        jCheckBoxValoresDefecto.setText(resourceMap.getString("jCheckBoxValoresDefecto.text")); // NOI18N
        jCheckBoxValoresDefecto.setName("jCheckBoxValoresDefecto"); // NOI18N
        jCheckBoxValoresDefecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxValoresDefectoActionPerformed(evt);
            }
        });

        jPasswordFieldSMTP.setText(resourceMap.getString("jPasswordFieldSMTP.text")); // NOI18N
        jPasswordFieldSMTP.setName("jPasswordFieldSMTP"); // NOI18N

        jButtonVerificarSMTP.setAction(actionMap.get("verificarConexion")); // NOI18N
        jButtonVerificarSMTP.setText(resourceMap.getString("jButtonVerificarSMTP.text")); // NOI18N
        jButtonVerificarSMTP.setName("jButtonVerificarSMTP"); // NOI18N

        jCheckBoxRequiereAutenticacion.setText(resourceMap.getString("jCheckBoxRequiereAutenticacion.text")); // NOI18N
        jCheckBoxRequiereAutenticacion.setName("jCheckBoxRequiereAutenticacion"); // NOI18N

        jCheckBoxUsarConexionSegura.setText(resourceMap.getString("jCheckBoxUsarConexionSegura.text")); // NOI18N
        jCheckBoxUsarConexionSegura.setName("jCheckBoxUsarConexionSegura"); // NOI18N

        jLabelPuerto.setText(resourceMap.getString("jLabelPuerto.text")); // NOI18N
        jLabelPuerto.setName("jLabelPuerto"); // NOI18N

        jComboBoxPuerto.setName("jComboBoxPuerto"); // NOI18N

        javax.swing.GroupLayout jPanelCorreoLayout = new javax.swing.GroupLayout(jPanelCorreo);
        jPanelCorreo.setLayout(jPanelCorreoLayout);
        jPanelCorreoLayout.setHorizontalGroup(
            jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCorreoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCorreoLayout.createSequentialGroup()
                        .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPuerto)
                            .addComponent(jLabelRemitente)
                            .addComponent(jLabelServidorSMTP)
                            .addComponent(jLabelUsuario)
                            .addComponent(jLabelPassword))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxUsarConexionSegura)
                            .addComponent(jCheckBoxRequiereAutenticacion)
                            .addComponent(jTextFieldServidorSMTP, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addComponent(jTextFieldUsuarioSMTP, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addComponent(jPasswordFieldSMTP, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addGroup(jPanelCorreoLayout.createSequentialGroup()
                                .addComponent(jCheckBoxValoresDefecto)
                                .addGap(318, 318, 318))
                            .addComponent(jTextFieldRemitente, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addComponent(jComboBoxPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonVerificarSMTP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanelCorreoLayout.setVerticalGroup(
            jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCorreoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxValoresDefecto)
                .addGap(1, 1, 1)
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRemitente)
                    .addComponent(jTextFieldRemitente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelServidorSMTP)
                    .addComponent(jTextFieldServidorSMTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPuerto)
                    .addComponent(jComboBoxPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUsuario)
                    .addComponent(jTextFieldUsuarioSMTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCorreoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPassword)
                    .addComponent(jPasswordFieldSMTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jCheckBoxRequiereAutenticacion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxUsarConexionSegura)
                .addGap(10, 10, 10)
                .addComponent(jButtonVerificarSMTP, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(290, Short.MAX_VALUE))
        );

        jTabbedPane.addTab(resourceMap.getString("jPanelCorreo.TabConstraints.tabTitle"), jPanelCorreo); // NOI18N

        jPanelGuia.setName("jPanelGuia"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextPaneGuiaFito.setBackground(resourceMap.getColor("jTextPaneGuiaFito.background")); // NOI18N
        jTextPaneGuiaFito.setEditable(false);
        jTextPaneGuiaFito.setFont(resourceMap.getFont("jTextPaneGuiaFito.font")); // NOI18N
        jTextPaneGuiaFito.setText(resourceMap.getString("jTextPaneGuiaFito.text")); // NOI18N
        jTextPaneGuiaFito.setName("jTextPaneGuiaFito"); // NOI18N
        jScrollPane1.setViewportView(jTextPaneGuiaFito);

        jLabelCarpetaGuia.setText(resourceMap.getString("jLabelCarpetaGuia.text")); // NOI18N
        jLabelCarpetaGuia.setName("jLabelCarpetaGuia"); // NOI18N

        jTextFieldCarpetaGuia.setEditable(false);
        jTextFieldCarpetaGuia.setText(resourceMap.getString("jTextFieldCarpetaGuia.text")); // NOI18N
        jTextFieldCarpetaGuia.setName("jTextFieldCarpetaGuia"); // NOI18N

        jButtonExaminarGuia.setAction(actionMap.get("examinarGuia")); // NOI18N
        jButtonExaminarGuia.setText(resourceMap.getString("jButtonExaminarGuia.text")); // NOI18N
        jButtonExaminarGuia.setName("jButtonExaminarGuia"); // NOI18N

        jCheckBoxDefaultGuia.setText(resourceMap.getString("jCheckBoxDefaultGuia.text")); // NOI18N
        jCheckBoxDefaultGuia.setName("jCheckBoxDefaultGuia"); // NOI18N
        jCheckBoxDefaultGuia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDefaultGuiaActionPerformed(evt);
            }
        });

        jLabelLogoCasafe.setIcon(resourceMap.getIcon("jLabelLogoCasafe.icon")); // NOI18N
        jLabelLogoCasafe.setText(resourceMap.getString("jLabelLogoCasafe.text")); // NOI18N
        jLabelLogoCasafe.setName("jLabelLogoCasafe"); // NOI18N

        jXImagePanelLogoCasafe.setName("jXImagePanelLogoCasafe"); // NOI18N

        javax.swing.GroupLayout jXImagePanelLogoCasafeLayout = new javax.swing.GroupLayout(jXImagePanelLogoCasafe);
        jXImagePanelLogoCasafe.setLayout(jXImagePanelLogoCasafeLayout);
        jXImagePanelLogoCasafeLayout.setHorizontalGroup(
            jXImagePanelLogoCasafeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jXImagePanelLogoCasafeLayout.setVerticalGroup(
            jXImagePanelLogoCasafeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jCheckBoxNoMostrarGuia.setText(resourceMap.getString("jCheckBoxNoMostrarGuia.text")); // NOI18N
        jCheckBoxNoMostrarGuia.setName("jCheckBoxNoMostrarGuia"); // NOI18N
        jCheckBoxNoMostrarGuia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNoMostrarGuiaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGuiaLayout = new javax.swing.GroupLayout(jPanelGuia);
        jPanelGuia.setLayout(jPanelGuiaLayout);
        jPanelGuiaLayout.setHorizontalGroup(
            jPanelGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCarpetaGuia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxDefaultGuia)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuiaLayout.createSequentialGroup()
                        .addComponent(jTextFieldCarpetaGuia, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExaminarGuia, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
            .addGroup(jPanelGuiaLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jXImagePanelLogoCasafe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(540, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuiaLayout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .addComponent(jLabelLogoCasafe, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuiaLayout.createSequentialGroup()
                .addContainerGap(591, Short.MAX_VALUE)
                .addComponent(jCheckBoxNoMostrarGuia)
                .addContainerGap())
        );
        jPanelGuiaLayout.setVerticalGroup(
            jPanelGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jCheckBoxDefaultGuia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGuiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCarpetaGuia)
                    .addComponent(jTextFieldCarpetaGuia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExaminarGuia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXImagePanelLogoCasafe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLogoCasafe, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jCheckBoxNoMostrarGuia)
                .addContainerGap())
        );

        jTabbedPane.addTab(resourceMap.getString("jPanelGuia.TabConstraints.tabTitle"), jPanelGuia); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
            .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxValoresDefectoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxValoresDefectoActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxValoresDefectoActionPerformed
        jTextFieldUsuarioSMTP.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jTextFieldRemitente.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jTextFieldServidorSMTP.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jPasswordFieldSMTP.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jButtonVerificarSMTP.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jCheckBoxRequiereAutenticacion.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jCheckBoxUsarConexionSegura.setEnabled(!jCheckBoxValoresDefecto.isSelected());
        jComboBoxPuerto.setEnabled(!jCheckBoxValoresDefecto.isSelected());
    }//GEN-LAST:event_jCheckBoxValoresDefectoActionPerformed

    private void jCheckBoxDefaultGuiaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxDefaultGuiaActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxDefaultGuiaActionPerformed
        jButtonExaminarGuia.setEnabled(!jCheckBoxDefaultGuia.isSelected());

        if (jCheckBoxDefaultGuia.isSelected()) {
            jTextFieldCarpetaGuia.setText(GuiaFitosanitariaMgr.DEFAULT_INSTALL_PATH);
            checkCarpetaGuia(jTextFieldCarpetaGuia.getText());
        }
    }//GEN-LAST:event_jCheckBoxDefaultGuiaActionPerformed

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jTabbedPaneStateChanged
    {//GEN-HEADEREND:event_jTabbedPaneStateChanged
        if (jTabbedPane.getSelectedComponent().equals(jPanelGuia)) {
            checkCarpetaGuia(jTextFieldCarpetaGuia.getText());
        }
        else {
            frameNotifier.showOkMessage();
        }
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void jCheckBoxSeleccionarTodasAlertasActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxSeleccionarTodasAlertasActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxSeleccionarTodasAlertasActionPerformed
        alertasTableModel.changeAll(jCheckBoxSeleccionarTodasAlertas.isSelected());
    }//GEN-LAST:event_jCheckBoxSeleccionarTodasAlertasActionPerformed

    private void jCheckBoxNoMostrarGuiaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxNoMostrarGuiaActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxNoMostrarGuiaActionPerformed
        if (jCheckBoxNoMostrarGuia.isSelected())
        {
            showAutomatically = false;
        }
        else
        {
            showAutomatically = true;
        }
    }//GEN-LAST:event_jCheckBoxNoMostrarGuiaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemovePanel addRemovePanel;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JButton jButtonExaminarGuia;
    private javax.swing.JButton jButtonSendTestMessage;
    private javax.swing.JButton jButtonVerificarSMTP;
    private javax.swing.JCheckBox jCheckBoxDefaultGuia;
    private javax.swing.JCheckBox jCheckBoxNoMostrarGuia;
    private javax.swing.JCheckBox jCheckBoxRequiereAutenticacion;
    private javax.swing.JCheckBox jCheckBoxSeleccionarTodasAlertas;
    private javax.swing.JCheckBox jCheckBoxUsarConexionSegura;
    private javax.swing.JCheckBox jCheckBoxValoresDefecto;
    private javax.swing.JComboBox jComboBoxPuerto;
    private javax.swing.JLabel jLabelCarpetaGuia;
    private javax.swing.JLabel jLabelDireccion;
    private javax.swing.JLabel jLabelLogoCasafe;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelPuerto;
    private javax.swing.JLabel jLabelRemitente;
    private javax.swing.JLabel jLabelServidorSMTP;
    private javax.swing.JLabel jLabelUsuario;
    private javax.swing.JPanel jPanelAlertas;
    private javax.swing.JPanel jPanelCorreo;
    private javax.swing.JPanel jPanelDirecciones;
    private javax.swing.JPanel jPanelGuia;
    private javax.swing.JPasswordField jPasswordFieldSMTP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneDirecciones;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextField jTextFieldCarpetaGuia;
    private javax.swing.JTextField jTextFieldDireccion;
    private javax.swing.JTextField jTextFieldRemitente;
    private javax.swing.JTextField jTextFieldServidorSMTP;
    private javax.swing.JTextField jTextFieldUsuarioSMTP;
    private javax.swing.JTextPane jTextPaneGuiaFito;
    private org.jdesktop.swingx.JXImagePanel jXImagePanelLogoCasafe;
    private org.jdesktop.swingx.JXList jXListDirecciones;
    private org.jdesktop.swingx.JXTable jXTableAlertas;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    // End of variables declaration//GEN-END:variables
}
