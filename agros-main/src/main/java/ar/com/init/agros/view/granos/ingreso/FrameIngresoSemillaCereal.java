package ar.com.init.agros.view.granos.ingreso;

import ar.com.init.agros.controller.ServicioJpaController;
import ar.com.init.agros.controller.almacenamiento.ValorAlmacenamientoJpaController;
import ar.com.init.agros.controller.inventario.granos.MovimientoGranoJpaController;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.inventario.cereales.DetalleIngresoCereal;
import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
import ar.com.init.agros.model.inventario.granos.IngresoGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.cultivos.DialogCultivo;
import ar.com.init.agros.view.granos.PanelSemillasCereales.TipoGrano;
import ar.com.init.agros.view.granos.PanelSemillasCereales.UnidadGrano;
import ar.com.init.agros.view.granos.ingreso.model.IngresoGranoTableModel;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI FrameIngresoSemillaCereal
 *
 * @author fbobbio
 * @version 11-dic-2010
 */
public class FrameIngresoSemillaCereal extends javax.swing.JFrame implements UpdatableListener {

    private static final long serialVersionUID = -1L;
    private IngresoGranoTableModel ingresoGranoTableModel;
    private MovimientoGranoJpaController ingresoGranoJpaController;

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameIngresoSemillaCereal(IngresoGrano ingresoGrano) {
        this();
        disableFieldsAndButtons();
        setIngresoGrano(ingresoGrano);
    }

    public void disableFieldsAndButtons() {
        panelSemillasCereales1.disableFields();
        singleDateChooserFechaRegistro.setEnabled(false);
        jPanel1.setVisible(false);
        oKCancelCleanPanel1.setVisible(false, true, false);
        addRemovePanel1.setEnabled(false);
        panelObservacion1.setEnabled(false);
    }

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameIngresoSemillaCereal() {
        ingresoGranoTableModel = new IngresoGranoTableModel();
        GUIUtility.initWindow(this);
        initComponents();

        //this.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        addRemovePanel1.addActionListener(new AddRemoveEventControl());
        oKCancelCleanPanel1.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel1.setOwner(this);

        UpdatableSubject.addUpdatableListener(this);

        setFrameNotifier1(frameNotifier1);
        jXTableDetalles.setModel(ingresoGranoTableModel);

        ingresoGranoJpaController = new MovimientoGranoJpaController();

        panelValorMonedaImporteTotal.setFrameNotifier(frameNotifier1);
        panelValorMonedaImporteTotal.setInputVerifier(new DecimalInputVerifier(frameNotifier1, false));

        try {
            refreshUI();
            jXTableDetalles.packAll();
        } catch (PersistenceException e) {
            if (frameNotifier1 != null) {
                frameNotifier1.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(FrameIngresoSemillaCereal.class, e);
        }

        jButtonAgregarVariedad3.setVisible(false);
        panelSemillasCereales1.setAgregarVariedadVisible(true);
    }

    public FrameNotifier getFrameNotifier1() {
        return frameNotifier1;
    }

    public void setFrameNotifier1(FrameNotifier frameNotifier1) {
        this.frameNotifier1 = frameNotifier1;
        panelSemillasCereales1.setFrameNotifier(frameNotifier1);
        panelValorMonedaImporteTotal.setFrameNotifier(frameNotifier1);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        frameNotifier1.showOkMessage();
    }

    public void setIngresoGrano(IngresoGrano ingreso) {
        ingresoGranoTableModel.setData(ingreso.getCastedDetalles());
        singleDateChooserFechaRegistro.setDate(ingreso.getFecha());
        panelObservacion1.setObservacion(ingreso.getObservaciones());
    }

    public void refresh() {
        refreshCombos();
    }

    private void refreshCombos() {
        ServicioJpaController proveedorController = new ServicioJpaController();
        GUIUtility.refreshComboBox(proveedorController.findByTipo(TipoServicio.PROVEEDOR_INSUMOS), jComboBoxProveedorInsumos);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jPanel1 = new javax.swing.JPanel();
        panelSemillasCereales1 = new ar.com.init.agros.view.granos.PanelSemillasCereales();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxProveedorInsumos = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        panelValorMonedaImporteTotal = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        addRemovePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jButtonAgregarVariedad3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        singleDateChooserFechaRegistro = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableDetalles = new org.jdesktop.swingx.JXTable();
        panelObservacion1 = new ar.com.init.agros.view.components.panels.PanelObservacion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        frameNotifier1.setName("frameNotifier1"); // NOI18N

        oKCancelCleanPanel1.setName("oKCancelCleanPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameIngresoSemillaCereal.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        panelSemillasCereales1.setName("panelSemillasCereales1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jComboBoxProveedorInsumos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        jComboBoxProveedorInsumos.setName("jComboBoxProveedorInsumos"); // NOI18N
        jComboBoxProveedorInsumos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxProveedorInsumosActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        panelValorMonedaImporteTotal.setEnabled(false);
        panelValorMonedaImporteTotal.setName("panelValorMonedaImporteTotal"); // NOI18N

        addRemovePanel1.setName("addRemovePanel1"); // NOI18N

        jButtonAgregarVariedad3.setText(resourceMap.getString("jButtonAgregarVariedad3.text")); // NOI18N
        jButtonAgregarVariedad3.setName("jButtonAgregarVariedad3"); // NOI18N
        jButtonAgregarVariedad3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarVariedad3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(panelSemillasCereales1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonAgregarVariedad3)
                .add(186, 186, 186))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(33, 33, 33)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel2)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(panelValorMonedaImporteTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 404, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jComboBoxProveedorInsumos, 0, 410, Short.MAX_VALUE))
                .add(addRemovePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jButtonAgregarVariedad3))
                    .add(panelSemillasCereales1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jComboBoxProveedorInsumos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(panelValorMonedaImporteTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(addRemovePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        singleDateChooserFechaRegistro.setName("singleDateChooserFechaRegistro"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(singleDateChooserFechaRegistro, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(500, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(singleDateChooserFechaRegistro, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap())
        );

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableDetalles.setName("jXTableDetalles"); // NOI18N
        jScrollPane1.setViewportView(jXTableDetalles);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        panelObservacion1.setName("panelObservacion1"); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelObservacion1.TabConstraints.tabTitle"), panelObservacion1); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(7, 7, 7)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        clean();
    }//GEN-LAST:event_formWindowClosing

    private void jComboBoxProveedorInsumosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxProveedorInsumosActionPerformed
    {//GEN-HEADEREND:event_jComboBoxProveedorInsumosActionPerformed
        if (getProveedorInsumos() != null) {
            panelValorMonedaImporteTotal.setEnabled(true);
        } else {
            panelValorMonedaImporteTotal.clear();
            panelValorMonedaImporteTotal.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxProveedorInsumosActionPerformed

    private void jButtonAgregarVariedad3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAgregarVariedad3ActionPerformed
    {//GEN-HEADEREND:event_jButtonAgregarVariedad3ActionPerformed
        if (getCultivo() != null) {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame(), getCultivo(), true);
            d.setTitle(jButtonAgregarVariedad3.getText());
            d.setVisible(true);
        } else {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame());
            d.setTitle(jButtonAgregarVariedad3.getText());
            d.setVisible(true);
        }

        refreshCombos();
    }//GEN-LAST:event_jButtonAgregarVariedad3ActionPerformed

    private class AddRemoveEventControl extends AbstractEventControl {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addRemovePanel1.getJButtonAdd()) {
                try {
                    DetalleIngresoGrano detalle = null;
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.SIN_SELECCION)) {
                        detalle = null;
                        frameNotifier1.showErrorMessage("Debe seleccionar si el ingreso será una Semilla o un Cereal");
                        return;
                    }
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.CEREAL)) {
                        detalle = new DetalleIngresoCereal();

                        ValorAlmacenamientoJpaController<ValorCereal> almController = new ValorAlmacenamientoJpaController<ValorCereal>(ValorCereal.class);
                        ValorCereal vc = almController.find((Silo) getAlmacenamiento(), getCultivo());

                        detalle.setValor(vc);
                    }
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.SEMILLA)) {
                        detalle = new DetalleIngresoSemilla();

                        ValorAlmacenamientoJpaController<ValorSemilla> almController = new ValorAlmacenamientoJpaController<ValorSemilla>(ValorSemilla.class);
                        ValorSemilla vs = almController.find(getAlmacenamiento(), getVariedadCultivo());

                        detalle.setValor(vs);
                    }
                    detalle.setFecha(panelSemillasCereales1.getFecha());
                    detalle.setEstablecimiento(panelSemillasCereales1.getEstablecimiento());
                    detalle.setHumedad(panelSemillasCereales1.getHumedad());
                    detalle.setServicio(getProveedorInsumos());
                    detalle.setImporte(panelValorMonedaImporteTotal.getValorMoneda());
                    if (panelSemillasCereales1.getUnidadIngreso().equals(UnidadGrano.SIN_SELECCION)) {
                        detalle = null;
                        frameNotifier1.showErrorMessage("Debe seleccionar si el ingreso será en bolsas o en Kg");
                        return;
                    }
                    if (panelSemillasCereales1.getUnidadIngreso().equals(UnidadGrano.BOLSAS)) {
                        detalle.setBolsas(panelSemillasCereales1.getCantidad());
                        detalle.setKgPorBolsa(panelSemillasCereales1.getKgPorBolsa());
                        double cantidadKg = 0;
                        if (panelSemillasCereales1.getKgPorBolsa() != null) {
                            cantidadKg = detalle.getBolsas() * panelSemillasCereales1.getKgPorBolsa();
                        }
                        if (cantidadKg > 0) {
                            detalle.setCantidad(new ValorUnidad(cantidadKg, MagnitudEnum.PESO.patron()));
                        } else {
                            detalle.setCantidad(null);
                        }
                    }
                    if (panelSemillasCereales1.getUnidadIngreso().equals(UnidadGrano.KILOGRAMO)) {
                        if (panelSemillasCereales1.getCantidad() != null && panelSemillasCereales1.getCantidad() > 0) {
                            detalle.setCantidad(new ValorUnidad(panelSemillasCereales1.getCantidad(), MagnitudEnum.PESO.patron()));
                        } else {
                            detalle.setCantidad(null);
                        }
                    }

                    if (validateImporte(detalle)) {
                        if (validateInput(detalle.getValor()) && validateInput(detalle)) {
                            ingresoGranoTableModel.addNoPersistido(detalle);
                            cleanAddDetalle();
                            frameNotifier1.showInformationMessage("Se agregó el Ingreso de Semilla/Cereal");
                        }
                    }
                } catch (Exception ex) {
                    frameNotifier1.showErrorMessage("No se pudo agregar el Ingreso de Semilla/Cereal");
                    Logger.getLogger(FrameIngresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (e.getSource() == addRemovePanel1.getJButtonRemove()) {

                int[] idxs = jXTableDetalles.getSelectedRows();

                boolean b = false;
                if (idxs.length > 0) {

                    for (int i = 0; i < idxs.length; i++) {
                        int j = jXTableDetalles.convertRowIndexToModel(idxs[i]);
                        b = ingresoGranoTableModel.removeNoPersistido(j) || b;
                    }

                    if (b) {
                        frameNotifier1.showInformationMessage("Se borró el Ingreso de Semilla/Cereal");
                    }
                }
            } else if (e.getSource() == addRemovePanel1.getJButtonClean()) {
                cleanNuevo();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier() {
            return frameNotifier1;
        }
    }

    private boolean validateImporte(DetalleIngresoGrano detalle) {
        if (detalle.getServicio() != null) {
            if (detalle.getImporte() == null) {
                frameNotifier1.showErrorMessage("Debe ingresar un importe si se seleccionó proveedor de insumos");
                return false;
            }
        }
        return true;
    }

    private Cultivo getCultivo() {
        return panelSemillasCereales1.getSemillaCereal();
    }

    private VariedadCultivo getVariedadCultivo() {
        return panelSemillasCereales1.getVariedad();
    }

    private Almacenamiento getAlmacenamiento() {
        return panelSemillasCereales1.getAlmacenamiento();
    }

    private UnidadMedida getUnidadMedida() {
        return MagnitudEnum.PESO.patron();
    }

    private Servicio getProveedorInsumos() {
        if (jComboBoxProveedorInsumos.getSelectedItem() instanceof Servicio) {
            Servicio s = (Servicio) jComboBoxProveedorInsumos.getSelectedItem();
            if (s.getTipo().equals(TipoServicio.PROVEEDOR_INSUMOS)) {
                return s;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /** Clase de control de eventos que maneja los eventos de la GUI FrameIngresoStock y las validaciones de la misma */
    private class OkCleanCancelEventControl extends AbstractEventControl {

        /** Método que maneja los eventos de la GUI FrameIngresoStock
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
                FrameIngresoSemillaCereal.this.clean();
                FrameIngresoSemillaCereal.this.dispose();
            } else if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {

                IngresoGrano ingreso = new IngresoGrano();

                ingreso.setFecha(singleDateChooserFechaRegistro.getDate());
                ingreso.getDetalles().addAll(ingresoGranoTableModel.getNoPersistidos());
                ingreso.setObservaciones(panelObservacion1.getObservacion());

                if (validateInput(ingreso) && validateDates()) {
                    try {
                        ingresoGranoJpaController.persist(ingreso);
                        frameNotifier1.showInformationMessage(
                                "Se ingresó con éxito el ingreso con fecha " + GUIUtility.toMediumDate(
                                ingreso.getFecha()));
                        FrameIngresoSemillaCereal.this.clean();
                    } catch (InvalidStateException ex) {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameIngresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameIngresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                FrameIngresoSemillaCereal.this.clean();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier() {
            return frameNotifier1;
        }

        private boolean validateDates() {
            Date fechaRegistro = singleDateChooserFechaRegistro.getDate();
            for (DetalleIngresoGrano d : ingresoGranoTableModel.getData()) {
                if (d.getFecha().before(fechaRegistro)) {
                    frameNotifier1.showErrorMessage("Existen fechas de detalles previas a la fecha general del ingreso");
                    return false;
                }
            }
            return true;
        }
    }

    /** Método donde se limpian todos los campos de la ventana */
    public void clean() {
        panelSemillasCereales1.clear();
        panelValorMonedaImporteTotal.clear();
        jComboBoxProveedorInsumos.setSelectedIndex(0);
        ingresoGranoTableModel.clearNoPersistidos();
        panelObservacion1.clear();

        cleanNuevo();
    }

    private void cleanNuevo() {
        cleanAddDetalle();
    }

    private void cleanAddDetalle() {
        panelSemillasCereales1.clear();
        panelValorMonedaImporteTotal.clear();
        jComboBoxProveedorInsumos.setSelectedIndex(0);
    }

    @Override
    public void refreshUI() {
        refreshCombos();
    }

    @Override
    public boolean isNowVisible() {
        return this.isVisible();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemovePanel addRemovePanel1;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JButton jButtonAgregarVariedad3;
    private javax.swing.JComboBox jComboBoxProveedorInsumos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXTable jXTableDetalles;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion1;
    private ar.com.init.agros.view.granos.PanelSemillasCereales panelSemillasCereales1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaImporteTotal;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFechaRegistro;
    // End of variables declaration//GEN-END:variables
}
