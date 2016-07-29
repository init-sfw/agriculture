package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.DetallePlanificacion;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DetallePlanificacionTableModel
 *
 *
 * @author fbobbio
 * @version 27-jun-2009 
 */
public class DetallePlanificacionTableModel extends TablizableEntityDataModel<DetallePlanificacion>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de MovimientoStockTableModel */
    public DetallePlanificacionTableModel(List<DetallePlanificacion> data)
    {
        super(data, DetallePlanificacion.TABLE_HEADERS);
    }

    public DetallePlanificacionTableModel()
    {
        this(new ArrayList<DetallePlanificacion>());
    }
}
