package ar.com.init.agros.view.configuracion.model;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.model.util.Configuration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AlertasTableModel
 *
 *
 * @author gmatheu
 * @version 13/07/2009 
 */
public class AlertasTableModel extends DefaultTableModel {

    public enum Alertas {

        ALERTA_INGRESO_STOCK("Ingreso de Agroquímicos al Stock"),
        ALERTA_STOCK_MINIMO("Stock mínimo alcanzado"),
        ALERTA_AJUSTE_INVENTARIO("Ajuste por control de inventario"),
        ALERTA_MOVIMIENTO_ENTRE_DEPOSITO("Movimiento de stock entre depositos"),
        ALERTA_CANCELACION_INGRESO("Cancelación de Ingreso de Stock"),
        ALERTA_INGRESO_GRANO("Ingreso de Semillas/Cereales"),
        ALERTA_EGRESO_GRANO("Egreso de Semillas/Cereales"),
        ALERTA_CANCELACION_INGRESO_GRANO("Cancelación de Ingreso de Semillas/Cereales"),
        ALERTA_CANCELACION_EGRESO_GRANO("Cancelación de Egreso de Semillas/Cereales");

        private Alertas(String caption) {
            this.caption = caption;

        }
        private final String caption;

        public String caption() {
            return caption;
        }
    }
    private static final long serialVersionUID = -1L;
    private List<Configuration> configurations;
    private static Class[] types = new Class[]{java.lang.Object.class, java.lang.Boolean.class};
    private static boolean[] canEdit = new boolean[]{false, true};
    private static String[] TABLE_HEADERS = {"Alertas Disponibles", "Selección"};
    public static final int SELECCION_COLUMN_IDX = 1;

    public AlertasTableModel() {
        super(new Object[][]{}, TABLE_HEADERS);
    }

    public void loadConfiguration() {
        configurations = new ArrayList<Configuration>();

        for (Alertas alertas : Alertas.values()) {
            configurations.add(ConfMgr.getInstance().getController().findUniqueByKey(
                    alertas.toString()));
        }

        setConfigurations(configurations);
    }

    private void setConfigurations(List<Configuration> configurations) {
        setRowCount(0);
        setRowCount(configurations.size());

        for (Configuration configuration : configurations) {
            int idx = configurations.indexOf(configuration);
            Boolean checked = false;
            try {
                checked = Boolean.parseBoolean(configuration.getConfValue());
            } catch (Exception exc) {
            }
            setValueAt(checked, idx, SELECCION_COLUMN_IDX);

            Alertas al = Alertas.valueOf(Alertas.class, configuration.getConfKey());

            setValueAt(al.caption(), idx, 0);
        }

        fireTableDataChanged();
    }

    /** Checkea si están seleccionadas todas las filas
     *
     * @return true si todas las filas están seleccionadas - false si no
     */
    public boolean isAllSelected() {
        if (configurations == null || configurations.isEmpty()) {
            return false;
        }
        return (getCheckedData().size() == configurations.size());
    }

    public List<Configuration> getCheckedData() {
        ArrayList<Configuration> r = new ArrayList<Configuration>();

        for (int i = 0; i < configurations.size(); i++) {
            Configuration a = configurations.get(i);

            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX)) {
                r.add(a);
            }
        }
        return r;
    }

    public List<Configuration> getConfigurations() {
        ArrayList<Configuration> r = new ArrayList<Configuration>();

        for (int i = 0; i < configurations.size(); i++) {
            Configuration c = configurations.get(i);

            Object o = this.getValueAt(i, SELECCION_COLUMN_IDX);
            Boolean checked = (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) o);
            c.setConfValue(checked.toString());
            r.add(c);
        }

        return r;
    }

    public void changeAll(boolean checked) {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(checked, i, SELECCION_COLUMN_IDX);
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return TABLE_HEADERS.length;
    }

    @Override
    public String getColumnName(int column) {
        return TABLE_HEADERS[column];
    }
}
