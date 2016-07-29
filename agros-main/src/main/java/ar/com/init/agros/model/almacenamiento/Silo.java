package ar.com.init.agros.model.almacenamiento;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author gmatheu
 */
@Entity
@DiscriminatorValue(value = "S")
public class Silo extends Almacenamiento {

    public static final String ENTITY_NAME = "Silo";
    private List<ValorCereal> valoresCereal;

    @OneToMany(mappedBy = "almacenamiento")
    public List<ValorCereal> getValoresCereal() {

        if (valoresCereal == null) {
            valoresCereal = new ArrayList<ValorCereal>();
        }
        return valoresCereal;
    }

    public void setValoresCereal(List<ValorCereal> valoresCereal) {
        this.valoresCereal = valoresCereal;
    }

    @Override
    public String entityName() {
        return ENTITY_NAME;
    }

    @Transient
    @Override
    public String getTipoAlmacenamiento() {
        return ENTITY_NAME;
    }
}
