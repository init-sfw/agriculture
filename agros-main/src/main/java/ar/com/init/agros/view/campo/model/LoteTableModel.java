package ar.com.init.agros.view.campo.model;

import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import ar.com.init.agros.view.campo.exceptions.LotesAsociadosALluviasException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase LoteTableModel
 *
 *
 * @author fbobbio
 * @version 04-jul-2009 
 */
public class LoteTableModel extends TablizableEntityDataModel<Lote>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de TratamientoPlagaTableModel */
    public LoteTableModel(List<Lote> data)
    {
        super(data, Lote.TABLE_HEADERS_LOTE);
    }

    public LoteTableModel()
    {
        super(new ArrayList<Lote>(), Lote.TABLE_HEADERS_LOTE);
    }

    /**
     * M�todo que borra los lotes pasados por par�metro de la tabla que NO est�n asociados a lluvias
     *
     * @param lotes los lotes a borrar
     * @exception LotesAsociadosALluviasException
     */
    public void borrarLotes(List<Lote> lotes) throws LotesAsociadosALluviasException
    {
        boolean hayLotesAsociadosALluvias = false;
        List<Lote> lotesSinLluvias = new ArrayList<Lote>();
        for (Lote l : lotes)
        {
            if (l.getLluvias().isEmpty())
            {
                // Cargo los lotes sin lluvias
                lotesSinLluvias.add(l);
            }
            else
            {
                // Pongo en true la bandera que indica que habr� lotes sin borrarse
                hayLotesAsociadosALluvias = true;
            }
        }
        //Borro los lotes que no est�n asociados a lluvias
        removeEntities(lotesSinLluvias);

        //Si hubo lotes que no se borraron, lanzo la excepci�n para informarlo
        if (hayLotesAsociadosALluvias)
        {
            throw new LotesAsociadosALluviasException("Hubo lotes que no se pudieron borrar por estar asociados a lluvias");
        }

    }

    /** M�todo que borra todos los lotes de la tabla que NO est�n asociados a lluvias
     *
     * @throws LotesAsociadosALluviasException cuando no se borran algunos lotes que est�n asociados a lluvias
     */
    public void borrarAllLotes() throws LotesAsociadosALluviasException
    {
        borrarLotes(data);
    }

    /** M�todo que devuelve la lista de lotes seleccionados a partir de una lista de �ndices del modelo */
    public List<Lote> getSelectedLotesFromIndexes(int[] modelIndexes)
    {
        List<Lote> ret = new ArrayList<Lote>();
        for (int i = 0; i < modelIndexes.length; i++)
        {
            ret.add(data.get(modelIndexes[i]));
        }
        return ret;
    }
}
