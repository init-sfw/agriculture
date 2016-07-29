package ar.com.init.agros.view.components.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AbstractTableModel
 *
 *
 * @author fbobbio
 * @version 20-dic-2010 
 */
public abstract class AbstractTableModel<T extends Object>  extends DefaultTableModel
{
    private static final long serialVersionUID = -1L;
    protected List<T> data;

    /** Constructor por defecto de AbstractSeleccionTableModel */
    public AbstractTableModel(String[] headers)
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

    public abstract void setData(List<T> data);

    @Override
    public abstract Class getColumnClass(int columnIndex);

    @Override
    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    @Override
    public abstract int getColumnCount();

    @Override
    public abstract String getColumnName(int column);
}
