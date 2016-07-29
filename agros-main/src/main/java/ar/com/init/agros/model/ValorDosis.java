package ar.com.init.agros.model;

import ar.com.init.agros.model.base.Formattable;
import ar.com.init.agros.util.gui.GUIUtility;
import java.io.Serializable;
import java.text.ParseException;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Clase ValorDosis
 *
 *
 * @author gmatheu
 * @version 04/06/2009 
 */
@Embeddable
public class ValorDosis implements Serializable, Formattable
{

    private static final long serialVersionUID = -1L;
    private Double dosis;

    /** Constructor por defecto de ValorDosis */
    public ValorDosis()
    {
        super();
    }

    public ValorDosis(Double dosis)
    {
        this();
        this.dosis = dosis;
    }

    public Double getDosis()
    {
        return dosis;
    }

    @Transient
    public boolean isValid()
    {
        return dosis != null;
    }

    public void setDosis(Double dosis)
    {
        this.dosis = dosis;
    }

    @Override
    @Transient
    public String getFormattedValue()
    {  
        return GUIUtility.NUMBER_FORMAT.format(dosis);
    }

    @Override
    public void setFormattedValue(String text)
    {
        try {
            setDosis(GUIUtility.NUMBER_FORMAT.parse(text).doubleValue());
        }
        catch (ParseException ex) {
            setDosis(null);
        }
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return dosis + "";
    }
}
