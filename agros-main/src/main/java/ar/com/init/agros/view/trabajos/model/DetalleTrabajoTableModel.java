package ar.com.init.agros.view.trabajos.model;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase DetalleTrabajoTableModel
 *
 *
 * @author fbobbio
 * @version 02-ago-2009 
 */
public class DetalleTrabajoTableModel extends TablizableEntityDataModel<DetalleTrabajo>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de MovimientoStockTableModel */
    public DetalleTrabajoTableModel(List<DetalleTrabajo> data)
    {
        super(data, DetalleTrabajo.TABLE_HEADERS);
    }

    public DetalleTrabajoTableModel()
    {
        this(new ArrayList<DetalleTrabajo>());
    }

    public List<DetalleTrabajo> getDetallesSeleccionados(int[] selectedIndexes)
    {
        List<DetalleTrabajo> aux = new ArrayList<DetalleTrabajo>();
        for (int i = 0; i < selectedIndexes.length; i++)
        {
            aux.add(data.get(selectedIndexes[i]));
        }
        return aux;
    }

    /**
     * Método que devuelve un mapa con el agroquímico asociado a un valor de unidades totales sumadas en todos los detalles NO PERSISTIDOS
     * @return
     */
    public Map<Agroquimico,ValorUnidad> getCantidadesAgroquimicosAUsar()
    {
        List<DetalleTrabajo> detallesPersistidos = getDetallesPersistidos();
        Map<Agroquimico,ValorUnidad> ret = new HashMap<Agroquimico, ValorUnidad>();
        for (DetalleTrabajo d : getData())
        {
            if (!detallesPersistidos.contains(d)) // si no es un detalle persistido
            {
                Agroquimico key = d.getAgroquimico();
                if (ret.containsKey(key))
                {
                    ret.get(key).sumar(d.getCantidadUtilizada());
                }
                else
                {
                    ret.put(key, new ValorUnidad(d.getCantidadUtilizada()));
                }
            }
        }
        return ret;
    }

    private List<DetalleTrabajo> getDetallesPersistidos()
    {
        List<DetalleTrabajo> aux = new ArrayList<DetalleTrabajo>();
        for (DetalleTrabajo d : data)
        {
            if (d.isPersistido())
            {
                aux.add(d);
            }
        }
        return aux;
    }
}
