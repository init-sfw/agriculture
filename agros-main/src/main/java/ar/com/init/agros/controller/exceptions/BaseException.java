package ar.com.init.agros.controller.exceptions;

/**
 * Clase BaseException para diferenciar a las excepciones de java
 * de las definidas para el negocio
 *
 *
 * @author fbobbio
 * @version 22-sep-2009 
 */
public class BaseException extends Exception
{
    /** Constructor por defecto de BaseException */
    public BaseException()
    {
    }

    public BaseException(String message)
    {
        super(message);
    }
}
