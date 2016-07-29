package ar.com.init.agros.model.base;

import ar.com.init.agros.util.gui.Tablizable;
import java.util.List;
import java.util.UUID;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.swing.table.TableModel;

/**
 * Clase TablizableEntity que otorga la funcionalidad a una entidad de poder traducirse a una tabla
 * para ser mostrada
 *
 *
 * @author fbobbio
 * @version 09-jun-2009 
 */
@MappedSuperclass
public abstract class TablizableEntity extends BaseEntity implements Tablizable
{

    /** Constructor por defecto de TablizableEntity */
    public TablizableEntity()
    {
    }

    public TablizableEntity(UUID uuid)
    {
        super(uuid);
    }

    @Override
    @Transient
    public abstract List<Object> getTableLine();

    @Override
    @Transient
    public abstract String[] getTableHeaders();

    @Override
    @Transient
    public TableModel getTableModel()
    {
        String[] headers = getTableHeaders();
        Object[][] objects = new Object[headers.length][1];
        final boolean[] canEdit = new boolean[headers.length];
        for (int i = 0; i < canEdit.length; i++) {
            canEdit[i] = false;

        }
        TableModel ret = new javax.swing.table.DefaultTableModel(objects, headers)
        {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit[columnIndex];
            }
        };
        return ret;
    }
}
