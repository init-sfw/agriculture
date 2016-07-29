package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
import ar.com.init.agros.model.util.EMailMessage;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Clase IngresoStock
 *
 *
 * @author gmatheu
 * @version 06/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.INGRESO_VALUE)
public class IngresoGrano extends MovimientoGrano<DetalleIngresoGrano> implements Emailable {

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de IngresoStock */
    public IngresoGrano() {
        super();
    }

    @Override
    public EMailMessage createEmailMessage() {
        EmailHelper helper = new EmailHelper();
        helper.writeLogo();
        helper.writeTitle(String.format("Alertas de %s", VALUE_TITLE));
        helper.writeBlankLine();
        helper.writeLine(String.format("Se ha realizado un ingreso de %s.", VALUE_TITLE));
        helper.writeBlankLine();
        helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
        helper.writeBlankLine();

        helper.writeLine("Detalle:");
        helper.writeBlankLine();

        helper.writeTable(DetalleIngresoSemilla.TABLE_HEADERS, getCastedDetalles());

        helper.writeBlankLine();
        helper.writeBlankLine();

        helper.writeLine("Observaciones:");
        helper.writeLine(getObservaciones());

        EMailMessage r = new EMailMessage(String.format("Alertas de %s: Ingreso - %s", VALUE_TITLE, EmailHelper.formatDate(getFecha())),
                helper.buildContent());

        return r;
    }

    @Override
    public String entityName() {
        return String.format("Ingreso de %s", VALUE_TITLE);
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> aux = new ArrayList<Object>(4);
        aux.add(getFecha());
        aux.add(getFechaCreacion());
        aux.add(getObservaciones());
        aux.add(contarDetalles());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return MovimientoGrano.TABLE_HEADERS;
    }
}
