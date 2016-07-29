package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 * Clase PanelCostosPostCosecha
 *
 *
 * @author fbobbio
 * @version 07-feb-2010 
 */
public class PanelCostosPostCosecha extends PanelCostos
{
    /** Constructor por defecto de PanelCostosCampania */
    public PanelCostosPostCosecha()
    {
        super();
        jComboBoxServicio.setVisible(true);
        jLabelServicio.setVisible(true);
    }

    @Override
    public void refreshUI()
    {
        try
        {
            GUIUtility.refreshComboBox(tipoCostoController.findCostosPostCosecha(), jComboBoxTipoCosto);
            /** Agregamos a los servicios Contratistas y Otros */
            List<Servicio> servicios = new ArrayList<Servicio>();
            servicios.addAll(servicioJpaController.findByTipo(TipoServicio.PROVEEDOR_POST_COSECHA));
            GUIUtility.refreshComboBox(servicios, jComboBoxServicio);
        }
        catch (PersistenceException e)
        {
            if (frameNotifier != null)
            {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelCostosPostCosecha.class, e);
        }
    }
}
