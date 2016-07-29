package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.inventario.agroquimicos.IngresoStockJpaController;
import ar.com.init.agros.model.inventario.agroquimicos.IngresoStock;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase IngresoStockFrameListEntity
 *
 *
 * @author fbobbio
 * @version 22-nov-2009 
 */
public class IngresoStockFrameListEntity extends FrameListEntity<IngresoStock>
{

    /** Constructor por defecto de IngresoStockFrameListEntity */
    public IngresoStockFrameListEntity(ResourceMap map)
    {
        super(IngresoStock.class, new IngresoStockJpaController(), IngresoStock.TABLE_HEADERS);
        setResourceMap(map);
        setVisibleButtons(true, false, false, true, false);
    }

    @Override
    public void list(IngresoStock entity)
    {
        FrameIngresoStock frame;
        IngresoStock p = getSelectedEntities().get(0);
        if (p != null) {
            frame = new FrameIngresoStock(p);
            frame.setTitle(getResourceMap().getString("ingreso.consulta.title"));
            frame.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un ingreso de stock para consultar");
        }
    }
    private FrameIngresoStock dialog;

    @Override
    public void newEntity()
    {
        if (dialog == null) {
            dialog = new FrameIngresoStock();
            dialog.setTitle(getResourceMap().getString("ingreso.alta.title"));
        }
        dialog.setVisible(true);
    }

    @Override
    public void update(IngresoStock entity)
    {
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        refreshTable();
    }
}
