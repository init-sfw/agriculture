package ar.com.init.agros.view.granos.egreso.model;

import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase EgresoGranoTableModel
 *
 *
 * @author fbobbio
 * @version 14-dic-2010 
 */
public class EgresoGranoTableModel extends TablizableEntityDataModel<DetalleEgresoGrano>
{

    private static final long serialVersionUID = -1L;
    private List<DetalleEgresoGrano> noPersistidos;

    /** Constructor por defecto de IngresoStockTableModel */
    public EgresoGranoTableModel(List<DetalleEgresoGrano> data)
    {
        super(data, DetalleEgresoGrano.TABLE_HEADERS);
        noPersistidos = new ArrayList<DetalleEgresoGrano>();
    }

    public EgresoGranoTableModel()
    {
        this(new ArrayList<DetalleEgresoGrano>());
    }

    public void addNoPersistido(DetalleEgresoGrano m)
    {
        noPersistidos.add(m);
        add(m);
    }

    public boolean removeNoPersistido(DetalleEgresoGrano m)
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
        DetalleEgresoGrano m = data.get(idx);
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

    public List<DetalleEgresoGrano> getNoPersistidos()
    {
        return noPersistidos;
    }

    @Override
    public void setData(List<DetalleEgresoGrano> persistidos)
    {
        Iterator<DetalleEgresoGrano> iter = persistidos.iterator();
        while (iter.hasNext()) {
            DetalleEgresoGrano dis = iter.next();
            if(dis.isCancelado() != null && dis.isCancelado())
            {
                iter.remove();
            }
        }

        persistidos.addAll(this.noPersistidos);
        super.setData(persistidos);
    }
}
