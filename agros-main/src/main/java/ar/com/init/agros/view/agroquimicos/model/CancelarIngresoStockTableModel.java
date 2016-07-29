package ar.com.init.agros.view.agroquimicos.model;

import ar.com.init.agros.controller.inventario.agroquimicos.DetalleMovimientoStockJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleCancelacionIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Clase CancelarIngresoStockTableModel
 *
 *
 * @author gmatheu
 * @version 21/07/2009 
 */
public class CancelarIngresoStockTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = -1L;
    private List<DetalleIngresoStock> detalles;
    private Date fecha;
    private static Class[] types =
            new Class[]{Boolean.class, String.class, String.class, String.class, String.class, String.class, ValorUnidad.class, ValorUnidad.class};
    private static boolean[] canEdit = new boolean[]{true, false, false, false, false, false, false, false};
    private static String[] TABLE_HEADERS = {"Selección", "Fecha", "Agroquímico", "Depósito", "Proveedor", "Remito", "Cantidad", "Stock Actual"};
    public static final int SELECCION_COLUMN_IDX = 0;
    private static final int FECHA_COLUMN_IDX = 1;
    private static final int AGROQUIMICO_COLUMN_IDX = 2;
    public static final int DEPOSITO_COLUMN_IDX = 3;
    private static final int PROVEEDOR_COLUMN_IDX = 4;
    public static final int REMITO_COLUMN_IDX = 5;
    private static final int CANTIDAD_COLUMN_IDX = 6;
    private static final int STOCK_ACTUAL_COLUMN_IDX = 7;

    public CancelarIngresoStockTableModel()
    {
        super(new Object[][]{}, TABLE_HEADERS);
    }

    public List<DetalleCancelacionIngresoStock> getCheckedDetalles()
    {
        ArrayList<DetalleCancelacionIngresoStock> r = new ArrayList<DetalleCancelacionIngresoStock>();

        for (int i = 0; i < detalles.size(); i++) {
            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i,
                    SELECCION_COLUMN_IDX)) {
                DetalleIngresoStock d = detalles.get(i);
                DetalleCancelacionIngresoStock detalle = new DetalleCancelacionIngresoStock(d);
                detalle.setFecha(fecha);
                d.setCancelado(true);
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

    public void setDetallesIngreso(Date fecha)
    {
        this.fecha = fecha;

        DetalleMovimientoStockJpaController<DetalleIngresoStock> ingresoController =
                new DetalleMovimientoStockJpaController<DetalleIngresoStock>(DetalleIngresoStock.class);

        this.detalles = ingresoController.findByFecha(fecha, TipoMovimientoStockEnum.INGRESO);

        setRowCount(0);
        setRowCount(detalles.size());

        for (DetalleIngresoStock d : detalles) {
            int row = detalles.indexOf(d);

            Deposito depo = d.getDepositoDestino();
            ValorUnidad cantidad = d.getCantidad();
            Agroquimico agro = d.getAgroquimico();
            ValorAgroquimico vd = depo.getValorDeposito(agro);

            setValueAt(GUIUtility.toMediumDate(d.getFecha()), row, FECHA_COLUMN_IDX);
            setValueAt(agro, row, AGROQUIMICO_COLUMN_IDX);
            setValueAt(depo, row, DEPOSITO_COLUMN_IDX);
            setValueAt(d.getProveedor(), row, PROVEEDOR_COLUMN_IDX);
            setValueAt(d.getRemito(), row, REMITO_COLUMN_IDX);
            setValueAt(cantidad, row, CANTIDAD_COLUMN_IDX);
            setValueAt(vd.getStockActual(), row, STOCK_ACTUAL_COLUMN_IDX);
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

    public boolean validate(int idx)
    {
        Boolean seleccion = (Boolean) getValueAt(idx, SELECCION_COLUMN_IDX);

        boolean r = true;
        if (seleccion) {
            DetalleIngresoStock d = detalles.get(idx);
            Deposito deposito = d.getDepositoDestino();
            ValorUnidad cantidad = new ValorUnidad(d.getCantidad());
            Agroquimico agroquimico = d.getAgroquimico();
            ValorAgroquimico valorDeposito = deposito.getValorDeposito(agroquimico);
            ValorUnidad stockActual = valorDeposito.getStockActual();

            //Verificar valor acumulado con otros detalles con mismo Agroquimico y Deposito seleccionados para ser cancelados
            for (int i = 0; i < detalles.size(); i++) {
                if (i != idx && this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i,
                        SELECCION_COLUMN_IDX)) {
                    DetalleIngresoStock aux = detalles.get(i);
                    Deposito depositoAux = aux.getDepositoDestino();
                    Agroquimico agroquimicoAux = aux.getAgroquimico();
                    if(deposito.equals(depositoAux) && agroquimico.equals(agroquimicoAux))
                    {
                        ValorUnidad cantidadAux = aux.getCantidad();
                        cantidad.sumar(cantidadAux);
                    }
                }
            }

            if (stockActual.compareTo(cantidad) < 0) {
                setValueAt(false, idx, SELECCION_COLUMN_IDX);
                r = false;
            }
        }

        return r;
    }
}
