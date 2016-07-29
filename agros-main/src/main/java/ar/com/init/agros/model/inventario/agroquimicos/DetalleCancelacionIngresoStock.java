package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleCancelacionIngresoStock
 *
 *
 * @author gmatheu
 * @version 24/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.CANCELACION_VALUE)
public class DetalleCancelacionIngresoStock extends DetalleMovimientoStock
{

    private static final long serialVersionUID = -1L;
    private static final int CANTIDAD_IDX = 6;
    public static String[] TABLE_HEADERS = {"Fecha Ingreso", "Deposito", "Agroquímico", "Stock Mínimo", "Proveedor", "Remito", "Cantidad", "Costo"};
    private DetalleIngresoStock detalleIngreso;

    protected DetalleCancelacionIngresoStock()
    {
        super();
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
    }

    /** Constructor por defecto de DetalleCancelacionIngresoStock */
    public DetalleCancelacionIngresoStock(DetalleIngresoStock detalleIngreso)
    {
        super();
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
        setDetalleIngreso(detalleIngreso);
        setCantidad(new ValorUnidad(-1D * detalleIngreso.getCantidad().getValor(),
                detalleIngreso.getCantidad().getUnidad()));
        setValor(detalleIngreso.getValor());
        detalleIngreso.setCancelado(Boolean.TRUE);
    }

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE)
    public DetalleIngresoStock getDetalleIngreso()
    {
        return detalleIngreso;
    }

    public void setDetalleIngreso(DetalleIngresoStock detalleIngreso)
    {
        this.detalleIngreso = detalleIngreso;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = getDetalleIngreso().getTableLine();

        r.set(CANTIDAD_IDX, getCantidad());      

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS;
    }
}
