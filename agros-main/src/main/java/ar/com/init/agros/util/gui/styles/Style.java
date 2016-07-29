package ar.com.init.agros.util.gui.styles;

/**
 * Clase Style
 *
 *
 * @author gmatheu
 * @version 23/06/2009 
 */
public class Style
{

    public enum StyleParam
    {
        TEXT_COLOR,
        BACKGROUND_COLOR,
        TEXT_SIZE,
        PREFIX,
        SUFFIX,
        HORIZONTAL_ALIGNMENT
    }
    private StyleParam styleParam;
    private Object value;

    /** Constructor por defecto de Style */
    public Style()
    {
    }

    public Style(StyleParam styleParam, Object value)
    {
        this.styleParam = styleParam;
        this.value = value;
    }

    public StyleParam getStyleParam()
    {
        return styleParam;
    }

    public void setStyleParam(StyleParam styleParam)
    {
        this.styleParam = styleParam;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
