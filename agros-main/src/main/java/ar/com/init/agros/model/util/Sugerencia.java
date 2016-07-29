package ar.com.init.agros.model.util;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.view.DialogSugerencias.TipoSugerencia;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

/**
 * Clase Sugerencia
 *
 *
 * @author fbobbio
 * @version 14-mar-2011 
 */
@Entity
public class Sugerencia extends BaseEntity implements Emailable {
    public static final String DEFAULT_RECIPIENT = "sugerencias@campoportunidades.com";

    private Date fechaEnvio;
    private TipoSugerencia tipoSugerencia;
    /** Nombre de la persona que realiza la sugerencia */
    private String nombre;
    /** Nombre de la persona que realiza la sugerencia */
    private String mail;
    /** Nombre de la persona que realiza la sugerencia */
    private String telefono;
    private String sugerencia;

    /** Constructor por defecto de Sugerencia */
    public Sugerencia() {
    }

    public Sugerencia(Date fechaEnvio, TipoSugerencia tipoSugerencia, String nombre, String mail, String telefono, String sugerencia) {
        this.fechaEnvio = fechaEnvio;
        this.tipoSugerencia = tipoSugerencia;
        this.nombre = nombre;
        this.mail = mail;
        this.telefono = telefono;
        this.sugerencia = sugerencia;
    }

    @NotEmpty
    @Length(min = 0, max = 1024)
    @Column(length = 1024)
    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    @NotNull
    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    @NotEmpty
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @NotEmpty
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @NotEmpty
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @NotNull
    public TipoSugerencia getTipoSugerencia() {
        return tipoSugerencia;
    }

    public void setTipoSugerencia(TipoSugerencia tipoSugerencia) {
        this.tipoSugerencia = tipoSugerencia;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String entityName() {
        return "Sugerencia";
    }

    @Override
    public EMailMessage createEmailMessage() {
        try {
            EmailHelper helper = new EmailHelper();
            helper.writeLogo();
            helper.writeTitle("Sugerencia");
            helper.writeBlankLine();
            helper.writeLine(String.format("Se ha realizado la siguiente sugerencia:"));
            helper.writeBlankLine();
            helper.writeLine(String.format("Tipo: %s", getTipoSugerencia().getListLine()));
            helper.writeLine(String.format("Nombre: %s", getNombre()));
            helper.writeLine(String.format("E-mail: %s", getMail()));
            helper.writeLine(String.format("Teléfono: %s", getTelefono()));
            helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFechaEnvio())));
            helper.writeBlankLine();

            helper.writeLine("Mensaje:");
            helper.writeBlankLine();
            helper.writeLine(getSugerencia());
            helper.writeBlankLine();
            helper.writeBlankLine();

//            helper.writeLine("Observaciones:");
//            helper.writeLine(getObservaciones());

            EMailMessage r = new EMailMessage("Sugerencia OSIRIS - " + EmailHelper.formatDate(getFechaEnvio()),
                    helper.buildContent());
            r.setSender(getMail());
            r.setRecipients(DEFAULT_RECIPIENT);
            return r;
        } catch (Exception ex) {
            Logger.getLogger(Sugerencia.class.getName()).info("Exception while creating email");

        }
        return null;
    }
}
