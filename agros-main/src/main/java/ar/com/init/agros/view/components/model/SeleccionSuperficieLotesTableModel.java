package ar.com.init.agros.view.components.model;

import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.view.components.CampaniaSelectionChangeListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase SeleccionSuperficieLotesTableModel
 * 
 *
 * @author fbobbio
 * @version 11-ago-2009 
 */
public class SeleccionSuperficieLotesTableModel extends SeleccionLotesTableModel implements CampaniaSelectionChangeListener
{

    private static final long serialVersionUID = -1L;
    private static Class[] typesSup = new Class[]
    {
        java.lang.Boolean.class, java.lang.Object.class, ValorUnidad.class, java.lang.Object.class
    };
    private static boolean[] canEditSup = new boolean[]
    {
        true, false, true, false
    };
    private static String[] TABLE_SUP_HEADERS =
    {
        "Selección", "Lote", "Superficie (ha)", "Cultivo"
    };
    public static final int CULTIVO_COLUMN_IDX = 3;
    private Campania campania;

    /** Constructor por defecto de SeleccionSuperficieLotesTableModel */
    public SeleccionSuperficieLotesTableModel()
    {
        super(TABLE_SUP_HEADERS);
    }

    public Campania getCampania()
    {
        return campania;
    }

    public void setCampania(Campania campania)
    {
        this.campania = campania;
    }

    public double getValorSuperficie(Superficie sup)
    {
        double ret = 0;
        Object aux = getValueAt(data.indexOf(sup), SUPERFICIE_COLUMN_IDX);
        if (aux instanceof Double)
        {
            ret = ((Double)aux).doubleValue();
        }
        if (aux instanceof ValorUnidad)
        {
            ret = ((ValorUnidad)aux).getValor().doubleValue();
        }
        return ret;
    }

    @Override
    public void setData(List<Lote> data, List<Lote> seleccionadas)
    {
        this.data = data;
        setRowCount(0);
        setRowCount(this.data.size());

        for (Lote lote : this.data)
        {
            this.setValueAt(lote, this.data.indexOf(lote), LOTE_COLUMN_IDX);
            this.setValueAt(lote.getSuperficie(), this.data.indexOf(lote), SUPERFICIE_COLUMN_IDX);
            Cultivo cultivo = findCultivo(lote);
            if (cultivo != null)
            {
                this.setValueAt(cultivo, this.data.indexOf(lote), CULTIVO_COLUMN_IDX);
            }
            else
            {
                this.setValueAt("", this.data.indexOf(lote), CULTIVO_COLUMN_IDX);
            }
            if (seleccionadas != null && seleccionadas.contains(lote))
            {
                this.setValueAt(true, this.data.indexOf(lote), SELECCION_COLUMN_IDX);
            }
            else
            {
                this.setValueAt(false, this.data.indexOf(lote), SELECCION_COLUMN_IDX);
            }
        }
    }

    public void reloadSuperficies()
    {
        for (Lote lote : this.data)
        {
            this.setValueAt(lote.getSuperficie(), this.data.indexOf(lote), SUPERFICIE_COLUMN_IDX);
        }
    }

    private Cultivo findCultivo(Superficie sup)
    {
        if (campania != null)
        {
            SiembraJpaController siembraController = new SiembraJpaController();
            return siembraController.findCultivoDeSuperficieEnCampania(sup, campania);
        }
        else
        {
            return null;
        }
    }

    public Set<Cultivo> getSelectedCultivos()
    {
        Set<Cultivo> r = new HashSet<Cultivo>();

        for (int i = 0; i < data.size(); i++)
        {
            if (this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX))
            {
                if (this.getValueAt(i, CULTIVO_COLUMN_IDX) != null && !this.getValueAt(i, CULTIVO_COLUMN_IDX).equals(""))
                {
                    r.add((Cultivo)this.getValueAt(i, CULTIVO_COLUMN_IDX));
                }
            }
        }
        return r;
    }

    @Override
    public Class getColumnClass(int columnIndex)
    {
        return typesSup[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (columnIndex != SELECCION_COLUMN_IDX && (this.getValueAt(rowIndex, SELECCION_COLUMN_IDX) == null || !(Boolean) this.getValueAt(rowIndex, SELECCION_COLUMN_IDX)))
        {
            return false;
        }
        return canEditSup[columnIndex];
    }

    @Override
    public int getColumnCount()
    {
        return TABLE_SUP_HEADERS.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return TABLE_SUP_HEADERS[column];
    }

    @Override
    public void campaniaSelectionChanged(Campania campania)
    {
        this.campania = campania;
    }
}
