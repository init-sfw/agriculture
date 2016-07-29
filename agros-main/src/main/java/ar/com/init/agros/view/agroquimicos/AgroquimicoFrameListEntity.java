package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import ar.com.init.agros.view.agroquimicos.renderers.CasafeLogoTableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.application.ResourceMap;

/**
 * Clase AgroquimicoFrameListEntity
 *
 *
 * @author gmatheu
 * @version 26/06/2009 
 */
public class AgroquimicoFrameListEntity extends FrameListEntity<Agroquimico>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de ProveedorFrameListEntity */
    public AgroquimicoFrameListEntity(ResourceMap map)
    {
        super(Agroquimico.class, Agroquimico.TABLE_HEADERS);
        setResourceMap(map);
        setVisibleButtons(false, false, true, false, true);

        //Seteo el renderer para el ícono de CASAFE
        CasafeLogoTableCellRenderer tableRenderer = new CasafeLogoTableCellRenderer();
        TableColumn column = jXTable1.getColumnModel().getColumn(jXTable1.getColumnModel().getColumnCount() - 1);
        column.setCellRenderer(tableRenderer);
        jXTable1.setRowHeight(35);
    }

    @Override
    public void list(Agroquimico entity)
    {
        DialogAgroquimico dialog = new DialogAgroquimico(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("agroquimico.consulta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void newEntity()
    {      
    }

    @Override
    public void update(Agroquimico entity)
    {       
        DialogAgroquimico dialog = new DialogAgroquimico(getCurrentFrame());      
        dialog.setTitle(getResourceMap().getString("agroquimico.modificacion.title"));
        dialog.setVisible(true);
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
