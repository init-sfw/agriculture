package ar.com.init.agros.view.almacenamientos;

import ar.com.init.agros.controller.almacenamiento.AlmacenamientoJpaController;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase AlmacenamientoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 05-dic-2010 
 */
public class AlmacenamientoFrameListEntity extends FrameListEntity<Almacenamiento>
{
    /** Constructor por defecto de tipocostoFrameListEntity */
    public AlmacenamientoFrameListEntity(ResourceMap map)
    {
        super(Almacenamiento.class, new AlmacenamientoJpaController<Almacenamiento>() ,Almacenamiento.TABLE_HEADERS);
        resourceMap = map;
    }

    @Override
    public void list(Almacenamiento entity)
    {
        DialogAlmacenamiento dialog;
        Almacenamiento a = getSelectedEntities().get(0);
        if (a != null)
        {
            dialog = new DialogAlmacenamiento(getCurrentFrame(),a,false);
            dialog.setTitle(getResourceMap().getString("deposito.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un almacenamiento para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogAlmacenamiento dialog = new DialogAlmacenamiento(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("deposito.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Almacenamiento entity)
    {
        DialogAlmacenamiento dialog;
        Almacenamiento a = getSelectedEntities().get(0);
        if (a != null)
        {
            dialog = new DialogAlmacenamiento(getCurrentFrame(),a,true);
            dialog.setTitle(getResourceMap().getString("deposito.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un almacenamiento para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
