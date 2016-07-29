package ar.com.init.agros.model.inventario;

import ar.com.init.agros.model.base.NamedEntity;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * Clase TipoMovimientoStock
 *
 *
 * @author gmatheu
 * @version 14/06/2009 
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre"})})
public class TipoMovimientoStock extends NamedEntity {

    private static final long serialVersionUID = -1L;
    private static final String ENTITY_NAME = "Tipo Movimiento de Stock";
    public static final String INGRESO_VALUE = "IN";
    public static final String CANCELACION_VALUE = "CA";
    public static final String AJUSTE_VALUE = "AJ";
    public static final String MOVIMIENTO_DEPOSITO_VALUE = "MVDE";
    public static final String OTRO_VALUE = "OT";
    public static final String EGRESO_VALUE = "EG";
    private List<DetalleMovimientoStockAlmacenamiento> movimientos;

    public TipoMovimientoStock() {
        super();
    }

    /** Constructor por defecto de TipoMovimientoStock */
    public TipoMovimientoStock(UUID id, String nombre, String desc) {
        super(id, nombre, desc);
    }

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tipo")
//    @OrderBy(value = "fecha")
    @Transient
    public List<DetalleMovimientoStockAlmacenamiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<DetalleMovimientoStockAlmacenamiento> movimientos) {
        this.movimientos = movimientos;
    }

    @Override
    public String entityName() {
        return ENTITY_NAME;
    }
}
