package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.UnidadMedida;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author gmatheu
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "dtipo")
@DiscriminatorValue("G")
public abstract class ValorGrano<T extends Almacenamiento> extends ValorAlmacenamiento<T> {

    public ValorGrano(T almacenamiento, UnidadMedida unidad) {
        super(almacenamiento, unidad);
    }

    public ValorGrano() {
        super();
    }   
}
