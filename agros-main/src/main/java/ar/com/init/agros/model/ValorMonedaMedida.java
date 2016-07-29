package ar.com.init.agros.model;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.base.Formattable;
import ar.com.init.agros.util.gui.GUIUtility;
import java.io.Serializable;
import java.text.ParseException;
import java.util.logging.Logger;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Transient;

/**
 * Clase ValorMonedaMedida
 *
 *
 * @author gmatheu
 * @version 12/06/2009 
 */
@Embeddable
public class ValorMonedaMedida implements Serializable, Formattable, Cloneable
{
    private static final long serialVersionUID = -1L;
    private UnidadMedida unidad;
    private Double monto;
    private Divisa divisa;

    /** Constructor por defecto de ValorMonedaMedida */
    public ValorMonedaMedida()
    {
        super();
        try {
            this.divisa = Divisa.getPatron();
        }
        catch (PersistenceException e) {
            GUIUtility.logPersistenceError(ValorMoneda.class, e);
        }
    }

    public ValorMonedaMedida(Double monto, UnidadMedida unidad)
    {
        this();
        this.monto = monto;
        this.unidad = unidad;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public UnidadMedida getUnidad()
    {
        if (unidad != null) {
            UnidadMedida oldValue = unidad;
            try {

                unidad = EntityManagerUtil.createEntityManager().merge(unidad);
            }
            catch (Exception exc) {
                Logger.getLogger(ValorMonedaMedida.class.getName()).fine(
                        "Exception merging unidad");
                
                unidad = oldValue;
            }
        }
        return unidad;
    }

    @Transient
    public boolean isValid()
    {
        return monto != null && unidad != null && divisa != null;
    }

    public void setUnidad(UnidadMedida unidad)
    {
        this.unidad = unidad;
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
                Logger.getLogger(ValorMonedaMedida.class.getName()).fine(
                        "OptimisticLockException merging divisa");
            }
            catch (Exception exc) {
                Logger.getLogger(ValorMonedaMedida.class.getName()).fine(
                        "Exception merging divisa");
                
                divisa = oldValue;
            }
        }
        return divisa;
    }

    public void setValorMoneda(ValorMoneda valorMoneda)
    {
        if (valorMoneda != null) {
            setDivisa(valorMoneda.getDivisa());
            setMonto(valorMoneda.getMonto());
        }
        else {
            setDivisa(null);
            setMonto(null);
        }
    }

    public ValorMoneda toValorMoneda()
    {
        return new ValorMoneda(monto);
    }

    protected void setDivisa(Divisa divisa)
    {
        this.divisa = divisa;
    }

    public Double getMonto()
    {
        return monto;
    }

    public void setMonto(Double monto)
    {
        this.monto = monto;
    }

    @Override
    @Transient
    public String getFormattedValue()
    {
        return GUIUtility.NUMBER_FORMAT.format(monto);
    }

    @Override
    public void setFormattedValue(String text)
    {
        try {
            setMonto(GUIUtility.NUMBER_FORMAT.parse(text).doubleValue());
        }
        catch (ParseException ex) {
            setMonto(null);
        }
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return divisa.getAbreviatura() + " " + getFormattedValue() + " x " + unidad.getAbreviatura();
    }

    @Override
    public Object clone()
    {
        ValorMonedaMedida r = new ValorMonedaMedida(monto, unidad);

        return r;
    }
}
