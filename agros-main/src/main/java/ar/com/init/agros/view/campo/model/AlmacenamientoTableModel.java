package ar.com.init.agros.view.campo.model;

import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AlmacenamientoTableModel
 *
 *
 * @author fbobbio
 * @version 07-dic-2010 
 */
public class AlmacenamientoTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = -1L;
    private List<Almacenamiento> almacenamientos;
    private static Class[] types = new Class[]{java.lang.Boolean.class,java.lang.Object.class,java.lang.Object.class};
    private static boolean[] canEdit = new boolean[]{true, false,false};
    private static String[] TABLE_HEADERS = {"Selección","Almacenamiento", "Tipo"};
    private static final int SELECCION_COLUMN_IDX = 0;
    private static final int ALMACENAMIENTO_COLUMN_IDX = 1;
    private static final int TIPO_COLUMN_IDX = 2;

    public AlmacenamientoTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        almacenamientos = new ArrayList<Almacenamiento>();
    }

    public List<Almacenamiento> getAlmacenamientos()
    {
        return almacenamientos;
    }

    public List<Almacenamiento> getCheckedAlmacenamientos()
    {
        ArrayList<Almacenamiento> r = new ArrayList<Almacenamiento>();

        for (int i = 0; i < almacenamientos.size(); i++) {
            Almacenamiento a = almacenamientos.get(i);

            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX)) {
                r.add(a);
            }
        }

        return r;
    }

    public void changeAll(boolean checked)
    {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(checked, i, SELECCION_COLUMN_IDX);
        }
    }

    public void setAlmacenamientos(List<Almacenamiento> almacenamientos, List<Almacenamiento> seleccionados)
    {
        this.almacenamientos = almacenamientos;
        setRowCount(0);
        setRowCount(almacenamientos.size());

        for (Almacenamiento almacenamiento : almacenamientos) {
            this.setValueAt(almacenamiento, almacenamientos.indexOf(almacenamiento), ALMACENAMIENTO_COLUMN_IDX);
            this.setValueAt(almacenamiento.getTipoAlmacenamiento(), almacenamientos.indexOf(almacenamiento), TIPO_COLUMN_IDX);
            if (seleccionados != null && seleccionados.contains(almacenamiento)) {
                this.setValueAt(true, almacenamientos.indexOf(almacenamiento), SELECCION_COLUMN_IDX);
            }
        }
    }

    public void setAlmacenamientos(List<Almacenamiento> almacenamientos)
    {
        setAlmacenamientos(almacenamientos, null);
    }

    public void checkAlmacenamientos(List<Almacenamiento> seleccionadas)
    {
        setAlmacenamientos(almacenamientos, seleccionadas);
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