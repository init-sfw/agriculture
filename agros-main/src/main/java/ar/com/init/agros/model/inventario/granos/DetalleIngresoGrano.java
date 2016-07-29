package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.Arrays;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Clase DetalleIngresoStock
 *
 *
 * @author gmatheu
 * @version 29/06/2009 
 */
@MappedSuperclass
public abstract class DetalleIngresoGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity>
        extends DetalleOperacionGrano<T, A, D> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS;

    static {

        String[] aux = Arrays.copyOf(DetalleOperacionGrano.TABLE_HEADERS, DetalleOperacionGrano.TABLE_HEADERS.length);
        aux[SERVICIO_COLUMN] = "Proveedor";
        TABLE_HEADERS = aux;
    }

    /** Constructor por defecto de DetalleIngresoStock */
    public DetalleIngresoGrano() {
        super();
        setTipo(TipoMovimientoStockEnum.INGRESO.tipo());
        setCancelado(false);
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
