package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase IngresoStockTableModel
 *
 *
 * @author gmatheu
 * @version 15/06/2009 
 */
public class IngresoStockTableModel extends TablizableEntityDataModel<DetalleIngresoStock>
{

    private static final long serialVersionUID = -1L;
    private List<DetalleIngresoStock> noPersistidos;

    /** Constructor por defecto de IngresoStockTableModel */
    public IngresoStockTableModel(List<DetalleIngresoStock> data)
    {
        super(data, DetalleIngresoStock.TABLE_HEADERS);
        noPersistidos = new ArrayList<DetalleIngresoStock>();
    }

    public IngresoStockTableModel()
    {
        this(new ArrayList<DetalleIngresoStock>());
    }

    public void addNoPersistido(DetalleIngresoStock m)
    {
        noPersistidos.add(m);
        add(m);
    }

    public boolean removeNoPersistido(DetalleIngresoStock m)
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
        DetalleIngresoStock m = data.get(idx);
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

    public List<DetalleIngresoStock> getNoPersistidos()
    {
        return noPersistidos;
    }

    @Override
    public void setData(List<DetalleIngresoStock> persistidos)
    {
        Iterator<DetalleIngresoStock> iter = persistidos.iterator();
        while (iter.hasNext()) {
            DetalleIngresoStock dis = iter.next();
            if(dis.isCancelado() != null && dis.isCancelado())
            {
                iter.remove();
            }
        }

        persistidos.addAll(this.noPersistidos);
        super.setData(persistidos);
    }
}
