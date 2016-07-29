package ar.com.init.agros.view.trabajos;

import ar.com.init.agros.controller.TrabajoLoteJpaController;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase TrabajoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 03-sep-2009 
 */
public class TrabajoFrameListEntity extends FrameListEntity<Trabajo>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CampaniaFrameListEntity */
    public TrabajoFrameListEntity(ResourceMap map)
    {
        super(Trabajo.class, Trabajo.TABLE_HEADERS);
        resourceMap = map;
        setVisibleButtons(true, true, true, true, false);
    }

    /** Constructor por defecto de CampaniaFrameListEntity */
    public TrabajoFrameListEntity(ResourceMap map, TrabajoLoteJpaController controller)
    {
        super(Trabajo.class, controller, Trabajo.TABLE_HEADERS);
        resourceMap = map;
        setVisibleButtons(true, true, true, true, false);
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
    public void list(Trabajo entity)
    {
        FrameRegistrarTrabajoDeLote dialog;
        if (entity != null) {
            setBusy(true);
            dialog = new FrameRegistrarTrabajoDeLote(entity, false);
            dialog.setTitle(getResourceMap().getString("trabajo.consulta.title"));
            setBusy(false);
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado una pulverización para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        FrameRegistrarTrabajoDeLote dialog = new FrameRegistrarTrabajoDeLote();
        dialog.setTitle(getResourceMap().getString("trabajo.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Trabajo entity)
    {
        FrameRegistrarTrabajoDeLote dialog;
        if (entity != null) {
            if (entity.getCampania().isCerrada()) //si la campaña está cerrada no dejamos modificar
            {
                showErrorMessage(
                        "Imposible modificar la pulverización debido a que la campaña " + entity.getCampania().getNombre() + " ha sido cerrada");
                return;
            }
            setBusy(true);
            dialog = new FrameRegistrarTrabajoDeLote(entity, true);
            dialog.setTitle(getResourceMap().getString("trabajo.modificacion.title"));
            setBusy(false);

            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado una pulverización para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }   

}
