package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.util.EMailMessage;
import java.util.logging.Logger;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Clase AjusteInventario
 *
 *
 * @author gmatheu
 * @version 21/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.AJUSTE_VALUE)
public class AjusteInventario extends MovimientoStock<DetalleAjusteInventario> implements Emailable
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CancelacionIngresoStock */
    public AjusteInventario()
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
            helper.writeLine("Se han realizados ajustes por control de inventario.");
            helper.writeBlankLine();
            helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
            helper.writeBlankLine();

            helper.writeLine("Detalle:");
            helper.writeBlankLine();

            helper.writeTable(DetalleAjusteInventario.TABLE_HEADERS, getCastedDetalles());

            helper.writeBlankLine();
            helper.writeBlankLine();

//            helper.writeLine("Observaciones:");
//            helper.writeLine(getObservaciones());

            EMailMessage r = new EMailMessage("Alertas de Stock: Ajuste por Control de Inventario - " + EmailHelper.formatDate(getFecha()),
                    helper.buildContent());
            return r;
        }
        catch (Exception ex) {
            Logger.getLogger(AjusteInventario.class.getName()).info("Exception while creating email");

        }
        return null;
    }
}
