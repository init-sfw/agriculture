package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 * Clase PanelCostosPulverizacion
 *
 *
 * @author fbobbio
 * @version 09-dic-2009 
 */
public class PanelCostosPulverizacion extends PanelCostos
{
    /** Constructor por defecto de PanelCostosPulverizacion */
    public PanelCostosPulverizacion()
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
            GUIUtility.refreshComboBox(tipoCostoController.findCostosPulverizacion(), jComboBoxTipoCosto);
            /** Agregamos a los servicios Contratistas y Otros */
            List<Servicio> servicios = new ArrayList<Servicio>();
            servicios.addAll(servicioJpaController.findByTipo(TipoServicio.CONTRATISTA));
            servicios.addAll(servicioJpaController.findByTipo(TipoServicio.OTROS));
            GUIUtility.refreshComboBox(servicios, jComboBoxServicio);
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
