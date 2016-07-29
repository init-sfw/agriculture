package ar.com.init.agros.util.gui.updateui;

/**
 * Interfaz UpdatableListener que deberán implementar las clases que deseen hacer refresh
 * cuando se produzca alguna inserción, borrado o modificación de algún dato de la base de datos.
 *
 *
 * @author fbobbio
 * @version 12-jul-2009 
 */

public interface UpdatableListener
{
    /** Método a redefinir que se llamará cuando se realice alguna operación de base de datos
     *  dentro del cual se deberá realizar el refresh de las ventanas que se encuentren abiertas.
     */
    void refreshUI();

    boolean isNowVisible();
}
