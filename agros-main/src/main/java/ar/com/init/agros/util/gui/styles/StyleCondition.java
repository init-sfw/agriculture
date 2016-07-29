package ar.com.init.agros.util.gui.styles;

import java.util.Arrays;
import java.util.List;

/**
 * Clase StyleCondition
 *
 *
 * @author gmatheu
 * @version 23/06/2009 
 */
public abstract class StyleCondition<T>
{

    private Style[] styles;

    /** Constructor por defecto de StyleCondition */
    public StyleCondition(Style... styles)
    {
        this.styles = styles;
    }


    public List<Style> getStyles()
    {
        return Arrays.asList(styles);
    }

    public abstract boolean isTrue(T value);
}
