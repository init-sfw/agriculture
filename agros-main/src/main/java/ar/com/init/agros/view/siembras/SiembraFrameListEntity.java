package ar.com.init.agros.view.siembras;

import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase SiembraFrameListEntity
 *
 *
 * @author fbobbio
 * @version 28-jun-2009 
 */
public class SiembraFrameListEntity extends FrameListEntity<Siembra>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de SiembraFrameListEntity */
    public SiembraFrameListEntity(ResourceMap map)
    {
        this(map, null);
    }

    /** Constructor por defecto de SiembraFrameListEntity */
    public SiembraFrameListEntity(ResourceMap map, SiembraJpaController controller)
    {
        super(Siembra.class, controller, Siembra.TABLE_HEADERS);
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
    public void list(Siembra entity)
    {
        DialogSiembra dialog;
        Siembra p = getSelectedEntities().get(0);
        if (p != null) {
            dialog = new DialogSiembra(getCurrentFrame(), p, false);
            dialog.setTitle(getResourceMap().getString("siembra.consulta.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado una siembra para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogSiembra dialog = new DialogSiembra(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("siembra.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Siembra entity)
    {
        DialogSiembra dialog;
        Siembra p = getSelectedEntities().get(0);
        if (p != null) {
            if (p.getCampania().isCerrada()) //si la campaña está cerrada no dejamos modificar
            {
                showErrorMessage(
                        "Imposible modificar la siembra debido a que la campaña " + p.getCampania().getNombre() + " ha sido cerrada");
                return;
            }
            dialog = new DialogSiembra(getCurrentFrame(), p, true);
            dialog.setTitle(getResourceMap().getString("siembra.modificacion.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado una siembra para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
