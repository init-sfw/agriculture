package ar.com.init.agros.view.trabajos;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.controller.TrabajoLoteJpaController;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.agroquimicos.FrameRegistrarPlanificacionAgroquimico;
import ar.com.init.agros.view.components.model.SeleccionSuperficieLotesTableModel;
import ar.com.init.agros.view.components.model.SeleccionSuperficieSubLotesTableModel;
import ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI FrameRegistrarTrabajoDeLote
 *
 * @author fbobbio
 * @version 02-ago-2009
 */
public class FrameRegistrarTrabajoDeLote extends javax.swing.JFrame implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private EventControl evt;
    private NamedEntityJpaController<FormaFumigacion> formaFumigacionJpaController;
    private Trabajo trabajo;
    private TrabajoLoteJpaController trabajoJpaController;
    private SeleccionSuperficieLotesTableModel lotesTM;
    private SeleccionSuperficieSubLotesTableModel subLotesTM;
    private String successMessage = "registró";

    /** Crea una nueva GUI tipo FrameRegistrarTrabajoDeLote */
    public FrameRegistrarTrabajoDeLote()
    {
        this(null, false);
    }

    public FrameRegistrarTrabajoDeLote(Trabajo trabajo, boolean isUpdate)
    {
        trabajoJpaController = new TrabajoLoteJpaController();
        formaFumigacionJpaController = new NamedEntityJpaController<FormaFumigacion>(FormaFumigacion.class);
        GUIUtility.initWindow(this);
        UpdatableSubject.addUpdatableListener(this);
        // Creo los tablemodel que se van a setear desde el initComponents()
        lotesTM = new SeleccionSuperficieLotesTableModel();
        subLotesTM = new SeleccionSuperficieSubLotesTableModel();
        initComponents();
        panelCampaniaCampoCultivoVariedad1.setFrameNotifier(frameNotifier1);
        refreshUI();
        panelCampaniaCampoCultivoVariedad1.addCampaniaSelectionChangeListener(lotesTM);
        panelCampaniaCampoCultivoVariedad1.addCampaniaSelectionChangeListener(subLotesTM);
        if (trabajo != null) // Caso en el que no será utilizada como ventana de alta
        {
            if (!isUpdate) // Caso en el que será de consulta
            {
                disableFieldsAndButtons();
            }
            else
            {
                successMessage = "modificó";
                panelAgroquimicosTrabajo1.enableAgroquimicos(false);
            }
            setTrabajo(trabajo);
        }
        else
        {
            panelAgroquimicosTrabajo1.disableForList();
        }
        panelCampaniaCampoCultivoVariedad1.hideFieldsForTrabajo();
        panelCampaniaCampoCultivoVariedad1.setTransaccion(PanelCampaniaCampoCultivoVariedad.TRABAJO);
        panelCampaniaCampoCultivoVariedad1.addSuperficieListener(panelAgroquimicosTrabajo1);
        panelCampaniaCampoCultivoVariedad1.addCampoSelectionChangeListener(panelAgroquimicosTrabajo1);
        panelAgroquimicosTrabajo1.setFrameNotifier(frameNotifier1);
        panelCostosPulverizacion1.setFrameNotifier(frameNotifier1);
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void disableFieldsAndButtons()
    {
        oKCancelCleanPanel1.setVisible(false, true, false);
        jComboBoxFormaFumigacion.setEnabled(false);
        singleDateChooserFecha.setEnabled(false);
        panelCampaniaCampoCultivoVariedad1.disableFields();
        panelCostosPulverizacion1.disableFields();
        panelObservacion1.disableFields();
        panelAgroquimicosTrabajo1.disableFields();
        jTextAreaCobertura.setEditable(false);
    }

    public Trabajo getTrabajo()
    {
        if (trabajo == null) {
            trabajo = new Trabajo();
        }
        trabajo.setCampania(panelCampaniaCampoCultivoVariedad1.getCampania());
        trabajo.setCampo(panelCampaniaCampoCultivoVariedad1.getCampo());
        trabajo.setDetalles(panelAgroquimicosTrabajo1.getDetallesCargados());
        trabajo.setSuperficies(panelCampaniaCampoCultivoVariedad1.getSuperficiesSeleccionadas());
        trabajo.setSuperficieSeleccionada(new ValorUnidad(
                panelCampaniaCampoCultivoVariedad1.getPanelSeleccionLotes1().getSuperficieTotal(),
                MagnitudEnum.SUPERFICIE.patron()));
        trabajo.setComentarios(jTextAreaCobertura.getText().trim());
        trabajo.setObservacion(panelObservacion1.getObservacion());
        trabajo.setFormaFumigacion(getFormaFumigacion());
        trabajo.setFecha(getFecha());
        trabajo.setCostos(panelCostosPulverizacion1.getCostosCargados());

        return trabajo;
    }

    public void setTrabajo(Trabajo trabajo)
    {
        panelCampaniaCampoCultivoVariedad1.setSelectedCampania(trabajo.getCampania());
        panelCampaniaCampoCultivoVariedad1.setSelectedCampo(trabajo.getCampo());
        panelCampaniaCampoCultivoVariedad1.setSelectedSuperficies(trabajo.getSuperficies(),
                trabajo.getSuperficieSeleccionada().getValor());
        panelAgroquimicosTrabajo1.campoSelectionChanged(trabajo.getCampo());
        panelAgroquimicosTrabajo1.setDetallesCargados(trabajo.getDetalles());
        panelAgroquimicosTrabajo1.setSuperficiePlanificada(trabajo.getSuperficieSeleccionada().getValor());
        jTextAreaCobertura.setText(trabajo.getComentarios());
        panelObservacion1.setObservacion(trabajo.getObservacion());
        jComboBoxFormaFumigacion.setSelectedItem(trabajo.getFormaFumigacion());
        singleDateChooserFecha.setDate(trabajo.getFecha());
        panelCostosPulverizacion1.setCostosCargados(trabajo.getCostos());

        this.trabajo = trabajo;
    }

    public boolean isUnique()
    {
        boolean r = trabajoJpaController.exists(trabajo);
        if (r) {
            frameNotifier1.showErrorMessage(
                    "Ya existe una pulverización con la combinación seleccionada de campaña, campo, lotes y sublotes");
            panelCampaniaCampoCultivoVariedad1.setRedBorders();
            return false;
        }
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        frameNotifier1.showOkMessage();
        return true;
    }

    private Date getFecha()
    {
        return singleDateChooserFecha.getDate();
    }

    private FormaFumigacion getFormaFumigacion()
    {
        if (jComboBoxFormaFumigacion.getSelectedItem() instanceof FormaFumigacion) {
            return (FormaFumigacion) jComboBoxFormaFumigacion.getSelectedItem();
        }
        else {
            return null;
        }
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
        jPanel1 = new javax.swing.JPanel();
        singleDateChooserFecha = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxFormaFumigacion = new javax.swing.JComboBox();
        panelCampaniaCampoCultivoVariedad1 = new ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad(lotesTM,subLotesTM);
        jTabbedPane2 = new javax.swing.JTabbedPane();
        panelAgroquimicosTrabajo1 = new ar.com.init.agros.view.components.panels.PanelAgroquimicosTrabajo();
        panelCostosPulverizacion1 = new ar.com.init.agros.view.components.panels.PanelCostosPulverizacion();
        panelObservacion1 = new ar.com.init.agros.view.components.panels.PanelObservacion();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCobertura = new javax.swing.JTextArea();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameRegistrarTrabajoDeLote.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jComboBoxFormaFumigacion.setRenderer(listableComboBoxRenderer1);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(panelCampaniaCampoCultivoVariedad1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jComboBoxFormaFumigacion, 0, 486, Short.MAX_VALUE)
                            .add(singleDateChooserFecha, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(singleDateChooserFecha, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jComboBoxFormaFumigacion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelCampaniaCampoCultivoVariedad1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab(resourceMap.getString("panelAgroquimicosTrabajo1.TabConstraints.tabTitle"), panelAgroquimicosTrabajo1); // NOI18N
        jTabbedPane2.addTab(resourceMap.getString("panelCostosPulverizacion1.TabConstraints.tabTitle"), panelCostosPulverizacion1); // NOI18N
        jTabbedPane2.addTab(resourceMap.getString("panelObservacion1.TabConstraints.tabTitle"), panelObservacion1); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N

        jTextAreaCobertura.setColumns(20);
        jTextAreaCobertura.setRows(5);
        jScrollPane1.setViewportView(jTextAreaCobertura);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        clear();
        frameNotifier1.showOkMessage();
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    @Override
    public void refreshUI()
    {
        GUIUtility.refreshComboBox(formaFumigacionJpaController.findEntities(), jComboBoxFormaFumigacion);
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    /** Clase de control de eventos que maneja los eventos de la GUI FrameRegistrarTrabajoDeLote y las validaciones de la misma */
    public class EventControl extends AbstractEventControl implements ActionListener
    {

        /** Método que maneja los eventos de la GUI FrameRegistrarTrabajoDeLote
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
                clear();
                frameNotifier1.showOkMessage();
                FrameRegistrarTrabajoDeLote.this.dispose();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                clear();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {
                if (validateInput(getTrabajo())) {
                    if (!isUnique()) {
                        return;
                    }
                    try {
                        if (!seRepitenCultivos()) {
                            if (trabajo.getSuperficieSeleccionada().getValor() == 0) {
                                frameNotifier1.showErrorMessage(
                                        "La superficie seleccionada debe ser mayor a 0");
                                return;
                            }
                            if (GUIUtility.confirmData(FrameRegistrarTrabajoDeLote.this)) {
                                panelAgroquimicosTrabajo1.setPersisting(true); // workaroun para que no aparezca el mensaje de cambio de campo
                                trabajoJpaController.persistOrUpdate(trabajo);
                                panelAgroquimicosTrabajo1.setPersisting(false);
                                frameNotifier1.showInformationMessage(
                                        "Se " + successMessage + " con éxito la pulverización del campo!");
                                clear();
                            }
                        }
                        else {
                            frameNotifier1.showErrorMessage(
                                    "Se han seleccionado diferentes cultivos, elija superficies que correspondan a uno");
                        }
                    }
                    catch (InvalidStateException ex) {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameRegistrarTrabajoDeLote.class.getName()).log(Level.SEVERE, null,
                                ex);
                    }
                    catch (ConstraintViolationException ex) {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameRegistrarTrabajoDeLote.class.getName()).log(Level.SEVERE, null,
                                ex);
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

    /** Método que valida que en las superficies seleccionadas no se repitan cultivos */
    private boolean seRepitenCultivos()
    {
        return panelCampaniaCampoCultivoVariedad1.seRepitenCultivos();
    }

    /** Método donde se limpian todos los campos de la ventana */
    public void clear()
    {
        panelAgroquimicosTrabajo1.clear();
        panelCampaniaCampoCultivoVariedad1.clear();
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        jComboBoxFormaFumigacion.setSelectedIndex(0);
        singleDateChooserFecha.setDate(new Date());
        jTextAreaCobertura.setText("");
        panelObservacion1.clear();
        panelCostosPulverizacion1.clear();
        trabajo = null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JComboBox jComboBoxFormaFumigacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextAreaCobertura;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelAgroquimicosTrabajo panelAgroquimicosTrabajo1;
    private ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad panelCampaniaCampoCultivoVariedad1;
    private ar.com.init.agros.view.components.panels.PanelCostosPulverizacion panelCostosPulverizacion1;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion1;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFecha;
    // End of variables declaration//GEN-END:variables
}
