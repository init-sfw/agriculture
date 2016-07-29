package ar.com.init.agros.model;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OptimisticLockException;
import javax.persistence.Transient;

/**
 * Clase MonedaMedida
 *
 *
 * @author fbobbio
 * @version 09-dic-2009 
 */
@Embeddable
public class MonedaMedida implements Serializable, Cloneable
{

    private static final long serialVersionUID = -1L;
    private UnidadMedida unidad;
    private Divisa divisa;

    /** Constructor por defecto de MonedaMedida */
    public MonedaMedida()
    {
    }

    public MonedaMedida(UnidadMedida unidad)
    {
        this.unidad = unidad;
    }

    public MonedaMedida(Divisa divisa)
    {
        this.divisa = divisa;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Divisa getDivisa()
    {
        if (divisa != null) {
            Divisa oldValue = divisa;
            try {
                divisa = EntityManagerUtil.createEntityManager().merge(divisa);
            }
            catch (OptimisticLockException ex) {
                Logger.getLogger(MonedaMedida.class.getName()).info(
                        "OptimisticLockException merging divisa");
            }
            catch (Exception exc) {
                Logger.getLogger(MonedaMedida.class.getName()).info(
                        "Exception merging divisa");

                divisa = oldValue;
            }
        }
        return divisa;
    }

    public void setDivisa(Divisa divisa)
    {
        this.divisa = divisa;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public UnidadMedida getUnidad()
    {
        return unidad;
    }

    public void setUnidad(UnidadMedida unidad)
    {
        this.unidad = unidad;
    }
    
    @Transient
    public String getAbreviatura()
    {
        if (divisa != null) {
            return "[" + getDivisa().getAbreviatura() + "]";
        }
        if (unidad != null) {
            return "[" + getUnidad().getAbreviatura() + "]";
        }
        return "";
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        if (divisa != null) {
            return getDivisa().toString();
        }
        if (unidad != null) {
            return getUnidad().toString();
        }
        return "";
    }
}
