package ar.com.init.agros.view.components;

import ar.com.init.agros.model.terreno.Campo;

/**
 * Clase CampoSelectionChangeListener
 *
 *
 * @author fbobbio
 * @version 04-ago-2009 
 */
public interface CampoSelectionChangeListener
{
    /** M�todo que se dispara cuando se cambia la selecci�n de un campo en el PanelCampaniaCampoCultivoVariedad
     * y nos devuelve true si se acepta el cambio de selecci�n o false en caso contrario
     * 
     * @param selected el nuevo campo seleccionado
     * @return true si el usuario acepta el cambio o este es posible, false si el usuario lo rechaza
     */
    boolean campoSelectionChanged(Campo selected);
}
