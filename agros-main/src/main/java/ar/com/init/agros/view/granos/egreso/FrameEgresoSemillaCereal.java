package ar.com.init.agros.view.granos.egreso;

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
import ar.com.init.agros.model.almacenamiento.ValorGrano;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.inventario.cereales.DetalleEgresoCereal;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.model.inventario.granos.EgresoGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleEgresoSemilla;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.granos.PanelSemillasCereales.TipoGrano;
import ar.com.init.agros.view.granos.PanelSemillasCereales.UnidadGrano;
import ar.com.init.agros.view.granos.egreso.model.EgresoGranoTableModel;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI FrameEgresoSemillaCereal
 *
 * @author fbobbio
 * @version 14-dic-2010
 */
public class FrameEgresoSemillaCereal extends javax.swing.JFrame implements UpdatableListener {

    private static final long serialVersionUID = -1L;
    private EgresoGranoTableModel egresoGranoTableModel;
    private MovimientoGranoJpaController egresoGranoJpaController;

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameEgresoSemillaCereal(EgresoGrano egresoGrano) {
        this();
        disableFieldsAndButtons();
        setEgresoGrano(egresoGrano);
    }

    public void disableFieldsAndButtons() {
        panelSemillasCereales1.disableFields();
        jPanel2.setVisible(false);
        oKCancelCleanPanel1.setVisible(false, true, false);
        singleDateChooserFechaCreacion.setEnabled(false);
        addRemovePanel1.setEnabled(false);
        panelObservacion1.setEnabled(false);
    }

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameEgresoSemillaCereal() {
        egresoGranoTableModel = new EgresoGranoTableModel();
        GUIUtility.initWindow(this);
        initComponents();

        //this.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        addRemovePanel1.addActionListener(new AddRemoveEventControl());
        oKCancelCleanPanel1.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel1.setOwner(this);

        UpdatableSubject.addUpdatableListener(this);

        setFrameNotifier1(frameNotifier1);
        jXTableDetalles.setModel(egresoGranoTableModel);

        egresoGranoJpaController = new MovimientoGranoJpaController();

        panelValorMonedaImporte.setFrameNotifier(frameNotifier1);
        panelValorMonedaImporte.setInputVerifier(new DecimalInputVerifier(frameNotifier1, false));

        try {
            refreshUI();
            jXTableDetalles.packAll();
        } catch (PersistenceException e) {
            if (frameNotifier1 != null) {
                frameNotifier1.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(FrameEgresoSemillaCereal.class, e);
        }
    }

    public FrameNotifier getFrameNotifier1() {
        return frameNotifier1;
    }

    public void setFrameNotifier1(FrameNotifier frameNotifier1) {
        this.frameNotifier1 = frameNotifier1;
        panelSemillasCereales1.setFrameNotifier(frameNotifier1);
        panelValorMonedaImporte.setFrameNotifier(frameNotifier1);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        frameNotifier1.showOkMessage();
    }

    public void setEgresoGrano(EgresoGrano egreso) {
        egresoGranoTableModel.setData(egreso.getCastedDetalles());
        singleDateChooserFechaCreacion.setDate(egreso.getFecha());
        panelObservacion1.setObservacion(egreso.getObservaciones());
    }

    public void refresh() {
        refreshCombos();
    }

    private void refreshCombos() {
        ServicioJpaController destinoController = new ServicioJpaController();
        GUIUtility.refreshComboBox(destinoController.findByTipo(TipoServicio.COMPRADOR), jComboBoxDestino);
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
        jLabel1 = new javax.swing.JLabel();
        singleDateChooserFechaCreacion = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jPanel2 = new javax.swing.JPanel();
        panelSemillasCereales1 = new ar.com.init.agros.view.granos.PanelSemillasCereales();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldCartaDePorte = new javax.swing.JTextField();
        jComboBoxDestino = new javax.swing.JComboBox();
        panelValorMonedaImporte = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        addRemovePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableDetalles = new org.jdesktop.swingx.JXTable();
        panelObservacion1 = new ar.com.init.agros.view.components.panels.PanelObservacion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameEgresoSemillaCereal.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(singleDateChooserFechaCreacion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(671, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(singleDateChooserFechaCreacion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        jComboBoxDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDestinoActionPerformed(evt);
            }
        });

        panelValorMonedaImporte.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(panelSemillasCereales1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .add(325, 325, 325))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(47, 47, 47)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jComboBoxDestino, 0, 380, Short.MAX_VALUE)
                    .add(jTextFieldCartaDePorte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(panelValorMonedaImporte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addRemovePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(panelSemillasCereales1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addRemovePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jTextFieldCartaDePorte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jComboBoxDestino, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel4)
                            .add(panelValorMonedaImporte, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
        );

        jScrollPane1.setViewportView(jXTableDetalles);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelObservacion1.TabConstraints.tabTitle"), panelObservacion1); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxDestinoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxDestinoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxDestinoActionPerformed
        if (jComboBoxDestino.getSelectedItem() instanceof Servicio) {
            panelValorMonedaImporte.setEnabled(true);
        } else {
            panelValorMonedaImporte.setEnabled(false);
            panelValorMonedaImporte.clear();
        }
    }//GEN-LAST:event_jComboBoxDestinoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        clean();
    }//GEN-LAST:event_formWindowClosing

    private class AddRemoveEventControl extends AbstractEventControl {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addRemovePanel1.getJButtonAdd()) {
                try {
                    DetalleEgresoGrano detalle = null;
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.SIN_SELECCION)) {
                        detalle = null;
                        frameNotifier1.showErrorMessage("Debe seleccionar si el egreso será una Semilla o un Cereal");
                        return;
                    }
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.CEREAL)) {
                        detalle = new DetalleEgresoCereal();

                        ValorAlmacenamientoJpaController<ValorCereal> almController = new ValorAlmacenamientoJpaController<ValorCereal>(ValorCereal.class);
                        ValorCereal vc = almController.find((Silo) getAlmacenamiento(), getCultivo());

                        detalle.setValor(vc);
                    }
                    if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.SEMILLA)) {
                        detalle = new DetalleEgresoSemilla();

                        ValorAlmacenamientoJpaController<ValorSemilla> almController = new ValorAlmacenamientoJpaController<ValorSemilla>(ValorSemilla.class);
                        ValorSemilla vs = almController.find(getAlmacenamiento(), getVariedadCultivo());

                        detalle.setValor(vs);
                    }
                    detalle.setFecha(panelSemillasCereales1.getFecha());
                    detalle.setEstablecimiento(panelSemillasCereales1.getEstablecimiento());
                    detalle.setHumedad(panelSemillasCereales1.getHumedad());
                    detalle.setServicio(getComprador());
                    detalle.setCartaPorte(jTextFieldCartaDePorte.getText().trim());
                    detalle.setImporte(panelValorMonedaImporte.getValorMoneda());
                    if (panelSemillasCereales1.getUnidadIngreso().equals(UnidadGrano.SIN_SELECCION)) {
                        detalle = null;
                        frameNotifier1.showErrorMessage("Debe seleccionar si el egreso será en bolsas o en Kg");
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
                    if (validarDestino()) {
                        if (validateInput(detalle.getValor()) && validateInput(detalle)) {
                            /** Informa si no hay stock del grano en el almacenamiento.
                             * Si hay pueden pasar dos cosas:
                             * 1) Informa cuánto hay si el [detalle actual + noPersistidos] es menor a lo que se trata de sacar
                             * 2) Se agrega el nuevo detalle de egreso, dando el mensaje de éxito
                             */
                            if (validarStock(detalle)) {
                                egresoGranoTableModel.addNoPersistido(detalle);
                                cleanAddDetalle();
                                frameNotifier1.showInformationMessage("Se agregó el Egreso de Semilla/Cereal");
                            }

                        }
                    }
                } catch (Exception ex) {
                    frameNotifier1.showErrorMessage("No se pudo agregar el Egreso de Semilla/Cereal");
                    Logger.getLogger(FrameEgresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (e.getSource() == addRemovePanel1.getJButtonRemove()) {

                int[] idxs = jXTableDetalles.getSelectedRows();

                boolean b = false;
                if (idxs.length > 0) {

                    for (int i = 0; i < idxs.length; i++) {
                        int j = jXTableDetalles.convertRowIndexToModel(idxs[i]);
                        b = egresoGranoTableModel.removeNoPersistido(j) || b;
                    }

                    if (b) {
                        frameNotifier1.showInformationMessage("Se borró el Egreso de Semilla/Cereal");
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

        private boolean validarDestino() {
            if (panelSemillasCereales1.getTipoIngreso().equals(TipoGrano.CEREAL) && getComprador() == null) {
                frameNotifier1.showErrorMessage("Si eligió hacer un Egreso de Cereal, debe indicar al comprador destino");
                return false;
            }
            return true;
        }

        private boolean validarStock(DetalleEgresoGrano detalle) {
            ValorAlmacenamientoJpaController valorAlmacenamientoController = new ValorAlmacenamientoJpaController(ValorGrano.class);
            Almacenamiento a = detalle.getValor().getAlmacenamiento();
            if (detalle instanceof DetalleEgresoCereal) {
                ValorCereal vc = valorAlmacenamientoController.find((Silo) a, (Cultivo) detalle.getDetalle());
                if (vc == null) {
                    frameNotifier1.showErrorMessage("No hay stock del Cereal " + detalle.getDetalle() + " en el almacenamiento " + detalle.getValor().getAlmacenamiento() + " para egresar");
                    return false;
                }
                if ((sumaDetallesNoPersistidos(detalle) + detalle.getCantidad().getValor().doubleValue()) > vc.getStockActual().getValor().doubleValue()) {
                    frameNotifier1.showErrorMessage("No hay stock suficiente del Cereal " + detalle.getDetalle() + ", la existencia actual es de: " + (new ValorUnidad(calcularStockActual(vc) - sumaDetallesNoPersistidos(detalle), MagnitudEnum.PESO.patron())));
                    return false;
                } else {
                    return true;
                }
            }
            if (detalle instanceof DetalleEgresoSemilla) {
                ValorSemilla vc = valorAlmacenamientoController.find(a, (VariedadCultivo) detalle.getDetalle());
                if (vc == null) {
                    VariedadCultivo varCul = (VariedadCultivo) detalle.getDetalle();
                    frameNotifier1.showErrorMessage("No hay stock de la Semilla " + varCul.getCultivo() + " - " + varCul + " en el almacenamiento " + detalle.getValor().getAlmacenamiento() + " para egresar");
                    return false;
                }
                // pregunto si la suma de los detalles ya cargados junto con el que se está ingresando es mayor al stock actual del almacenamiento
                if ((sumaDetallesNoPersistidos(detalle) + detalle.getCantidad().getValor().doubleValue()) > vc.getStockActual().getValor().doubleValue()) {
                    frameNotifier1.showErrorMessage("No hay stock suficiente de la Semilla " + detalle.getDetalle() + ", la existencia actual es de: " + (new ValorUnidad(calcularStockActual(vc) - sumaDetallesNoPersistidos(detalle), MagnitudEnum.PESO.patron())));
                    return false;
                } else {
                    return true;
                }
            }
            frameNotifier1.showErrorMessage("Se produjo un error interno al intentar checkear el stock en: " + FrameEgresoSemillaCereal.class.getName());
            return false;
        }

        /** Método que suma la cantidad de kg a ingresar para detalles del mismo almacenamiento-grano */
        private double sumaDetallesNoPersistidos(DetalleEgresoGrano det) {
            double acum = 0;
            for (DetalleEgresoGrano d : egresoGranoTableModel.getData()) {
                //Si el detalle posee el mismo grano y el mismo almacenamiento, sumamos
                if (d.getDetalle().equals(det.getDetalle())
                        && d.getValor().getAlmacenamiento().equals(det.getValor().getAlmacenamiento())
                        && d.getCantidad().getValor() != null) {
                    acum += d.getCantidad().getValor().doubleValue();
                }
            }
            return acum;
        }

        /** Método que calcula el stock actual del almacenamiento y devuelve el valor en kilogramos tipo double */
        private double calcularStockActual(ValorGrano vc) {
            if (vc != null && vc.getStockActual() != null && vc.getStockActual().getValor() != null) {
                return vc.getStockActual().getValor().doubleValue();
            } else {
                return 0;
            }
        }
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

    private Servicio getComprador() {
        if (jComboBoxDestino.getSelectedItem() instanceof Servicio) {
            Servicio s = (Servicio) jComboBoxDestino.getSelectedItem();
            if (s.getTipo().equals(TipoServicio.COMPRADOR)) {
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
                FrameEgresoSemillaCereal.this.clean();
                FrameEgresoSemillaCereal.this.dispose();
            } else if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {

                EgresoGrano egreso = new EgresoGrano();

                egreso.setFecha(singleDateChooserFechaCreacion.getDate());
                egreso.getDetalles().addAll(egresoGranoTableModel.getNoPersistidos());
                egreso.setObservaciones(panelObservacion1.getObservacion());

                if (validateInput(egreso) && validateDates()) {
                    try {
                        egresoGranoJpaController.persist(egreso);
                        frameNotifier1.showInformationMessage(
                                "Se ingresó con éxito el egreso con fecha " + GUIUtility.toMediumDate(
                                egreso.getFecha()));
                        FrameEgresoSemillaCereal.this.clean();
                    } catch (InvalidStateException ex) {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameEgresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameEgresoSemillaCereal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                FrameEgresoSemillaCereal.this.clean();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier() {
            return frameNotifier1;
        }

        private boolean validateDates() {
            Date fechaRegistro = singleDateChooserFechaCreacion.getDate();
            for (DetalleEgresoGrano d : egresoGranoTableModel.getData()) {
                if (d.getFecha().before(fechaRegistro)) {
                    frameNotifier1.showErrorMessage("Existen fechas de detalles previas a la fecha general del egreso");
                    return false;
                }
            }
            return true;
        }
    }

    /** Método donde se limpian todos los campos de la ventana */
    public void clean() {
        panelSemillasCereales1.clear();
        panelValorMonedaImporte.clear();
        jComboBoxDestino.setSelectedIndex(0);
        egresoGranoTableModel.clearNoPersistidos();
        panelObservacion1.clear();

        cleanNuevo();
    }

    private void cleanNuevo() {
        cleanAddDetalle();
    }

    private void cleanAddDetalle() {
        panelSemillasCereales1.clear();
        panelValorMonedaImporte.clear();
        jComboBoxDestino.setSelectedIndex(0);
        jTextFieldCartaDePorte.setText("");
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
    private javax.swing.JComboBox jComboBoxDestino;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldCartaDePorte;
    private org.jdesktop.swingx.JXTable jXTableDetalles;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion1;
    private ar.com.init.agros.view.granos.PanelSemillasCereales panelSemillasCereales1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaImporte;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFechaCreacion;
    // End of variables declaration//GEN-END:variables
}
