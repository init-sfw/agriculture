package ar.com.init.agros.util.gui.styles;

/**
 * Clase BooleanCondition
 *
 *
 * @author gmatheu
 * @version 03/07/2009 
 */
public class BooleanCondition extends StyleCondition<Boolean>
{
    /** Constructor por defecto de BooleanCondition */
    public BooleanCondition( Style... styles)
    {
        super(styles);
    }
  
    @Override
    public boolean isTrue(Boolean value)
    {
        return value;
    }
}
