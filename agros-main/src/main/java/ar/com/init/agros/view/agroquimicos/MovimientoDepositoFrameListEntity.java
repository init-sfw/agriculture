package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.model.inventario.agroquimicos.MovimientoDeposito;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase MovimientoDepositoFrameListEntity
 *
 *
 * @author fbobbio
 * @version 17-dic-2009 
 */
public class MovimientoDepositoFrameListEntity extends FrameListEntity<MovimientoDeposito>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de ProveedorFrameListEntity */
    public MovimientoDepositoFrameListEntity(ResourceMap map)
    {
        super(MovimientoDeposito.class, MovimientoDeposito.TABLE_HEADERS_MOV_DEPO);
        setResourceMap(map);
        setVisibleButtons(true, false, false, true, false);
    }

    @Override
    public void list(MovimientoDeposito entity)
    {
        DialogMovimientoDeposito dialog;
        MovimientoDeposito c = getSelectedEntities().get(0);
        if (c != null)
        {
            dialog = new DialogMovimientoDeposito(getCurrentFrame(), c);
            dialog.setTitle(getResourceMap().getString("mov_deposito.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un movimiento para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogMovimientoDeposito dialog = new DialogMovimientoDeposito(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("mov_deposito.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(MovimientoDeposito entity)
    {
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
