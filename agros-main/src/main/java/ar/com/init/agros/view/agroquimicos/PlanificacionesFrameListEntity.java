package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.PlanificacionAgroquimicoJpaController;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase PlanificacionesFrameListEntity
 *
 *
 * @author fbobbio
 * @version 03-sep-2009 
 */
public class PlanificacionesFrameListEntity extends FrameListEntity<PlanificacionAgroquimico>
{
    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CampaniaFrameListEntity */
    public PlanificacionesFrameListEntity(ResourceMap map)
    {
        this(map,null);
    }

    /** Constructor por defecto de CampaniaFrameListEntity */
    public PlanificacionesFrameListEntity(ResourceMap map,PlanificacionAgroquimicoJpaController controller)
    {
        super(PlanificacionAgroquimico.class,controller, PlanificacionAgroquimico.TABLE_HEADERS);
        resourceMap = map;
        setVisibleButtons(true,true,true,true,false);
    }

    /** M�todo que retorna una descripci�n de la clase
     *  @return descripci�n de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public void list(PlanificacionAgroquimico entity)
    {
        FrameRegistrarPlanificacionAgroquimico dialog;
        if (entity != null)
        {
            dialog = new FrameRegistrarPlanificacionAgroquimico(entity, false);
            dialog.setTitle(getResourceMap().getString("planificacion.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una planificaci�n para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        FrameRegistrarPlanificacionAgroquimico dialog = new FrameRegistrarPlanificacionAgroquimico();
        dialog.setTitle(getResourceMap().getString("planificacion.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(PlanificacionAgroquimico entity)
    {
        FrameRegistrarPlanificacionAgroquimico dialog;
        if (entity != null)
        {
            if (entity.getCampania().isCerrada()) //si la campa�a est� cerrada no dejamos modificar
            {
                showErrorMessage("Imposible modificar la planificaci�n debido a que la campa�a " + entity.getCampania().getNombre() + " ha sido cerrada");
                return;
            }
            dialog = new FrameRegistrarPlanificacionAgroquimico(entity, true);
            dialog.setTitle(getResourceMap().getString("planificacion.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado una planificaci�n para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }    

}