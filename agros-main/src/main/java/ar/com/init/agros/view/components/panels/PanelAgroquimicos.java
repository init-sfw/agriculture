/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelAgroquimicos.java
 *
 * Created on 12-jun-2009, 10:35:44
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.view.components.*;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.DetallePlanificacion;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.namedentities.FrameNamedEntity;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.DoubleTableCellEditor;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.agroquimicos.model.DetallePlanificacionTableModel;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.jdesktop.application.Action;

/**
 *
 * @author fbobbio
 */
public class PanelAgroquimicos extends javax.swing.JPanel implements SuperficieListener, UpdatableListener
{

    private BaseEntityJpaController<Agroquimico> jpaControllerAgroquimico;
    private NamedEntityJpaController<MomentoAplicacion> jpaControllerMomento;
    private List<Agroquimico> listaAgroquimicos;
    private List<MomentoAplicacion> momentosDeAplicacion;
    private DetallePlanificacion detalle;
    private FrameNotifier frameNotifier1;
    private double superficiePlanificada;
    private DetallePlanificacionTableModel detallePlanificacionTableModel1;

    /** Creates new form PanelAgroquimicos */
    public PanelAgroquimicos()
    {
        jpaControllerAgroquimico = new BaseEntityJpaController<Agroquimico>(Agroquimico.class);
        jpaControllerMomento = new NamedEntityJpaController<MomentoAplicacion>(MomentoAplicacion.class);
        detallePlanificacionTableModel1 = new DetallePlanificacionTableModel();
        initComponents();
        jXTableDetalles.setModel(detallePlanificacionTableModel1);
        jXTableDetalles.setDefaultEditor(Double.class, new DoubleTableCellEditor(frameNotifier1));
        jXTableDetalles.setDefaultEditor(ValorUnidad.class, new ValorUnidadTableCellEditor(frameNotifier1));
        UpdatableSubject.addUpdatableListener(this);
        dosisPorHectarea.setEnabled(true, false); 
        panelValorMonedaCostoPlanificado.setEnabled(false);
        addRemoveUpdatePanel1.addActionListener(new AddRemoveUpdateEvent());
        addRemoveUpdatePanel1.setVisible(true, true, true, false);
        addRemoveUpdatePanel1.setBtnRemoveEnabled(false);

        try
        {
            refreshUI();
            jXTableDetalles.packAll();
        }
        catch (PersistenceException e)
        {
            if (frameNotifier1 != null)
            {
                frameNotifier1.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelAgroquimicos.class, e);
        }

        jXTableDetalles.getModel().addTableModelListener(new TableModelListener()
        {

            @Override
            public void tableChanged(TableModelEvent e)
            {
                try
                {
                    panelValorMonedaCostoPlanificado.setValorMoneda(new ValorMoneda(PlanificacionAgroquimico.calcularCostoPlanificado(getDetallesCargados())));
                }
                catch (PersistenceException ex)
                {
                    if (frameNotifier1 != null)
                    {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                    }
                    GUIUtility.logPersistenceError(PanelAgroquimicos.class, ex);
                }
            }
        });

        ListSelectionModel rowsModel = jXTableDetalles.getSelectionModel();
        rowsModel.addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting())
                {
                    return;
                }
                if (jXTableDetalles.getSelectedRows().length > 0)
                {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(true);
                }
                else
                {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
                }
            }
        });
    }

    public void disableFields()
    {
        cbxAgroquimicos.setEnabled(false);
        cbxMomentoDeAplicacion.setEnabled(false);
        panelValorMoneda1.setEnabled(false);
        jXTableDetalles.setEnabled(false);
        addRemoveUpdatePanel1.setEnabled(false);
        dosisPorHectarea.setEnabled(false, false);
    }

    public double getSuperficiePlanificada()
    {
        return superficiePlanificada;
    }

    public void setSuperficiePlanificada(double superficiePlanificada)
    {
        this.superficiePlanificada = superficiePlanificada;
        reCalculateDetailsQuantities();
    }

    public List<DetallePlanificacion> getDetallesCargados()
    {
        return detallePlanificacionTableModel1.getData();
    }

    public void setDetallesCargados(List<DetallePlanificacion> detallesCargados)
    {
        detallePlanificacionTableModel1.setData(detallesCargados);
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier1;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier1 = frameNotifier;
        panelValorMoneda1.setFrameNotifier(frameNotifier1);
        dosisPorHectarea.setFrameNotifier(frameNotifier1);
    }

    private void findMomentosDeAplicacion()
    {
        momentosDeAplicacion = jpaControllerMomento.findEntities();
    }

    private Agroquimico getSelectedAgroquimico()
    {
        Object aux = cbxAgroquimicos.getSelectedItem();
        if (aux instanceof Agroquimico)
        {
            return (Agroquimico) aux;
        }
        else
        {
            return null;
        }
    }

    private MomentoAplicacion getSelectedMomento()
    {
        Object aux = cbxMomentoDeAplicacion.getSelectedItem();
        if (aux instanceof MomentoAplicacion)
        {
            return (MomentoAplicacion) aux;
        }
        else
        {
            return null;
        }
    }

    /** Metodo que recalcula los valores de cantidad de todos los detalles cada vez q se cambia la superficie */
    private void reCalculateDetailsQuantities()
    {
        for (int i = 0; i < getDetallesCargados().size(); i++)
        {
            DetallePlanificacion det = getDetallesCargados().get(i);
            det.setSuperficiePlanificada(superficiePlanificada);
            detallePlanificacionTableModel1.update(det);
        }
        jXTableDetalles.repaint();
    }

    public void clear()
    {
        detalle = null;
        cbxAgroquimicos.setSelectedIndex(0);
        cbxMomentoDeAplicacion.setSelectedIndex(0);
        dosisPorHectarea.clear(true, false);
        panelValorMoneda1.clear();
        detallePlanificacionTableModel1.setData(new ArrayList<DetallePlanificacion>());
    }

    private void refresh()
    {
        detalle = null;
        cbxAgroquimicos.setSelectedIndex(0);
        cbxMomentoDeAplicacion.setSelectedIndex(0);
        dosisPorHectarea.clear(true, false);
        panelValorMoneda1.clear();
    }

    private DetallePlanificacion getDetalle()
    {
        if (detalle == null)
        {
            detalle =  new DetallePlanificacion();
        }
        detalle.setAgroquimico(getSelectedAgroquimico());
        detalle.setDosisPorHectarea(dosisPorHectarea.getValorUnidad());
        detalle.setPrecioReferencia(panelValorMoneda1.getValorMoneda());
        detalle.setMomentoAplicacion(getSelectedMomento());
        detalle.setSuperficiePlanificada(superficiePlanificada);
        return detalle;
    }

    private void refreshAgroquimicos()
    {
        GUIUtility.refreshComboBox(listaAgroquimicos, cbxAgroquimicos);
    }

    public void refreshTablaAgroquimicos(List<DetallePlanificacion> detalles)
    {
        detallePlanificacionTableModel1.setData(detalles);
    }

    private void refreshMomentos()
    {
        GUIUtility.refreshComboBox(momentosDeAplicacion, cbxMomentoDeAplicacion);
    }

    private void refreshUnidades()
    {
        dosisPorHectarea.showAllUnidades();
    }

    private void findAgroquimicos()
    {
        listaAgroquimicos = jpaControllerAgroquimico.findEntities();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableComboBoxRenderer1 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        listableTableRenderer1 = new ar.com.init.agros.util.gui.ListableTableRenderer();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbxAgroquimicos = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableDetalles = new org.jdesktop.swingx.JXTable();
        jLabel5 = new javax.swing.JLabel();
        dosisPorHectarea = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jLabel3 = new javax.swing.JLabel();
        cbxMomentoDeAplicacion = new javax.swing.JComboBox();
        btnAgregarMomento = new javax.swing.JButton();
        panelValorMoneda1 = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        addRemoveUpdatePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel();
        panelValorMonedaCostoPlanificado = new ar.com.init.agros.view.components.valores.PanelValorMoneda();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelAgroquimicos.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        listableTableRenderer1.setText(resourceMap.getString("listableTableRenderer1.text")); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        cbxAgroquimicos.setRenderer(listableComboBoxRenderer1);
        cbxAgroquimicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAgroquimicosActionPerformed(evt);
            }
        });

        jXTableDetalles.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableDetalles);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        cbxMomentoDeAplicacion.setRenderer(listableComboBoxRenderer1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(PanelAgroquimicos.class, this);
        btnAgregarMomento.setAction(actionMap.get("addMomentoDeAplicacion")); // NOI18N
        btnAgregarMomento.setText(resourceMap.getString("btnAgregarMomento.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnAgregarMomento, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbxAgroquimicos, 0, 125, Short.MAX_VALUE)
                                    .addComponent(cbxMomentoDeAplicacion, 0, 125, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panelValorMoneda1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(dosisPorHectarea, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)))
                            .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(385, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelValorMonedaCostoPlanificado, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbxAgroquimicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2))
                    .addComponent(dosisPorHectarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cbxMomentoDeAplicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregarMomento))
                    .addComponent(panelValorMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(panelValorMonedaCostoPlanificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbxAgroquimicosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxAgroquimicosActionPerformed
    {//GEN-HEADEREND:event_cbxAgroquimicosActionPerformed
        if (cbxAgroquimicos.getSelectedItem() instanceof Agroquimico)
        {
            addRemoveUpdatePanel1.setBtnAddEnabled(true);
            Agroquimico a = (Agroquimico) cbxAgroquimicos.getSelectedItem();
            dosisPorHectarea.selectUnidad(a.getUnidad());
        }
        else
        {
            addRemoveUpdatePanel1.setBtnAddEnabled(false);
        }
    }//GEN-LAST:event_cbxAgroquimicosActionPerformed


    @Action
    public void addMomentoDeAplicacion()
    {
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelAgroquimicos.class);
        FrameNamedEntity<MomentoAplicacion> f = new FrameNamedEntity<MomentoAplicacion>((Frame) SwingUtilities.getRoot(this), true, MomentoAplicacion.class);
        f.setTitle(resourceMap.getString("title"));
        f.setVisible(true);
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    class AddRemoveUpdateEvent extends AbstractEventControl
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonAdd())
            {
                if (validateInput(getDetalle()))
                {
                    detallePlanificacionTableModel1.add(detalle);
                    refresh();
                }
            }
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonRemove())
            {
                detallePlanificacionTableModel1.removeRows(GUIUtility.getModelSelectedRows(jXTableDetalles));
            }
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonClean())
            {
                clear();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel addRemoveUpdatePanel1;
    private javax.swing.JButton btnAgregarMomento;
    private javax.swing.JComboBox cbxAgroquimicos;
    private javax.swing.JComboBox cbxMomentoDeAplicacion;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad dosisPorHectarea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableDetalles;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.ListableTableRenderer listableTableRenderer1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMoneda1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaCostoPlanificado;
    // End of variables declaration//GEN-END:variables

    @Override
    public void updateSuperficie(double sup)
    {
        setSuperficiePlanificada(sup);
    }

    @Override
    public void refreshUI()
    {
        findAgroquimicos();
        refreshAgroquimicos();
        findMomentosDeAplicacion();
        refreshMomentos();
        refreshUnidades();
    }
}
