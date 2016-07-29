package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.agroquimicos.AjusteInventario;
import ar.com.init.agros.model.util.EMailMessage;
import java.util.logging.Logger;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Clase CancelacionIngresoStock
 *
 *
 * @author gmatheu
 * @version 21/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.CANCELACION_VALUE + "_" +  TipoMovimientoStock.INGRESO_VALUE)
public class CancelacionIngresoGrano extends MovimientoGrano<DetalleCancelacionIngresoGrano> implements
        Emailable {

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CancelacionIngresoStock */
    public CancelacionIngresoGrano() {
        super();
    }

    @Override
    public EMailMessage createEmailMessage() {
        try {
            EmailHelper helper = new EmailHelper();
            helper.writeLogo();
            helper.writeTitle(String.format("Alertas de %s", VALUE_TITLE));
            helper.writeBlankLine();
            helper.writeLine(String.format("Se han cancelado los siguientes ingresos de %s.", VALUE_TITLE));
            helper.writeBlankLine();
            helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
            helper.writeBlankLine();

            helper.writeLine("Detalle:");
            helper.writeBlankLine();

            helper.writeTable(DetalleCancelacionIngresoGrano.TABLE_HEADERS, getCastedDetalles());

            helper.writeBlankLine();
            helper.writeBlankLine();

            helper.writeLine("Observaciones:");
            helper.writeLine(getObservaciones());

            EMailMessage r = new EMailMessage(String.format("Alertas de Semillas: Cancelacion de Ingreso de %s - %s", VALUE_TITLE, EmailHelper.formatDate(getFecha())),
                    helper.buildContent());

            return r;
        } catch (Exception ex) {
            Logger.getLogger(AjusteInventario.class.getName()).info("Exception while creating email");
        }

        return null;
    }
}
