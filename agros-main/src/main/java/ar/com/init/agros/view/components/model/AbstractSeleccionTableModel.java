package ar.com.init.agros.view.components.model;

import ar.com.init.agros.model.base.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AbstractSeleccionTableModel
 *
 *
 * @author fbobbio
 * @version 11-ago-2009 
 */
public abstract class AbstractSeleccionTableModel<T extends BaseEntity>  extends DefaultTableModel
{
    private static final long serialVersionUID = -1L;
    protected List<T> data;
    public static final int SELECCION_COLUMN_IDX = 0;

    /** Constructor por defecto de AbstractSeleccionTableModel */
    public AbstractSeleccionTableModel(String[] headers)
    {
        super(new Object[][]
                {
                }, headers);
        data = new ArrayList<T>();
    }

    public void clear()
    {
        data = new ArrayList<T>();
        setData(data);
    }

    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        setData(data, null);
    }

    public abstract void setData(List<T> data, List<T> seleccionadas);

    public void checkData(List<T> seleccionadas)
    {
        setData(data, seleccionadas);
    }

    public List<T> getCheckedData()
    {
        ArrayList<T> r = new ArrayList<T>();

        for (int i = 0; i < data.size(); i++)
        {
            T a = data.get(i);

            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX))
            {
                r.add(a);
            }
        }

        return r;
    }

    public void changeAll(boolean checked)
    {
        for (int i = 0; i < getRowCount(); i++)
        {
            setValueAt(checked, i, SELECCION_COLUMN_IDX);
        }
    }

    /** Checkea si están seleccionadas todas las filas
     *
     * @return true si todas las filas están seleccionadas - false si no
     */
    public boolean isAllSelected()
    {
        if (data == null || data.size() == 0)
        {
            return false;
        }
        return (getCheckedData().size() == data.size());
    }

    @Override
    public abstract Class getColumnClass(int columnIndex);

    @Override
    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    @Override
    public abstract int getColumnCount();

    @Override
    public abstract String getColumnName(int column);
}
