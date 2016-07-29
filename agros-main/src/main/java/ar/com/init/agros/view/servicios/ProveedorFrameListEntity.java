package ar.com.init.agros.view.servicios;

import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import org.jdesktop.application.ResourceMap;

/**
 * Clase ProveedorFrameListEntity
 *
 *
 * @author fbobbio
 * @version 14-jun-2009 
 */
public class ProveedorFrameListEntity extends FrameListEntity<Servicio>
{
    private static final long serialVersionUID = -1L;  

    /** Constructor por defecto de ProveedorFrameListEntity */
    public ProveedorFrameListEntity(ResourceMap map)
    {
        super(Servicio.class, Servicio.TABLE_HEADERS);
        resourceMap = map;
    }    

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public void list(Servicio entity)
    {
        DialogProveedor dialog;
        if (entity != null)
        {
            dialog = new DialogProveedor(getCurrentFrame(), entity, false);
            dialog.setTitle(getResourceMap().getString("proveedor.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un servicio para consultar");
        }
    }

    @Override
    public void newEntity()
    {
        DialogProveedor dialog = new DialogProveedor(getCurrentFrame());
        dialog.setTitle(getResourceMap().getString("proveedor.alta.title"));
        dialog.setVisible(true);
    }

    @Override
    public void update(Servicio entity)
    {
        DialogProveedor dialog;
        if (entity != null)
        {
            dialog = new DialogProveedor(getCurrentFrame(), entity, true);
            dialog.setTitle(getResourceMap().getString("proveedor.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un servicio para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
