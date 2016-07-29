package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
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
public class IngresoStock extends MovimientoStock<DetalleIngresoStock> implements Emailable
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de IngresoStock */
    public IngresoStock()
    {
        super();
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public EMailMessage createEmailMessage()
    {
        EmailHelper helper = new EmailHelper();
        helper.writeLogo();
        helper.writeTitle("Alertas de Stock");
        helper.writeBlankLine();
        helper.writeLine("Se ha realizado un ingreso de stock.");
        helper.writeBlankLine();
        helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
        helper.writeBlankLine();

        helper.writeLine("Detalle:");
        helper.writeBlankLine();

        helper.writeTable(DetalleIngresoStock.TABLE_HEADERS, getCastedDetalles());

        helper.writeBlankLine();
        helper.writeBlankLine();

        helper.writeLine("Observaciones:");
        helper.writeLine(getObservaciones());

        EMailMessage r = new EMailMessage("Alertas de Stock: Ingreso - " + EmailHelper.formatDate(getFecha()),
                helper.buildContent());

        return r;
    }

    @Override
    public String entityName()
    {
        return "Ingreso de Stock";
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>(4);
        aux.add(getFecha());
        aux.add(getFechaCreacion());
        aux.add(getObservaciones());
        aux.add(contarDetalles());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return MovimientoStock.TABLE_HEADERS;
    }
}
