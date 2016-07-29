package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import java.util.List;

/**
 * Clase ContratistaPostCosechaTableModel
 *
 *
 * @author fbobbio
 * @version 20-dic-2010 
 */
public class ContratistaPostCosechaTableModel extends ServicioTableModel<ContratistaPostCosechaTableModelLine>
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
         "Actividad","Campaña","Establecimiento", "Empresa de Servicio", "Tipo de Costo", "Importe[U$S]"
    };

    public static final int ACTIVIDAD_COLUMN_IDX = 0;
    public static final int CAMPANIA_COLUMN_IDX = 1;
    public static final int ESTABLECIMIENTO_COLUMN_IDX = 2;
    public static final int TIPO_COSTO_COLUMN_IDX = 4;


    public ContratistaPostCosechaTableModel()
    {
        this(TABLE_HEADERS);
    }

    public ContratistaPostCosechaTableModel(String[] headers)
    {
        super(headers);
        SERVICIO_COLUMN_IDX = 3;
        IMPORTE_COLUMN_IDX = 5;
    }

    @Override
    public void setData(List<ContratistaPostCosechaTableModelLine> data)
    {
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (int i = 0; i < this.data.size(); i++)
        {
            ContratistaPostCosechaTableModelLine l = this.data.get(i);
            this.setValueAt(l.getTransaction().entityName(), i, ACTIVIDAD_COLUMN_IDX);
            this.setValueAt(l.getTransaction().getCampania(), i, CAMPANIA_COLUMN_IDX);
            this.setValueAt(l.getTransaction().getCampo(),i, ESTABLECIMIENTO_COLUMN_IDX);
            this.setValueAt(l.getCosto().getServicio(), i, SERVICIO_COLUMN_IDX);
            this.setValueAt(l.getCosto().getTipoCosto(), i, TIPO_COSTO_COLUMN_IDX);
            this.setValueAt(l.calcularImporte(), i, IMPORTE_COLUMN_IDX);
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
