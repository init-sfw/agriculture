package ar.com.init.agros.view.components.model;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase SeleccionSubLotesTableModel
 *
 *
 * @author fbobbio
 * @version 30-jun-2009 
 */
public class SeleccionSubLotesTableModel extends AbstractSeleccionTableModel<SubLote>
{

    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Boolean.class,java.lang.Object.class, java.lang.Object.class, ValorUnidad.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        true,false, false, false
    };
    private static String[] TABLE_HEADERS =
    {
        "Selección","Lote", "Sub Lote","Superficie (ha)"
    };
    public static final int LOTE_COLUMN_IDX = 1;
    public static final int SUBLOTE_COLUMN_IDX = 2;
    public static final int SUPERFICIE_COLUMN_IDX = 3;

    public SeleccionSubLotesTableModel()
    {
        super(TABLE_HEADERS);
    }

    public SeleccionSubLotesTableModel(String[] headers)
    {
        super(headers);
    }

    public List<SubLote> getSubLotes()
    {
        return data;
    }

    public Set<Lote> getLotesFromCheckedSubLotes()
    {
        Set<Lote> ret = new HashSet<Lote>();
        for(SubLote s : getCheckedData())
        {
            ret.add(s.getPadre());
        }
        return ret;
    }

    public List<SubLote> getCheckedSubLotesFromSelectedLote(Lote lote)
    {
        List<SubLote> ret = new ArrayList<SubLote>();
        for (int i = 0; i < data.size(); i++)
        {
            SubLote s = data.get(i);
            if (s.getPadre().equals(lote) && this.getValueAt(i, SELECCION_COLUMN_IDX) != null && (Boolean) this.getValueAt(i, SELECCION_COLUMN_IDX))
            {
                ret.add(s);
            }
        }
        return ret;
    }

    @Override
    public void setData(List<SubLote> subLotes, List<SubLote> seleccionadas)
    {
        this.data = subLotes;
        setRowCount(0);
        setRowCount(subLotes.size());

        for (SubLote lote : subLotes)
        {
            this.setValueAt(lote.getPadre(), subLotes.indexOf(lote), LOTE_COLUMN_IDX);
            this.setValueAt(lote, subLotes.indexOf(lote), SUBLOTE_COLUMN_IDX);
            this.setValueAt(lote.getSuperficie(), subLotes.indexOf(lote), SUPERFICIE_COLUMN_IDX);
            if (seleccionadas != null && seleccionadas.contains(lote))
            {
                this.setValueAt(true, subLotes.indexOf(lote), SELECCION_COLUMN_IDX);
            }
            else
            {
                this.setValueAt(false, subLotes.indexOf(lote), SELECCION_COLUMN_IDX);
            }

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
