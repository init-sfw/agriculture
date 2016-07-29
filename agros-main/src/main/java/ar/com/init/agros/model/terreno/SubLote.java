package ar.com.init.agros.model.terreno;

import ar.com.init.agros.model.ValorUnidad;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Clase SubLote
 *
 *
 * @author fbobbio
 * @version 12-jul-2009 
 */
@Entity
public class SubLote extends DivisionCampo implements Cloneable
{

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS_SUB_LOTE = {
        "Nombre", "Superficie (ha)"
    };
    private Lote padre;

    /** Constructor por defecto de SubLote */
    public SubLote()
    {
        super();
    }

    /** Constructor por defecto de SubLote
     *
     * @param p el lote padre
     */
    public SubLote(Lote p)
    {
        super();
        this.padre = p;
    }

    public SubLote(UUID uuid)
    {
        super(uuid);
    }

    /** Constructor copia */
    public SubLote(Lote p, SubLote subLote)
    {
        super(subLote);
        this.padre = p;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Lote getPadre()
    {
        return padre;
    }

    public void setPadre(Lote padre)
    {
        this.padre = padre;
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
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>();
        aux.add(getNombre());
        aux.add(getSuperficie().getFormattedValue());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS_SUB_LOTE;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return "SubLote " + getNombre();
    }

    @Override
    public boolean tieneSuperficiesRepetidas(Superficie entity)
    {
        if (entity instanceof Campo) {
            Campo c = (Campo) entity;
            return c.getSubLotes().contains(this);
        }
        if (entity instanceof Lote) {
            Lote l = (Lote) entity;
            return l.getSubLotes().contains(this);
        }
        if (entity instanceof SubLote) {
            return this.equals(entity);
        }
        return false;
    }

    @Override
    public ValorUnidad calcularSuperficie()
    {
        return getSuperficie();
    }

    @Override
    public boolean hasChildren()
    {
        return false;
    }

    @Override
    public boolean contiene(Superficie sup)
    {
        return this.equals(sup);
    }

    @Override
    public String entityName()
    {
        return "Sublote";
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SubLote clone = new SubLote(padre, this);
        clone.id = this.id;        

        return super.clone();
    }
}
