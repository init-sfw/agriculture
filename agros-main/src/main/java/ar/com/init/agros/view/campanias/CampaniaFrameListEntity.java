package ar.com.init.agros.view.campanias;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase CampaniaFrameListEntity
 *
 *
 * @author fbobbio
 * @version 14-jun-2009 
 */
public class CampaniaFrameListEntity extends FrameListEntity<Campania>
{
    private static final long serialVersionUID = -1L;
    private ResourceMap resourceMap;

    /** Constructor por defecto de CampaniaFrameListEntity */
    public CampaniaFrameListEntity(ResourceMap map)
    {
        super(Campania.class, Campania.TABLE_HEADERS);
        resourceMap = map;
    }

    public ResourceMap getResourceMap()
    {
        return resourceMap;
    }

    public void setResourceMap(ResourceMap resourceMap)
    {
        this.resourceMap = resourceMap;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public void list(Campania entity)
    {
        DialogCampania dialog;
        Campania c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogCampania(getCurrentFrame(), c, false);
            dialog.setTitle(getResourceMap().getString("campania.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una campaña para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogCampania dialog = new DialogCampania(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("campania.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Campania entity)
    {
        DialogCampania dialog;
        Campania c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogCampania(getCurrentFrame(), c, true);
            dialog.setTitle(getResourceMap().getString("campania.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una campaña para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }


}
