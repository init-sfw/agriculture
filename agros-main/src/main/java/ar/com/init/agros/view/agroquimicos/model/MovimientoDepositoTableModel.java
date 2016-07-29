package ar.com.init.agros.view.agroquimicos.model;

import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoDeposito;
import ar.com.init.agros.model.inventario.agroquimicos.MovimientoDeposito;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase MovimientoDepositoTableModel
 *
 *
 * @author gmatheu
 * @version 17/07/2009 
 */
public class MovimientoDepositoTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<Agroquimico> agroquimicos;
    private Deposito depositoOrigen;
    private Date fecha;
    private static Class[] types =
            new Class[]{Boolean.class, String.class, ConditionalStyle.class, Double.class};
    private static boolean[] canEdit = new boolean[]{true, false, false, true};
    private static String[] TABLE_HEADERS = {"Selección", "Agroquímico", "Existente en Depósito Origen", "Cantidad a Mover"};
    public static final int SELECCION_COLUMN_IDX = 0;
    public static final int AGROQUIMICO_COLUMN_IDX = 1;
    public static final int EXISTENTE_COLUMN_IDX = 2;
    public static final int CANTIDAD_COLUMN_IDX = 3;

    public MovimientoDepositoTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
    }

    public List<DetalleMovimientoDeposito> getCheckedDetalles(Deposito depositoDestino)
    {
        if (agroquimicos == null) {
            return null;
        }

        ArrayList<DetalleMovimientoDeposito> r = new ArrayList<DetalleMovimientoDeposito>();

        for (int i = 0; i < agroquimicos.size(); i++) {
            Agroquimico a = agroquimicos.get(i);

            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i,
                    SELECCION_COLUMN_IDX)) {
                DetalleMovimientoDeposito detalle = new DetalleMovimientoDeposito();
                detalle.setValorDepositoOrigen(a.getValorDeposito(depositoOrigen));
                detalle.setFecha(fecha);
                detalle.setValorDeposito(a.getValorDeposito(depositoDestino));

                ValorUnidad existente = (ValorUnidad) getValueAt(i, EXISTENTE_COLUMN_IDX);
                if (getValueAt(i, CANTIDAD_COLUMN_IDX) != null && getValueAt(i, CANTIDAD_COLUMN_IDX) instanceof Double) {
                    Double c = (Double) getValueAt(i, CANTIDAD_COLUMN_IDX);
                    ValorUnidad valor = new ValorUnidad(c, existente.getUnidad());
                    detalle.setCantidad(valor);
                }
                else {
                    detalle.setCantidad(null);
                }

                r.add(detalle);
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

    public void setDetalles(MovimientoDeposito mov)
    {
        for (int i = 0; i < mov.getCastedDetalles().size(); i++)
        {
            setValueAt(mov.getCastedDetalles().get(i).getCantidad().getValor(), i, CANTIDAD_COLUMN_IDX);
        }
    }

    /** Método para setear la lista de detalles cuando se realiza una consulta de movimientos de stock */
    public void setDetallesConsulta(MovimientoDeposito mov)
    {
        for (int i = 0; i < mov.getCastedDetalles().size(); i++)
        {
            DetalleMovimientoDeposito det = mov.getCastedDetalles().get(i);
            setValueAt(det.getCantidad().getValor(), agroquimicos.indexOf(det.getValorDepositoOrigen().getAgroquimico()), CANTIDAD_COLUMN_IDX);
        }
    }

    public void setAgroquimicos(List<Agroquimico> agroquimicos, Date fecha, Deposito deposito)
    {
        this.fecha = fecha;
        this.depositoOrigen = deposito;
        this.agroquimicos = agroquimicos;
        setRowCount(0);
        setRowCount(agroquimicos.size());

        for (Iterator<Agroquimico> it = agroquimicos.iterator(); it.hasNext();) {
            Agroquimico a = it.next();

            int row = agroquimicos.indexOf(a);

            //TODO ver tema de fecha y ValorDeposito
            ValorAgroquimico valorDeposito = a.getValorDeposito(deposito);

            if (valorDeposito.getStockActual().getValor() > 0) {
                this.setValueAt(a, row, AGROQUIMICO_COLUMN_IDX);
                this.setValueAt(valorDeposito.getStockActual(), row, EXISTENTE_COLUMN_IDX);
            }
            else {
                removeRow(row);
                it.remove();
            }
        }

        for (Agroquimico a : agroquimicos) {
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
