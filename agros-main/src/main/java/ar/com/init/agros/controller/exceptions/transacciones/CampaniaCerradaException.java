package ar.com.init.agros.controller.exceptions.transacciones;

import ar.com.init.agros.controller.exceptions.BaseException;

/**
 * Clase CampaniaCerradaException
 *
 *
 * @author fbobbio
 * @version 22-sep-2009 
 */
public class CampaniaCerradaException extends BaseException
{
    /** Constructor por defecto de CampaniaCerradaException */
    public CampaniaCerradaException()
    {
        super("La campaña asociada a la transacción ha sido cerrada");
    }

    /** Constructor por defecto de CampaniaCerradaException */
    public CampaniaCerradaException(String msg)
    {
        super(msg);
    }
}
