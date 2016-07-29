package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;


import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Clase DetalleMovimientoGrano
 *
 *
 * @author gmatheu
 * @version 09/12/2010
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "dtipo")
@DiscriminatorValue(TipoMovimientoStock.OTRO_VALUE)
public abstract class DetalleMovimientoGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity>
        extends DetalleMovimientoStockAlmacenamiento<T, A, D> {

    public static final String VALUE_TITLE = "Semilla/Cereal";
    public static String[] TABLE_HEADERS = {VALUE_TITLE, "Almacenamiento", "Cantidad"};
    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DetalleMovimientoStock */
    public DetalleMovimientoGrano() {
        super();
    }

//    @ManyToOne
//    @Override
//    public T getValor() {
//       return valor;
//    }


}
