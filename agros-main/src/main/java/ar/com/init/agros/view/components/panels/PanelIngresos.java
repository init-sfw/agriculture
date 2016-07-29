/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelIngresos.java
 *
 * Created on 22-nov-2009, 11:24:35
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ingreso.Ingreso;
import ar.com.init.agros.model.ingreso.TipoIngreso;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author fbobbio
 */
public class PanelIngresos extends javax.swing.JPanel implements UpdatableListener
{

    private NamedEntityJpaController<TipoIngreso> tipoIngresoJpaController;
    private TablizableEntityDataModel<Ingreso> ingresoTableModel;
    private FrameNotifier frameNotifier;
    private Ingreso ingreso;

    /** Creates new form PanelIngresos */
    public PanelIngresos()
    {
        initComponents();
        tipoIngresoJpaController = new NamedEntityJpaController<TipoIngreso>(TipoIngreso.class);
        UpdatableSubject.addUpdatableListener(this);
        addRemovePanel1.addActionListener(new AddRemoveEventControl());
        addRemovePanel1.setBtnRemoveEnabled(false);
        ingresoTableModel = new TablizableEntityDataModel<Ingreso>(Ingreso.TABLE_HEADERS);
        jXTableIngresos.setModel(ingresoTableModel);

        jXTableIngresos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTableIngresos.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (jXTableIngresos.getSelectedRow() > -1) {
                    addRemovePanel1.setBtnRemoveEnabled(true);
                }
                else {
                    addRemovePanel1.setBtnRemoveEnabled(false);
                }
            }
        });
        refreshUI();
        jXTableIngresos.packAll();
    }

    public void clear()
    {
        ingresoTableModel.setData(new ArrayList<Ingreso>());
        panelValorMoneda1.clear();
        jComboBoxTipoIngreso.setSelectedIndex(0);
    }

    public List<Ingreso> getIngresosCargados()
    {
        return ingresoTableModel.getData();
    }

    public void setIngresosCargados(List<Ingreso> ingresos)
    {
        ingresoTableModel.setData(ingresos);
    }

    public void disableFields()
    {
        jComboBoxTipoIngreso.setEnabled(false);
        panelValorMoneda1.setEnabled(false);
        addRemovePanel1.setEnabled(false);
        jXTableIngresos.setEnabled(false);
    }

    private Ingreso getIngreso()
    {
        if (ingreso == null) {
            ingreso = new Ingreso();
        }
        ingreso.setImporte(getImporte());
        ingreso.setTipoIngreso(getTipoIngreso());
        return ingreso;
    }

    private void setIngreso(Ingreso c)
    {
        panelValorMoneda1.setValorMoneda(new ValorMoneda(c.getImporte().getMonto()));
        jComboBoxTipoIngreso.setSelectedItem(c.getTipoIngreso());
        ingreso = c;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
        panelValorMoneda1.setFrameNotifier(this.frameNotifier);
    }

    private ValorMoneda getImporte()
    {
        return panelValorMoneda1.getValorMoneda();
    }

    private TipoIngreso getTipoIngreso()
    {
        if (jComboBoxTipoIngreso.getSelectedItem() instanceof TipoIngreso) {
            return (TipoIngreso) jComboBoxTipoIngreso.getSelectedItem();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private class AddRemoveEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemovePanel1.getJButtonAdd()) {
                if (validateInput(getIngreso()) && !exists()) {
                    ingresoTableModel.add(ingreso);
                    frameNotifier.showInformationMessage("Se cargó el ingreso con éxito");
                    clearFields();
                }
            }
            else if (e.getSource() == addRemovePanel1.getJButtonRemove()) {
                ingresoTableModel.removeRows(GUIUtility.getModelSelectedRows(jXTableIngresos));
            }
            else if (e.getSource() == addRemovePanel1.getJButtonClean()) {
                clearFields();
            }
        }

        private boolean exists()
        {
            for (Ingreso c : ingresoTableModel.getData()) {
                if (c.equalToEntity(ingreso)) {
                    frameNotifier.showErrorMessage("Ya existe un ingreso con los mismos valores");
                    return true;
                }
            }
            return false;
        }
    }

    private void clearFields()
    {
        panelValorMoneda1.clear();
        jComboBoxTipoIngreso.setSelectedIndex(-1);
        ingreso = null;
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
        jLabel1 = new javax.swing.JLabel();
        jComboBoxTipoIngreso = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        addRemovePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableIngresos = new org.jdesktop.swingx.JXTable();
        panelValorMoneda1 = new ar.com.init.agros.view.components.valores.PanelValorMoneda();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelIngresos.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N
        listableComboBoxRenderer1.setName("listableComboBoxRenderer1"); // NOI18N

        setName("Form"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jComboBoxTipoIngreso.setName("jComboBoxTipoIngreso"); // NOI18N
        jComboBoxTipoIngreso.setNextFocusableComponent(panelValorMoneda1.getjTextFieldMonto());
        jComboBoxTipoIngreso.setRenderer(listableComboBoxRenderer1);
        jComboBoxTipoIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoIngresoActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        addRemovePanel1.setName("addRemovePanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableIngresos.setName("jXTableIngresos"); // NOI18N
        jXTableIngresos.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableIngresos);

        panelValorMoneda1.setName("panelValorMoneda1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelValorMoneda1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(jComboBoxTipoIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(addRemovePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jComboBoxTipoIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(panelValorMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRemovePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxTipoIngresoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxTipoIngresoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxTipoIngresoActionPerformed
        if (jComboBoxTipoIngreso.getSelectedItem() instanceof TipoIngreso)
        {
            TipoIngreso t = (TipoIngreso) jComboBoxTipoIngreso.getSelectedItem();
            panelValorMoneda1.setLabel(t.unidadMedida());
        }
        else
        {
            panelValorMoneda1.setLabel(null);
        } 
    }//GEN-LAST:event_jComboBoxTipoIngresoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemovePanel addRemovePanel1;
    private javax.swing.JComboBox jComboBoxTipoIngreso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableIngresos;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMoneda1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshUI()
    {
        try {
            GUIUtility.refreshComboBox(tipoIngresoJpaController.findEntities(), jComboBoxTipoIngreso);
        }
        catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelAgroquimicos.class, e);
        }
    }
}
