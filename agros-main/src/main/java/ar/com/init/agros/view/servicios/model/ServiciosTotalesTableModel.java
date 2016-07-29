package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import java.util.List;

/**
 * Clase ServiciosTotalesTableModel
 *
 *
 * @author fbobbio
 * @version 22-dic-2010 
 */
public class ServiciosTotalesTableModel extends ServicioTableModel<ServicioImporteLinea>
{
    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Object.class,ValorMoneda.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        false,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Contratista", "Importe[U$S]"
    };

    public ServiciosTotalesTableModel()
    {
        this(TABLE_HEADERS);
    }

    public ServiciosTotalesTableModel(String[] headers)
    {
        super(headers);
    }

    @Override
    public void setData(List<ServicioImporteLinea> data)
    {
        SERVICIO_COLUMN_IDX = 0;
        IMPORTE_COLUMN_IDX = 1;
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (int i = 0; i < this.data.size(); i++)
        {
            ServicioImporteLinea e = this.data.get(i);
            this.setValueAt(e.getServicio(), i, SERVICIO_COLUMN_IDX);
            this.setValueAt(e.getImporte(), i, IMPORTE_COLUMN_IDX);
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
