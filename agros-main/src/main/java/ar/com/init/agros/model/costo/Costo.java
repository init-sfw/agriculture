package ar.com.init.agros.model.costo;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.model.servicio.Servicio;
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase Costo
 *
 *
 * @author fbobbio
 * @version 22-jul-2009 
 */
@Entity
public class Costo extends TablizableEntity implements ComparableEntity<Costo> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS = {"Tipo de costo", "Importe", "Unidad Medida", "Servicio"};
    private TipoCosto tipoCosto;
    private ValorMoneda importe;
    private Servicio servicio;

    /** Constructor por defecto de Costo */
    public Costo() {
        super();
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "importe", column =
        @Column(name = "importe"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "divisa", joinColumns =
        @JoinColumn(name = "divisaImporte_id"))
    })
    public ValorMoneda getImporte() {
        return importe;
    }

    public void setImporte(ValorMoneda importe) {
        this.importe = importe;
    }

    @NotNull
    @ManyToOne
    public TipoCosto getTipoCosto() {
        return tipoCosto;
    }

    public void setTipoCosto(TipoCosto tipoCosto) {
        this.tipoCosto = tipoCosto;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> aux = new ArrayList<Object>();
        aux.add(tipoCosto);
        aux.add(importe.getMonto());
        aux.add(tipoCosto.getUnidadMedida().getAbreviatura());
        aux.add((servicio != null ? servicio : ""));
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Override
    public boolean equalToEntity(Costo entity) {
        return this.importe.getMonto().equals(entity.importe.getMonto()) && this.tipoCosto.equalToEntity(
                entity.tipoCosto);
    }

    @Override
    public String entityName() {
        return "Costo";
    }
}
