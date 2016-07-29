package ar.com.init.agros.view.components.model;

import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.ValorMoneda;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase CostoTotalCampaniaTableModel
 *
 *
 * @author fbobbio
 * @version 06-ago-2009 
 */
public class CostoTotalCampaniaTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<PlanificacionAgroquimico> planificaciones;
    private static Class[] types = new Class[]
    {
        java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, ValorMoneda.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        false,false,false,false,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Campaña", "Establecimiento","Cant. Lotes","Cant. Sublotes","Costos"
    };
    public static final int CAMPANIA_COLUMN_IDX = 0;
    public static final int CAMPO_COLUMN_IDX = 1;
    public static final int LOTE_COLUMN_IDX = 2;
    public static final int SUBLOTE_COLUMN_IDX = 3;
    public static final int COSTOS_COLUMN_IDX = 4;

    public CostoTotalCampaniaTableModel()
    {
        super(new Object[][]
                {
                }, TABLE_HEADERS);
        planificaciones = new ArrayList<PlanificacionAgroquimico>();
    }

    public double calcularCostoTotalCampania()
    {
        double costo = 0;
        for (PlanificacionAgroquimico p : planificaciones)
        {
            costo+= p.calcularCostos().getMonto();
        }
        return costo;
    }

    public void clear()
    {
        planificaciones.clear();
        setPlanificaciones(planificaciones);
    }

    public void setPlanificaciones(List<PlanificacionAgroquimico> planificaciones)
    {
        this.planificaciones = planificaciones;
        setRowCount(0);
        setRowCount(this.planificaciones.size());

        for (PlanificacionAgroquimico planificacion : this.planificaciones)
        {
            this.setValueAt(planificacion.getCampania(), this.planificaciones.indexOf(planificacion), CAMPANIA_COLUMN_IDX);
            this.setValueAt(planificacion.getCampo(), this.planificaciones.indexOf(planificacion), CAMPO_COLUMN_IDX);
            this.setValueAt(planificacion.contarLotes(), this.planificaciones.indexOf(planificacion), LOTE_COLUMN_IDX);
            this.setValueAt(planificacion.contarSublotes(), this.planificaciones.indexOf(planificacion), SUBLOTE_COLUMN_IDX);
            this.setValueAt(planificacion.calcularCostos(), this.planificaciones.indexOf(planificacion), COSTOS_COLUMN_IDX);
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
