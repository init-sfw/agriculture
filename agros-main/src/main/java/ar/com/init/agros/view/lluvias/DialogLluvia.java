/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.view.lluvias;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.LluviaJpaController;
import ar.com.init.agros.controller.exceptions.BaseException;
import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI DialogLluvia
 *
 * @author fbobbio
 * @version 17-jul-2009
 */
public class DialogLluvia extends javax.swing.JDialog implements UpdatableListener
{

    private EventControl evt;
    private LluviaJpaController lluviaJpaController;
    private CampoJpaController campoJpaController;
    private Lluvia lluvia;
    private String successMessage = "registr�";
    private boolean consulta = false;

    /** Creates new form DialogLluvia */
    public DialogLluvia(java.awt.Frame parent)
    {
        this(parent, null, true);
    }

    /** Creates new form DialogLluvia
     *
     * @param parent
     * @param lluvia la instancia de la lluvia a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     */
    public DialogLluvia(java.awt.Frame parent, Lluvia lluvia, boolean isUpdate)
    {
        super(parent, true);
        lluviaJpaController = new LluviaJpaController();
        campoJpaController = new CampoJpaController();
        GUIUtility.initWindow(this);
        UpdatableSubject.addUpdatableListener(this);
        initComponents();
        refreshUI();
        panelSeleccionLote1.setFrameNotifier1(frameNotifier1);
        singleDateChooser1.setDate(new Date());
        panelValorUnidadCantidad.addUnidades(MagnitudEnum.LLUVIA_CAIDA.patron());
        panelValorUnidadCantidad.setSelectedUnidadMedida(MagnitudEnum.LLUVIA_CAIDA.patron());
        panelValorUnidadCantidad.setEnabled(true, false);
        panelValorUnidadCantidad.setFrameNotifier(frameNotifier1);
        if (lluvia != null) // Caso en el que no ser� utilizada como ventana de alta
        {
            if (!isUpdate) // Caso en el que ser� de consulta
            {
                disableFieldsAndButtons();
                refreshUI();
            }
            else
            {
                successMessage = "modific�";
            }
            setLluvia(lluvia);
        }
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
    }

    public Lluvia getLluvia()
    {
        if (lluvia == null)
        {
            lluvia = new Lluvia();
        }
        lluvia.setCantidad(panelValorUnidadCantidad.getValorUnidad());
        lluvia.setFecha(singleDateChooser1.getDate());
        lluvia.setLotes(panelSeleccionLote1.getCheckedLotes());
        lluvia.setCampo(lluvia.getLotes().get(0).getCampo());
        lluvia.setObservaciones(panelObservacion1.getObservacion());
        return lluvia;
    }

    public void setLluvia(Lluvia lluvia)
    {
        panelValorUnidadCantidad.setValorUnidad(lluvia.getCantidad());
        jComboBoxCampo.setSelectedItem(lluvia.getCampo());
        singleDateChooser1.setDate(lluvia.getFecha());
        panelSeleccionLote1.setCampo(lluvia.getCampo());
        panelSeleccionLote1.checkData(lluvia.getLotes());
        panelObservacion1.setObservacion(lluvia.getObservaciones());
        this.lluvia = lluvia;
    }

    private void disableFieldsAndButtons()
    {
        consulta = true;
        singleDateChooser1.setEnabled(false);
        panelObservacion1.disableFields();
        panelValorUnidadCantidad.setEnabled(false);
        oKCancelCleanPanel1.disableForList();
        panelSeleccionLote1.disableFieldsAndButtons();
        jComboBoxCampo.setEnabled(false);
        panelValorUnidadCantidad.setEnabled(false, false);
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
        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxCampo = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        panelSeleccionLote1 = new ar.com.init.agros.view.components.panels.PanelSeleccionLote();
        panelValorUnidadCantidad = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        panelObservacion1 = new ar.com.init.agros.view.components.panels.PanelObservacion();
        singleDateChooser1 = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogLluvia.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jComboBoxCampo.setRenderer(listableComboBoxRenderer1);
        jComboBoxCampo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampoActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(panelSeleccionLote1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(panelSeleccionLote1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelValorUnidadCantidad, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .add(jComboBoxCampo, 0, 397, Short.MAX_VALUE)
                    .add(singleDateChooser1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panelObservacion1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(singleDateChooser1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel2)
                    .add(panelValorUnidadCantidad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jComboBoxCampo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelObservacion1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCampoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampoActionPerformed
        eventCampo();
    }//GEN-LAST:event_jComboBoxCampoActionPerformed

    private void eventCampo()
    {
        Campo oldSelection = panelSeleccionLote1.getCampo();
        Object aux = jComboBoxCampo.getSelectedItem();
        if ((oldSelection == null && aux == null) || (oldSelection != null && oldSelection.equals(aux))) // Si no hay cambio de selecci�n no procesamos
        {
            return;
        }
        if (aux instanceof Campo)
        {
            Campo c = (Campo) aux;
            panelSeleccionLote1.setCampo(c);
            panelSeleccionLote1.enableCheckBox(true);
        }
        else
        {
            panelSeleccionLote1.clear();
            panelSeleccionLote1.enableCheckBox(false);
        }
    }

    @Override
    public void refreshUI()
    {
        if (!consulta)
        {
            GUIUtility.refreshComboBox(campoJpaController.findEntities(), jComboBoxCampo);
        }
        else
        {
            GUIUtility.refreshComboBox(campoJpaController.findAllEntities(), jComboBoxCampo);
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    /** Clase de control de eventos que maneja los eventos de la GUI DialogLluvia y las validaciones de la misma */
    public class EventControl extends AbstractEventControl implements ActionListener
    {

        /** M�todo que maneja los eventos de la GUI DialogLluvia
         *  @param e el evento de acci�n lanzado por alg�n componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {

            if (e.getSource() == oKCancelCleanPanel1.getBtnClean())
            {
                clean();
                frameNotifier1.showOkMessage();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar())
            {
                closeWindow(DialogLluvia.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar())
            {
                if (validateInput(getLluvia()))
                {
                    try
                    {
                        if (GUIUtility.confirmData(DialogLluvia.this))
                        {
                            lluviaJpaController.persistOrUpdate(lluvia);
                            frameNotifier1.showInformationMessage("Se " + successMessage + " con �xito la lluvia de la fecha " + GUIUtility.toMediumDate(lluvia.getFecha()));
                            clear();
                        }
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogLluvia.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (BaseException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                    }
                    catch (Exception ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogLluvia.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }
    }

    public void clear()
    {
        singleDateChooser1.setDate(new Date());
        panelValorUnidadCantidad.clear(true, false);
        panelObservacion1.clear();
        jComboBoxCampo.setSelectedIndex(0);
        panelSeleccionLote1.clear();
        lluvia = null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JComboBox jComboBoxCampo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion1;
    private ar.com.init.agros.view.components.panels.PanelSeleccionLote panelSeleccionLote1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadCantidad;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooser1;
    // End of variables declaration//GEN-END:variables
}