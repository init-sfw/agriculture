/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DialogMovimientoDeposito.java
 *
 * Created on 12/07/2009, 13:08:02
 */
package ar.com.init.agros.view.agroquimicos;

import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.agroquimicos.MovimientoDeposito;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.styles.ConditionalStyleCellRenderer;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.agroquimicos.model.MovimientoDepositoTableModel;
import ar.com.init.agros.view.components.editors.EditableColumnHighlighter;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import java.awt.event.ActionEvent;
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
public class DialogMovimientoDeposito extends javax.swing.JDialog implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private DepositoJpaController depositoController;
    private MovimientoDepositoTableModel movimientoDepositoTableModel;
    private boolean consulta;

    /** Creates new form DialogMovimientoDeposito */
    public DialogMovimientoDeposito(java.awt.Frame parent)
    {
        this(parent,null);
    }

    public DialogMovimientoDeposito(java.awt.Frame parent, MovimientoDeposito mov)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();
        UpdatableSubject.addUpdatableListener(this);
        oKCancelCleanPanel.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel.setOwner(this);

        try
        {
            jXTableAgroquimicos.setDefaultRenderer(ConditionalStyle.class, new ConditionalStyleCellRenderer());
            jXTableAgroquimicos.setDefaultEditor(Double.class, new ValorUnidadTableCellEditor(frameNotifier,
                    new DecimalInputVerifier(frameNotifier, false),
                    MovimientoDepositoTableModel.EXISTENTE_COLUMN_IDX));

            depositoController = new DepositoJpaController();
            movimientoDepositoTableModel = new MovimientoDepositoTableModel();
            jXTableAgroquimicos.setModel(movimientoDepositoTableModel);

            refreshUI();
            jXTableAgroquimicos.packAll();
        }
        catch (PersistenceException e)
        {
            if (frameNotifier != null)
            {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(DialogMovimientoDeposito.class, e);
        }

        if (mov != null) // Caso en el que no será utilizada como ventana de alta
        {
            consulta = true;
            setMovimientoDeposito(mov);
            disableFieldsAndButtons();
        }
    }

    private void refreshComboOrigen()
    {
        GUIUtility.refreshComboBox(depositoController.findEntities(), jComboBoxOrigen);
    }

    private void refreshComboDestino()
    {
        if (jComboBoxOrigen.getSelectedItem() instanceof Deposito)
        {
            GUIUtility.refreshComboBox(depositoController.findEntities(), jComboBoxDestino);
            jComboBoxDestino.removeItem(jComboBoxOrigen.getSelectedItem());
        }
        else
        {
            jComboBoxDestino.removeAllItems();
        }
    }

    private void refreshTable()
    {
        if (jComboBoxOrigen.getSelectedItem() instanceof Deposito)
        {
            AgroquimicoJpaController agroquimicoController = new AgroquimicoJpaController();
            movimientoDepositoTableModel.setAgroquimicos(agroquimicoController.findEntities(),
                    singleDateChooserFecha.getDate(), (Deposito) jComboBoxOrigen.getSelectedItem());

            //Poner foco en la celda para editar
            if (movimientoDepositoTableModel.getRowCount() > 0 && !consulta)
            {
                jXTableAgroquimicos.changeSelection(0, MovimientoDepositoTableModel.CANTIDAD_COLUMN_IDX, false,
                        false);
                jXTableAgroquimicos.requestFocus();
                jXTableAgroquimicos.setColumnSelectionInterval(0, MovimientoDepositoTableModel.CANTIDAD_COLUMN_IDX);
                jXTableAgroquimicos.setRowSelectionInterval(0, 0);
                jXTableAgroquimicos.editCellAt(0, MovimientoDepositoTableModel.CANTIDAD_COLUMN_IDX);
            }
        }
        else
        {
            movimientoDepositoTableModel.setRowCount(0);
        }
        setHighlighters();
    }

    private void clear()
    {
        refreshComboOrigen();
        refreshComboDestino();
        refreshTable();
        jComboBoxDestino.setSelectedIndex(-1);
        jComboBoxOrigen.setSelectedIndex(-1);
        panelObservacion.setObservacion("");
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private void disableFieldsAndButtons()
    {
        jComboBoxDestino.setEnabled(false);
        jComboBoxOrigen.setEnabled(false);
        singleDateChooserFecha.setEnabled(false);
        jXTableAgroquimicos.setEnabled(false);
        panelObservacion.disableFields();
    }

    private void setMovimientoDeposito(MovimientoDeposito mov)
    {
        singleDateChooserFecha.setDate(mov.getFecha());
        jComboBoxOrigen.setSelectedItem(mov.getDepositoOrigen());
        jComboBoxDestino.setSelectedItem(mov.getDepositoDestino());
        if (jComboBoxOrigen.getSelectedItem() instanceof Deposito)
        {
            refreshTable();
            if (!consulta)
            {
                movimientoDepositoTableModel.setDetalles(mov);
            }
            else
            {
                movimientoDepositoTableModel.setDetallesConsulta(mov);
            }
            jXTableAgroquimicos.getColumnExt(MovimientoDepositoTableModel.EXISTENTE_COLUMN_IDX).setVisible(false);
        }
        panelObservacion.setObservacion(mov.getObservaciones());
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
            if (e.getSource() == oKCancelCleanPanel.getBtnAceptar())
            {

                MovimientoDeposito m = new MovimientoDeposito();
                m.setFecha(singleDateChooserFecha.getDate());
                m.setObservaciones(panelObservacion.getObservacion());
                m.setCastedDetalles(
                        movimientoDepositoTableModel.getCheckedDetalles(
                        (jComboBoxDestino.getSelectedItem() instanceof Deposito) ? ((Deposito) jComboBoxDestino.getSelectedItem()) : null));
                if (jComboBoxDestino.getSelectedItem() instanceof Deposito)
                {
                    m.setDepositoDestino((Deposito) jComboBoxDestino.getSelectedItem());
                }
                else
                {
                    m.setDepositoDestino(null);
                }
                if (jComboBoxOrigen.getSelectedItem() instanceof Deposito)
                {
                    m.setDepositoOrigen((Deposito) jComboBoxOrigen.getSelectedItem());
                }
                else
                {
                    m.setDepositoDestino(null);
                }

                if (validateInput(m))
                {
                    try
                    {
                        List<BaseEntity> invalidDetalles = validateInput(m.getDetalles());
                        if (invalidDetalles.size() == 0 && showConfirmMessage(DialogMovimientoDeposito.this,
                                "Confirmación", "¿Desea confirmar los movimientos realizados?"))
                        {

                            MovimientoStockJpaController movController = new MovimientoStockJpaController();
                            movController.persist(m);

                            frameNotifier.showInformationMessage(
                                    "Se registro el/los movimientos");
                            DialogMovimientoDeposito.this.clear();
                        }
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (ConstraintViolationException ex)
                    {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogMovimientoDeposito.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (e.getSource() == oKCancelCleanPanel.getBtnCancelar())
            {
                DialogMovimientoDeposito.this.dispose();
            }
            if (e.getSource() == oKCancelCleanPanel.getBtnClean())
            {
                DialogMovimientoDeposito.this.clear();
            }
        }
    };

    @Override
    public void refreshUI()
    {
        refreshComboOrigen();
        refreshTable();
    }

    private void setHighlighters()
    {
        jXTableAgroquimicos.setHighlighters(new EditableColumnHighlighter(
                MovimientoDepositoTableModel.CANTIDAD_COLUMN_IDX));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        panelObservacion = new ar.com.init.agros.view.components.panels.PanelObservacion();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableAgroquimicos = new org.jdesktop.swingx.JXTable();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jLabelFecha = new javax.swing.JLabel();
        jLabelOrigen = new javax.swing.JLabel();
        jLabelDestino = new javax.swing.JLabel();
        singleDateChooserFecha = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jComboBoxOrigen = new javax.swing.JComboBox();
        jComboBoxDestino = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        panelObservacion.setName("panelObservacion"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableAgroquimicos.setName("jXTableAgroquimicos"); // NOI18N
        jXTableAgroquimicos.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableAgroquimicos);

        frameNotifier.setName("frameNotifier"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogMovimientoDeposito.class);
        jLabelFecha.setText(resourceMap.getString("jLabelFecha.text")); // NOI18N
        jLabelFecha.setName("jLabelFecha"); // NOI18N

        jLabelOrigen.setText(resourceMap.getString("jLabelOrigen.text")); // NOI18N
        jLabelOrigen.setName("jLabelOrigen"); // NOI18N

        jLabelDestino.setText(resourceMap.getString("jLabelDestino.text")); // NOI18N
        jLabelDestino.setName("jLabelDestino"); // NOI18N

        singleDateChooserFecha.setName("singleDateChooserFecha"); // NOI18N

        jComboBoxOrigen.setName("jComboBoxOrigen"); // NOI18N
        jComboBoxOrigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOrigenActionPerformed(evt);
            }
        });

        jComboBoxDestino.setName("jComboBoxDestino"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            .addComponent(panelObservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelOrigen)
                    .addComponent(jLabelDestino)
                    .addComponent(jLabelFecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jComboBoxDestino, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxOrigen, javax.swing.GroupLayout.Alignment.LEADING, 0, 262, Short.MAX_VALUE)))
                .addContainerGap(220, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelFecha)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOrigen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDestino)
                    .addComponent(jComboBoxDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(panelObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxOrigenActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxOrigenActionPerformed
    {//GEN-HEADEREND:event_jComboBoxOrigenActionPerformed
        refreshComboDestino();
        refreshTable();
    }//GEN-LAST:event_jComboBoxOrigenActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JComboBox jComboBoxDestino;
    private javax.swing.JComboBox jComboBoxOrigen;
    private javax.swing.JLabel jLabelDestino;
    private javax.swing.JLabel jLabelFecha;
    private javax.swing.JLabel jLabelOrigen;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableAgroquimicos;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFecha;
    // End of variables declaration//GEN-END:variables
}
