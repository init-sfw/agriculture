package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
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
@DiscriminatorValue(TipoMovimientoStock.CANCELACION_VALUE)
public class CancelacionIngresoStock extends MovimientoStock<DetalleCancelacionIngresoStock> implements
        Emailable
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CancelacionIngresoStock */
    public CancelacionIngresoStock()
    {
        super();
    }

    @Override
    public EMailMessage createEmailMessage()
    {
        try {
            EmailHelper helper = new EmailHelper();
            helper.writeLogo();
            helper.writeTitle("Alertas de Stock");
            helper.writeBlankLine();
            helper.writeLine("Se han cancelado los siguientes ingresos de stock.");
            helper.writeBlankLine();
            helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
            helper.writeBlankLine();

            helper.writeLine("Detalle:");
            helper.writeBlankLine();

            helper.writeTable(DetalleCancelacionIngresoStock.TABLE_HEADERS, getCastedDetalles());

            helper.writeBlankLine();
            helper.writeBlankLine();

            helper.writeLine("Observaciones:");
            helper.writeLine(getObservaciones());

            EMailMessage r = new EMailMessage("Alertas de Stock: Cancelacion de Ingreso - " + EmailHelper.formatDate(getFecha()),
                    helper.buildContent());

            return r;
        }
        catch (Exception ex) {
            Logger.getLogger(AjusteInventario.class.getName()).info("Exception while creating email");
        }

        return null;
    }
}
