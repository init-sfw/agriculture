package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
import java.util.List;

/**
 * Clase ProveedorInsumosTableModel
 *
 *
 * @author fbobbio
 * @version 21-dic-2010 
 */
public class ProveedorInsumosTableModel  extends ServicioTableModel<DetalleMovimientoStockAlmacenamiento>
{
    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,ValorMoneda.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        false,false,false,false,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Actividad", "Fecha","Almacenamiento","Detalle", "Proveedor de Insumos", "Importe[U$S]"
    };

    public static final int ACTIVIDAD_COLUMN_IDX = 0;
    public static final int FECHA_COLUMN_IDX = 1;
    public static final int ALMACENAMIENTO_COLUMN_IDX = 2;
    public static final int DETALLE_COLUMN_IDX = 3;


    public ProveedorInsumosTableModel()
    {
        this(TABLE_HEADERS);
    }

    public ProveedorInsumosTableModel(String[] headers)
    {
        super(headers);
        SERVICIO_COLUMN_IDX = 4;
        IMPORTE_COLUMN_IDX = 5;
    }

    @Override
    public void setData(List<DetalleMovimientoStockAlmacenamiento> data)
    {
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (int i = 0; i < this.data.size(); i++)
        {
            DetalleMovimientoStockAlmacenamiento e = this.data.get(i);
            this.setValueAt(e.entityName(), i, ACTIVIDAD_COLUMN_IDX);
            this.setValueAt(e.getFecha(),i, FECHA_COLUMN_IDX);
            this.setValueAt(e.getValor().getAlmacenamiento(), i, ALMACENAMIENTO_COLUMN_IDX);
            this.setValueAt(e.getDetalle(), i, DETALLE_COLUMN_IDX);

            if (e instanceof DetalleIngresoStock)
            {
                DetalleIngresoStock d = (DetalleIngresoStock)e;
                this.setValueAt(d.getProveedor(), i, SERVICIO_COLUMN_IDX);
                this.setValueAt(d.getCostoTotal(), i, IMPORTE_COLUMN_IDX);
            }
            if (e instanceof DetalleIngresoGrano)
            {
                DetalleIngresoGrano d = (DetalleIngresoGrano)e;
                this.setValueAt(d.getServicio(), i, SERVICIO_COLUMN_IDX);
                this.setValueAt(d.getImporte(), i, IMPORTE_COLUMN_IDX);
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
