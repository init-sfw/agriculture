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
    /** Método que se dispara cada vez que se cambia la selección de la campaña en el PanelCampaniaCampoCultivoVariedad
     * 
     * @param campania
     */
    void campaniaSelectionChanged(Campania campania);
}
