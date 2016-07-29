package ar.com.init.agros.model.ingreso;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.util.ComparableEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase Ingreso
 *
 *
 * @author fbobbio
 * @version 22-nov-2009 
 */
@Entity
public class Ingreso extends TablizableEntity implements ComparableEntity<Ingreso>
{
    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS = {"Tipo de ingreso", "Importe", "Unidad Medida"};
    private TipoIngreso tipoIngreso;
    private ValorMoneda importe;

    /** Constructor por defecto de Ingreso */
    public Ingreso()
    {
        super();
    }

    @NotNull
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

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>();
        aux.add(tipoIngreso);
        aux.add(importe);
        aux.add(tipoIngreso.unidadMedida());
        return aux;
    }

    @NotNull
    @ManyToOne
    public TipoIngreso getTipoIngreso()
    {
        return tipoIngreso;
    }

    public void setTipoIngreso(TipoIngreso tipoIngreso)
    {
        this.tipoIngreso = tipoIngreso;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS;
    }

    @Override
    public boolean equalToEntity(Ingreso entity)
    {
        return this.importe.getMonto().equals(entity.importe.getMonto()) && this.tipoIngreso.equalToEntity(
                entity.tipoIngreso);
    }

    @Override
    public String entityName()
    {
        return "Ingreso";
    }
}
