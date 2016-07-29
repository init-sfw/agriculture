package ar.com.init.agros.view.lluvias;

import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase LluviaFrameListEntity
 *
 *
 * @author fbobbio
 * @version 17-jul-2009 
 */
public class LluviaFrameListEntity extends FrameListEntity<Lluvia>
{

    private static final long serialVersionUID = -1L;


    /** Constructor por defecto de ProveedorFrameListEntity */
    public LluviaFrameListEntity(ResourceMap map)
    {
        super(Lluvia.class, Lluvia.TABLE_HEADERS);
        resourceMap = map;
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
    public void list(Lluvia entity)
    {
        DialogLluvia dialog;
        if (entity != null)
        {
            dialog = new DialogLluvia(getCurrentFrame(), entity, false);
            dialog.setTitle(getResourceMap().getString("lluvia.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una lluvia para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogLluvia dialog = new DialogLluvia(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("lluvia.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Lluvia entity)
    {
        DialogLluvia dialog;
        if (entity != null)
        {
            if (entity.getCampo().getStatus() == BaseEntityStateEnum.INACTIVE) //si el campo está como inactivo no permito modificar
            {
                showErrorMessage(
                        "Imposible modificar la lluvia debido a que el establecimiento " + entity.getCampo() + " ha sido modificado luego de cerrrar las campañas asociadas a este");
                return;
            }
            dialog = new DialogLluvia(getCurrentFrame(), entity, true);
            dialog.setTitle(getResourceMap().getString("lluvia.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una lluvia para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
