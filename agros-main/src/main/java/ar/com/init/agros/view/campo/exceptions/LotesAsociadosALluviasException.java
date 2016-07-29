package ar.com.init.agros.view.campo.exceptions;

/**
 * Clase de Excepci�n LotesAsociadosALluviasException
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
     * @param msg la descripci�n de la excepci�n.
     */
    public LotesAsociadosALluviasException(String msg) 
    {
        super(msg);
    }
}
