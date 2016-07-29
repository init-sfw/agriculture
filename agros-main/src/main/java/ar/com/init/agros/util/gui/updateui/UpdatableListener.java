package ar.com.init.agros.util.gui.updateui;

/**
 * Interfaz UpdatableListener que deber�n implementar las clases que deseen hacer refresh
 * cuando se produzca alguna inserci�n, borrado o modificaci�n de alg�n dato de la base de datos.
 *
 *
 * @author fbobbio
 * @version 12-jul-2009 
 */

public interface UpdatableListener
{
    /** M�todo a redefinir que se llamar� cuando se realice alguna operaci�n de base de datos
     *  dentro del cual se deber� realizar el refresh de las ventanas que se encuentren abiertas.
     */
    void refreshUI();

    boolean isNowVisible();
}
