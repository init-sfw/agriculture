package ar.com.init.agros.view.campanias.model;

import ar.com.init.agros.model.RendimientoSuperficie;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.view.campanias.DialogCierreDeCampania;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 * Clase SeleccionSuperficiesTableModel
 *
 *
 * @author fbobbio
 * @version 24-jul-2009 
 */
public class SeleccionSuperficiesTableModel extends DefaultTableModel
{

    private static final long serialVersionUID = -1L;
    private List<RendimientoSuperficie> rendimientoSuperficies;
    private static Class[] types = new Class[]
    {
        java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        false, false, false, false, false, true, true, false,false
    };
    private static String[] TABLE_HEADERS =
    {
        "Establecimiento", "Lote", "SubLote", "Cultivo", "Variedad", "Rinde Promedio (qq)", "Valor por Quintal (U$S)", "Rendimiento en ha (U$S/ha)", "Rendimiento (U$S)"
    };
    public static final int CAMPO_COLUMN_IDX = 0;
    public static final int LOTE_COLUMN_IDX = 1;
    public static final int SUBLOTE_COLUMN_IDX = 2;
    public static final int CULTIVO_COLUMN_IDX = 3;
    public static final int VARIEDAD_COLUMN_IDX = 4;
    public static final int RINDE_COLUMN_IDX = 5;
    public static final int VALORPORQUINTAL_COLUMN_IDX = 6;
    public static final int RENDIMIENTO_HA = 7;
    public static final int RENDIMIENTO = 8;

    public SeleccionSuperficiesTableModel()
    {
        super(new Object[][]
                {
                }, TABLE_HEADERS);
        rendimientoSuperficies = new ArrayList<RendimientoSuperficie>();
    }

    public void clear()
    {
        rendimientoSuperficies.clear();
        setRendimientoSuperficies(rendimientoSuperficies);
    }

    public List<RendimientoSuperficie> getRendimientoSuperficies()
    {
        return rendimientoSuperficies;
    }

    public void updateRendimientosValues()
    {
        for (RendimientoSuperficie rendimiento : rendimientoSuperficies)
        {
            Object rinde = this.getValueAt(this.rendimientoSuperficies.indexOf(rendimiento), RINDE_COLUMN_IDX);
            Object valorPorQuintal = this.getValueAt(this.rendimientoSuperficies.indexOf(rendimiento), VALORPORQUINTAL_COLUMN_IDX);
            try
            {
                rinde = new Double(GUIUtility.NUMBER_FORMAT.parse(rinde.toString()).doubleValue());
            }
            catch (ParseException ex)
            {
            }
            try
            {
                valorPorQuintal = new Double(GUIUtility.NUMBER_FORMAT.parse(valorPorQuintal.toString()).doubleValue());
            }
            catch (ParseException ex)
            {
            }
            if (rinde instanceof Double)
            {
                rendimiento.setRinde(new ValorUnidad((Double) rinde, DialogCierreDeCampania.UNIDAD_MEDIDA_RINDE));
            }
            else
            {
                rendimiento.setRinde(null);
            }
            if (valorPorQuintal instanceof Double)
            {
                rendimiento.setValorPorQuintal(new ValorMoneda((Double) valorPorQuintal));
            }
            else
            {
                rendimiento.setValorPorQuintal(null);
            }
            this.setValueAt(rendimiento.calcularRendimientoPorHectarea(), this.rendimientoSuperficies.indexOf(rendimiento), RENDIMIENTO_HA);
            this.setValueAt(rendimiento.calcularRendimientoTotal(), this.rendimientoSuperficies.indexOf(rendimiento), RENDIMIENTO);
        }
    }

    public void setRendimientoSuperficies(List<RendimientoSuperficie> rendimientos)
    {
        this.rendimientoSuperficies = rendimientos;
        setRowCount(0);
        setRowCount(rendimientos.size());

        for (RendimientoSuperficie rendimiento : rendimientos)
        {
            if (rendimiento.getSuperficie() instanceof Campo)
            {
                this.setValueAt(rendimiento.getSuperficie().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), CAMPO_COLUMN_IDX);
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), LOTE_COLUMN_IDX);
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), SUBLOTE_COLUMN_IDX);
            }
            if (rendimiento.getSuperficie() instanceof Lote)
            {
                Lote l = (Lote) rendimiento.getSuperficie();
                this.setValueAt(l.getCampo().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), CAMPO_COLUMN_IDX);
                this.setValueAt(l.getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), LOTE_COLUMN_IDX);
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), SUBLOTE_COLUMN_IDX);
            }
            if (rendimiento.getSuperficie() instanceof SubLote)
            {
                SubLote s = (SubLote) rendimiento.getSuperficie();
                this.setValueAt(s.getPadre().getCampo().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), CAMPO_COLUMN_IDX);
                this.setValueAt(s.getPadre().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), LOTE_COLUMN_IDX);
                this.setValueAt(s.getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), SUBLOTE_COLUMN_IDX);
            }
            this.setValueAt(rendimiento.getSiembra().getCultivo().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), CULTIVO_COLUMN_IDX);
            if (rendimiento.getSiembra().getVariedadCultivo() != null)
            {
                this.setValueAt(rendimiento.getSiembra().getVariedadCultivo().getListLine(), this.rendimientoSuperficies.indexOf(rendimiento), VARIEDAD_COLUMN_IDX);
            }
            else
            {
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), VARIEDAD_COLUMN_IDX);
            }
            if (rendimiento.getRinde() != null && rendimiento.getRinde().isValid())
            {
                this.setValueAt(rendimiento.getRinde().getFormattedValue(), this.rendimientoSuperficies.indexOf(rendimiento), RINDE_COLUMN_IDX);
            }
            else
            {
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), RINDE_COLUMN_IDX);
            }
            if (rendimiento.getValorPorQuintal() != null && rendimiento.getValorPorQuintal().isValid())
            {
                this.setValueAt(rendimiento.getValorPorQuintal().getFormattedValue(), this.rendimientoSuperficies.indexOf(rendimiento), VALORPORQUINTAL_COLUMN_IDX);
            }
            else
            {
                this.setValueAt("", this.rendimientoSuperficies.indexOf(rendimiento), VALORPORQUINTAL_COLUMN_IDX);
            }
            this.setValueAt(rendimiento.calcularRendimientoPorHectarea(), this.rendimientoSuperficies.indexOf(rendimiento), RENDIMIENTO_HA);
            this.setValueAt(rendimiento.calcularRendimientoTotal(), this.rendimientoSuperficies.indexOf(rendimiento), RENDIMIENTO);
        }
    }

    public void setSiembras(List<Siembra> siembras)
    {
        List<RendimientoSuperficie> rendimientos = new ArrayList<RendimientoSuperficie>();
        for (Siembra s : siembras)
        {
            rendimientos.addAll(s.getRendimientoSuperficies());
        }
        setRendimientoSuperficies(rendimientos);
    }

    public List<Siembra> getSiembras()
    {
        Set<Siembra> siembras = new HashSet<Siembra>();
        for (RendimientoSuperficie r : getRendimientoSuperficies())
        {
            siembras.add(r.getSiembra());
        }
        return new ArrayList<Siembra>(siembras);
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
