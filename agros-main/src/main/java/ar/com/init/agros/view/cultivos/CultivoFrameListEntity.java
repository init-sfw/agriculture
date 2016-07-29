package ar.com.init.agros.view.cultivos;

import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase CultivoFrameListEntity
 *
 *
 * @author gmatheu
 * @version 30/06/2009 
 */
public class CultivoFrameListEntity extends FrameListEntity<Cultivo>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CultivoFrameListEntity */
    public CultivoFrameListEntity(ResourceMap map)
    {
        super(Cultivo.class, new CultivoJpaController(), Cultivo.TABLE_HEADERS);
        setResourceMap(map);
    }

    @Override
    public void list(Cultivo entity)
    {
        DialogCultivo dialog;
        Cultivo c = getSelectedEntities().get(0);
        if (c != null) {
            dialog = new DialogCultivo(getCurrentFrame(), c, false);
            dialog.setTitle(getResourceMap().getString("cultivo.consulta.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un cultivo para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogCultivo f = new DialogCultivo(getCurrentFrame());
        f.setTitle(getResourceMap().getString("cultivo.alta.title"));
        f.setVisible(true);
    }

    @Override
    public void update(Cultivo entity)
    {
        //TODO: cambiar el mensaje de éxito de la modificación en todos los frameListEntity
        DialogCultivo dialog;
        Cultivo c = getSelectedEntities().get(0);
        if (c != null) {
            dialog = new DialogCultivo(getCurrentFrame(), c, true);
            dialog.setTitle(getResourceMap().getString("cultivo.modificacion.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un cultivo para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
