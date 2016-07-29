package ar.com.init.agros.view.costos;

import ar.com.init.agros.controller.TipoCostoController;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase TipoCostoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 07-dic-2009 
 */
public class TipoCostoFrameListEntity extends FrameListEntity<TipoCosto>
{
    /** Constructor por defecto de tipocostoFrameListEntity */
    public TipoCostoFrameListEntity(ResourceMap map)
    {
        super(TipoCosto.class, new TipoCostoController() ,TipoCosto.TABLE_HEADERS_TIPO_COSTO);
        resourceMap = map;
    }

    @Override
    public void list(TipoCosto entity)
    {
        DialogTipoCosto dialog;
        TipoCosto c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogTipoCosto(getCurrentFrame(),false,c);
            dialog.setTitle(getResourceMap().getString("tipocosto.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un tipo de ingreso para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogTipoCosto dialog = new DialogTipoCosto(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("tipocosto.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(TipoCosto entity)
    {
        DialogTipoCosto dialog;
        TipoCosto c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogTipoCosto(getCurrentFrame(), true,c);
            dialog.setTitle(getResourceMap().getString("tipocosto.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un tipo de ingreso para modificar");
        }
    }
}
