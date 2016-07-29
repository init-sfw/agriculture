package ar.com.init.agros.util.gui.styles;

import java.util.Arrays;
import java.util.List;

/**
 * Clase ConditionalStyle
 *
 *
 * @author gmatheu
 * @version 23/06/2009 
 */
public class ConditionalStyle<T>
{

    private T value;
    private List<StyleCondition> conditions;

    /** Constructor por defecto de ConditionalStyle */
    public ConditionalStyle(T value, StyleCondition... conditions)
    {
        this.value = value;
        this.conditions = Arrays.asList(conditions);
    }

    public List<StyleCondition> getConditions()
    {
        return conditions;
    }

    public T getValue()
    {
        return value;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return (value != null ? value.toString() : "");
    }
}
