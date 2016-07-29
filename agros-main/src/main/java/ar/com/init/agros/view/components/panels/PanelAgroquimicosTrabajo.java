
/*
 * PanelAgroquimicosTrabajo.java
 *
 * Created on 02-ago-2009, 18:54:03
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.namedentities.FrameNamedEntity;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.DoubleTableCellEditor;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.components.CampoSelectionChangeListener;
import ar.com.init.agros.view.components.SuperficieListener;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import ar.com.init.agros.view.trabajos.model.DetalleTrabajoTableModel;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.application.Action;
import javax.swing.SwingUtilities;

/**
 *
 * @author fbobbio
 */
public class PanelAgroquimicosTrabajo extends javax.swing.JPanel implements SuperficieListener, UpdatableListener, CampoSelectionChangeListener {

    private DetalleTrabajoTableModel detalleTrabajoTableModel;
    private DetalleTrabajo detalle;
    private FrameNotifier frameNotifier1;
    private AgroquimicoJpaController jpaControllerAgroquimico;
    private NamedEntityJpaController<MomentoAplicacion> jpaControllerMomento;
    private double superficiePlanificada;
    /** Lista de depósitos que están asociados al campo **/
    private List<Deposito> depositos;
    /** Depósito seleccionado en pantalla */
    private Deposito deposito;
    /** Variable que indica si la operación que dispara el evento de cambio de campo es de persistencia */
    private boolean persisting;
    /** Variable que nos indica si el panel está en modo de consulta por lo que no debería habilitarse */
    private boolean disabled = false;
    private List<DetalleTrabajo> detallesEliminados;

    /** Creates new form PanelAgroquimicosTrabajo */
    public PanelAgroquimicosTrabajo() {
        detalleTrabajoTableModel = new DetalleTrabajoTableModel();
        jpaControllerAgroquimico = new AgroquimicoJpaController();
        jpaControllerMomento = new NamedEntityJpaController<MomentoAplicacion>(MomentoAplicacion.class);
        detallesEliminados = new ArrayList<DetalleTrabajo>();
        initComponents();
        UpdatableSubject.addUpdatableListener(this);
        addRemoveUpdatePanel1.setBtnAddEnabled(false);
        addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
        addRemoveUpdatePanel1.setVisible(true, true, true, false);
        addRemoveUpdatePanel1.addActionListener(new AddRemoveUpdateEvent());
        jXTableDetalles.setModel(detalleTrabajoTableModel);
        jXTableDetalles.setDefaultEditor(Double.class, new DoubleTableCellEditor(frameNotifier1));
        jXTableDetalles.setDefaultEditor(ValorUnidad.class, new ValorUnidadTableCellEditor(frameNotifier1));
        panelValorUnidadAgua.setEnabled(true, false);
        panelValorUnidadAgua.setFrameNotifier(frameNotifier1);
        panelValorUnidadUnidadesTotales.setEnabled(true, false);
        panelValorUnidadUnidadesTotales.setFrameNotifier(frameNotifier1);

        GUIUtility.refreshComboBox(new ArrayList(), jComboBoxDeposito);

        ListSelectionModel rowsModel = jXTableDetalles.getSelectionModel();
        rowsModel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                if (jXTableDetalles.getSelectedRows().length > 0) {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(true);
                } else {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
                }
            }
        });

        try {
            refreshUI();
            panelValorUnidadAgua.addUnidades(MagnitudEnum.VOLUMEN.patron());
            panelValorUnidadAgua.selectUnidad(MagnitudEnum.VOLUMEN.patron());
            jXTableDetalles.packAll();
        } catch (PersistenceException e) {
            if (frameNotifier1 != null) {
                frameNotifier1.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelAgroquimicos.class, e);
        }
    }

    public double getSuperficiePlanificada() {
        return superficiePlanificada;
    }

    public void setSuperficiePlanificada(double superficiePlanificada) {
        this.superficiePlanificada = superficiePlanificada;
        reCalculateDetailsQuantities();
    }

    public FrameNotifier getFrameNotifier() {
        return frameNotifier1;
    }

    public void disableForList() {
        jComboBoxDeposito.setEnabled(false);
        cbxAgroquimicos.setEnabled(false);
        cbxMomentoDeAplicacion.setEnabled(false);
        panelValorUnidadAgua.setEnabled(false, false);
        panelValorUnidadUnidadesTotales.setEnabled(false, false);
        jXTableDetalles.setEditable(false);
    }

    public void disableFields() {
        disabled = true;
        cbxAgroquimicos.setEnabled(false);
        cbxMomentoDeAplicacion.setEnabled(false);
        jComboBoxDeposito.setEnabled(false);
        panelValorUnidadAgua.setEnabled(false, false);
        panelValorUnidadUnidadesTotales.setEnabled(false, false);
        jXTableDetalles.setEnabled(false);
        addRemoveUpdatePanel1.setEnabled(false);
    }

    public void enableFields(boolean val) {
        jComboBoxDeposito.setEnabled(val);
        if (!val) {
            cbxAgroquimicos.setEnabled(val);
            cbxMomentoDeAplicacion.setEnabled(val);
            panelValorUnidadAgua.setEnabled(val, false);
            panelValorUnidadUnidadesTotales.setEnabled(val, false);
            jXTableDetalles.setEditable(val);
        }
    }

    private ValorUnidad getAgua() {
        ValorUnidad ret = panelValorUnidadAgua.getValorUnidad();
        if (ret != null && ret.isValid()) {
            return ret;
        } else {
            return new ValorUnidad(0.0, MagnitudEnum.VOLUMEN.patron());
        }
    }

    private ValorUnidad getUnidadesTotales() {
        ValorUnidad ret = panelValorUnidadUnidadesTotales.getValorUnidad();
        if (ret != null && ret.isValid()) {
            return ret;
        } else {
            return null;
        }
    }

    private Agroquimico getSelectedAgroquimico() {
        Object aux = cbxAgroquimicos.getSelectedItem();
        if (aux instanceof Agroquimico) {
            return (Agroquimico) aux;
        } else {
            return null;
        }
    }

    private MomentoAplicacion getSelectedMomento() {
        Object aux = cbxMomentoDeAplicacion.getSelectedItem();
        if (aux instanceof MomentoAplicacion) {
            return (MomentoAplicacion) aux;
        } else {
            return null;
        }
    }

    /** Metodo que recalcula los valores de cantidad de todos los detalles cada vez q se cambia la superficie */
    private void reCalculateDetailsQuantities() {
        for (int i = 0; i < getDetallesCargados().size(); i++) {
            DetalleTrabajo det = getDetallesCargados().get(i);
            det.setSuperficiePlanificada(superficiePlanificada);
            detalleTrabajoTableModel.update(det);
        }
        jXTableDetalles.repaint();
    }

    private DetalleTrabajo getDetalle() {
        if (detalle == null) {
            detalle = new DetalleTrabajo();
        }
        detalle.setAgroquimico(getSelectedAgroquimico());
        detalle.setAgua(getAgua());
        detalle.setMomentoAplicacion(getSelectedMomento());
        detalle.setCantidadUtilizada(getUnidadesTotales());
        detalle.setDeposito(deposito);
        return detalle;
    }

    public List<DetalleTrabajo> getDetallesCargados() {
        return detalleTrabajoTableModel.getData();
    }

    public void setDetallesCargados(List<DetalleTrabajo> detallesCargados) {
        detalleTrabajoTableModel.setData(detallesCargados);
    }

    public void setFrameNotifier(FrameNotifier frameNotifier) {
        this.frameNotifier1 = frameNotifier;
        panelValorUnidadAgua.setFrameNotifier(frameNotifier1);
        panelValorUnidadUnidadesTotales.setFrameNotifier(frameNotifier1);
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
        addRemoveUpdatePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableDetalles = new org.jdesktop.swingx.JXTable();
        cbxAgroquimicos = new javax.swing.JComboBox();
        jComboBoxDeposito = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbxMomentoDeAplicacion = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelValorUnidadUnidadesTotales = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        panelValorUnidadAgua = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        btnAgregarMomento = new javax.swing.JButton();
        jLabelAvisoCantidad = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelAgroquimicosTrabajo.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        jXTableDetalles.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableDetalles);

        cbxAgroquimicos.setRenderer(listableComboBoxRenderer1);
        cbxAgroquimicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAgroquimicosActionPerformed(evt);
            }
        });

        jComboBoxDeposito.setRenderer(listableComboBoxRenderer1);
        jComboBoxDeposito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDepositoActionPerformed(evt);
            }
        });

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        cbxMomentoDeAplicacion.setRenderer(listableComboBoxRenderer1);

        jLabel2.setForeground(resourceMap.getColor("jLabel2.foreground")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(PanelAgroquimicosTrabajo.class, this);
        btnAgregarMomento.setAction(actionMap.get("addMomentoDeAplicacion")); // NOI18N
        btnAgregarMomento.setText(resourceMap.getString("btnAgregarMomento.text")); // NOI18N

        jLabelAvisoCantidad.setFont(resourceMap.getFont("jLabelAvisoCantidad.font")); // NOI18N
        jLabelAvisoCantidad.setForeground(resourceMap.getColor("jLabel2.foreground")); // NOI18N
        jLabelAvisoCantidad.setText(resourceMap.getString("jLabelAvisoCantidad.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxAgroquimicos, 0, 194, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbxMomentoDeAplicacion, 0, 117, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregarMomento))
                            .addComponent(jComboBoxDeposito, 0, 194, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelValorUnidadUnidadesTotales, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .addComponent(panelValorUnidadAgua, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                    .addComponent(jLabelAvisoCantidad, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel5)
                        .addComponent(jComboBoxDeposito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelValorUnidadUnidadesTotales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(panelValorUnidadAgua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxAgroquimicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cbxMomentoDeAplicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregarMomento))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAvisoCantidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void clear() {
        refresh();
        detalleTrabajoTableModel.setData(new ArrayList<DetalleTrabajo>());
    }

    public void refresh() {
        detalle = null;
        if (cbxAgroquimicos.getItemCount() > 0) {
            cbxAgroquimicos.setSelectedIndex(0);
        }
        if (cbxMomentoDeAplicacion.getItemCount() > 0) {
            cbxMomentoDeAplicacion.setSelectedIndex(0);
        }
        if (jComboBoxDeposito.getItemCount() > 0) {
            jComboBoxDeposito.setSelectedIndex(0);
        }
        deposito = null;
        panelValorUnidadAgua.clear(true, false);
        panelValorUnidadUnidadesTotales.clear(true, false);
    }

    public boolean isPersisting() {
        return persisting;
    }

    public void setPersisting(boolean persisting) {
        this.persisting = persisting;
    }

    @Override
    public boolean campoSelectionChanged(Campo selected) {
        if (detalleTrabajoTableModel.getData().size() > 0 && !persisting) {
            int ret = GUIUtility.askQuestionMessage(GUIUtility.getParentFrame(PanelAgroquimicosTrabajo.this), "Cambio de selección de establecimiento", "El cambio de selección de establecimiento le hará perder los detalles cargados. ¿Desea realizarlo de todas formas?");
            if (ret == JOptionPane.YES_OPTION) {
                clear();
                setCampo(selected);
                return true;
            } else {
                return false;
            }
        } else {
            setCampo(selected);
            return true;
        }
    }

    private void setCampo(Campo selected) {
        if (selected != null) {
            if (!disabled) {
                enableFields(true);
            }
            depositos = selected.getDepositos();
            GUIUtility.refreshComboBox(depositos, jComboBoxDeposito);
        } else {
            enableFields(false);
            depositos = null;
        }
    }

    @Override
    public boolean isNowVisible() {
        return this.isVisible();
    }

    private void checkStock(ValorAgroquimico valorDeposito, Agroquimico a) {
        double valorDep = 0;
        valorDep = valorDeposito.getStockActual().getValor() + getTotalDetallesBorrados(a) - getTotalDetallesNoPersistidos(a); // sumamos del stock los detalles q fueron eliminados
        if (valorDep <= valorDeposito.getStockMinimo().getValor() && valorDep > 0) {
            frameNotifier1.showRedWarningMessage("Atención: Alerta de stock mínimo del agroquímico " + a.getInformacion().getNombreComercial() + " del depósito " + deposito.getNombre());
            Toolkit.getDefaultToolkit().beep();
            addRemoveUpdatePanel1.setBtnAddEnabled(true);
        } else {
            if (valorDep <= 0) {
                frameNotifier1.showErrorMessage("No hay Stock suficiente del agroquímico " + a.getInformacion().getNombreComercial() + " en el depósito " + deposito.getNombre() + " para realizar el trabajo");
                addRemoveUpdatePanel1.setBtnAddEnabled(false);
                return;
            }
            frameNotifier1.showOkMessage();
            addRemoveUpdatePanel1.setBtnAddEnabled(true);
        }
    }

    private double getTotalDetallesBorrados(Agroquimico a) {
        double ret = 0;
        for (DetalleTrabajo d : detallesEliminados) {
            if (d.getAgroquimico().equals(a)) {
                ret += d.getCantidadUtilizada().getValor();
            }
        }
        return ret;
    }

    private double getTotalDetallesNoPersistidos(Agroquimico a) {
        double ret = 0;
        ValorUnidad v = detalleTrabajoTableModel.getCantidadesAgroquimicosAUsar().get(a);
        if (v != null) {
            ret = v.getValor();
        }
        return ret;
    }

    public void enableAgroquimicos(boolean val) {
        cbxAgroquimicos.setEnabled(val);
        cbxMomentoDeAplicacion.setEnabled(val);
        panelValorUnidadAgua.setEnabled(val, false);
        panelValorUnidadUnidadesTotales.setEnabled(val, false);
        jXTableDetalles.setEditable(val);
    }

    class AddRemoveUpdateEvent extends AbstractEventControl {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonAdd()) {
                if (deposito != null) {
                    if (validateInput(getDetalle())) {
                        detalleTrabajoTableModel.add(detalle);
                        if (detallesNoExcedenStock()) {
                            reCalculateDetailsQuantities();
                            Agroquimico a = detalle.getAgroquimico();
                            ValorAgroquimico valorDeposito = deposito.getValorDeposito(a);
                            checkStock(valorDeposito, a);
                            refresh();
                        } else {
                            detalleTrabajoTableModel.remove(detalle);
                        }
                    }
                } else {
                    frameNotifier1.showErrorMessage("Debe seleccionar el depósito del cual se descontarán los agroquímicos a utilizar");
                }
            }
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonRemove()) {
                detallesEliminados.addAll(detalleTrabajoTableModel.getDetallesSeleccionados(GUIUtility.getModelSelectedRows(jXTableDetalles)));
                detalleTrabajoTableModel.removeRows(GUIUtility.getModelSelectedRows(jXTableDetalles));
            }
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonClean()) {
                clear();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier() {
            return frameNotifier1;
        }
    }

    private boolean detallesNoExcedenStock() {
        Map<Agroquimico, ValorUnidad> mapa = detalleTrabajoTableModel.getCantidadesAgroquimicosAUsar();
        for (Agroquimico a : mapa.keySet()) {
            if (!hasAvailableStock(a, mapa.get(a))) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAvailableStock(Agroquimico a, ValorUnidad totalDetallado) {
        ValorAgroquimico valorDep = deposito.getValorDeposito(a);
        double stockActualPersistido = valorDep.getStockActual().getValor().doubleValue();
        double detallesBorrados = getTotalDetallesBorrados(a);
        double totalDet = totalDetallado.getValor().doubleValue();
        double stockActualizadoSinPersistir =  stockActualPersistido + detallesBorrados - totalDet;
        double round = Math.round(stockActualizadoSinPersistir * 10000); //Se hace el rounding para evitar los valores que quedan con decimales infinitos
        double aux =  round / 10000;
        boolean ret = (aux) >= 0;
        if (!ret) {
            frameNotifier1.showErrorMessage("El depósito " + deposito.getNombre() + " no posee stock suficiente del agroquímico " + a.toString() + "\nHay disponibles: " + new ValorUnidad(aux + detalle.getCantidadUtilizada().getValor().doubleValue(), totalDetallado.getUnidad()).toString());
        }
        return ret;
    }

    @Action
    public void addMomentoDeAplicacion() {
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelAgroquimicos.class);
        FrameNamedEntity<MomentoAplicacion> f = new FrameNamedEntity<MomentoAplicacion>((Frame) SwingUtilities.getRoot(this), true, MomentoAplicacion.class);
        f.setTitle(resourceMap.getString("title"));
        f.setVisible(true);
    }

    private void cbxAgroquimicosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxAgroquimicosActionPerformed
    {//GEN-HEADEREND:event_cbxAgroquimicosActionPerformed
        if (cbxAgroquimicos.getSelectedItem() instanceof Agroquimico) {
            //TODO: revisar acá qué pasa cuando se carga un ingreso a stock estando la ventana abierta
            Agroquimico a = (Agroquimico) cbxAgroquimicos.getSelectedItem();
            panelValorUnidadUnidadesTotales.selectUnidad(a.getUnidad());
            ValorAgroquimico valorDeposito = deposito.getValorDeposito(a);
            checkStock(valorDeposito, a);
        } else {
            panelValorUnidadUnidadesTotales.selectUnidad(null);
            addRemoveUpdatePanel1.setBtnAddEnabled(false);
        }
}//GEN-LAST:event_cbxAgroquimicosActionPerformed

    private void jComboBoxDepositoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxDepositoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxDepositoActionPerformed
        if (jComboBoxDeposito.getSelectedItem() instanceof Deposito) {
            deposito = (Deposito) jComboBoxDeposito.getSelectedItem();
            enableAgroquimicos(true);
        } else {
            deposito = null;
            enableAgroquimicos(false);
        }
}//GEN-LAST:event_jComboBoxDepositoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel addRemoveUpdatePanel1;
    private javax.swing.JButton btnAgregarMomento;
    private javax.swing.JComboBox cbxAgroquimicos;
    private javax.swing.JComboBox cbxMomentoDeAplicacion;
    private javax.swing.JComboBox jComboBoxDeposito;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelAvisoCantidad;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTableDetalles;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadAgua;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadUnidadesTotales;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshUI() {
        GUIUtility.refreshComboBox(jpaControllerAgroquimico.findEntities(), cbxAgroquimicos);
        GUIUtility.refreshComboBox(jpaControllerMomento.findEntities(), cbxMomentoDeAplicacion);
        panelValorUnidadUnidadesTotales.showAllUnidades();
    }

    @Override
    public void updateSuperficie(double sup) {
        setSuperficiePlanificada(sup);
    }
}
