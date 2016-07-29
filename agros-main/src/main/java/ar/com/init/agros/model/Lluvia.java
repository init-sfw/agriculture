package ar.com.init.agros.model;

import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.model.terreno.Campo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import ar.com.init.agros.model.terreno.Lote;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.Length;

/**
 * Clase Lluvia
 *
 *
 * @author fbobbio
 * @version 17-jul-2009 
 */
@Entity
public class Lluvia extends TablizableEntity implements Comparable<Lluvia> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS = {"Fecha", "Cantidad (mm)", "Establecimiento", "Observaciones"};
    private Date fecha;
    private ValorUnidad cantidad;
    private List<Lote> lotes;
    private Campo campo;
    private String observaciones;

    /** Constructor por defecto de Lluvia */
    public Lluvia() {
        super();
    }

    @NotNull
    @ManyToOne
    public Campo getCampo() {
        return campo;
    }

    public void setCampo(Campo campo) {
        this.campo = campo;
    }

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column =
        @Column(name = "valorCant"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns =
        @JoinColumn(name = "unidadCant_id"))
    })
    public ValorUnidad getCantidad() {
        return cantidad;
    }

    public void setCantidad(ValorUnidad cantidad) {
        this.cantidad = cantidad;
    }

    @NotNull
    @Temporal(TemporalType.DATE)
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @NotEmpty
    @ManyToMany(fetch = FetchType.LAZY)
    public List<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(List<Lote> lotes) {
        this.lotes = lotes;
    }

    @Length(min = 0, max = 1024)
    @Column(length = 1024)
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> aux = new ArrayList<Object>();
        aux.add(fecha);
        aux.add(cantidad.getValor());
        aux.add(getCampo());
        aux.add(observaciones);
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Override
    public int compareTo(Lluvia o) {
        return this.fecha.compareTo(o.getFecha());
    }

    @Override
    public String entityName() {
        return "Lluvia";
    }
}
