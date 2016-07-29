package ar.com.init.agros.view.campo.model;

import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase SubLoteTableModel
 *
 *
 * @author fbobbio
 * @version 04-jul-2009 
 */
public class SubLoteTableModel extends TablizableEntityDataModel<SubLote>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de TratamientoPlagaTableModel */
    public SubLoteTableModel(List<SubLote> data)
    {
        super(data, SubLote.TABLE_HEADERS_SUB_LOTE);
    }

    public SubLoteTableModel()
    {
        super(new ArrayList<SubLote>(), SubLote.TABLE_HEADERS_SUB_LOTE);
    }

    @Override
    protected List<Object> toTableLine(SubLote entity)
    {
        return entity.getTableLine();
    }
}
