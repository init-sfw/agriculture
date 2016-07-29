package ar.com.init.agros.view.campo;

import ar.com.init.agros.controller.BaseCampaniaTransactionJpaController;
import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.model.BaseCampaniaTransaction;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase CampoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 10-jul-2009 
 */
public class CampoFrameListEntity extends FrameListEntity<Campo>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CampaniaFrameListEntity */
    public CampoFrameListEntity(ResourceMap map)
    {
        super(Campo.class, new CampoJpaController(), Campo.TABLE_HEADERS_CAMPO);
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
    public void list(Campo entity)
    {
        DialogCampo dialog;
        if (entity != null) {
            dialog = new DialogCampo(getCurrentFrame(), entity, false, resourceMap);
            dialog.setTitle(getResourceMap().getString("campo.consulta.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un establecimiento para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogCampo dialog = new DialogCampo(getCurrentFrame(), resourceMap);
        dialog.setTitle(getResourceMap().getString("campo.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Campo entity)
    {
        DialogCampo dialog;
        BaseCampaniaTransactionJpaController<BaseCampaniaTransaction> transactionController =
                new BaseCampaniaTransactionJpaController<BaseCampaniaTransaction>(
                BaseCampaniaTransaction.class);
        Campo aux = transactionController.checkTransactionOpenedForCampo(entity);
        if (aux == null) {
            showErrorMessage(
                    "Imposible modificar establecimiento " + entity.toString() + ", ya que está asociado a una campaña abierta");
            return;
        }
        if (aux != null) {
            dialog = new DialogCampo(getCurrentFrame(), aux, true, resourceMap);
            if (!aux.equals(entity)) // CASO EN EL QUE TENDRÉ QUE MARCAR COMO INACTIVO EL CAMPO MODIFICADO
            {
                dialog.setOldCampo(entity);
            }
            dialog.setController((CampoJpaController) jpaController);
            dialog.setTitle(getResourceMap().getString("campo.modificacion.title"));
            dialog.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un establecimiento para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
