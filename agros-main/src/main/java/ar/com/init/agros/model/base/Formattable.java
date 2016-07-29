package ar.com.init.agros.model.base;

/**
 * Interfaz usada para aquellas entidades que tienen un valor que necesita ser convertido a cadena
 * y viceversa con un formato particular
 *
 *
 * @author gmatheu
 * @version 12/06/2009 
 */
public interface Formattable
{

    /**
     * Convierte el valor a cadena de caracteres para ser mostrada
     *
     * @return el valor como cadena
     */
    public String getFormattedValue();

    /**
     * Convierte el parametro en el valor necesario, con el formato determinado.
     *
     * @param text a convertir en el valor de la instancia
     */
    public void setFormattedValue(String text);
}
