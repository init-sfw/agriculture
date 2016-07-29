package ar.com.init.agros.view.granos.ingreso.model;

import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
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
public class IngresoGranoTableModel extends TablizableEntityDataModel<DetalleIngresoGrano>
{

    private static final long serialVersionUID = -1L;
    private List<DetalleIngresoGrano> noPersistidos;

    /** Constructor por defecto de IngresoStockTableModel */
    public IngresoGranoTableModel(List<DetalleIngresoGrano> data)
    {
        super(data, DetalleIngresoGrano.TABLE_HEADERS);
        noPersistidos = new ArrayList<DetalleIngresoGrano>();
    }

    public IngresoGranoTableModel()
    {
        this(new ArrayList<DetalleIngresoGrano>());
    }

    public void addNoPersistido(DetalleIngresoGrano m)
    {
        noPersistidos.add(m);
        add(m);
    }

    public boolean removeNoPersistido(DetalleIngresoGrano m)
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
        DetalleIngresoGrano m = data.get(idx);
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

    public List<DetalleIngresoGrano> getNoPersistidos()
    {
        return noPersistidos;
    }

    @Override
    public void setData(List<DetalleIngresoGrano> persistidos)
    {
        Iterator<DetalleIngresoGrano> iter = persistidos.iterator();
        while (iter.hasNext()) {
            DetalleIngresoGrano dis = iter.next();
            if(dis.isCancelado() != null && dis.isCancelado())
            {
                iter.remove();
            }
        }

        persistidos.addAll(this.noPersistidos);
        super.setData(persistidos);
    }
}
