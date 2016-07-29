package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.view.components.model.AbstractSeleccionTableModel;
import java.util.List;

/**
 * Clase FiltroServiciosTableModel
 *
 *
 * @author fbobbio
 * @version 18-dic-2010 
 */
public class FiltroServiciosTableModel extends AbstractSeleccionTableModel<Servicio>
{
    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Boolean.class,java.lang.Object.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        true,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Selección", "Empresa de Servicio"
    };
    public static final int SERVICIO_COLUMN_IDX = 1;

    public FiltroServiciosTableModel()
    {
        this(TABLE_HEADERS);
    }

    public FiltroServiciosTableModel(String[] headers)
    {
        super(headers);
    }

    @Override
    public void setData(List<Servicio> data, List<Servicio> seleccionadas)
    {
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (Servicio servicio : this.data)
        {
            this.setValueAt(servicio, this.data.indexOf(servicio), SERVICIO_COLUMN_IDX);
            if (seleccionadas != null && seleccionadas.contains(servicio))
            {
                this.setValueAt(true, this.data.indexOf(servicio), SELECCION_COLUMN_IDX);
            }
            else
            {
                this.setValueAt(false, this.data.indexOf(servicio), SELECCION_COLUMN_IDX);
            }
        }
    }

    @Override
    public Class getColumnClass(int columnIndex)
    {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return canEdit[columnIndex];
    }

    @Override
    public int getColumnCount()
    {
        return TABLE_HEADERS.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return TABLE_HEADERS[column];
    }
}
