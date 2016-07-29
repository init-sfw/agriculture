package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.PlanificacionAgroquimicoJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.components.CampaniaSelectionChangeListener;
import ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad;
import ar.com.init.agros.view.siembras.DialogSiembraCampania;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI FrameRegistrarPlanificacionAgroquimico
 *
 * @author fbobbio
 * @version 16-jun-2009
 */
public class FrameRegistrarPlanificacionAgroquimico extends javax.swing.JFrame implements
        CampaniaSelectionChangeListener
{

    private static final long serialVersionUID = -1L;
    private EventControl evt;
    private PlanificacionAgroquimicoJpaController planificacionJPAController;
    private PlanificacionAgroquimico planificacion;
    private String successMessage = "registró";

    /** Crea una nueva GUI tipo FrameRegistrarPlanificacionAgroquimico */
    public FrameRegistrarPlanificacionAgroquimico()
    {
        this(null, false);
    }

    public FrameRegistrarPlanificacionAgroquimico(PlanificacionAgroquimico planificacion, boolean isUpdate)
    {
        GUIUtility.initWindow(this);
        initComponents();
        if (planificacion != null) // Caso en el que no será utilizada como ventana de alta
        {
            if (!isUpdate) // Caso en el que será de consulta
            {
                disableFieldsAndButtons();
            }
            else {
                successMessage = "modificó";
            }
            setPlanifificacion(planificacion);
        }
        panelCampaniaCampoCultivoVariedad1.addSuperficieListener(panelAgroquimicos1);
        panelCampaniaCampoCultivoVariedad1.addSuperficieListener(panelSemillas1);
        panelCampaniaCampoCultivoVariedad1.addCampaniaSelectionChangeListener(this);
        planificacionJPAController = new PlanificacionAgroquimicoJpaController();
        panelCampaniaCampoCultivoVariedad1.setFrameNotifier(frameNotifier1);
        panelCampaniaCampoCultivoVariedad1.setTransaccion(PanelCampaniaCampoCultivoVariedad.PLANIFICACION);
        panelAgroquimicos1.setFrameNotifier(frameNotifier1);
        panelCostoTotalCampania1.setFrameNotifier(frameNotifier1);
        panelSemillas1.setFrameNotifier(frameNotifier1);
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void disableFieldsAndButtons()
    {
        oKCancelCleanPanel1.setVisible(false, true, false);
        panelCampaniaCampoCultivoVariedad1.disableFields();
        panelSemillas1.disableFields();
        panelCostoTotalCampania1.disableFields();
        panelAgroquimicos1.disableFields();
    }

    public PlanificacionAgroquimico getPlanificacion()
    {
        if (planificacion == null) {
            planificacion = new PlanificacionAgroquimico();
        }
        planificacion.setCampania(panelCampaniaCampoCultivoVariedad1.getCampania());
        planificacion.setCampo(panelCampaniaCampoCultivoVariedad1.getCampo());
        planificacion.setCultivo(panelCampaniaCampoCultivoVariedad1.getCultivo());
        planificacion.setVariedadCultivo(panelCampaniaCampoCultivoVariedad1.getVariedad());
        planificacion.setDetallesPlanificacion(panelAgroquimicos1.getDetallesCargados());
        planificacion.setSuperficies(panelCampaniaCampoCultivoVariedad1.getSuperficiesSeleccionadas());
        planificacion.setSemilla(panelSemillas1.getSemillaCargadas(
                panelCampaniaCampoCultivoVariedad1.getCultivo(),
                panelCampaniaCampoCultivoVariedad1.getVariedad()));
        planificacion.setSuperficiePlanificada(panelAgroquimicos1.getSuperficiePlanificada());

        return planificacion;
    }

    public void setPlanifificacion(PlanificacionAgroquimico planificacion)
    {
        panelCampaniaCampoCultivoVariedad1.setSelectedCampania(planificacion.getCampania());
        panelCampaniaCampoCultivoVariedad1.setSelectedCultivo(planificacion.getCultivo());
        panelCampaniaCampoCultivoVariedad1.setSelectedVariedad(planificacion.getVariedadCultivo());
        panelCampaniaCampoCultivoVariedad1.setSelectedCampo(planificacion.getCampo());
        panelCampaniaCampoCultivoVariedad1.setSelectedSuperficies(planificacion.getSuperficies());
        panelAgroquimicos1.refreshTablaAgroquimicos(planificacion.getDetallesPlanificacion());
        panelAgroquimicos1.setSuperficiePlanificada(planificacion.getSuperficiePlanificada());
        panelCostoTotalCampania1.refreshTable(planificacion.getCampania());
        panelSemillas1.setSemillasCargadas(planificacion.getSemilla());
        this.planificacion = planificacion;
    }

    public boolean isUnique()
    {
        boolean r = planificacionJPAController.exists(planificacion);
        if (r) {
            frameNotifier1.showErrorMessage(
                    "Ya existe una planificación con la combinación seleccionada de campaña, establecimiento, lotes y sublotes");
            panelCampaniaCampoCultivoVariedad1.setRedBorders();
            return false;
        }
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        frameNotifier1.showOkMessage();
        return true;
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
        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelAgroquimicos1 = new ar.com.init.agros.view.components.panels.PanelAgroquimicos();
        panelSemillas1 = new ar.com.init.agros.view.components.panels.PanelSemillas();
        panelCostoTotalCampania1 = new ar.com.init.agros.view.components.panels.PanelCostoTotalCampania();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        panelCampaniaCampoCultivoVariedad1 = new ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad();
        jButtonConsultarSiembras = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameRegistrarPlanificacionAgroquimico.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        listableComboBoxRenderer2.setText(resourceMap.getString("listableComboBoxRenderer2.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jTabbedPane1.border.title"))); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelAgroquimicos1.TabConstraints.tabTitle"), panelAgroquimicos1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelSemillas1.TabConstraints.tabTitle"), panelSemillas1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelCostoTotalCampania1.TabConstraints.tabTitle"), panelCostoTotalCampania1); // NOI18N

        panelCampaniaCampoCultivoVariedad1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelCampaniaCampoCultivoVariedad1.border.title"))); // NOI18N

        jButtonConsultarSiembras.setText(resourceMap.getString("jButtonConsultarSiembras.text")); // NOI18N
        jButtonConsultarSiembras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarSiembrasActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(510, Short.MAX_VALUE)
                .add(jButtonConsultarSiembras)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panelCampaniaCampoCultivoVariedad1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonConsultarSiembras)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelCampaniaCampoCultivoVariedad1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConsultarSiembrasActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonConsultarSiembrasActionPerformed
    {//GEN-HEADEREND:event_jButtonConsultarSiembrasActionPerformed
        DialogSiembraCampania.showInstance();
    }//GEN-LAST:event_jButtonConsultarSiembrasActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        clear();
        frameNotifier1.showOkMessage();
    }//GEN-LAST:event_formWindowClosing

    /** Clase de control de eventos que maneja los eventos de la GUI FrameRegistrarPlanificacionAgroquimico y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** Método que maneja los eventos de la GUI FrameRegistrarPlanificacionAgroquimico
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
                clear();
                frameNotifier1.showOkMessage();
                closeWindow(FrameRegistrarPlanificacionAgroquimico.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                clear();
                frameNotifier1.showOkMessage();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {
                if (validateInput(getPlanificacion())) {
                    if (planificacion.getSemilla() != null && !validateInput(planificacion.getSemilla())) {
                        return;
                    }
                    if (!isUnique()) {
                        return;
                    }
                    try {
                        if (GUIUtility.confirmData(FrameRegistrarPlanificacionAgroquimico.this)) {
                            planificacionJPAController.persistOrUpdate(planificacion);
                            frameNotifier1.showInformationMessage(
                                    "Se " + successMessage + " con éxito la planificación");
                            clear();
                        }
                    }
                    catch (InvalidStateException ex) {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameRegistrarPlanificacionAgroquimico.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    catch (ConstraintViolationException ex) {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameRegistrarPlanificacionAgroquimico.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    catch (Exception ex) {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameRegistrarPlanificacionAgroquimico.class.getName()).log(
                                Level.SEVERE, null, ex);
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

    /** Método donde se limpian todos los campos de la ventana */
    public void clear()
    {
        panelCampaniaCampoCultivoVariedad1.clear();
        panelAgroquimicos1.clear();
        panelCostoTotalCampania1.clear();
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        panelSemillas1.clear();
        planificacion = null;
    }

    @Override
    public void campaniaSelectionChanged(Campania campania)
    {
        panelCostoTotalCampania1.refreshTable(campania);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JButton jButtonConsultarSiembras;
    private javax.swing.JTabbedPane jTabbedPane1;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer2;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelAgroquimicos panelAgroquimicos1;
    private ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad panelCampaniaCampoCultivoVariedad1;
    private ar.com.init.agros.view.components.panels.PanelCostoTotalCampania panelCostoTotalCampania1;
    private ar.com.init.agros.view.components.panels.PanelSemillas panelSemillas1;
    // End of variables declaration//GEN-END:variables
}
