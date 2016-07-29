package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.view.components.model.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Clase ServicioTableModel
 *
 *
 * @author fbobbio
 * @version 22-dic-2010 
 */
public abstract class ServicioTableModel<K extends Object> extends AbstractTableModel<K>
{
    public static int SERVICIO_COLUMN_IDX = 0;
    public static int IMPORTE_COLUMN_IDX = 1;

    public ServicioTableModel(String[] headers)
    {
        super(headers);
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    public String toString()
    {
        return super.toString();
    }

    public List<ServicioImporteLinea> sumarizarImportesPorServicio()
    {
        Map<Servicio,ValorMoneda> hash = new HashMap<Servicio,ValorMoneda>();
        List<ServicioImporteLinea> ret = new ArrayList<ServicioImporteLinea>();
        for (int i = 0; i < this.data.size(); i++)
        {
            Servicio s = (Servicio)this.getValueAt(i, SERVICIO_COLUMN_IDX);
            ValorMoneda importe = (ValorMoneda)this.getValueAt(i, IMPORTE_COLUMN_IDX);
            if (hash.get(s) == null)
            {
                hash.put(s, importe);
            }
            //sumarizo
            else
            {
                hash.put(s, new ValorMoneda(hash.get(s).getMonto() + importe.getMonto()));
            }
        }
        Iterator it = hash.entrySet().iterator();
        while (it.hasNext())
        {
            ServicioImporteLinea sL;
            Map.Entry<Servicio,ValorMoneda> e = (Map.Entry<Servicio,ValorMoneda>)it.next();
            sL = new ServicioImporteLinea(e.getKey(), e.getValue());
            ret.add(sL);
        }
        return ret;
    }
}
