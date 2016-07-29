package ar.com.init.agros.view.campanias.model;

import ar.com.init.agros.model.Bonificacion;
import ar.com.init.agros.model.ValorMoneda;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase BonificacionTableModel
 *
 *
 * @author fbobbio
 * @version 29-nov-2009 
 */
public class BonificacionTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<Bonificacion> bonificaciones;
    private static Class[] types = new Class[]{Object.class, Object.class, Double.class};
    private static boolean[] canEdit = new boolean[]{false, false, true};
    private static String[] TABLE_HEADERS = {"Establecimiento", "Cultivo", "Importe [U$S]"};
    public static final int ESTABLECIMIENTO_COLUMN_IDX = 0;
    public static final int CULTIVO_COLUMN_IDX = 1;
    public static final int IMPORTE_ACTIVO_COLUMN_IDX = 2;

    public BonificacionTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        bonificaciones = new ArrayList<Bonificacion>();
    }

    public List<Bonificacion> getBonificaciones()
    {
        actualizarImportes();
        return bonificaciones;
    }

    public void clear()
    {
        setData(new ArrayList<Bonificacion>());
    }

    public void setData(List<Bonificacion> bonif)
    {
        bonificaciones = bonif;

        setRowCount(0);
        setRowCount(bonificaciones.size());

        for (Bonificacion b : bonificaciones)
        {
            int row = bonificaciones.indexOf(b);

            setValueAt(b.getEstablecimiento(), row, ESTABLECIMIENTO_COLUMN_IDX);
            setValueAt(b.getCultivo(), row, CULTIVO_COLUMN_IDX);
            setValueAt(b.getImporte().getMonto(), row, IMPORTE_ACTIVO_COLUMN_IDX);
        }
    }

    private void actualizarImportes()
    {
        for (Bonificacion b : bonificaciones)
        {
            int row = bonificaciones.indexOf(b);

            b.setImporte(new ValorMoneda((Double)getValueAt(row, IMPORTE_ACTIVO_COLUMN_IDX)));
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
