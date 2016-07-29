package ar.com.init.agros.model.terreno;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.util.ComparableEntity;
import ar.com.init.agros.util.gui.Listable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Clase Superficie
 *
 *
 * @author fbobbio
 * @version 29-jul-2009 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Superficie extends TablizableEntity implements Listable, ComparableEntity<Superficie>,Comparable<Superficie>
{
    private static final long serialVersionUID = -2L;
    public static final String[] TABLE_HEADERS_SUPERFICIE =
    {
        "Nombre", "Superficie (ha)"
    };
    private String nombre;
    private ValorUnidad superficie;
    
    /** Constructor por defecto de Superficie */
    public Superficie()
    {
        super();
    }

    public Superficie(UUID uuid)
    {
        super(uuid);
    }

    public Superficie(UUID uuid,String nombre)
    {
        super();
        this.nombre = nombre;
    }

    public Superficie(UUID uuid, String nombre, ValorUnidad superficie)
    {
        super(uuid);
        this.nombre = nombre;
        this.superficie = superficie;
    }

    public Superficie(Superficie sup)
    {
        super();
        this.nombre = sup.nombre;
        this.superficie = new ValorUnidad(sup.superficie.getValor().doubleValue(),sup.superficie.getUnidad());
    }

    public abstract boolean contiene(Superficie sup);

    @NotNull
    @Length(min = 3, max = 150)
    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    @NotNull
    @Embedded
    @AttributeOverrides(
    {
        @AttributeOverride(name = "valor", column = @Column(name = "valorSup"))
    })
    @AssociationOverrides(
    {
        @AssociationOverride(name = "unidad", joinColumns = @JoinColumn(name = "unidadSup_id"))
    })
    public ValorUnidad getSuperficie()
    {
        return superficie;
    }

    public void setSuperficie(ValorUnidad superficie)
    {
        this.superficie = superficie;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return nombre;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>();
        aux.add(this.getNombre());
        aux.add(this.getSuperficie().getFormattedValue());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS_SUPERFICIE;
    }

    @Override
    public boolean equalToEntity(Superficie entity)
    {
        return this.nombre.equalsIgnoreCase(entity.nombre);
    }

    @Override
    public int compareTo(Superficie o) {
        return nombre.compareTo(o.nombre);
    }



    @Transient
    /** Método que compara la superficie ingresada por el usuario con la sumatoria de superficies que posea la misma
     *
     * @return true si la superficie ingresada coincide con la calculada, false de otro modo
     */
    public boolean isSuperficieConsistente()
    {
        if (hasChildren())
        {
            return superficie.equals(calcularSuperficie());
        }
        else
        {
            return true;
        }
    }

    /** Metodo que nos indica si una superficie tiene sub-superficies asociadas
     * 
     * @return
     */
    public abstract boolean hasChildren();

    /**
     * Método que compara una superficie con otra para checkear si poseen datos repetidos
     * @param entity
     * @return
     */
    public abstract boolean tieneSuperficiesRepetidas(Superficie entity);

    /** Método que calcula el valor de superficie de esta superficie a través de las sub-superficies que tenga asociadas
     * 
     * @return
     */
    public abstract ValorUnidad calcularSuperficie();
}
