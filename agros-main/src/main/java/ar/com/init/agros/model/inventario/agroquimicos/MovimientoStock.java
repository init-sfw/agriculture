package ar.com.init.agros.model.inventario.agroquimicos;

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
 * Clase MovimientoStock
 *
 *
 * @author gmatheu
 * @version 21/07/2009 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "dtipo")
@DiscriminatorValue(TipoMovimientoStock.OTRO_VALUE)
public class MovimientoStock<T extends DetalleMovimientoStock> extends MovimientoStockAlmacenamiento<T>
{

    private static final long serialVersionUID = -1L;   

    /** Constructor por defecto de MovimientoStock */
    public MovimientoStock()
    {
        super();      
    }
    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, targetEntity = DetalleMovimientoStock.class)
    @Override
    public List<T> getDetalles() {
        return super.getDetalles();
    }


    @Override
    public String entityName()
    {
        return "Movimiento de stock de Agroquímicos";
    }    
}
