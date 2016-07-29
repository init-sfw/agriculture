/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelCostos.java
 *
 * Created on 22-jul-2009, 15:25:04
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.controller.ServicioJpaController;
import ar.com.init.agros.controller.TipoCostoController;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.costo.Costo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author fbobbio
 */
public class PanelCostos extends javax.swing.JPanel implements UpdatableListener
{
    protected TipoCostoController tipoCostoController;
    protected ServicioJpaController servicioJpaController;
    private TablizableEntityDataModel<Costo> costoTableModel;
    protected FrameNotifier frameNotifier;
    private Costo costo;

    /** Creates new form PanelCostos */
    public PanelCostos()
    {
        try
        {
            tipoCostoController = new TipoCostoController();
            servicioJpaController = new ServicioJpaController();
            initComponents();
            jLabelServicio.setVisible(false);
            jComboBoxServicio.setVisible(false);
            GUIUtility.refreshComboBox(new ArrayList<Servicio>(), jComboBoxServicio);
            UpdatableSubject.addUpdatableListener(this);
            addRemovePanel1.addActionListener(new AddRemoveEventControl());
            addRemovePanel1.setBtnRemoveEnabled(false);
            costoTableModel = new TablizableEntityDataModel<Costo>(Costo.TABLE_HEADERS);
            jXTableCostos.setModel(costoTableModel);

            jXTableCostos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jXTableCostos.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {

                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                    if (jXTableCostos.getSelectedRow() > -1) {
                        addRemovePanel1.setBtnRemoveEnabled(true);
                    }
                    else {
                        addRemovePanel1.setBtnRemoveEnabled(false);
                    }
                }
            });
            refreshUI();
            jXTableCostos.packAll();
        }
        catch(Exception e)
        {
            GUIUtility.logError(PanelCostos.class, e);
        }
    }

    public void clear()
    {
        costoTableModel.setData(new ArrayList<Costo>());
        panelValorMoneda1.clear();
        jComboBoxTipoCosto.setSelectedIndex(0);
        jComboBoxServicio.setSelectedIndex(0);
    }

    public List<Costo> getCostosCargados()
    {
        return costoTableModel.getData();
    }

    public void setCostosCargados(List<Costo> costos)
    {
        costoTableModel.setData(costos);
    }

    public void disableFields()
    {
        jComboBoxTipoCosto.setEnabled(false);
        jComboBoxServicio.setEnabled(false);
        panelValorMoneda1.setEnabled(false);
        addRemovePanel1.setEnabled(false);
        jXTableCostos.setEnabled(false);
    }

    private Costo getCosto()
    {
        if (costo == null) {
            costo = new Costo();
        }
        costo.setImporte(getImporte());
        costo.setTipoCosto(getTipoCosto());
        costo.setServicio(getServicio());
        return costo;
    }

    private void setCosto(Costo c)
    {
        panelValorMoneda1.setValorMoneda(new ValorMoneda(c.getImporte().getMonto()));
        jComboBoxTipoCosto.setSelectedItem(c.getTipoCosto());
        jComboBoxServicio.setSelectedItem(c.getServicio());
        costo = c;
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

    private TipoCosto getTipoCosto()
    {
        if (jComboBoxTipoCosto.getSelectedItem() instanceof TipoCosto) {
            return (TipoCosto) jComboBoxTipoCosto.getSelectedItem();
        }
        else {
            return null;
        }
    }

    private Servicio getServicio()
    {
        if (jComboBoxServicio.getSelectedItem() instanceof Servicio) {
            return (Servicio) jComboBoxServicio.getSelectedItem();
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

    @Override
    public void refreshUI()
    {
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
                if (validateInput(getCosto()) && !exists()) {
                    costoTableModel.add(costo);
                    frameNotifier.showInformationMessage("Se cargó el costo con éxito");
                    clearFields();
                }
            }
            else if (e.getSource() == addRemovePanel1.getJButtonRemove()) {
                costoTableModel.removeRows(GUIUtility.getModelSelectedRows(jXTableCostos));
            }
            else if (e.getSource() == addRemovePanel1.getJButtonClean()) {
                clearFields();
            }
        }

        private boolean exists()
        {
            for (Costo c : costoTableModel.getData()) {
                if (c.equalToEntity(costo)) {
                    frameNotifier.showErrorMessage("Ya existe un costo con los mismos valores");
                    return true;
                }
            }
            return false;
        }
    }

    private void clearFields()
    {
        panelValorMoneda1.clear();
        jComboBoxTipoCosto.setSelectedIndex(-1);
        jComboBoxServicio.setSelectedIndex(-1);
        costo = null;
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
        listableComboBoxRenderer2 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxTipoCosto = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabelServicio = new javax.swing.JLabel();
        jComboBoxServicio = new javax.swing.JComboBox();
        addRemovePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableCostos = new org.jdesktop.swingx.JXTable();
        panelValorMoneda1 = new ar.com.init.agros.view.components.valores.PanelValorMoneda();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelCostos.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N
        listableComboBoxRenderer2.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jComboBoxTipoCosto.setNextFocusableComponent(panelValorMoneda1.getjTextFieldMonto());
        jComboBoxTipoCosto.setRenderer(listableComboBoxRenderer1);
        jComboBoxTipoCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoCostoActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabelServicio.setText(resourceMap.getString("jLabelServicio.text")); // NOI18N

        jComboBoxServicio.setRenderer(listableComboBoxRenderer2);

        jXTableCostos.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableCostos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelServicio, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxTipoCosto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jComboBoxServicio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelValorMoneda1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(addRemovePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(188, 188, 188))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jComboBoxTipoCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(panelValorMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelServicio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addRemovePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxTipoCostoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxTipoCostoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxTipoCostoActionPerformed
        if (jComboBoxTipoCosto.getSelectedItem() instanceof TipoCosto)
        {
            TipoCosto t = (TipoCosto)jComboBoxTipoCosto.getSelectedItem();
            panelValorMoneda1.setLabel(t.getUnidadMedida().getAbreviatura());
        }
        else
        {
            panelValorMoneda1.setLabel(null);
        }
    }//GEN-LAST:event_jComboBoxTipoCostoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemovePanel addRemovePanel1;
    protected javax.swing.JComboBox jComboBoxServicio;
    protected javax.swing.JComboBox jComboBoxTipoCosto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    protected javax.swing.JLabel jLabelServicio;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableCostos;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer2;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMoneda1;
    // End of variables declaration//GEN-END:variables

//    @Override
//    public abstract void refreshUI();
}
