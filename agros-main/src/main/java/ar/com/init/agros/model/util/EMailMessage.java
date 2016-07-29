package ar.com.init.agros.model.util;

import ar.com.init.agros.model.base.BaseEntity;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

/**
 * Clase EMailMessage
 *
 *
 * @author gmatheu
 * @version 27/06/2009 
 */
@Entity
public class EMailMessage extends BaseEntity
{

    private static final long serialVersionUID = -1L;
    public static final String RECIPIENT_SEPARATOR = ";";
    private String message;
    private String recipients;
    private String subject;
    private int sentTimes;
    private Date lastSent;
    private String sender;

    public EMailMessage()
    {
        super();
    }

    /** Constructor por defecto de EMailMessage */
    public EMailMessage(String subject, String message)
    {
        sentTimes = 0;
        this.subject = subject;
        this.message = message;
    }

    @NotEmpty
    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastSent()
    {
        return lastSent;
    }

    public void setLastSent(Date lastSent)
    {
        this.lastSent = lastSent;
    }

    @NotNull
    @Lob
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @NotEmpty
    public String getRecipients()
    {
        return recipients;
    }

    public void setRecipients(String recipients)
    {
        this.recipients = recipients;
    }

    public int getSentTimes()
    {
        return sentTimes;
    }

    public void setSentTimes(int sentTimes)
    {
        this.sentTimes = sentTimes;
    }

    @NotEmpty
    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void addSent()
    {
        sentTimes++;
    }

    @Override
    public String toString()
    {
        return message;
    }

    @Override
    public String entityName()
    {
        return "Mensaje de correo electrónico";
    }
}
