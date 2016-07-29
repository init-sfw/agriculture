package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.base.NamedEntity;
import ar.com.init.agros.model.terreno.Campo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * Clase Deposito
 *
 *
 * @author gmatheu
 * @version 11/07/2009 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.CHAR)
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"nombre"}))
public abstract class Almacenamiento extends NamedEntity implements Almacenable {

    private static final long serialVersionUID = -1L;
    public static final String ENTITY_NAME = "Almacenamiento";
    public static final String[] TABLE_HEADERS = {"Nombre", "Tipo", "Descripción"};
    private Set<Campo> campos;
    private List<ValorSemilla> valoresSemilla;

    /** Constructor por defecto de Deposito */
    public Almacenamiento() {
        super();
        campos = new HashSet<Campo>();
    }

    /** Constructor por defecto de Deposito */
    public Almacenamiento(UUID uuid, String nombre, String desc) {
        super(uuid, nombre, desc);
        campos = new HashSet<Campo>();
    }

    @ManyToMany(mappedBy = "almacenamientos")
    @Override
    public Set<Campo> getCampos() {
        return campos;
    }

    @Override
    public void setCampos(Set<Campo> campos) {
        this.campos = campos;
    }

    @OneToMany(mappedBy = "almacenamiento", targetEntity = ValorSemilla.class)
    @Override
    public List<ValorSemilla> getValoresSemilla() {

        if (valoresSemilla == null) {
            valoresSemilla = new ArrayList<ValorSemilla>();
        }

        return valoresSemilla;
    }

    @Override
    public void setValoresSemilla(List<ValorSemilla> valoresSemilla) {
        this.valoresSemilla = valoresSemilla;
    }

    @Override
    public boolean addCampo(Campo campo) {
        return getCampos().add(campo);
    }

    @Override
    public boolean removeCampo(Campo campo) {
        return getCampos().remove(campo);
    }

    @Override
    public void removeAllCampos() {
        getCampos().clear();
    }

    @Override
    @Transient
    public abstract String getTipoAlmacenamiento();

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = new ArrayList<Object>();
        r.add(getNombre());
        r.add(entityName());
        r.add(getDescripcion());
        return r;
    }

    @Override
    public String entityName() {
        return ENTITY_NAME;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", super.toString(), entityName());
    }
}
