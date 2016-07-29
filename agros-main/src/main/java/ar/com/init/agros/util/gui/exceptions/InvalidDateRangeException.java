package ar.com.init.agros.util.gui.exceptions;

/**
 * Clase InvalidDateRangeException
 *
 *
 * @author fbobbio
 * @version 26-ago-2009 
 */
public class InvalidDateRangeException extends Exception
{
    /** Constructor por defecto de InvalidDateRangeException */
    public InvalidDateRangeException()
    {
        super("El rango de fechas ingresado es incorrecto");
    }
}
