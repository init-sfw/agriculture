package ar.com.init.agros.util.gui.table;

import ar.com.init.agros.model.base.TablizableEntity;
import java.util.List;

/**
 * Clase TablizableEntityDataModel
 * Sirve como base para los TableModel de las TablizableEntity.
 * Por defecto todas las celdas son de solo lectura.
 * No necesita ser redefinida para ser usada, aunque esto no trae mayor inconveniente.
 *
 * @author gmatheu
 * @version 17/06/2009 
 */
public class TablizableEntityDataModel<T extends TablizableEntity> extends BaseEntityTableModel<T>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de TablizableEntityDataModel */
    public TablizableEntityDataModel(List<T> data, String[] headers)
    {
        super(data, headers);
    }

    public TablizableEntityDataModel(String[] headers)
    {
        super(headers);
    }

    @Override
    protected List<Object> toTableLine(T entity)
    {
      return entity.getTableLine();
    }  
}
