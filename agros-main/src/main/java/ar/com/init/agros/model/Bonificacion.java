package ar.com.init.agros.model;

import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.terreno.Campo;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * Clase Bonificacion
 *
 *
 * @author fbobbio
 * @version 28-nov-2009 
 */
@Entity
public class Bonificacion extends BaseEntity
{

    private ValorMoneda importe;
    /** Listado de siembras que deben ser del mismo establecimiento-cultivo que se encontraron en la campaña a la que pertenece la bonificación */
    private Set<Siembra> agrupacionSiembras;

    /** Constructor por defecto de Bonificacion */
    public Bonificacion()
    {
    }

    public Bonificacion(Siembra s)
    {
        importe = new ValorMoneda(0.0);
        addSiembra(s);
    }

    @Transient
    public Campo getEstablecimiento()
    {
        for (Siembra s : getAgrupacionSiembras())
        {
            return s.getCampo();
        }
        return null;
    }

    @Transient
    public Cultivo getCultivo()
    {
        for (Siembra s : getAgrupacionSiembras())
        {
            return s.getCultivo();
        }
        return null;
    }

    public boolean addSiembra(Siembra s)
    {
        return getAgrupacionSiembras().add(s);
    }

    @OneToMany
    public Set<Siembra> getAgrupacionSiembras()
    {
        if (agrupacionSiembras == null) {
            agrupacionSiembras = new HashSet<Siembra>();
        }
        return agrupacionSiembras;
    }

    public void setAgrupacionSiembras(Set<Siembra> agrupacionSiembras)
    {
        this.agrupacionSiembras = agrupacionSiembras;
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "importe", column = @Column(name = "importe"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "divisa", joinColumns = @JoinColumn(name = "divisaImporte_id"))
    })
    public ValorMoneda getImporte()
    {
        return importe;
    }

    public void setImporte(ValorMoneda importe)
    {
        this.importe = importe;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public String entityName()
    {
        return "Bonificación";
    }
}
