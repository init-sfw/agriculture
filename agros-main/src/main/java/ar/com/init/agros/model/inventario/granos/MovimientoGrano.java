package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.inventario.MovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import org.hibernate.validator.NotEmpty;

/**
 * Clase MovimientoCerealGrano
 *
 *
 * @author gmatheu
 * @version 09/12/2010
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "dtipo")
@DiscriminatorValue(TipoMovimientoStock.OTRO_VALUE)
public class MovimientoGrano<T extends DetalleMovimientoGrano> extends MovimientoStockAlmacenamiento<T> {

    private static final long serialVersionUID = -1L;
    public static final String VALUE_TITLE = "Semillas/Cereales";

    /** Constructor por defecto de MovimientoSemilla */
    public MovimientoGrano() {
        super();
    }

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, targetEntity = DetalleMovimientoGrano.class)
    @Override
    public List<T> getDetalles() {
        return super.getDetalles();
    }

    @Override
    public String entityName() {
        return String.format("Movimiento de stock de %s", VALUE_TITLE);
    }
}
