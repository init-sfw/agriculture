package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.MappedSuperclass;
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
@MappedSuperclass
public abstract class DetalleCancelacionEgresoGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity>
        extends DetalleMovimientoGrano<T, A, D> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS;
    static {

        List<String> aux = new ArrayList<String>(Arrays.asList(DetalleEgresoGrano.TABLE_HEADERS));
        TABLE_HEADERS = aux.toArray(new String[0]);
    }

    private DetalleEgresoGrano<T, A, D> detalleEgreso;

    protected DetalleCancelacionEgresoGrano() {
        super();
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
        setPositivo(false);
    }

    /** Constructor por defecto de DetalleCancelacionIngresoStock */
    public DetalleCancelacionEgresoGrano(DetalleEgresoGrano<T, A, D> detalleEgreso) {
        super();
        setFecha(detalleEgreso.getFecha());
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
        setDetalleEgreso(detalleEgreso);
        setCantidad(new ValorUnidad(-1D * detalleEgreso.getCantidad().getValor(),
                detalleEgreso.getCantidad().getUnidad()));
        setValor(detalleEgreso.getValor());
        detalleEgreso.setCancelado(Boolean.TRUE);
        setPositivo(false);
    }

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = DetalleMovimientoGrano.class)
    public DetalleEgresoGrano<T, A, D> getDetalleEgreso() {
        return detalleEgreso;
    }

    public void setDetalleEgreso(DetalleEgresoGrano<T, A, D> detalleEgreso) {
        this.detalleEgreso = detalleEgreso;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = getDetalleEgreso().getTableLine();

         r.set(DetalleIngresoGrano.CANTIDAD_COLUMN, getCantidad());

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
