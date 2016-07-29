package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import ar.com.init.agros.view.agroquimicos.renderers.CasafeLogoTableCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.jdesktop.application.ResourceMap;

/**
 * Clase InformacionAgroquimicoFrameListEntity
 *
 *
 * @author gmatheu
 * @version 27/07/2009 
 */
public class InformacionAgroquimicoFrameListEntity extends FrameListEntity<InformacionAgroquimico>
{

    private static final long serialVersionUID = -1L;
    private int detallesIndex = jXTable1.getColumnModel().getColumnIndex("Detalle");

    /** Constructor por defecto de ProveedorFrameListEntity */
    public InformacionAgroquimicoFrameListEntity(BaseEntityJpaController<InformacionAgroquimico> jpaController, ResourceMap map)
    {
        super(InformacionAgroquimico.class, jpaController, InformacionAgroquimico.TABLE_HEADERS);
        setResourceMap(map);
        setVisibleButtons(true, true, true, true, true);
        btnConsultar.setEnabled(true);
        btnEliminar.setEnabled(false);
        btnModificar.setEnabled(false);

        //Seteo el renderer para el ícono de CASAFE
        CasafeLogoTableCellRenderer tableRenderer = new CasafeLogoTableCellRenderer();
        TableColumn column = jXTable1.getColumnModel().getColumn(detallesIndex);
        column.setCellRenderer(tableRenderer);
        jXTable1.setRowHeight(35);

        jXTable1.getSelectionModel().removeListSelectionListener(listSelectionListener);
        jXTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTable1.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {
                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        if (e.getValueIsAdjusting())
                                        {
                                            return;
                                        }
                                    //checkeo si es un agroquim ingresado manualmente
                                        if (jXTable1.getSelectedRow() > -1 && jXTable1.getRowCount() > 0 && isManual())
                                        {
                                            btnEliminar.setEnabled(true);
                                            btnModificar.setEnabled(true);
                                        }
                                        else
                                        {
                                            btnEliminar.setEnabled(false);
                                            btnModificar.setEnabled(false);
                                        }
                                }
                        });
    }

    private boolean isManual()
    {
        InformacionAgroquimico ia = tableModel.getData().get(GUIUtility.getModelRowIndex(jXTable1, jXTable1.getSelectedRow()));
        boolean ret;
        if (ia.isIngresoManual() != null)
        {
            ret = ia.isIngresoManual();
        }
        else
        {
            ret = false;
        }//Boolean.parseBoolean((String)jXTable1.getModel().getValueAt(GUIUtility.getModelRowIndex(jXTable1,jXTable1.getSelectedRow()), detallesIndex));
        return ret;
    }

    @Override
    public void doDoubleClick()
    {
       doList();
    }

    @Override
    public void list(InformacionAgroquimico entity)
    {
        if (isManual())
        {
            DialogAgroquimicoManual dialog;
            dialog = new DialogAgroquimicoManual(getCurrentFrame(), entity, false);
            dialog.setTitle(getResourceMap().getString("agroquimico.consulta.title"));
            dialog.setVisible(true);
        }
        else
        {
            DialogInformacionAgroquimico dialog = new DialogInformacionAgroquimico(getCurrentFrame(), entity);
            dialog.setTitle(getResourceMap().getString("agroquimico.informacion.consulta.title"));
            dialog.setVisible(true);
        }
    }

    @Override
    public void newEntity()
    {
        DialogAgroquimicoManual d = new DialogAgroquimicoManual(getCurrentFrame(), null, false);
        d.setTitle(getResourceMap().getString("agroquimico.registrar.title"));
        d.setVisible(true);
    }

    @Override
    public void update(InformacionAgroquimico entity)
    {
        DialogAgroquimicoManual dialog;
        if (entity != null) {
            dialog = new DialogAgroquimicoManual(getCurrentFrame(), entity, true);
            dialog.setTitle(getResourceMap().getString("agroquimico.modificacion.title"));
            dialog.setVisible(true);
        }
        else
        {
            showErrorMessage("No se ha seleccionado un agroquímico para modificar");
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
