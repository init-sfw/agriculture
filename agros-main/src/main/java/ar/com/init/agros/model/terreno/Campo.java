package ar.com.init.agros.model.terreno;

import ar.com.init.agros.model.*;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Listable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.NotEmpty;

/**
 * Clase Campo
 *
 *
 * @author gmatheu
 * @version 31/05/2009 
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "nombre"
    })
})
public class Campo extends Superficie implements Listable {

    private static final long serialVersionUID = -2L;
    //"Campañas Asociadas",
    public static final String[] TABLE_HEADERS_CAMPO = {
        "Nombre", "Campañas Asociadas", "Superficie (ha)", "Cant. de Lotes"
    };
    private int cantidadLote;
    private List<Lote> lotes;
    private Set<Almacenamiento> almacenamientos;
    /** Fecha en la cual se registró la modificación del campo luego de haber sido trabajado y modificado */
    private Date fechaModificacion;
    private String info = "";

    /** Constructor por defecto de Campo */
    public Campo() {
        super();
    }

    public Campo(UUID id, String nombre) {
        super(id, nombre);
    }

    public Campo(UUID id, String nombre, ValorUnidad superficie) {
        super(id, nombre, superficie);
    }

    @Transient
    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    /** Constructor copia */
    public Campo(Campo campo) {
        super(campo);
        this.cantidadLote = campo.cantidadLote;
        for (Lote l : campo.getLotes()) {
            this.getLotes().add(new Lote(this, l));
        }
        for (Almacenamiento a : campo.getAlmacenamientos()) {
            this.addAlmacenamiento(a);
        }
    }

    public int contarLotes() {
        return getLotes().size();
    }

    public int contarSubLotes() {
        int acum = 0;
        for (Lote l : getLotes()) {
            acum += l.contarSubLotes();
        }
        return acum;
    }

    public int getCantidadLote() {
        if (this.lotes != null) {
            cantidadLote = this.lotes.size();
        } else {
            cantidadLote = 0;
        }

        return cantidadLote;
    }

    public void setCantidadLote(int cantidadLote) {
        this.cantidadLote = cantidadLote;
    }

    public void addLote(Lote l) {
        if (getLotes().add(l)) {
            l.setCampo(this);
        }
    }

    public void removeLote(Lote l) {
        getLotes().remove(l);
    }

    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campo", fetch = FetchType.LAZY, orphanRemoval = true)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<Lote> getLotes() {
        if (lotes == null) {
            lotes = new ArrayList<Lote>();
        }
        return lotes;
    }

    public void setLotes(List<Lote> lotes) {
        this.lotes = lotes;
    }

    @Transient
    public List<SubLote> getSubLotes() {
        List<SubLote> sublotes = new ArrayList<SubLote>();
        for (Lote l : getLotes()) {
            sublotes.addAll(l.getSubLotes());
        }
        return sublotes;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @NotEmpty
    public Set<Almacenamiento> getAlmacenamientos() {
        if (almacenamientos == null) {
            almacenamientos = new HashSet<Almacenamiento>();
        }
        return almacenamientos;
    }

    @Transient
    public List<Almacenamiento> getAlmacenamientosAsList()
    {
        return new ArrayList<Almacenamiento>(getAlmacenamientos());
    }

    public void setAlmacenamientos(Set<Almacenamiento> almacenamientos) {
        this.almacenamientos = almacenamientos;
    }

    public final boolean addAlmacenamiento(Almacenamiento a) {
        a.addCampo(this);
        return getAlmacenamientos().add(a);
    }

    public boolean removeAlmacenamiento(Almacenamiento a) {
        a.removeCampo(this);
        return getAlmacenamientos().remove(a);
    }

    private <T extends Almacenamiento> List<T> getAlmacenamientos(Class<T> clazz)
    {
        List<T> r = new ArrayList<T>();
        for (Almacenamiento a : getAlmacenamientos())
        {
            if (a.getClass().equals(clazz))
            {
                r.add((T) a);
            }
        }
        return r;
}
    @Transient
    public List<Deposito> getDepositos()
    {
        return getAlmacenamientos(Deposito.class);
    }

    @Transient
    public List<Silo> getSilos()
    {
        return getAlmacenamientos(Silo.class);
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return getNombreExtendido();
    }

    @Override
    @Transient
    public String getListLine() {
        return super.getListLine();
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> aux = new ArrayList<Object>();
        aux.add(this.getNombre());
        aux.add(this.getInfo());
        aux.add(this.getSuperficie().getFormattedValue());
        aux.add(getLotes().size());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS_CAMPO;
    }

    @Override
    public boolean tieneSuperficiesRepetidas(Superficie entity) {
        if (entity instanceof Campo) {
            return this.equals(entity);
        }
        if (entity instanceof Lote) {
            return this.getLotes().contains((Lote) entity);
        }
        if (entity instanceof SubLote) {
            return this.getSubLotes().contains((SubLote) entity);
        }
        return false;
    }

    @Override
    public ValorUnidad calcularSuperficie() {
        if (hasChildren()) {
            double val = 0;
            for (Lote l : lotes) {
                val += l.calcularSuperficie().getValor().doubleValue();
            }
            val = GUIUtility.redondearDecimales(val);
            return new ValorUnidad(val, MagnitudEnum.SUPERFICIE.patron());
        } else {
            return getSuperficie();
        }
    }

    @Override
    public boolean hasChildren() {
        return getLotes().size() > 0;
    }

    @Override
    public boolean contiene(Superficie sup) {
        if (this.equals(sup)) {
            return true;
        }
        for (Lote l : getLotes()) {
            if (l.contiene(sup)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String entityName() {
        return "Establecimiento";
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getFechaModificacion()
    {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion)
    {
        this.fechaModificacion = fechaModificacion;
    }

    @Transient
    public String getNombreExtendido()
    {
        return getNombre() + " (" +  info + ")";
    }
}
