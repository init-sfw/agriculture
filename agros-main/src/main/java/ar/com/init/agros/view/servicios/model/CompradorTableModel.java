package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import java.util.List;

/**
 * Clase CompradorTableModel
 *
 *
 * @author fbobbio
 * @version 21-dic-2010 
 */
public class CompradorTableModel extends ServicioTableModel<DetalleEgresoGrano>
{
    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,ValorUnidad.class,ValorMoneda.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        false,false,false,false,false,false,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Actividad","Almacenamiento","Establecimiento", "Comprador", "Semilla/Cereal", "Cantidad [Kg]", "Importe[U$S]"
    };

    public static final int ACTIVIDAD_COLUMN_IDX = 0;
    public static final int ALMACENAMIENTO_COLUMN_IDX = 1;
    public static final int ESTABLECIMIENTO_COLUMN_IDX = 2;
    public static final int GRANO_COLUMN_IDX = 4;
    public static final int CANTIDAD_COLUMN_IDX = 5;


    public CompradorTableModel()
    {
        this(TABLE_HEADERS);
    }

    public CompradorTableModel(String[] headers)
    {
        super(headers);
        SERVICIO_COLUMN_IDX = 3;
        IMPORTE_COLUMN_IDX = 6;
    }

    @Override
    public void setData(List<DetalleEgresoGrano> data)
    {
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (int i = 0; i < this.data.size(); i++)
        {
            DetalleEgresoGrano e = this.data.get(i);
            this.setValueAt("Detalle de Egreso de Semilla/Cereal", i, ACTIVIDAD_COLUMN_IDX);
            this.setValueAt(e.getValor().getAlmacenamiento(), i, ALMACENAMIENTO_COLUMN_IDX);
            this.setValueAt(e.getEstablecimiento(),i, ESTABLECIMIENTO_COLUMN_IDX);
            this.setValueAt(e.getServicio(), i, SERVICIO_COLUMN_IDX);
            this.setValueAt(e.getDetalle(), i, GRANO_COLUMN_IDX);
            this.setValueAt(e.getCantidad(), i, CANTIDAD_COLUMN_IDX);
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
