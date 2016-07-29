package ar.com.init.agros.model;

import ar.com.init.agros.model.base.Formattable;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Listable;
import java.io.Serializable;
import java.text.ParseException;
import java.util.UUID;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Clase ValorUnidad
 *
 *
 * @author gmatheu
 * @version 02/06/2009 
 */
@Embeddable
public class ValorUnidad implements Serializable, Formattable, Comparable<ValorUnidad>, Listable
{

    private static final long serialVersionUID = -1L;
    private Double valor;
    private UnidadMedida unidad;

    /** Constructor por defecto de ValorUnidad */
    public ValorUnidad()
    {
        super();
    }

    public ValorUnidad(ValorUnidad valorUnidad)
    {
        this(valorUnidad.getValor(), valorUnidad.getUnidad());
    }

    public ValorUnidad(Double valor, UnidadMedida unidad)
    {
        this();
        this.valor = valor;
        this.unidad = unidad;
    }

    public ValorUnidad(String valor, UnidadMedida unidad) throws ParseException
    {
        this();
        setValorString(valor);
        this.unidad = unidad;
    }

    public ValorUnidad(UUID id, Double valor, UnidadMedida unidad)
    {
        this();
        this.valor = valor;
        this.unidad = unidad;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public UnidadMedida getUnidad()
    {
        return unidad;
    }

    @Transient
    public boolean isValid()
    {
        return valor != null && unidad != null;
    }

    public void setUnidad(UnidadMedida unidad)
    {
        this.unidad = unidad;
    }

    public Double getValor()
    {
        return valor;
    }

    public void setValor(Double valor)
    {
        this.valor = valor;
    }

    public void setValorString(String valor) throws ParseException
    {
        this.valor = GUIUtility.NUMBER_FORMAT.parse(valor).doubleValue();
    }

    public void sumar(ValorUnidad v)
    {
        valor += v.getValor(this.unidad);
    }

    public void sumar(Double v)
    {
        valor += v;
    }

    @Override
    @Transient
    public String getFormattedValue()
    {
        String ret = GUIUtility.NUMBER_FORMAT.format(valor);
        return ret;
    }

    @Override
    public void setFormattedValue(String text)
    {
        try
        {
            setValor(GUIUtility.NUMBER_FORMAT.parse(text).doubleValue());
        }
        catch (ParseException ex)
        {
            setValor(null);
        }
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        if (valor != null && unidad != null)
        {
            return GUIUtility.NUMBER_FORMAT.format(valor) + " " + unidad.getAbreviatura();
        }
        else
        {
            return "";
        }
    }

    public Double getValor(UnidadMedida unidad)
    {
        return valor;
    }

    @Override
    public int compareTo(ValorUnidad o)
    {
        if (o == null)
        {
            return Integer.MAX_VALUE;
        }

        return this.valor.compareTo(o.valor);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ValorUnidad)
        {
            ValorUnidad val = (ValorUnidad) obj;
            return this.getValor().equals(val.getValor()) && this.getUnidad().equals(val.getUnidad());
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + (this.valor != null ? this.valor.hashCode() : 0);
        return hash;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return valor.doubleValue() + unidad.getAbreviatura();
    }
}
