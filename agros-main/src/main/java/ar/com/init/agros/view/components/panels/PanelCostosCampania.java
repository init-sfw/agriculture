package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.util.gui.GUIUtility;
import javax.persistence.PersistenceException;

/**
 * Clase PanelCostosCampania
 *
 *
 * @author fbobbio
 * @version 09-dic-2009 
 */
public class PanelCostosCampania extends PanelCostos
{
    /** Constructor por defecto de PanelCostosCampania */
    public PanelCostosCampania()
    {
        super();
    }

    @Override
    public void refreshUI()
    {
        try
        {
            GUIUtility.refreshComboBox(tipoCostoController.findCostosCampania(), jComboBoxTipoCosto);
        }
        catch (PersistenceException e)
        {
            if (frameNotifier != null)
            {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelAgroquimicos.class, e);
        }
    }
}
