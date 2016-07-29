package ar.com.init.agros.util.gui.table;

import ar.com.init.agros.model.base.BaseEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Clase BaseEntityTableModel
 * Sirve como base para los TableModel de las BaseEntity.
 * Por defecto todas las celdas son de solo lectura.
 * Necesita ser redefinida para ser usada indicando el valor de cada fila en el metodo toTableLine.
 * @author gmatheu
 * @version 25/06/2009 
 */
public abstract class BaseEntityTableModel<T extends BaseEntity> extends AbstractTableModel
{

    private static final long serialVersionUID = -1L;
    protected List<T> data;
    protected List<String> headers;

    public BaseEntityTableModel(List<T> data, String[] headers)
    {
        super();
        this.data = data;
        this.headers = Arrays.asList(headers);
    }

    public BaseEntityTableModel(String[] headers)
    {
        this(new ArrayList<T>(), headers);
    }

    /**
     * Devuelve un vector con los valores que seran mostrados en una fila de la tabla.
     * Debe ser redefinido por cada tabla.
     *
     * @param entity cuyos valores van a ser mostrados en la fila.
     * @return una lista con los valores que se mostraran en la tabla.
     */
    protected abstract List<Object> toTableLine(T entity);

    public void add(T entity)
    {
        data.add(entity);
        fireTableDataChanged();
    }

    public void add(int idx, T entity)
    {
        data.add(idx, entity);
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        try
        {
            return (data != null && data.size() > 0 ? toTableLine(data.get(0)).get(columnIndex).getClass() : Object.class);
        }
        catch (NullPointerException ex)
        {
            return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return toTableLine(data.get(rowIndex)).get(columnIndex);
    }

    @Override
    public int getColumnCount()
    {
        return this.headers.size();
    }

    @Override
    public String getColumnName(int column)
    {
        return headers.get(column);
    }

    public List<T> getData()
    {
        return data;
    }

    public List<String> getHeaders()
    {
        return headers;
    }

    @Override
    public int getRowCount()
    {
        return (this.data != null ? this.data.size() : 0);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public void remove(T entity)
    {
        remove(entity, true);
    }

    public void removeRow(int row)
    {
        remove(data.get(row), true);
    }

    /**
     *
     * @param row
     * @param updateTable determina si la tabla se actualiza despues de remover este valor.
     */
    public void removeRow(int row, boolean updateTable)
    {
        remove(data.get(row), updateTable);
    }

    public void removeRows(int[] selectedRows)
    {
        for (int i = 0; i < selectedRows.length; i++)
        {
            removeRow(selectedRows[i], true);
        }
    }

    /**
     * Método que elimina todas las entidades del tablemodel
     */
    public void removeAllEntities()
    {
        data.removeAll(data);
        fireTableDataChanged();
    }

    /**
     * Método que elimina las entidades pasadas por parámetro del tablemodel
     */
    public void removeEntities(List<T> entities)
    {
        data.removeAll(entities);
        fireTableDataChanged();
    }

    private void remove(T entity, boolean updateTable)
    {
        data.remove(entity);
        if (updateTable)
        {
            fireTableDataChanged();
        }
    }

    public void setData(List<T> data)
    {
        if (data == null)
        {
            data = new ArrayList<T>();
        }
        this.data = data;
        fireTableDataChanged();
    }

    /**
     * Actualiza los valores de la entidad pasada por parametro, si es que esta esta previamente en la tabla.
     * Si esta no existe en la tabla, no hace nada
     *
     * @param entity para actualizar valores
     * @return Si la entidad fue actualizada o no.
     */
    public boolean update(T entity)
    {
        int idx = data.indexOf(entity);
        if (idx >= 0)
        {
            data.remove(idx);
            data.add(idx, entity);
            fireTableDataChanged();
            return true;
        }
        return false;
    }

    /**
     * Actualiza los valores de la entidad si esta existe en la tabla, si no existe la inserta al final.
     *
     * @param entity
     */
    public void updateOrAdd(T entity)
    {
        if (!update(entity))
        {
            add(entity);
        }
    }
}
