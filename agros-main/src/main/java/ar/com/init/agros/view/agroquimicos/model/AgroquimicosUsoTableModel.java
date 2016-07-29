package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.controller.InformacionAgroquimicoJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.base.BaseEntityStateEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/**
 * Clase AgroquimicosUsoTableModel
 * 
 * 
 * @author gmatheu
 * @version 27/07/2009
 */
public class AgroquimicosUsoTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<InformacionAgroquimico> informacionesAgroquimico;
    private HashSet<Integer> modifiedRows;
    private static Class[] types = new Class[]{Boolean.class, Object.class,
        Object.class, Object.class, UnidadMedida.class, Double.class, String.class};
    private static boolean[] canEdit = new boolean[]{true, false, false,
        false, true, true, false};
    private static String[] TABLE_HEADERS = {"Selección", "Nombre Comercial",
        "Principio Activo", "Concentración", "Unidad", "Stock Mínimo por Defecto","Detalle"};
    public static final int SELECCION_COLUMN_IDX = 0;
    public static final int NOMBRE_COLUMN_IDX = 1;
    public static final int PPIO_ACTIVO_COLUMN_IDX = 2;
    public static final int CONC_COLUMN_IDX = 3;
    public static final int UNIDAD_COLUMN_IDX = 4;
    public static final int STOCK_MINIMO_COLUMN_IDX = 5;
    public static final int DETALLE_COLUMN_IDX = 6;

    public AgroquimicosUsoTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        informacionesAgroquimico = new ArrayList<InformacionAgroquimico>();
        modifiedRows = new HashSet<Integer>();
    }

    public List<InformacionAgroquimico> getInformacionesAgroquimico()
    {
        return informacionesAgroquimico;
    }

    // TODO: agregar validación cuando no se hayan ingresado datos correctos
    public List<InformacionAgroquimico> getInformacionesAgroquimicoModificadas()
    {
        ArrayList<InformacionAgroquimico> r = new ArrayList<InformacionAgroquimico>();

        for (Iterator<Integer> it = modifiedRows.iterator(); it.hasNext();) {
            Integer row = it.next();

            InformacionAgroquimico ia = informacionesAgroquimico.get(row);

            Agroquimico a = ia.getAgroquimico();
            if (isRowSelected(row)) {
                if (!ia.hasAgroquimico()) {
                    if (a == null) {
                        a = new Agroquimico();
                    }
                    a.setInformacion(ia);
                    ia.setAgroquimico(a);
                }

                ia.setSeleccionadoUso(Boolean.TRUE);
                a.setStatus(BaseEntityStateEnum.ACTIVE);

                if (getValueAt(row, UNIDAD_COLUMN_IDX) instanceof UnidadMedida) {
                    UnidadMedida u = (UnidadMedida) getValueAt(row,
                            UNIDAD_COLUMN_IDX);
                    a.setUnidad(u);

                    if (getValueAt(row, STOCK_MINIMO_COLUMN_IDX) instanceof Double) {
                        Double valor = (Double) getValueAt(row,
                                STOCK_MINIMO_COLUMN_IDX);
                        ValorUnidad vu = new ValorUnidad(valor, u);
                        a.setStockMinimo(vu);
                    }
                    else {
                        a.setStockMinimo(new ValorUnidad(0D, u));
                    }
                }
                else {
                    a.setUnidad(null);
                    a.setStockMinimo(null);
                }
            }
            else {
                if (a != null) {
                    ia.setSeleccionadoUso(Boolean.FALSE);
                    a.setStatus(BaseEntityStateEnum.INACTIVE);
                }
            }
            r.add(ia);
        }

        return r;
    }

    public boolean isRowSelected(int i)
    {
        return this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i,
                SELECCION_COLUMN_IDX);
    }

    public void setData(InformacionAgroquimicoJpaController controller)
    {
        informacionesAgroquimico = controller.findEntities();

        setRowCount(0);
        setRowCount(informacionesAgroquimico.size());

        for (InformacionAgroquimico ia : informacionesAgroquimico) {
            int row = informacionesAgroquimico.indexOf(ia);

            setValueAt(ia.isSeleccionadoUso(), row, SELECCION_COLUMN_IDX);
            setValueAt(ia.getNombreComercial(), row, NOMBRE_COLUMN_IDX);
            setValueAt(ia.getPrincipioActivo(), row, PPIO_ACTIVO_COLUMN_IDX);
            setValueAt(ia.getConcentracion(), row, CONC_COLUMN_IDX);
            if (ia.getAgroquimico() != null && ia.getAgroquimico().getUnidad() != null) {
                setValueAt(ia.getAgroquimico().getUnidad(), row,
                        UNIDAD_COLUMN_IDX);
            }
            else {
                setValueAt(null, row, UNIDAD_COLUMN_IDX);
            }
            if (ia.getAgroquimico() != null && ia.getAgroquimico().getStockMinimo() != null) {
                setValueAt(ia.getAgroquimico().getStockMinimo().getValor(),
                        row, STOCK_MINIMO_COLUMN_IDX);
            }
            else {
                setValueAt(null, row, STOCK_MINIMO_COLUMN_IDX);
            }
            setValueAt(ia.getIngresoManualAsString(), row, DETALLE_COLUMN_IDX);
        }

        modifiedRows.clear();
    }

    public void changeAll(boolean checked)
    {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(checked, i, SELECCION_COLUMN_IDX);
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
        if (columnIndex == UNIDAD_COLUMN_IDX || columnIndex == STOCK_MINIMO_COLUMN_IDX) {
            return isRowSelected(rowIndex);
        }
        return canEdit[columnIndex];
    }

    @Override
    public void fireTableCellUpdated(int row, int column)
    {
        modifiedRows.add(row);
        super.fireTableCellUpdated(row, column);
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
