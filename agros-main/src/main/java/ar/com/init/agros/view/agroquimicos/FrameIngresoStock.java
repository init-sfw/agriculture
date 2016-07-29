package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.controller.ServicioJpaController;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.IngresoStock;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.agroquimicos.model.IngresoStockTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI FrameIngresoStock
 *
 * @author gmatheu
 * @version 12/06/2009
 */
public class FrameIngresoStock extends javax.swing.JFrame implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private IngresoStockTableModel ingresoStockTableModel;
    private MovimientoStockJpaController ingresoStockJpaController;
    private DecimalInputVerifier cantidadInputVerifier;
    private DecimalInputVerifier costoInputVerifier;

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameIngresoStock(IngresoStock ingresoStock)
    {
        this();
        disableFieldsAndButtons();
        setIngresoStock(ingresoStock);
    }

    public void disableFieldsAndButtons()
    {
        jPanelNuevo.setVisible(false);
        oKCancelCleanPanel.setVisible(false, true, false);
        jXTableValores.setEnabled(false);
        jComboBoxAgroquimicos.setEnabled(false);
        jComboBoxDeposito.setEnabled(false);
        jComboBoxProveedores.setEnabled(false);
        jTextFieldObservaciones.setEnabled(false);
        jTextFieldRemito.setEnabled(false);
        singleDateChooser.setEnabled(false);
        panelObservacion.disableFields();
        panelValorMonedaCosto.setEnabled(false);
        panelValorUnidadCantidad.setEnabled(false, false);
        panelValorUnidadStockMinimo.setEnabled(false, false);
        addRemovePanel.setEnabled(false);
    }

    /** Crea una nueva GUI tipo FrameIngresoStock */
    public FrameIngresoStock()
    {
        GUIUtility.initWindow(this);
        initComponents();

        this.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        addRemovePanel.addActionListener(new AddRemoveEventControl());
        oKCancelCleanPanel.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel.setOwner(this);

        this.getRootPane().registerKeyboardAction(oKCancelCleanPanel.createEscapeAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        UpdatableSubject.addUpdatableListener(this);

        ingresoStockTableModel = new IngresoStockTableModel();
        jXTableValores.setModel(ingresoStockTableModel);

        panelValorUnidadCantidad.setFrameNotifier(frameNotifier);
        cantidadInputVerifier = new DecimalInputVerifier(frameNotifier, false);
        panelValorUnidadCantidad.setInputVerifier(cantidadInputVerifier);
        panelValorUnidadCantidad.setEnabled(true, false);

        panelValorMonedaCosto.setFrameNotifier(frameNotifier);
        costoInputVerifier = new DecimalInputVerifier(frameNotifier, false);
        panelValorMonedaCosto.setInputVerifier(costoInputVerifier);

        ingresoStockJpaController = new MovimientoStockJpaController();

        panelValorUnidadCantidad.setFrameNotifier(frameNotifier);
        panelValorUnidadCantidad.setInputVerifier(new DecimalInputVerifier(frameNotifier, false));

        panelValorUnidadStockMinimo.setFrameNotifier(frameNotifier);
        panelValorUnidadStockMinimo.setInputVerifier(new DecimalInputVerifier(frameNotifier, false));
        panelValorUnidadStockMinimo.setEnabled(true, false);

        try {
            refreshUI();
            jXTableValores.packAll();
        }
        catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(FrameIngresoStock.class, e);
        }
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        frameNotifier.showOkMessage();
    }

    public void setIngresoStock(IngresoStock ingreso)
    {
        ingresoStockTableModel.setData(ingreso.getCastedDetalles());
        panelObservacion.setObservacion(ingreso.getObservaciones());
        singleDateChooser.setDate(ingreso.getFecha());
    }

    public void refresh()
    {
        refreshCombos();
    }

    private void refreshCombos()
    {
        ServicioJpaController proveedorController = new ServicioJpaController();
        GUIUtility.refreshComboBox(proveedorController.findByTipo(TipoServicio.PROVEEDOR_INSUMOS), jComboBoxProveedores);

        DepositoJpaController depositoController = new DepositoJpaController();
        GUIUtility.refreshComboBox(depositoController.findEntities(), jComboBoxDeposito);

        AgroquimicoJpaController agroquimicoController = new AgroquimicoJpaController();
        GUIUtility.refreshComboBox(agroquimicoController.findEntities(), jComboBoxAgroquimicos);
        setDefaultCosto();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableComboBoxRenderer = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        jPanelNuevo = new javax.swing.JPanel();
        addRemovePanel = new ar.com.init.agros.util.gui.components.buttons.AddRemovePanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelAgroquimicos = new javax.swing.JLabel();
        jComboBoxAgroquimicos = new javax.swing.JComboBox();
        jLabelCantidad = new javax.swing.JLabel();
        panelValorUnidadCantidad = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jLabelCosto = new javax.swing.JLabel();
        jLabelDeposito = new javax.swing.JLabel();
        jComboBoxDeposito = new javax.swing.JComboBox();
        jTextFieldRemito = new javax.swing.JTextField();
        jLabelRemito = new javax.swing.JLabel();
        jComboBoxProveedores = new javax.swing.JComboBox();
        jLabelProveedor = new javax.swing.JLabel();
        panelValorUnidadStockMinimo = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jLabelStockMinimo = new javax.swing.JLabel();
        panelValorMonedaCosto = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldObservaciones = new javax.swing.JTextField();
        jLabelFechaDetalle = new javax.swing.JLabel();
        singleDateChooserDetalle = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jPanelValores = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableValores = new org.jdesktop.swingx.JXTable();
        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jXPanelIngreso = new org.jdesktop.swingx.JXPanel();
        jLabelFecha = new javax.swing.JLabel();
        singleDateChooser = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        panelObservacion = new ar.com.init.agros.view.components.panels.PanelObservacion();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameIngresoStock.class);
        listableComboBoxRenderer.setText(resourceMap.getString("listableComboBoxRenderer.text")); // NOI18N
        listableComboBoxRenderer.setName("listableComboBoxRenderer"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelNuevo.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelNuevo.border.title"))); // NOI18N
        jPanelNuevo.setName("jPanelNuevo"); // NOI18N

        addRemovePanel.setName("addRemovePanel"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jLabelAgroquimicos.setText(resourceMap.getString("jLabelAgroquimicos.text")); // NOI18N
        jLabelAgroquimicos.setName("jLabelAgroquimicos"); // NOI18N

        jComboBoxAgroquimicos.setName("jComboBoxAgroquimicos"); // NOI18N
        jComboBoxAgroquimicos.setRenderer(listableComboBoxRenderer);
        jComboBoxAgroquimicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAgroquimicosActionPerformed(evt);
            }
        });

        jLabelCantidad.setText(resourceMap.getString("jLabelCantidad.text")); // NOI18N
        jLabelCantidad.setName("jLabelCantidad"); // NOI18N

        panelValorUnidadCantidad.setName("panelValorUnidadCantidad"); // NOI18N

        jLabelCosto.setText(resourceMap.getString("jLabelCosto.text")); // NOI18N
        jLabelCosto.setName("jLabelCosto"); // NOI18N

        jLabelDeposito.setText(resourceMap.getString("jLabelDeposito.text")); // NOI18N
        jLabelDeposito.setName("jLabelDeposito"); // NOI18N

        jComboBoxDeposito.setName("jComboBoxDeposito"); // NOI18N
        jComboBoxDeposito.setRenderer(listableComboBoxRenderer);
        jComboBoxDeposito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDepositoActionPerformed(evt);
            }
        });

        jTextFieldRemito.setText(resourceMap.getString("jTextFieldRemito.text")); // NOI18N
        jTextFieldRemito.setName("jTextFieldRemito"); // NOI18N

        jLabelRemito.setText(resourceMap.getString("jLabelRemito.text")); // NOI18N
        jLabelRemito.setName("jLabelRemito"); // NOI18N

        jComboBoxProveedores.setName("jComboBoxProveedores"); // NOI18N
        jComboBoxProveedores.setRenderer(listableComboBoxRenderer);

        jLabelProveedor.setText(resourceMap.getString("jLabelProveedor.text")); // NOI18N
        jLabelProveedor.setName("jLabelProveedor"); // NOI18N

        panelValorUnidadStockMinimo.setName("panelValorUnidadStockMinimo"); // NOI18N

        jLabelStockMinimo.setText(resourceMap.getString("jLabelStockMinimo.text")); // NOI18N
        jLabelStockMinimo.setName("jLabelStockMinimo"); // NOI18N

        panelValorMonedaCosto.setName("panelValorMonedaCosto"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextFieldObservaciones.setText(resourceMap.getString("jTextFieldObservaciones.text")); // NOI18N
        jTextFieldObservaciones.setName("jTextFieldObservaciones"); // NOI18N

        jLabelFechaDetalle.setText(resourceMap.getString("jLabelFechaDetalle.text")); // NOI18N
        jLabelFechaDetalle.setName("jLabelFechaDetalle"); // NOI18N

        singleDateChooserDetalle.setName("singleDateChooserDetalle"); // NOI18N
        singleDateChooserDetalle.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                singleDateChooserDetallePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelAgroquimicos)
                    .addComponent(jLabelDeposito)
                    .addComponent(jLabelRemito)
                    .addComponent(jLabelProveedor)
                    .addComponent(jLabelStockMinimo)
                    .addComponent(jLabelCantidad)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabelCosto))
                    .addComponent(jLabelFechaDetalle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxProveedores, 0, 220, Short.MAX_VALUE)
                    .addComponent(jTextFieldRemito, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(panelValorUnidadCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(jComboBoxAgroquimicos, 0, 220, Short.MAX_VALUE)
                    .addComponent(jComboBoxDeposito, 0, 220, Short.MAX_VALUE)
                    .addComponent(panelValorUnidadStockMinimo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(panelValorMonedaCosto, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(jTextFieldObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(singleDateChooserDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabelFechaDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(singleDateChooserDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProveedor)
                    .addComponent(jComboBoxProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRemito)
                    .addComponent(jTextFieldRemito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDeposito)
                    .addComponent(jComboBoxDeposito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAgroquimicos, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAgroquimicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelStockMinimo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelValorUnidadStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabelCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelValorUnidadCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelValorMonedaCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelNuevoLayout = new javax.swing.GroupLayout(jPanelNuevo);
        jPanelNuevo.setLayout(jPanelNuevoLayout);
        jPanelNuevoLayout.setHorizontalGroup(
            jPanelNuevoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNuevoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(addRemovePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelNuevoLayout.setVerticalGroup(
            jPanelNuevoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelNuevoLayout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addComponent(addRemovePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelValores.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelValores.border.title"))); // NOI18N
        jPanelValores.setName("jPanelValores"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTableValores.setName("jXTableValores"); // NOI18N
        jXTableValores.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableValores);

        javax.swing.GroupLayout jPanelValoresLayout = new javax.swing.GroupLayout(jPanelValores);
        jPanelValores.setLayout(jPanelValoresLayout);
        jPanelValoresLayout.setHorizontalGroup(
            jPanelValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );
        jPanelValoresLayout.setVerticalGroup(
            jPanelValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        frameNotifier.setName("frameNotifier"); // NOI18N

        jXPanelIngreso.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jXPanelIngreso.border.title"))); // NOI18N
        jXPanelIngreso.setName("jXPanelIngreso"); // NOI18N

        jLabelFecha.setText(resourceMap.getString("jLabelFecha.text")); // NOI18N
        jLabelFecha.setName("jLabelFecha"); // NOI18N

        singleDateChooser.setName("singleDateChooser"); // NOI18N
        singleDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                singleDateChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jXPanelIngresoLayout = new javax.swing.GroupLayout(jXPanelIngreso);
        jXPanelIngreso.setLayout(jXPanelIngresoLayout);
        jXPanelIngresoLayout.setHorizontalGroup(
            jXPanelIngresoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanelIngresoLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabelFecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(singleDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(562, Short.MAX_VALUE))
        );
        jXPanelIngresoLayout.setVerticalGroup(
            jXPanelIngresoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanelIngresoLayout.createSequentialGroup()
                .addGroup(jXPanelIngresoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(singleDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelObservacion.setName("panelObservacion"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelObservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXPanelIngreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelValores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXPanelIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelValores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setDefaultCosto()
    {
        Object oAgroquimico = jComboBoxAgroquimicos.getSelectedItem();
        Object oDeposito = jComboBoxDeposito.getSelectedItem();

        if (oAgroquimico instanceof Agroquimico && oDeposito instanceof Deposito) {
            Agroquimico a = (Agroquimico) oAgroquimico;
            Deposito d = (Deposito) oDeposito;

            panelValorUnidadCantidad.setSelectedUnidadMedida(a.getUnidad());

            ValorAgroquimico vd = a.getValorDeposito(d);
            if (vd.getStockMinimo() != null) {
                panelValorUnidadStockMinimo.setValorUnidad(vd.getStockMinimo());
            }
            else {
                if (a.getStockMinimo() != null) {
                    panelValorUnidadStockMinimo.setValorUnidad(a.getStockMinimo());
                }
                else {
                    panelValorUnidadStockMinimo.clear();
                    panelValorUnidadStockMinimo.setSelectedUnidadMedida(a.getUnidad());
                }
            }
        }
        else {
            panelValorUnidadCantidad.clear(false, true);
            panelValorMonedaCosto.clear();
            panelValorUnidadStockMinimo.clear();
        }
    }

    @Override
    public void refreshUI()
    {
        refreshCombos();
        panelValorUnidadCantidad.showAllUnidades();
        panelValorUnidadStockMinimo.showAllUnidades();
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private class AddRemoveEventControl extends AbstractEventControl
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemovePanel.getJButtonAdd()) {
                try {
                    DetalleIngresoStock m = new DetalleIngresoStock();
                    m.setFecha(singleDateChooserDetalle.getDate());

                    m.setRemito(jTextFieldRemito.getText());
                    if (jComboBoxProveedores.getSelectedItem() instanceof Servicio) {
                        m.setProveedor((Servicio) jComboBoxProveedores.getSelectedItem());
                    }
                    else {
                        m.setProveedor(null);
                    }
                    ValorAgroquimico valorDepositoDestino = new ValorAgroquimico(null, null, null);
                    if (jComboBoxDeposito.getSelectedItem() instanceof Deposito) {
                        valorDepositoDestino.setDeposito((Deposito) jComboBoxDeposito.getSelectedItem());
                    }
                    else {
                        valorDepositoDestino.setDeposito(null);
                    }
                    if (jComboBoxAgroquimicos.getSelectedItem() instanceof Agroquimico) {
                        valorDepositoDestino.setAgroquimico(
                                (Agroquimico) jComboBoxAgroquimicos.getSelectedItem());
                    }
                    else {
                        valorDepositoDestino.setAgroquimico(null);
                    }
                    valorDepositoDestino.setStockMinimo(panelValorUnidadStockMinimo.getValorUnidad());

                    m.setCantidad(panelValorUnidadCantidad.getValorUnidad());
                    m.setCostoTotal(panelValorMonedaCosto.getValorMoneda());
                    m.setObservaciones(jTextFieldObservaciones.getText().trim());

                    if (validateInput(valorDepositoDestino) && panelValorUnidadCantidad.verify() && panelValorMonedaCosto.verify()) {

                        valorDepositoDestino = valorDepositoDestino.getAgroquimico().getValorDeposito(
                                valorDepositoDestino.getDeposito());
                        valorDepositoDestino.setStockMinimo(panelValorUnidadStockMinimo.getValorUnidad());

                        m.setValorDeposito(valorDepositoDestino);
                        m.setCostoTotal(panelValorMonedaCosto.getValorMoneda()); //Este le aplica el costo al Agroquimico

                        if (validateInput(m)) {
                            ingresoStockTableModel.addNoPersistido(m);
                            cleanAddDetalle();
                            frameNotifier.showInformationMessage("Se agregó el Movimiento de stock");
                        }
                    }
                }
                catch (Exception ex) {
                    frameNotifier.showErrorMessage("No se pudo agregar el Movimiento de stock");
                    Logger.getLogger(FrameIngresoStock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (e.getSource() == addRemovePanel.getJButtonRemove()) {

                int[] idxs = jXTableValores.getSelectedRows();

                boolean b = false;
                if (idxs.length > 0) {

                    for (int i = 0; i < idxs.length; i++) {
                        int j = jXTableValores.convertRowIndexToModel(idxs[i]);
                        b = ingresoStockTableModel.removeNoPersistido(j) || b;
                    }

                    if (b) {
                        frameNotifier.showInformationMessage("Se borró el Movimiento de stock");
                    }
                }
            }
            else if (e.getSource() == addRemovePanel.getJButtonClean()) {
                cleanNuevo();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }
    }

    /** Clase de control de eventos que maneja los eventos de la GUI FrameIngresoStock y las validaciones de la misma */
    private class OkCleanCancelEventControl extends AbstractEventControl
    {

        /** Mï¿½todo que maneja los eventos de la GUI FrameIngresoStock
         *  @param e el evento de acciï¿½n lanzado por algï¿½n componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                FrameIngresoStock.this.clean();
                FrameIngresoStock.this.dispose();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnAceptar()) {

                IngresoStock ingreso = new IngresoStock();

                ingreso.setFecha(singleDateChooser.getDate());
                ingreso.setObservaciones(panelObservacion.getObservacion());
                ingreso.getDetalles().addAll(ingresoStockTableModel.getNoPersistidos());

                if (validateInput(ingreso) && checkDates(ingreso))
                {
                    try
                    {
                        ingresoStockJpaController.persist(ingreso);
                        frameNotifier.showInformationMessage(
                                "Se ingresó con éxito el movimiento con fecha " + GUIUtility.toMediumDate(
                                ingreso.getFecha()));
                        FrameIngresoStock.this.clean();
                    }
                    catch (InvalidStateException ex) {
                        frameNotifier.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameIngresoStock.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex) {
                        frameNotifier.showErrorMessage(ex.getMessage());
                        Logger.getLogger(FrameIngresoStock.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
                FrameIngresoStock.this.clean();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }
    }

    /** Checkea que las fechas particulares no sean anteriores a la general */
    private boolean checkDates(IngresoStock i)
    {
        Date general = i.getFecha();
        for (DetalleIngresoStock d: i.getDetalles())
        {
            if (d.getFecha().before(general))
            {
                frameNotifier.showErrorMessage("Se encontró una fecha de detalle " + GUIUtility.toMediumDate(d.getFecha()) + " anterior a la fecha general " + GUIUtility.toMediumDate(general));
                return false;
            }
        }
        return true;
    }

    /** Mï¿½todo donde se limpian todos los campos de la ventana */
    public void clean()
    {
        ingresoStockTableModel.clearNoPersistidos();
        panelObservacion.clear();
        singleDateChooser.setDate(new Date());

        cleanNuevo();
    }

    private void cleanNuevo()
    {
        cleanAddDetalle();

        jComboBoxProveedores.setSelectedItem(GUIUtility.DEFAULT_COMBO_VALUE);
        jTextFieldRemito.setText("");
    }

    private void cleanAddDetalle()
    {
        jComboBoxAgroquimicos.setSelectedItem(GUIUtility.DEFAULT_COMBO_VALUE);
        jComboBoxDeposito.setSelectedItem(GUIUtility.DEFAULT_COMBO_VALUE);
        panelValorMonedaCosto.clear();
        panelValorUnidadCantidad.clear();
        jTextFieldObservaciones.setText("");
        singleDateChooserDetalle.setDate(singleDateChooser.getDate());
    }

    private void jComboBoxAgroquimicosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxAgroquimicosActionPerformed
    {//GEN-HEADEREND:event_jComboBoxAgroquimicosActionPerformed
        setDefaultCosto();
    }//GEN-LAST:event_jComboBoxAgroquimicosActionPerformed

    private void singleDateChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_singleDateChooserPropertyChange
    {//GEN-HEADEREND:event_singleDateChooserPropertyChange
        singleDateChooserDetalle.setDate(singleDateChooser.getDate());
    }//GEN-LAST:event_singleDateChooserPropertyChange

    private void jComboBoxDepositoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxDepositoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxDepositoActionPerformed
        setDefaultCosto();
    }//GEN-LAST:event_jComboBoxDepositoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        clean();
    }//GEN-LAST:event_formWindowClosing

    private void singleDateChooserDetallePropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_singleDateChooserDetallePropertyChange
    {//GEN-HEADEREND:event_singleDateChooserDetallePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_singleDateChooserDetallePropertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemovePanel addRemovePanel;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JComboBox jComboBoxAgroquimicos;
    private javax.swing.JComboBox jComboBoxDeposito;
    private javax.swing.JComboBox jComboBoxProveedores;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelAgroquimicos;
    private javax.swing.JLabel jLabelCantidad;
    private javax.swing.JLabel jLabelCosto;
    private javax.swing.JLabel jLabelDeposito;
    private javax.swing.JLabel jLabelFecha;
    private javax.swing.JLabel jLabelFechaDetalle;
    private javax.swing.JLabel jLabelProveedor;
    private javax.swing.JLabel jLabelRemito;
    private javax.swing.JLabel jLabelStockMinimo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelNuevo;
    private javax.swing.JPanel jPanelValores;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldObservaciones;
    private javax.swing.JTextField jTextFieldRemito;
    private org.jdesktop.swingx.JXPanel jXPanelIngreso;
    private org.jdesktop.swingx.JXTable jXTableValores;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaCosto;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadCantidad;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadStockMinimo;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooser;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserDetalle;
    // End of variables declaration//GEN-END:variables
}
