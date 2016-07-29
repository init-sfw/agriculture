package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase MovimientoStockTableModel
 *
 *
 * @author gmatheu
 * @version 15/06/2009 
 */
public class MovimientoStockTableModel extends TablizableEntityDataModel<DetalleMovimientoStockAlmacenamiento>
{

    private static final long serialVersionUID = -1L;
    private List<DetalleMovimientoStockAlmacenamiento> noPersistidos;

    /** Constructor por defecto de MovimientoStockTableModel */
    public MovimientoStockTableModel(List<DetalleMovimientoStockAlmacenamiento> data)
    {
        super(data, DetalleMovimientoStockAlmacenamiento.TABLE_HEADERS);
        noPersistidos = new ArrayList<DetalleMovimientoStockAlmacenamiento>();
    }

    public MovimientoStockTableModel()
    {
        this(new ArrayList<DetalleMovimientoStockAlmacenamiento>());
    }

    public void addNoPersistido(DetalleMovimientoStockAlmacenamiento m)
    {
        noPersistidos.add(m);
        add(m);
    }

    public boolean removeNoPersistido(DetalleMovimientoStockAlmacenamiento m)
    {
        if (noPersistidos.contains(m)) {
            noPersistidos.remove(m);
            remove(m);
            return true;
        }
        return false;
    }

    public boolean removeNoPersistido(int idx)
    {
        DetalleMovimientoStockAlmacenamiento m = data.get(idx);
        if (noPersistidos.contains(m)) {
            noPersistidos.remove(m);
            remove(m);
            return true;
        }
        return false;
    }

    public void clearNoPersistidos()
    {
        data.removeAll(noPersistidos);
        noPersistidos.clear();
        fireTableDataChanged();
    }

    public List<DetalleMovimientoStockAlmacenamiento> getNoPersistidos()
    {
        return noPersistidos;
    }

    @Override
    public void setData(List<DetalleMovimientoStockAlmacenamiento> persistidos)
    {
        persistidos.addAll(this.noPersistidos);
        super.setData(persistidos);
    }
}
