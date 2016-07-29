package ar.com.init.agros.view.ingresos;

import ar.com.init.agros.model.ingreso.TipoIngreso;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase TipoIngresoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 04-dic-2009 
 */
public class TipoIngresoFrameListEntity extends FrameListEntity<TipoIngreso>
{
    /** Constructor por defecto de TipoIngresoFrameListEntity */
    public TipoIngresoFrameListEntity(ResourceMap map)
    {
        super(TipoIngreso.class, TipoIngreso.TABLE_HEADERS_TIPO_INGRESO);
        resourceMap = map;
    }

    @Override
    public void list(TipoIngreso entity)
    {
        DialogTipoIngreso dialog;
        TipoIngreso c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogTipoIngreso(getCurrentFrame(),false,c);
            dialog.setTitle(getResourceMap().getString("tipoingreso.consulta.title"));
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
        DialogTipoIngreso dialog = new DialogTipoIngreso(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("tipoingreso.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(TipoIngreso entity)
    {
        DialogTipoIngreso dialog;
        TipoIngreso c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogTipoIngreso(getCurrentFrame(), true,c);
            dialog.setTitle(getResourceMap().getString("tipoingreso.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un tipo de ingreso para modificar");
        }
    }
}
