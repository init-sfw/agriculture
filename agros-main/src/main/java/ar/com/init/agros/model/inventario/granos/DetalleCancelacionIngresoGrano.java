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
public abstract class DetalleCancelacionIngresoGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity>
        extends DetalleMovimientoGrano<T, A, D> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS;

    static {

        List<String> aux = new ArrayList<String>(Arrays.asList(DetalleIngresoGrano.TABLE_HEADERS));
        TABLE_HEADERS = aux.toArray(new String[0]);
    }
    private DetalleIngresoGrano<T, A, D> detalleIngreso;

    public DetalleCancelacionIngresoGrano() {
        super();
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
        setPositivo(true);
    }

    /** Constructor por defecto de DetalleCancelacionIngresoStock */
    public DetalleCancelacionIngresoGrano(DetalleIngresoGrano<T, A, D> detalleIngreso) {
        super();
        setFecha(detalleIngreso.getFecha());
        setTipo(TipoMovimientoStockEnum.CANCELACION.tipo());
        setDetalleIngreso(detalleIngreso);
        setCantidad(new ValorUnidad(-1D * detalleIngreso.getCantidad().getValor(),
                detalleIngreso.getCantidad().getUnidad()));
        setValor(detalleIngreso.getValor());
        detalleIngreso.setCancelado(Boolean.TRUE);
        setPositivo(true);
    }

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = DetalleMovimientoGrano.class)
    public DetalleIngresoGrano<T, A, D> getDetalleIngreso() {
        return detalleIngreso;
    }

    public void setDetalleIngreso(DetalleIngresoGrano<T, A, D> detalleIngreso) {
        this.detalleIngreso = detalleIngreso;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = getDetalleIngreso().getTableLine();

        r.set(DetalleIngresoGrano.CANTIDAD_COLUMN, getCantidad());

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
