package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.inventario.agroquimicos.DetalleAjusteInventario;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.util.gui.styles.ConditionalStyle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase ResumenAjusteInventarioTableModel
 *
 *
 * @author gmatheu
 * @version 27/06/2009 
 */
public class ResumenAjusteInventarioTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<DetalleAjusteInventario> ajustes;
    private static Class[] types = new Class[]{Boolean.class, String.class, ConditionalStyle.class};
    private static boolean[] canEdit = new boolean[]{true, false, false};
    private static String[] TABLE_HEADERS = {"Selección", "Agroquímico", "Diferencia"};
    private static int SELECCION_COLUMN_IDX = 0;

    public ResumenAjusteInventarioTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        ajustes = new ArrayList<DetalleAjusteInventario>();
    }

    public List<DetalleAjusteInventario> getCheckedMovimientos()
    {
        ArrayList<DetalleAjusteInventario> r = new ArrayList<DetalleAjusteInventario>();

        for (int i = 0; i < ajustes.size(); i++) {
            DetalleAjusteInventario m = ajustes.get(i);

            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX)) {
                r.add(m);
            }
        }

        return r;
    }

    public void setAjustesInventario(List<DetalleAjusteInventario> ajustes)
    {
        this.ajustes = ajustes;
        setRowCount(0);
        setRowCount(ajustes.size());

        for (DetalleMovimientoStock m : ajustes) {
            this.setValueAt(m.getAgroquimico(), ajustes.indexOf(m), 1);            
            this.setValueAt(m.getConditionalStyledCantidad(), ajustes.indexOf(m), 2);
        }
    }

    public void clearMovimientos()
    {
        ajustes = new ArrayList<DetalleAjusteInventario>();
        setRowCount(0);
        fireTableDataChanged();
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

