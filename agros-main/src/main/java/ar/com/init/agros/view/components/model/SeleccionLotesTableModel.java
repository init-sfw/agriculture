package ar.com.init.agros.view.components.model;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase SeleccionLotesTableModel
 *
 *
 * @author fbobbio
 * @version 30-jun-2009 
 */
public class SeleccionLotesTableModel extends AbstractSeleccionTableModel<Lote>
{
    private static final long serialVersionUID = -1L;
    private static Class[] types = new Class[]
    {
        java.lang.Boolean.class,java.lang.Object.class, ValorUnidad.class
    };
    private static boolean[] canEdit = new boolean[]
    {
        true,false,false
    };
    private static String[] TABLE_HEADERS =
    {
         "Selección", "Lote","Superficie (ha)"
    };
    public static final int LOTE_COLUMN_IDX = 1;
    public static final int SUPERFICIE_COLUMN_IDX = 2;

    public SeleccionLotesTableModel()
    {
        this(TABLE_HEADERS);
    }

    public SeleccionLotesTableModel(String[] headers)
    {
        super(headers);
    }

    public void checkLotesFromSublotes(List<SubLote> subLotes)
    {
        Set<Lote> lotesFromSubLotes = new HashSet<Lote>();
        for (SubLote s : subLotes)
        {
            lotesFromSubLotes.add(s.getPadre());
        }
        checkDataSinBorrar(new ArrayList<Lote>(lotesFromSubLotes));
    }

    public List<SubLote> getSubLotesFromCheckedLotes()
    {
        ArrayList<SubLote> list = new ArrayList<SubLote>();
        List<Lote> aux = getCheckedData();
        for (int i = 0; i < aux.size(); i++)
        {
            Lote l = aux.get(i);
            list.addAll(l.getSubLotes());
        }
        return list;
    }

    public void checkDataSinBorrar(List<Lote> data)
    {
        for (Lote l : this.data)
        {
            for (Lote aux : data)
            {
                if (l.equals(aux))
                {
                    this.setValueAt(true, this.data.indexOf(l), SELECCION_COLUMN_IDX);
                }
            }
        }
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
