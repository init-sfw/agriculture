package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleAjusteInventario;
import ar.com.init.agros.util.gui.styles.ConditionalStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * Clase AjusteInventarioTableModel
 *
 *
 * @author gmatheu
 * @version 27/06/2009 
 */
public class AjusteInventarioTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<Agroquimico> agroquimicos;
    private static Class[] types =
            new Class[]{Object.class, Double.class, Double.class, ConditionalStyle.class, String.class};
    private static boolean[] canEdit = new boolean[]{false, false, true, false, true};
    private static String[] TABLE_HEADERS = {"Agroquímico", "Existente en Stock", "Existente en Depósito", "Diferencia", "Motivo"};
    private Date fecha;
    private Deposito deposito;
    private static final int AGROQUIMICO_COLUMN_IDX = 0;
    public static final int STOCK_COLUMN_IDX = 1;
    public static final int DEPOSITO_COLUMN_IDX = 2;
    private static final int DIFERENCIA_COLUMN_IDX = 3;
    public static final int MOTIVO_COLUMN_IDX = 4;

    public AjusteInventarioTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
        agroquimicos = new ArrayList<Agroquimico>();

        addTableModelListener(new ValorDepositoTableModelListener());
    }

    public List<Agroquimico> getAgroquimicos()
    {
        return agroquimicos;
    }

    public List<DetalleAjusteInventario> getAjustesInventario()
    {
        ArrayList<DetalleAjusteInventario> r = new ArrayList<DetalleAjusteInventario>();

        for (Agroquimico a : agroquimicos) {
            int row = agroquimicos.indexOf(a);

            Object diferencia = getValueAt(row, DIFERENCIA_COLUMN_IDX);
            if (diferencia != null && diferencia instanceof ConditionalStyle) {

                DetalleAjusteInventario m = new DetalleAjusteInventario();
                m.setFecha(fecha);
                m.setValorDeposito(a.getValorDeposito(deposito));
                ConditionalStyle cs = (ConditionalStyle) diferencia;
                m.setCantidad((ValorUnidad) cs.getValue());
                String motivo = (String) getValueAt(row, MOTIVO_COLUMN_IDX);
                m.setMotivo(motivo);

                r.add(m);
            }
        }

        return r;
    }

    public void setAgroquimicos(List<Agroquimico> agroquimicos, Date fecha, Deposito deposito)
    {
        this.fecha = fecha;
        this.deposito = deposito;
        this.agroquimicos = agroquimicos;
        setRowCount(0);
        setRowCount(agroquimicos.size());

        for (Agroquimico a : agroquimicos) {
            int row = agroquimicos.indexOf(a);
            this.setValueAt(a, row, AGROQUIMICO_COLUMN_IDX);
            this.setValueAt(a.getValorDeposito(deposito).getStockActual(), row, STOCK_COLUMN_IDX);
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

    private class ValorDepositoTableModelListener implements TableModelListener
    {

        @Override
        public void tableChanged(TableModelEvent e)
        {
            if (getRowCount() > 0) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                Object o = getValueAt(row, DEPOSITO_COLUMN_IDX);
                if (column == DEPOSITO_COLUMN_IDX) {
                    if (o instanceof Double) {
                        Double valorDeposito = (Double) o;

                        ValorUnidad stock = (ValorUnidad) getValueAt(row, STOCK_COLUMN_IDX);

                        ValorUnidad diferencia = new ValorUnidad();
                        diferencia.setValor(valorDeposito - stock.getValor());
                        diferencia.setUnidad(stock.getUnidad());
                        ConditionalStyle cs = DetalleAjusteInventario.createCantidadConditionalStyle(
                                diferencia);
                        setValueAt(cs, row, DIFERENCIA_COLUMN_IDX);
                    }
                    else {
                        setValueAt(null, row, DIFERENCIA_COLUMN_IDX);
                    }
                }
            }
        }
    }
}
