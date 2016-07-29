package ar.com.init.agros.view.campo.exceptions;

/**
 * Clase de Excepción LotesAsociadosALluviasException
 *
 *
 * @author fbobbio
 * @version 22-abr-2011 
 */

public class LotesAsociadosALluviasException extends Exception 
{
    /**
     * Constructor por defecto de LotesAsociadosALluviasException
     */
    public LotesAsociadosALluviasException() 
    {
        super("Existen lotes asociados a lluvias");
    }

    /**
     * Constructor que crea una instancia de LotesAsociadosALluviasException con el mensaje parametrizado
     * @param msg la descripción de la excepción.
     */
    public LotesAsociadosALluviasException(String msg) 
    {
        super(msg);
    }
}
