package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.Accion;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AccionesTableModel
 *
 *
 * @author gmatheu
 * @version 23/06/2009 
 */
public class AccionesTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<Accion> acciones;
    private static Class[] types = new Class[]{java.lang.Object.class, java.lang.Boolean.class};
    private static boolean[] canEdit = new boolean[]{false, true};
    private static String[] TABLE_HEADERS = {"Acción", "Selección"};
    private static final int SELECCION_COLUMN_IDX = 1;

    public AccionesTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        acciones = new ArrayList<Accion>();
    }

    public List<Accion> getAcciones()
    {
        return acciones;
    }

    public List<Accion> getCheckedAcciones()
    {
        ArrayList<Accion> r = new ArrayList<Accion>();

        for (int i = 0; i < acciones.size(); i++) {
            Accion a = acciones.get(i);

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

    public void setAcciones(List<Accion> acciones, List<Accion> seleccionadas)
    {
        this.acciones = acciones;
        setRowCount(0);
        setRowCount(acciones.size());

        for (Accion accion : acciones) {
            this.setValueAt(accion, acciones.indexOf(accion), 0);
            if (seleccionadas != null && seleccionadas.contains(accion)) {
                this.setValueAt(true, acciones.indexOf(accion), SELECCION_COLUMN_IDX);
            }
        }
    }

    public void setAcciones(List<Accion> acciones)
    {
        setAcciones(acciones, null);
    }

    public void checkAcciones(List<Accion> seleccionadas)
    {
        setAcciones(acciones, seleccionadas);
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
