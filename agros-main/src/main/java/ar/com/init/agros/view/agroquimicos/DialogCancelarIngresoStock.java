/*
 * DialogCancelarIngresoStock.java
 *
 * Created on 21/07/2009, 23:34:14
 */
package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.model.inventario.agroquimicos.CancelacionIngresoStock;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.agroquimicos.model.CancelarIngresoStockTableModel;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 *
 * @author gmatheu
 */
public class DialogCancelarIngresoStock extends javax.swing.JDialog
{

    private static final long serialVersionUID = -1L;
    private CancelarIngresoStockTableModel cancelarIngresoStockTableModel;

    /** Creates new form DialogCancelarIngresoStock */
    public DialogCancelarIngresoStock(java.awt.Frame parent)
    {
        super(parent, true);

        GUIUtility.initWindow(this);
        initComponents();

        try {
            cancelarIngresoStockTableModel = new CancelarIngresoStockTableModel();
            jXTableValores.setModel(cancelarIngresoStockTableModel);

            refreshTable();
            jXTableValores.packAll();

            oKCancelCleanPanel.setListenerToButtons(new OkCleanCancelEventControl());
            oKCancelCleanPanel.setOwner(this);

            cancelarIngresoStockTableModel.addTableModelListener(new TableModelListener()
            {

                @Override
                public void tableChanged(TableModelEvent e)
                {
                    frameNotifier.showOkMessage();
                    if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == CancelarIngresoStockTableModel.SELECCION_COLUMN_IDX) {
                        int idx = e.getFirstRow();
                        idx = jXTableValores.convertRowIndexToModel(idx);

                        if (!cancelarIngresoStockTableModel.validate(idx)) {
                            frameNotifier.showErrorMessage(
                                    "NO se puede cancelar el ingreso debido a que no hay stock suficiente en el depósito.");
                        }
                    }
                }
            });
        }
        catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(DialogMovimientoDeposito.class, e);
        }
    }

    private void clear()
    {
        refreshTable();
    }

    private void refreshTable()
    {
        frameNotifier.showOkMessage();
        if (cancelarIngresoStockTableModel != null) {
            cancelarIngresoStockTableModel.setDetallesIngreso(singleDateChooserFecha.getDate());
        }
    }

    private class OkCleanCancelEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel.getBtnAceptar()) {

                CancelacionIngresoStock c = new CancelacionIngresoStock();
                c.setFecha(singleDateChooserFecha.getDate());
                c.setCastedDetalles(cancelarIngresoStockTableModel.getCheckedDetalles());
                c.setObservaciones(panelObservacion.getObservacion());

                if (validateInput(c) && showConfirmMessage(DialogCancelarIngresoStock.this,
                        "Confirmación", "¿Desea confirmar la cancelación de los ingresos seleccionados?")) {
                    try {

                        MovimientoStockJpaController cancController = new MovimientoStockJpaController();
                        cancController.persist(c);

                        DialogCancelarIngresoStock.this.clear();
                        frameNotifier.showInformationMessage(
                                "Se cancelaron el/los movimientos");
                    }
                    catch (InvalidStateException ex) {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (ConstraintViolationException ex) {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex) {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                DialogCancelarIngresoStock.this.dispose();
            }
            if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
                DialogCancelarIngresoStock.this.clear();
            }
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        singleDateChooserFecha = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jLabelFecha = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableValores = new org.jdesktop.swingx.JXTable();
        panelObservacion = new ar.com.init.agros.view.components.panels.PanelObservacion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        frameNotifier.setName("frameNotifier"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        singleDateChooserFecha.setName("singleDateChooserFecha"); // NOI18N
        singleDateChooserFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                singleDateChooserFechaPropertyChange(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogCancelarIngresoStock.class);
        jLabelFecha.setText(resourceMap.getString("jLabelFecha.text")); // NOI18N
        jLabelFecha.setName("jLabelFecha"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableValores.setName("jXTableValores"); // NOI18N
        jScrollPane1.setViewportView(jXTableValores);

        panelObservacion.setName("panelObservacion"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(593, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
                    .addComponent(panelObservacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFecha)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void singleDateChooserFechaPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_singleDateChooserFechaPropertyChange
    {//GEN-HEADEREND:event_singleDateChooserFechaPropertyChange
        refreshTable();
    }//GEN-LAST:event_singleDateChooserFechaPropertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JLabel jLabelFecha;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableValores;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFecha;
    // End of variables declaration//GEN-END:variables
}
