package ar.com.init.agros.model.terreno;

import ar.com.init.agros.model.*;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * Clase Lote
 *
 *
 * @author fbobbio
 * @version 31-may-2009 
 */
@Entity
public class Lote extends DivisionCampo
{

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS_LOTE =
    {
        "Nombre", "Superficie (ha)", "Cant. de Sublotes", "Lluvias asociadas"
    };
    private List<SubLote> subLotes;
    private Campo campo;
    private List<Lluvia> lluvias;

    /** Constructor por defecto de Lote */
    public Lote()
    {
        super();
    }

    /** Constructor por defecto de Lote */
    public Lote(Campo c)
    {
        super();
        this.campo = c;
    }

    public Lote(UUID uuid)
    {
        super(uuid);
    }

    public Lote(Campo c,Lote lote)
    {
        super(lote);
        this.campo = c;
        this.subLotes = new ArrayList<SubLote>(lote.subLotes.size());
        for (SubLote s : lote.subLotes)
        {
            this.subLotes.add(new SubLote(this,s));
        }
    }

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<SubLote> getSubLotes()
    {
        if (subLotes == null)
        {
            subLotes = new ArrayList<SubLote>();
        }
        return subLotes;
    }

    public void setSubLotes(List<SubLote> subLotes)
    {
        this.subLotes = subLotes;
    }

    public void addSubLote(SubLote sub)
    {
        sub.setPadre(this);
        getSubLotes().add(sub);
    }

    public void removeSubLote(SubLote sub)
    {
        getSubLotes().remove(sub);
    }   

    public int contarSubLotes()
    {
        return getSubLotes().size();
    }


    @ManyToMany(mappedBy = "lotes", fetch = FetchType.LAZY)
    public List<Lluvia> getLluvias()
    {
        if (lluvias == null)
        {
            lluvias = new ArrayList<Lluvia>();
        }
        return lluvias;
    }

    public void setLluvias(List<Lluvia> lluvias)
    {
        this.lluvias = lluvias;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return "Lote " + getNombre();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Campo getCampo()
    {
        return campo;
    }

    public void setCampo(Campo campo)
    {
        this.campo = campo;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>();
        aux.add(getNombre());
        aux.add(getSuperficie().getFormattedValue());
        aux.add(getSubLotes().size());
        aux.add(getLluvias().size());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS_LOTE;
    }

    @Override
    public boolean tieneSuperficiesRepetidas(Superficie entity)
    {
        if (entity instanceof Campo)
        {
            Campo c = (Campo) entity;
            return c.getLotes().contains(this);
        }
        if (entity instanceof Lote)
        {
            return this.equals(entity);
        }
        if (entity instanceof SubLote)
        {
            return this.getSubLotes().contains((SubLote) entity);
        }
        return false;
    }

    @Override
    public ValorUnidad calcularSuperficie()
    {
        if (hasChildren())
        {
            double val = 0;
            for (SubLote s : subLotes)
            {
                val += s.getSuperficie().getValor().doubleValue();
            }
            val = GUIUtility.redondearDecimales(val);
            return new ValorUnidad(val, MagnitudEnum.SUPERFICIE.patron());
        }
        else
        {
            return getSuperficie();
        }
    }

    @Override
    public boolean hasChildren()
    {
        return getSubLotes().size() > 0;
    }

    @Override
    public boolean contiene(Superficie sup)
    {
        if (this.equals(sup))
        {
            return true;
        }
        for (SubLote s : getSubLotes())
        {
            if (s.contiene(sup))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String entityName()
    {
        return "Lote";
    }
}
