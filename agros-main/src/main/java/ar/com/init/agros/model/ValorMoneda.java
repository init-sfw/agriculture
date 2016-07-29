package ar.com.init.agros.model;

import ar.com.init.agros.model.base.Formattable;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Listable;
import java.io.Serializable;
import java.text.ParseException;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceException;
import javax.persistence.Transient;

/**
 * Clase ValorMoneda
 * 
 *
 * @author gmatheu
 * @version 04/06/2009 
 */
@Embeddable
public class ValorMoneda implements Serializable, Formattable, Listable
{

    private static final long serialVersionUID = -1L;
    private Double monto;
    private Divisa divisa;

    /** Constructor por defecto de ValorMoneda */
    public ValorMoneda()
    {
        super();
        try
        {
            this.divisa = Divisa.getPatron();
        }
        catch (PersistenceException e)
        {
            GUIUtility.logPersistenceError(ValorMoneda.class, e);
        }
        this.monto = 0.0;
    }

    public ValorMoneda(Double monto)
    {
        this();
        this.monto = monto;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Divisa getDivisa()
    {
        return divisa;
    }

    @Transient
    public boolean isValid()
    {
        return monto != null && divisa != null;
    }

    public void setDivisa(Divisa divisa)
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

    public void sumar(ValorMoneda val)
    {
        this.monto = this.monto + val.monto;
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
        try
        {
            setMonto(GUIUtility.NUMBER_FORMAT.parse(text).doubleValue());
        }
        catch (ParseException ex)
        {
            setMonto(null);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ValorMoneda)
        {
            ValorMoneda val = (ValorMoneda) obj;
            return this.getMonto().equals(val.getMonto()) && this.getDivisa().equals(val.getDivisa());
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 59 * hash + (this.monto != null ? this.monto.hashCode() : 0);
        hash = 59 * hash + (this.divisa != null ? this.divisa.hashCode() : 0);
        return hash;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return divisa.getAbreviatura() + " " + GUIUtility.NUMBER_FORMAT.format(monto.doubleValue());
    }

    @Override
    @Transient
    public String getListLine()
    {
        return divisa.getAbreviatura() + " " + GUIUtility.NUMBER_FORMAT.format(monto.doubleValue());
    }
}
