/*
 * DialogAjusteInventario.java
 *
 * Created on 27/06/2009, 19:29:26
 */
package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.util.gui.styles.ConditionalStyleCellRenderer;
import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.inventario.agroquimicos.AjusteInventario;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleAjusteInventario;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIConstants;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.styles.ConditionalStyle;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.agroquimicos.model.AjusteInventarioTableModel;
import ar.com.init.agros.view.components.editors.EditableColumnHighlighter;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 *
 * @author gmatheu
 */
public class DialogAjusteInventario extends javax.swing.JDialog implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private AgroquimicoJpaController agroquimicoJpaController;

    /** Creates new form DialogAjusteInventario */
    public DialogAjusteInventario(java.awt.Frame parent)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();

        try {
            UpdatableSubject.addUpdatableListener(this);

            oKCancelCleanPanel.setListenerToButtons(new EventControl());
            oKCancelCleanPanel.setOwner(this);

            jXTableAjustes.setDefaultRenderer(ConditionalStyle.class, new ConditionalStyleCellRenderer());
            jXTableAjustes.setDefaultEditor(Double.class, new ValorUnidadTableCellEditor(frameNotifier,
                    new DecimalInputVerifier(frameNotifier, false)));
            jXTableAjustes.setRowHeight(GUIConstants.TEXTAREA_ROW_HEIGHT);
            jXTableAjustes.setModel(ajusteInventarioTableModel);

            jXTableResumen.setDefaultRenderer(ConditionalStyle.class, new ConditionalStyleCellRenderer());
            jXTableResumen.setModel(resumenAjusteInventarioTableModel);
            agroquimicoJpaController = new AgroquimicoJpaController();

//            final InputMap im = jXTableAjustes.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//            final KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
//            im.put(key, "selectNextColumnCell");

            refreshUI();
            jXTableAjustes.packAll();
            jXTableResumen.packAll();
        }
        catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(DialogAjusteInventario.class, e);
        }
    }

    private void crearResumen()
    {
        resumenAjusteInventarioTableModel.setAjustesInventario(
                ajusteInventarioTableModel.getAjustesInventario());
    }

    private void refreshTable()
    {
        if (jComboBoxDeposito.getSelectedItem() instanceof Deposito) {
            ajusteInventarioTableModel.setAgroquimicos(agroquimicoJpaController.findEntities(),
                    singleDateChooserFecha.getDate(), (Deposito) jComboBoxDeposito.getSelectedItem());

            if (ajusteInventarioTableModel.getRowCount() > 0) {
                jXTableAjustes.changeSelection(0, AjusteInventarioTableModel.DEPOSITO_COLUMN_IDX,
                        false,
                        false);
                jXTableAjustes.requestFocus();
                jXTableAjustes.setColumnSelectionInterval(0,
                        AjusteInventarioTableModel.DEPOSITO_COLUMN_IDX);
                jXTableAjustes.setRowSelectionInterval(0, 0);
                jXTableAjustes.editCellAt(0, AjusteInventarioTableModel.DEPOSITO_COLUMN_IDX);
            }
        }
        else {
            ajusteInventarioTableModel.setRowCount(0);
        }
        resumenAjusteInventarioTableModel.clearMovimientos();
        setHighlighters();
    }

    private void refreshCombo()
    {
        BaseEntityJpaController<Deposito> depositoController = new BaseEntityJpaController<Deposito>(
                Deposito.class);
        GUIUtility.refreshComboBox(depositoController.findEntities(), jComboBoxDeposito);
    }

    @Override
    public void refreshUI()
    {
        refreshTable();
        refreshCombo();
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private class EventControl extends AbstractEventControl
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

                if (jTabbedPane.getSelectedComponent().equals(jPanelAgroquimicos)) {
                    jTabbedPane.setSelectedComponent(jPanelResumen);
                }
                else if (jTabbedPane.getSelectedComponent().equals(jPanelResumen)) {
                    List<DetalleAjusteInventario> ajustesRealizados =
                            resumenAjusteInventarioTableModel.getCheckedMovimientos();

                    if (ajustesRealizados.size() > 0 && showConfirmMessage(DialogAjusteInventario.this,
                            "Confirmación", "¿Desea confirmar los ajustes realizados?")) {

                        AjusteInventario ai = new AjusteInventario();
                        ai.setFecha(singleDateChooserFecha.getDate());
                        ai.setCastedDetalles(ajustesRealizados);

                        MovimientoStockJpaController ajusteController = new MovimientoStockJpaController();
                        try {
                            ajusteController.persist(ai);

                            refreshTable();
                            jTabbedPane.setSelectedComponent(jPanelAgroquimicos);
                            frameNotifier.showInformationMessage(
                                    "Se realizaron los ajustes correspondientes y se guardaron los nuevos datos con éxito");
                        }
                        catch (InvalidStateException ex) {
                            frameNotifier.showInformationMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogAjusteInventario.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        }
                        catch (ConstraintViolationException ex) {
                            frameNotifier.showInformationMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogAjusteInventario.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        }
                        catch (Exception ex) {
                            frameNotifier.showInformationMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogAjusteInventario.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        }
                    }
                }
                else if (jTabbedPane.getSelectedComponent().equals(jPanelAgroquimicos)) {
                    refreshTable();
                }
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                DialogAjusteInventario.this.dispose();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
                singleDateChooserFecha.setDate(new Date());
                jComboBoxDeposito.setSelectedItem(GUIUtility.DEFAULT_COMBO_VALUE);
            }
        }
    }

    private void setHighlighters()
    {
        jXTableAjustes.setHighlighters(new EditableColumnHighlighter(
                AjusteInventarioTableModel.DEPOSITO_COLUMN_IDX, AjusteInventarioTableModel.MOTIVO_COLUMN_IDX));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ajusteInventarioTableModel = new ar.com.init.agros.view.agroquimicos.model.AjusteInventarioTableModel();
        resumenAjusteInventarioTableModel = new ar.com.init.agros.view.agroquimicos.model.ResumenAjusteInventarioTableModel();
        textAreaColumnTableRenderer = new ar.com.init.agros.view.agroquimicos.model.TextAreaColumnTableRenderer();
        listableComboBoxRenderer = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelAgroquimicos = new javax.swing.JPanel();
        singleDateChooserFecha = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jLabelFecha = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableAjustes = new org.jdesktop.swingx.JXTable();
        jComboBoxDeposito = new javax.swing.JComboBox();
        jLabelDeposito = new javax.swing.JLabel();
        jPanelResumen = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTableResumen = new org.jdesktop.swingx.JXTable();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogAjusteInventario.class);
        textAreaColumnTableRenderer.setText(resourceMap.getString("textAreaColumnTableRenderer.text")); // NOI18N
        textAreaColumnTableRenderer.setName("textAreaColumnTableRenderer"); // NOI18N

        listableComboBoxRenderer.setText(resourceMap.getString("listableComboBoxRenderer.text")); // NOI18N
        listableComboBoxRenderer.setName("listableComboBoxRenderer"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("Form"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        frameNotifier.setName("frameNotifier"); // NOI18N

        jTabbedPane.setName("jTabbedPane"); // NOI18N
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });
        jTabbedPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTabbedPanePropertyChange(evt);
            }
        });

        jPanelAgroquimicos.setName("jPanelAgroquimicos"); // NOI18N

        singleDateChooserFecha.setName("singleDateChooserFecha"); // NOI18N
        singleDateChooserFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                singleDateChooserFechaPropertyChange(evt);
            }
        });

        jLabelFecha.setText(resourceMap.getString("jLabelFecha.text")); // NOI18N
        jLabelFecha.setName("jLabelFecha"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableAjustes.setName("jXTableAjustes"); // NOI18N
        jXTableAjustes.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableAjustes);

        jComboBoxDeposito.setName("jComboBoxDeposito"); // NOI18N
        jComboBoxDeposito.setRenderer(listableComboBoxRenderer);
        jComboBoxDeposito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDepositoActionPerformed(evt);
            }
        });

        jLabelDeposito.setText(resourceMap.getString("jLabelDeposito.text")); // NOI18N
        jLabelDeposito.setName("jLabelDeposito"); // NOI18N

        javax.swing.GroupLayout jPanelAgroquimicosLayout = new javax.swing.GroupLayout(jPanelAgroquimicos);
        jPanelAgroquimicos.setLayout(jPanelAgroquimicosLayout);
        jPanelAgroquimicosLayout.setHorizontalGroup(
            jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAgroquimicosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAgroquimicosLayout.createSequentialGroup()
                        .addGroup(jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDeposito)
                            .addComponent(jLabelFecha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelAgroquimicosLayout.createSequentialGroup()
                                .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83))
                            .addComponent(jComboBoxDeposito, 0, 479, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelAgroquimicosLayout.setVerticalGroup(
            jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAgroquimicosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelAgroquimicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxDeposito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDeposito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane.addTab(resourceMap.getString("jPanelAgroquimicos.TabConstraints.tabTitle"), jPanelAgroquimicos); // NOI18N

        jPanelResumen.setName("jPanelResumen"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jXTableResumen.setName("jXTableResumen"); // NOI18N
        jXTableResumen.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jXTableResumen);

        javax.swing.GroupLayout jPanelResumenLayout = new javax.swing.GroupLayout(jPanelResumen);
        jPanelResumen.setLayout(jPanelResumenLayout);
        jPanelResumenLayout.setHorizontalGroup(
            jPanelResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelResumenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelResumenLayout.setVerticalGroup(
            jPanelResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelResumenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane.addTab(resourceMap.getString("jPanelResumen.TabConstraints.tabTitle"), jPanelResumen); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPanePropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_jTabbedPanePropertyChange
    {//GEN-HEADEREND:event_jTabbedPanePropertyChange
}//GEN-LAST:event_jTabbedPanePropertyChange

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jTabbedPaneStateChanged
    {//GEN-HEADEREND:event_jTabbedPaneStateChanged
        if (jTabbedPane.getSelectedComponent().equals(jPanelResumen)) {
            crearResumen();
        }
}//GEN-LAST:event_jTabbedPaneStateChanged

    private void singleDateChooserFechaPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_singleDateChooserFechaPropertyChange
    {//GEN-HEADEREND:event_singleDateChooserFechaPropertyChange
        if (agroquimicoJpaController != null && ajusteInventarioTableModel != null) {
            refreshTable();
        }
    }//GEN-LAST:event_singleDateChooserFechaPropertyChange

    private void jComboBoxDepositoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxDepositoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxDepositoActionPerformed
        refreshTable();
    }//GEN-LAST:event_jComboBoxDepositoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.view.agroquimicos.model.AjusteInventarioTableModel ajusteInventarioTableModel;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JComboBox jComboBoxDeposito;
    private javax.swing.JLabel jLabelDeposito;
    private javax.swing.JLabel jLabelFecha;
    private javax.swing.JPanel jPanelAgroquimicos;
    private javax.swing.JPanel jPanelResumen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane;
    private org.jdesktop.swingx.JXTable jXTableAjustes;
    private org.jdesktop.swingx.JXTable jXTableResumen;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    private ar.com.init.agros.view.agroquimicos.model.ResumenAjusteInventarioTableModel resumenAjusteInventarioTableModel;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFecha;
    private ar.com.init.agros.view.agroquimicos.model.TextAreaColumnTableRenderer textAreaColumnTableRenderer;
    // End of variables declaration//GEN-END:variables
    }
