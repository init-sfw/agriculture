package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Clase DetalleAjusteInventario
 *
 *
 * @author gmatheu
 * @version 29/06/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.AJUSTE_VALUE)
public class DetalleAjusteInventario extends DetalleMovimientoStock
{

    public static String[] TABLE_HEADERS = {"Agroquímico", "Depósito", "Cantidad", "Motivo"};
    private static final long serialVersionUID = -1L;
    private String motivo;

    /** Constructor por defecto de DetalleAjusteInventario */
    public DetalleAjusteInventario()
    {
        super();
        setTipo(TipoMovimientoStockEnum.AJUSTE.tipo());
    }

    public String getMotivo()
    {
        return motivo;
    }

    public void setMotivo(String motivo)
    {
        this.motivo = motivo;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = super.getTableLine();
        r.add(getMotivo());

        return r;
    }
}
