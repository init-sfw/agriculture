package ar.com.init.agros.view.components;

import ar.com.init.agros.model.Campania;

/**
 * Clase CampaniaSelectionChangeListener
 *
 *
 * @author fbobbio
 * @version 06-ago-2009 
 */
public interface CampaniaSelectionChangeListener
{
    /** M�todo que se dispara cada vez que se cambia la selecci�n de la campa�a en el PanelCampaniaCampoCultivoVariedad
     * 
     * @param campania
     */
    void campaniaSelectionChanged(Campania campania);
}
