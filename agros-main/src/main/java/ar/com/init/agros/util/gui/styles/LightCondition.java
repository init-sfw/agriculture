package ar.com.init.agros.util.gui.styles;

/**
 * Clase LightCondition
 *
 *
 * @author gmatheu
 * @version 23/06/2009 
 */
public class LightCondition<T extends Comparable<T>> extends StyleCondition<T>
{
    private T minValue;
    private T maxValue;
    
    public LightCondition(T minValue, T maxValue, Style... styles)
    {
        super(styles);
        this.minValue = minValue;
        this.maxValue = maxValue;       
    }

    public LightCondition(T maxValue, Style... styles)
    {
        this(null, maxValue, styles);
    }
   
    @Override
    public boolean isTrue(T value)
    {
        return (minValue == null && value.compareTo(maxValue) < 0 || value.compareTo(minValue) > 0 && maxValue == null || value.compareTo(minValue) > 0 && value.compareTo(maxValue) < 0);
    }
}
