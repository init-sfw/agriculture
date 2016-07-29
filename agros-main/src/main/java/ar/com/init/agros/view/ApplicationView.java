/*
 * ApplicationView.java
 */
package ar.com.init.agros.view;

import ar.com.init.agros.controller.InformacionAgroquimicoJpaController;
import ar.com.init.agros.controller.PlanificacionAgroquimicoJpaController;
import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.controller.TrabajoLoteJpaController;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.namedentities.FrameListNamedEntity;
import ar.com.init.agros.util.gui.components.namedentities.FrameNamedEntity;
import ar.com.init.agros.util.gui.db.DatabaseUtil;
import ar.com.init.agros.view.agroquimicos.AgroquimicoFrameListEntity;
import ar.com.init.agros.view.agroquimicos.AgroquimicosTotalizer;
import ar.com.init.agros.view.agroquimicos.DialogAgroquimico;
import ar.com.init.agros.view.agroquimicos.DialogAgroquimicoManual;
import ar.com.init.agros.view.agroquimicos.DialogAjusteInventario;
import ar.com.init.agros.view.agroquimicos.DialogCancelarIngresoStock;
import ar.com.init.agros.view.agroquimicos.DialogMovimientoDeposito;
import ar.com.init.agros.view.agroquimicos.FrameIngresoStock;
import ar.com.init.agros.view.agroquimicos.FrameRegistrarPlanificacionAgroquimico;
import ar.com.init.agros.view.agroquimicos.InformacionAgroquimicoFrameListEntity;
import ar.com.init.agros.view.agroquimicos.IngresoStockFrameListEntity;
import ar.com.init.agros.view.agroquimicos.MovimientoDepositoFrameListEntity;
import ar.com.init.agros.view.agroquimicos.PlanificacionesFrameListEntity;
//import ar.com.init.agros.view.agroquimicos.reportes.AgroquimicosPlanificadosReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.AgroquimicosPlanificadosReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.ComparacionCostosAgroquimicosReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.ConsultarPlanificacionesReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.CostoAgroquimicosReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.HistoricoTrabajoCampoLoteReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.PlanificadoVSFumigadoReportFrame;
import ar.com.init.agros.view.agroquimicos.reportes.StockAlmacenamientosReportFrame;
import ar.com.init.agros.view.almacenamientos.AlmacenamientoFrameListEntity;
import ar.com.init.agros.view.almacenamientos.DialogAlmacenamiento;
import ar.com.init.agros.view.campanias.CampaniaFrameListEntity;
import ar.com.init.agros.view.campanias.DialogCampania;
import ar.com.init.agros.view.campanias.DialogCierreDeCampania;
import ar.com.init.agros.view.campanias.reportes.CostosDeCampaniaReportFrame;
//import ar.com.init.agros.view.campanias.reportes.MargenBrutoCampaniaReportFrame;
import ar.com.init.agros.view.campo.CampoFrameListEntity;
import ar.com.init.agros.view.campo.DialogCampo;
import ar.com.init.agros.view.campo.reportes.CostosPorEstablecimientoReportFrame;
//import ar.com.init.agros.view.campo.reportes.MargenBrutoEstablecimientoReportFrame;
import ar.com.init.agros.view.campo.reportes.MargenBrutoEstablecimientoReportFrame;
import ar.com.init.agros.view.configuracion.DialogConfiguracion;
import ar.com.init.agros.view.costos.DialogTipoCosto;
import ar.com.init.agros.view.costos.TipoCostoFrameListEntity;
import ar.com.init.agros.view.cultivos.CultivoFrameListEntity;
import ar.com.init.agros.view.cultivos.DialogCultivo;
import ar.com.init.agros.view.granos.egreso.EgresoSemillaCerealFrameListEntity;
import ar.com.init.agros.view.granos.egreso.FrameEgresoSemillaCereal;
import ar.com.init.agros.view.granos.ingreso.FrameIngresoSemillaCereal;
import ar.com.init.agros.view.granos.ingreso.IngresoSemillaCerealFrameListEntity;
import ar.com.init.agros.view.ingresos.DialogTipoIngreso;
import ar.com.init.agros.view.ingresos.TipoIngresoFrameListEntity;
import ar.com.init.agros.view.lluvias.DialogLluvia;
import ar.com.init.agros.view.lluvias.LluviaFrameListEntity;
import ar.com.init.agros.view.lluvias.LluviasTotalizer;
import ar.com.init.agros.view.lluvias.reportes.LluviasReportFrame;
import ar.com.init.agros.view.servicios.DialogProveedor;
import ar.com.init.agros.view.servicios.FrameReporteEmpresasDeServicios;
import ar.com.init.agros.view.servicios.ProveedorFrameListEntity;
import ar.com.init.agros.view.siembras.DialogSiembra;
import ar.com.init.agros.view.siembras.SiembraFrameListEntity;
import ar.com.init.agros.view.siembras.reportes.SiembrasReportFrame;
import ar.com.init.agros.view.trabajos.FrameRegistrarTrabajoDeLote;
import ar.com.init.agros.view.trabajos.TrabajoFrameListEntity;
import java.awt.Window;
import java.util.EventObject;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application.ExitListener;
import org.jdesktop.application.Task;

/**
 * The application's main frame.
 */
public class ApplicationView extends FrameView {

    public ApplicationView(SingleFrameApplication app) {
        super(app);
        initComponents();
        this.getFrame().setIconImage(getResourceMap().getImageIcon("frame.icon").getImage());
        this.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
        getFrame().pack();


        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        this.getApplication().addExitListener(new ExitListener() {

            @Override
            public boolean canExit(EventObject event) {
                int r = JOptionPane.showConfirmDialog(getFrame(),
                        "¿Está seguro que desea cerrar la aplicación?", "Cerrando Osiris",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                return (JOptionPane.YES_OPTION == r);
            }

            @Override
            public void willExit(EventObject event) {
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = Application.getApplication().getMainFrame();
            aboutBox = new ApplicationAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Application.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblImage2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        itemConfiguracion = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        itemRealizarCopiaBD = new javax.swing.JMenuItem();
        itemRestaurarBD = new javax.swing.JMenuItem();
        itemImportarBD = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        menuAdministracion = new javax.swing.JMenu();
        menuProveedores = new javax.swing.JMenu();
        itemRegistrarProveedor = new javax.swing.JMenuItem();
        itemConsultarProveedores = new javax.swing.JMenuItem();
        menuDepositos = new javax.swing.JMenu();
        itemRegistrarDeposito = new javax.swing.JMenuItem();
        itemConsultarDepositos = new javax.swing.JMenuItem();
        menuTiposIngreso = new javax.swing.JMenu();
        itemRegistrarTipoIngreso = new javax.swing.JMenuItem();
        itemConsultarTiposIngreso = new javax.swing.JMenuItem();
        menuTiposCosto = new javax.swing.JMenu();
        itemRegistrarTipoCosto = new javax.swing.JMenuItem();
        itemConsultarTiposCosto = new javax.swing.JMenuItem();
        menuMomentoAplicacion = new javax.swing.JMenu();
        itemRegistrarMomentoAplicacion = new javax.swing.JMenuItem();
        itemConsultarMomentoAplicacion = new javax.swing.JMenuItem();
        menuCampo = new javax.swing.JMenu();
        itemRegistrarCampo = new javax.swing.JMenuItem();
        itemConsultarCampos = new javax.swing.JMenuItem();
        menuCampania = new javax.swing.JMenu();
        itemRegistrarCampania = new javax.swing.JMenuItem();
        itemConsultarCampanias = new javax.swing.JMenuItem();
        itemRegistrarCierreDeCampania = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        menuPlanificacion = new javax.swing.JMenu();
        itemMenuRegistrarPlanificacionAgroquimico = new javax.swing.JMenuItem();
        itemMenuConsultarPlanificaciones = new javax.swing.JMenuItem();
        menuSiembra = new javax.swing.JMenu();
        itemMenuRegistrarSiembra = new javax.swing.JMenuItem();
        itemMenuConsultarSiembras = new javax.swing.JMenuItem();
        itemRegistrarCultivos = new javax.swing.JMenuItem();
        itemConsultarCultivos = new javax.swing.JMenuItem();
        menuPulverizaciones = new javax.swing.JMenu();
        itemRegistrarTrabajo = new javax.swing.JMenuItem();
        itemConsultarTrabajo = new javax.swing.JMenuItem();
        menuAgroquimicos = new javax.swing.JMenu();
        itemMenuRegistrarAgroquimico = new javax.swing.JMenuItem();
        itemMenuConsultarAgroquimicos = new javax.swing.JMenuItem();
        itemSeleccionarAgroquimicosUso = new javax.swing.JMenuItem();
        itemConsultarAgroquimicosUso = new javax.swing.JMenuItem();
        menuStock = new javax.swing.JMenu();
        itemIngresoStock = new javax.swing.JMenuItem();
        itemAjusteInventario = new javax.swing.JMenuItem();
        itemRegistrarMovimientoDeposito = new javax.swing.JMenuItem();
        itemCancelarIngresoStock = new javax.swing.JMenuItem();
        jMenuItemTotalizadorAgroquim = new javax.swing.JMenuItem();
        itemConsultarMovimientosDepositos = new javax.swing.JMenuItem();
        itemConsultarIngresosAgroquimicos = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        itemIngresoSemCer = new javax.swing.JMenuItem();
        itemConsultarIngresoSemCer = new javax.swing.JMenuItem();
        itemEgresoSemCer = new javax.swing.JMenuItem();
        itemConsultarEgresoSemCer = new javax.swing.JMenuItem();
        menuLluvia = new javax.swing.JMenu();
        itemRegistrarLluvia = new javax.swing.JMenuItem();
        itemConsultarLluvias = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuReportes = new javax.swing.JMenu();
        itemReporteStockAlmacenamientos = new javax.swing.JMenuItem();
        itemReporteLluvias = new javax.swing.JMenuItem();
        itemReporteComparacionCostoAgroquimico = new javax.swing.JMenuItem();
        itemReporteHistoricoTrabajo = new javax.swing.JMenuItem();
        itemReportePlanificacion = new javax.swing.JMenuItem();
        itemReporteCostoAgroquimico = new javax.swing.JMenuItem();
        itemReporteCostosCampania = new javax.swing.JMenuItem();
        itemReporteCostosEstablecimiento = new javax.swing.JMenuItem();
        itemReporteSiembra = new javax.swing.JMenuItem();
        itemReporteComparativoInsumos = new javax.swing.JMenuItem();
        itemReporteAgroquimicosPlanificados = new javax.swing.JMenuItem();
        itemReporteMargenBrutoCampania = new javax.swing.JMenuItem();
        itemReporteEmpresasDeServicio = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jMenuItemAutogestion = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        sugerenciasMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jMenu1 = new javax.swing.JMenu();
        itemFormaFumigacion = new javax.swing.JMenuItem();
        itemTipoTrabajo = new javax.swing.JMenuItem();
        itemTipoCampania = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        itemRegistrarPlaga = new javax.swing.JMenuItem();
        itemConsultarPlagas = new javax.swing.JMenuItem();
        itemRegistrarTipoPlaga = new javax.swing.JMenuItem();
        itemConsultarTiposPlaga = new javax.swing.JMenuItem();
        menuTipoSemilla = new javax.swing.JMenu();
        itemRegistrarTipoSemilla = new javax.swing.JMenuItem();
        itemConsultarTipoSemilla = new javax.swing.JMenuItem();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(ApplicationView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                mainPanelComponentShown(evt);
            }
        });
        mainPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        lblImage2.setIcon(resourceMap.getIcon("lblImage2.icon")); // NOI18N
        lblImage2.setText(resourceMap.getString("lblImage2.text")); // NOI18N
        lblImage2.setName("lblImage2"); // NOI18N

        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(lblImage2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 190, Short.MAX_VALUE)
                .add(jLabel1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblImage2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        mainPanel.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        mainPanel.add(jPanel2, java.awt.BorderLayout.CENTER);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setIcon(resourceMap.getIcon("fileMenu.icon")); // NOI18N
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setToolTipText(resourceMap.getString("fileMenu.toolTipText")); // NOI18N
        fileMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fileMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(ApplicationView.class, this);
        itemConfiguracion.setAction(actionMap.get("configurar")); // NOI18N
        itemConfiguracion.setText(resourceMap.getString("itemConfiguracion.text")); // NOI18N
        itemConfiguracion.setName("itemConfiguracion"); // NOI18N
        fileMenu.add(itemConfiguracion);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        itemRealizarCopiaBD.setAction(actionMap.get("realizarCopiaBD")); // NOI18N
        itemRealizarCopiaBD.setText(resourceMap.getString("itemRealizarCopiaBD.text")); // NOI18N
        itemRealizarCopiaBD.setName("itemRealizarCopiaBD"); // NOI18N
        fileMenu.add(itemRealizarCopiaBD);

        itemRestaurarBD.setAction(actionMap.get("restaurarBD")); // NOI18N
        itemRestaurarBD.setText(resourceMap.getString("itemRestaurarBD.text")); // NOI18N
        itemRestaurarBD.setName("itemRestaurarBD"); // NOI18N
        fileMenu.add(itemRestaurarBD);

        itemImportarBD.setAction(actionMap.get("importarBD")); // NOI18N
        itemImportarBD.setText(resourceMap.getString("itemImportarBD.text")); // NOI18N
        fileMenu.add(itemImportarBD);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setIcon(resourceMap.getIcon("exitMenuItem.icon")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        menuAdministracion.setIcon(resourceMap.getIcon("menuAdministracion.icon")); // NOI18N
        menuAdministracion.setText(resourceMap.getString("menuAdministracion.text")); // NOI18N
        menuAdministracion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuAdministracion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuAdministracion.setName("menuAdministracion"); // NOI18N
        menuAdministracion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        menuProveedores.setIcon(resourceMap.getIcon("menuProveedores.icon")); // NOI18N
        menuProveedores.setText(resourceMap.getString("menuProveedores.text")); // NOI18N
        menuProveedores.setName("menuProveedores"); // NOI18N

        itemRegistrarProveedor.setAction(actionMap.get("registrarProveedor")); // NOI18N
        itemRegistrarProveedor.setText(resourceMap.getString("itemRegistrarProveedor.text")); // NOI18N
        itemRegistrarProveedor.setName("itemRegistrarProveedor"); // NOI18N
        menuProveedores.add(itemRegistrarProveedor);

        itemConsultarProveedores.setAction(actionMap.get("consultarProveedores")); // NOI18N
        itemConsultarProveedores.setText(resourceMap.getString("itemConsultarProveedores.text")); // NOI18N
        itemConsultarProveedores.setName("itemConsultarProveedores"); // NOI18N
        menuProveedores.add(itemConsultarProveedores);

        menuAdministracion.add(menuProveedores);

        menuDepositos.setIcon(resourceMap.getIcon("menuDepositos.icon")); // NOI18N
        menuDepositos.setText(resourceMap.getString("menuDepositos.text")); // NOI18N
        menuDepositos.setName("menuDepositos"); // NOI18N

        itemRegistrarDeposito.setAction(actionMap.get("registrarDeposito")); // NOI18N
        itemRegistrarDeposito.setText(resourceMap.getString("itemRegistrarDeposito.text")); // NOI18N
        itemRegistrarDeposito.setName("itemRegistrarDeposito"); // NOI18N
        menuDepositos.add(itemRegistrarDeposito);

        itemConsultarDepositos.setAction(actionMap.get("consultarDepositos")); // NOI18N
        itemConsultarDepositos.setText(resourceMap.getString("itemConsultarDepositos.text")); // NOI18N
        itemConsultarDepositos.setName("itemConsultarDepositos"); // NOI18N
        menuDepositos.add(itemConsultarDepositos);

        menuAdministracion.add(menuDepositos);

        menuTiposIngreso.setIcon(resourceMap.getIcon("menuTiposIngreso.icon")); // NOI18N
        menuTiposIngreso.setText(resourceMap.getString("menuTiposIngreso.text")); // NOI18N
        menuTiposIngreso.setName("menuTiposIngreso"); // NOI18N

        itemRegistrarTipoIngreso.setAction(actionMap.get("registrarTipoIngreso")); // NOI18N
        itemRegistrarTipoIngreso.setText(resourceMap.getString("itemRegistrarTipoIngreso.text")); // NOI18N
        itemRegistrarTipoIngreso.setName("itemRegistrarTipoIngreso"); // NOI18N
        menuTiposIngreso.add(itemRegistrarTipoIngreso);

        itemConsultarTiposIngreso.setAction(actionMap.get("consultarTiposIngreso")); // NOI18N
        itemConsultarTiposIngreso.setText(resourceMap.getString("itemConsultarTiposIngreso.text")); // NOI18N
        itemConsultarTiposIngreso.setName("itemConsultarTiposIngreso"); // NOI18N
        menuTiposIngreso.add(itemConsultarTiposIngreso);

        menuAdministracion.add(menuTiposIngreso);

        menuTiposCosto.setIcon(resourceMap.getIcon("menuTiposCosto.icon")); // NOI18N
        menuTiposCosto.setText(resourceMap.getString("menuTiposCosto.text")); // NOI18N
        menuTiposCosto.setName("menuTiposCosto"); // NOI18N

        itemRegistrarTipoCosto.setAction(actionMap.get("registrarTipoCosto")); // NOI18N
        itemRegistrarTipoCosto.setText(resourceMap.getString("itemRegistrarTipoCosto.text")); // NOI18N
        itemRegistrarTipoCosto.setName("itemRegistrarTipoCosto"); // NOI18N
        menuTiposCosto.add(itemRegistrarTipoCosto);

        itemConsultarTiposCosto.setAction(actionMap.get("consultarTiposCosto")); // NOI18N
        itemConsultarTiposCosto.setText(resourceMap.getString("itemConsultarTiposCosto.text")); // NOI18N
        itemConsultarTiposCosto.setName("itemConsultarTiposCosto"); // NOI18N
        menuTiposCosto.add(itemConsultarTiposCosto);

        menuAdministracion.add(menuTiposCosto);

        menuMomentoAplicacion.setIcon(resourceMap.getIcon("menuMomentoAplicacion.icon")); // NOI18N
        menuMomentoAplicacion.setText(resourceMap.getString("menuMomentoAplicacion.text")); // NOI18N
        menuMomentoAplicacion.setName("menuMomentoAplicacion"); // NOI18N

        itemRegistrarMomentoAplicacion.setAction(actionMap.get("registrarMomentoAplicacion")); // NOI18N
        itemRegistrarMomentoAplicacion.setText(resourceMap.getString("itemRegistrarMomentoAplicacion.text")); // NOI18N
        itemRegistrarMomentoAplicacion.setName("itemRegistrarMomentoAplicacion"); // NOI18N
        menuMomentoAplicacion.add(itemRegistrarMomentoAplicacion);

        itemConsultarMomentoAplicacion.setAction(actionMap.get("consultarMomentoAplicacion")); // NOI18N
        itemConsultarMomentoAplicacion.setText(resourceMap.getString("itemConsultarMomentoAplicacion.text")); // NOI18N
        itemConsultarMomentoAplicacion.setName("itemConsultarMomentoAplicacion"); // NOI18N
        menuMomentoAplicacion.add(itemConsultarMomentoAplicacion);

        menuAdministracion.add(menuMomentoAplicacion);

        menuBar.add(menuAdministracion);

        menuCampo.setIcon(resourceMap.getIcon("menuCampo.icon")); // NOI18N
        menuCampo.setText(resourceMap.getString("menuCampo.text")); // NOI18N
        menuCampo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuCampo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuCampo.setName("menuCampo"); // NOI18N
        menuCampo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemRegistrarCampo.setAction(actionMap.get("registrarCampo")); // NOI18N
        itemRegistrarCampo.setText(resourceMap.getString("itemRegistrarCampo.text")); // NOI18N
        itemRegistrarCampo.setName("itemRegistrarCampo"); // NOI18N
        menuCampo.add(itemRegistrarCampo);

        itemConsultarCampos.setAction(actionMap.get("consultarCampos")); // NOI18N
        itemConsultarCampos.setText(resourceMap.getString("itemConsultarCampos.text")); // NOI18N
        itemConsultarCampos.setName("itemConsultarCampos"); // NOI18N
        menuCampo.add(itemConsultarCampos);

        menuBar.add(menuCampo);

        menuCampania.setIcon(resourceMap.getIcon("menuCampania.icon")); // NOI18N
        menuCampania.setText(resourceMap.getString("menuCampania.text")); // NOI18N
        menuCampania.setToolTipText(resourceMap.getString("menuCampania.toolTipText")); // NOI18N
        menuCampania.setFont(resourceMap.getFont("menuCampania.font")); // NOI18N
        menuCampania.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuCampania.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuCampania.setName("menuCampania"); // NOI18N
        menuCampania.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemRegistrarCampania.setAction(actionMap.get("registrarCampania")); // NOI18N
        itemRegistrarCampania.setText(resourceMap.getString("itemRegistrarCampania.text")); // NOI18N
        itemRegistrarCampania.setName("itemRegistrarCampania"); // NOI18N
        menuCampania.add(itemRegistrarCampania);

        itemConsultarCampanias.setAction(actionMap.get("consultarCampanias")); // NOI18N
        itemConsultarCampanias.setText(resourceMap.getString("itemConsultarCampanias.text")); // NOI18N
        itemConsultarCampanias.setName("itemConsultarCampanias"); // NOI18N
        menuCampania.add(itemConsultarCampanias);

        itemRegistrarCierreDeCampania.setAction(actionMap.get("registrarCierreDeCampania")); // NOI18N
        itemRegistrarCierreDeCampania.setText(resourceMap.getString("itemRegistrarCierreDeCampania.text")); // NOI18N
        itemRegistrarCierreDeCampania.setName("itemRegistrarCierreDeCampania"); // NOI18N
        menuCampania.add(itemRegistrarCierreDeCampania);

        jMenuItem2.setAction(actionMap.get("consultarCierreCampania")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        menuCampania.add(jMenuItem2);

        menuBar.add(menuCampania);

        menuPlanificacion.setIcon(resourceMap.getIcon("menuPlanificacion.icon")); // NOI18N
        menuPlanificacion.setText(resourceMap.getString("menuPlanificacion.text")); // NOI18N
        menuPlanificacion.setToolTipText(resourceMap.getString("menuPlanificacion.toolTipText")); // NOI18N
        menuPlanificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuPlanificacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuPlanificacion.setName("menuPlanificacion"); // NOI18N
        menuPlanificacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemMenuRegistrarPlanificacionAgroquimico.setAction(actionMap.get("registrarPlanificacionAgroquimicos")); // NOI18N
        itemMenuRegistrarPlanificacionAgroquimico.setText(resourceMap.getString("itemMenuRegistrarPlanificacionAgroquimico.text")); // NOI18N
        itemMenuRegistrarPlanificacionAgroquimico.setName("itemMenuRegistrarPlanificacionAgroquimico"); // NOI18N
        menuPlanificacion.add(itemMenuRegistrarPlanificacionAgroquimico);

        itemMenuConsultarPlanificaciones.setAction(actionMap.get("consultarPlanificaciones")); // NOI18N
        itemMenuConsultarPlanificaciones.setText(resourceMap.getString("itemMenuConsultarPlanificaciones.text")); // NOI18N
        itemMenuConsultarPlanificaciones.setName("itemMenuConsultarPlanificaciones"); // NOI18N
        menuPlanificacion.add(itemMenuConsultarPlanificaciones);

        menuBar.add(menuPlanificacion);

        menuSiembra.setIcon(resourceMap.getIcon("menuSiembra.icon")); // NOI18N
        menuSiembra.setText(resourceMap.getString("menuSiembra.text")); // NOI18N
        menuSiembra.setToolTipText(resourceMap.getString("menuSiembra.toolTipText")); // NOI18N
        menuSiembra.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuSiembra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuSiembra.setName("menuSiembra"); // NOI18N
        menuSiembra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemMenuRegistrarSiembra.setAction(actionMap.get("registrarSiembra")); // NOI18N
        itemMenuRegistrarSiembra.setText(resourceMap.getString("itemMenuRegistrarSiembra.text")); // NOI18N
        itemMenuRegistrarSiembra.setName("itemMenuRegistrarSiembra"); // NOI18N
        menuSiembra.add(itemMenuRegistrarSiembra);

        itemMenuConsultarSiembras.setAction(actionMap.get("consultarSiembras")); // NOI18N
        itemMenuConsultarSiembras.setText(resourceMap.getString("itemMenuConsultarSiembras.text")); // NOI18N
        itemMenuConsultarSiembras.setName("itemMenuConsultarSiembras"); // NOI18N
        menuSiembra.add(itemMenuConsultarSiembras);

        itemRegistrarCultivos.setAction(actionMap.get("registrarCultivo")); // NOI18N
        itemRegistrarCultivos.setText(resourceMap.getString("itemRegistrarCultivos.text")); // NOI18N
        itemRegistrarCultivos.setName("itemRegistrarCultivos"); // NOI18N
        menuSiembra.add(itemRegistrarCultivos);

        itemConsultarCultivos.setAction(actionMap.get("consultarCultivos")); // NOI18N
        itemConsultarCultivos.setText(resourceMap.getString("itemConsultarCultivos.text")); // NOI18N
        itemConsultarCultivos.setName("itemConsultarCultivos"); // NOI18N
        menuSiembra.add(itemConsultarCultivos);

        menuBar.add(menuSiembra);

        menuPulverizaciones.setIcon(resourceMap.getIcon("menuPulverizaciones.icon")); // NOI18N
        menuPulverizaciones.setText(resourceMap.getString("menuPulverizaciones.text")); // NOI18N
        menuPulverizaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuPulverizaciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuPulverizaciones.setName("menuPulverizaciones"); // NOI18N
        menuPulverizaciones.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemRegistrarTrabajo.setAction(actionMap.get("registrarTrabajo")); // NOI18N
        itemRegistrarTrabajo.setText(resourceMap.getString("itemRegistrarTrabajo.text")); // NOI18N
        itemRegistrarTrabajo.setName("itemRegistrarTrabajo"); // NOI18N
        menuPulverizaciones.add(itemRegistrarTrabajo);

        itemConsultarTrabajo.setAction(actionMap.get("consultarTrabajos")); // NOI18N
        itemConsultarTrabajo.setText(resourceMap.getString("itemConsultarTrabajo.text")); // NOI18N
        menuPulverizaciones.add(itemConsultarTrabajo);

        menuBar.add(menuPulverizaciones);

        menuAgroquimicos.setAction(actionMap.get("showTotalizadorAgroquimicos")); // NOI18N
        menuAgroquimicos.setIcon(resourceMap.getIcon("menuAgroquimicos.icon")); // NOI18N
        menuAgroquimicos.setText(resourceMap.getString("menuAgroquimicos.text")); // NOI18N
        menuAgroquimicos.setToolTipText(resourceMap.getString("menuAgroquimicos.toolTipText")); // NOI18N
        menuAgroquimicos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuAgroquimicos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuAgroquimicos.setName("menuAgroquimicos"); // NOI18N
        menuAgroquimicos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemMenuRegistrarAgroquimico.setAction(actionMap.get("registrarAgroquimico")); // NOI18N
        itemMenuRegistrarAgroquimico.setText(resourceMap.getString("itemMenuRegistrarAgroquimico.text")); // NOI18N
        itemMenuRegistrarAgroquimico.setName("itemMenuRegistrarAgroquimico"); // NOI18N
        menuAgroquimicos.add(itemMenuRegistrarAgroquimico);

        itemMenuConsultarAgroquimicos.setAction(actionMap.get("consultarAgroquimicos")); // NOI18N
        itemMenuConsultarAgroquimicos.setIcon(resourceMap.getIcon("itemMenuConsultarAgroquimicos.icon")); // NOI18N
        itemMenuConsultarAgroquimicos.setText(resourceMap.getString("itemMenuConsultarAgroquimicos.text")); // NOI18N
        itemMenuConsultarAgroquimicos.setName("itemMenuConsultarAgroquimicos"); // NOI18N
        menuAgroquimicos.add(itemMenuConsultarAgroquimicos);

        itemSeleccionarAgroquimicosUso.setAction(actionMap.get("seleccionarAgroquimicoUso")); // NOI18N
        itemSeleccionarAgroquimicosUso.setText(resourceMap.getString("itemSeleccionarAgroquimicosUso.text")); // NOI18N
        itemSeleccionarAgroquimicosUso.setName("itemSeleccionarAgroquimicosUso"); // NOI18N
        menuAgroquimicos.add(itemSeleccionarAgroquimicosUso);

        itemConsultarAgroquimicosUso.setAction(actionMap.get("consultarAgroquimicosUso")); // NOI18N
        itemConsultarAgroquimicosUso.setText(resourceMap.getString("itemConsultarAgroquimicosUso.text")); // NOI18N
        itemConsultarAgroquimicosUso.setName("itemConsultarAgroquimicosUso"); // NOI18N
        menuAgroquimicos.add(itemConsultarAgroquimicosUso);

        menuBar.add(menuAgroquimicos);

        menuStock.setIcon(resourceMap.getIcon("menuStock.icon")); // NOI18N
        menuStock.setText(resourceMap.getString("menuStock.text")); // NOI18N
        menuStock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuStock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuStock.setName("menuStock"); // NOI18N
        menuStock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemIngresoStock.setAction(actionMap.get("registrarIngresoAgroquimicoAlStock")); // NOI18N
        itemIngresoStock.setText(resourceMap.getString("itemIngresoStock.text")); // NOI18N
        itemIngresoStock.setName("itemIngresoStock"); // NOI18N
        menuStock.add(itemIngresoStock);

        itemAjusteInventario.setAction(actionMap.get("ajustarInventario")); // NOI18N
        itemAjusteInventario.setText(resourceMap.getString("itemAjusteInventario.text")); // NOI18N
        itemAjusteInventario.setName("itemAjusteInventario"); // NOI18N
        menuStock.add(itemAjusteInventario);

        itemRegistrarMovimientoDeposito.setAction(actionMap.get("registrarMovimientoDeposito")); // NOI18N
        itemRegistrarMovimientoDeposito.setText(resourceMap.getString("itemRegistrarMovimientoDeposito.text")); // NOI18N
        itemRegistrarMovimientoDeposito.setName("itemRegistrarMovimientoDeposito"); // NOI18N
        menuStock.add(itemRegistrarMovimientoDeposito);

        itemCancelarIngresoStock.setAction(actionMap.get("cancelarIngresoStock")); // NOI18N
        itemCancelarIngresoStock.setText(resourceMap.getString("itemCancelarIngresoStock.text")); // NOI18N
        itemCancelarIngresoStock.setName("itemCancelarIngresoStock"); // NOI18N
        menuStock.add(itemCancelarIngresoStock);

        jMenuItemTotalizadorAgroquim.setAction(actionMap.get("showTotalizadorAgroquimicos")); // NOI18N
        jMenuItemTotalizadorAgroquim.setText(resourceMap.getString("jMenuItemTotalizadorAgroquim.text")); // NOI18N
        jMenuItemTotalizadorAgroquim.setName("jMenuItemTotalizadorAgroquim"); // NOI18N
        menuStock.add(jMenuItemTotalizadorAgroquim);

        itemConsultarMovimientosDepositos.setAction(actionMap.get("consultarMovimientosDepositos")); // NOI18N
        itemConsultarMovimientosDepositos.setText(resourceMap.getString("itemConsultarMovimientosDepositos.text")); // NOI18N
        itemConsultarMovimientosDepositos.setName("itemConsultarMovimientosDepositos"); // NOI18N
        menuStock.add(itemConsultarMovimientosDepositos);

        itemConsultarIngresosAgroquimicos.setAction(actionMap.get("consultarIngresosDeStock")); // NOI18N
        itemConsultarIngresosAgroquimicos.setName("itemConsultarIngresosAgroquimicos"); // NOI18N
        menuStock.add(itemConsultarIngresosAgroquimicos);

        jSeparator4.setName("jSeparator4"); // NOI18N
        menuStock.add(jSeparator4);

        itemIngresoSemCer.setAction(actionMap.get("registrarIngresoSemillaCereal")); // NOI18N
        itemIngresoSemCer.setText(resourceMap.getString("itemIngresoSemCer.text")); // NOI18N
        itemIngresoSemCer.setName("itemIngresoSemCer"); // NOI18N
        menuStock.add(itemIngresoSemCer);

        itemConsultarIngresoSemCer.setAction(actionMap.get("consultarIngresoSemillaCereal")); // NOI18N
        itemConsultarIngresoSemCer.setText(resourceMap.getString("itemConsultarIngresoSemCer.text")); // NOI18N
        itemConsultarIngresoSemCer.setName("itemConsultarIngresoSemCer"); // NOI18N
        menuStock.add(itemConsultarIngresoSemCer);

        itemEgresoSemCer.setAction(actionMap.get("registrarEgresoSemillaCereal")); // NOI18N
        itemEgresoSemCer.setText(resourceMap.getString("itemEgresoSemCer.text")); // NOI18N
        itemEgresoSemCer.setName("itemEgresoSemCer"); // NOI18N
        menuStock.add(itemEgresoSemCer);

        itemConsultarEgresoSemCer.setAction(actionMap.get("consultarEgresoSemCer")); // NOI18N
        itemConsultarEgresoSemCer.setText(resourceMap.getString("itemConsultarEgresoSemCer.text")); // NOI18N
        itemConsultarEgresoSemCer.setName("itemConsultarEgresoSemCer"); // NOI18N
        menuStock.add(itemConsultarEgresoSemCer);

        menuBar.add(menuStock);

        menuLluvia.setIcon(resourceMap.getIcon("menuLluvia.icon")); // NOI18N
        menuLluvia.setText(resourceMap.getString("menuLluvia.text")); // NOI18N
        menuLluvia.setToolTipText(resourceMap.getString("menuLluvia.toolTipText")); // NOI18N
        menuLluvia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuLluvia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuLluvia.setName("menuLluvia"); // NOI18N
        menuLluvia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemRegistrarLluvia.setAction(actionMap.get("registrarLluvia")); // NOI18N
        itemRegistrarLluvia.setText(resourceMap.getString("itemRegistrarLluvia.text")); // NOI18N
        itemRegistrarLluvia.setName("itemRegistrarLluvia"); // NOI18N
        menuLluvia.add(itemRegistrarLluvia);

        itemConsultarLluvias.setAction(actionMap.get("consultarLluvias")); // NOI18N
        itemConsultarLluvias.setText(resourceMap.getString("itemConsultarLluvias.text")); // NOI18N
        itemConsultarLluvias.setName("itemConsultarLluvias"); // NOI18N
        menuLluvia.add(itemConsultarLluvias);

        jMenuItem1.setAction(actionMap.get("showTotalizadorLluvias")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        menuLluvia.add(jMenuItem1);

        menuBar.add(menuLluvia);

        menuReportes.setIcon(resourceMap.getIcon("menuReportes.icon")); // NOI18N
        menuReportes.setText(resourceMap.getString("menuReportes.text")); // NOI18N
        menuReportes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuReportes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuReportes.setName("menuReportes"); // NOI18N
        menuReportes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        itemReporteStockAlmacenamientos.setAction(actionMap.get("generarReporteStockAlmacenamientos")); // NOI18N
        itemReporteStockAlmacenamientos.setName("itemReporteStockAlmacenamientos"); // NOI18N
        menuReportes.add(itemReporteStockAlmacenamientos);

        itemReporteLluvias.setAction(actionMap.get("generarReporteLluvias")); // NOI18N
        itemReporteLluvias.setName("itemReporteLluvias"); // NOI18N
        menuReportes.add(itemReporteLluvias);

        itemReporteComparacionCostoAgroquimico.setAction(actionMap.get("generarReporteComparacionCostoAgroquimicos")); // NOI18N
        itemReporteComparacionCostoAgroquimico.setText(resourceMap.getString("itemReporteComparacionCostoAgroquimico.text")); // NOI18N
        itemReporteComparacionCostoAgroquimico.setName("itemReporteComparacionCostoAgroquimico"); // NOI18N
        menuReportes.add(itemReporteComparacionCostoAgroquimico);

        itemReporteHistoricoTrabajo.setAction(actionMap.get("generarReporteHistoricoTrabajo")); // NOI18N
        itemReporteHistoricoTrabajo.setText(resourceMap.getString("itemReporteHistoricoTrabajo.text")); // NOI18N
        itemReporteHistoricoTrabajo.setName("itemReporteHistoricoTrabajo"); // NOI18N
        menuReportes.add(itemReporteHistoricoTrabajo);

        itemReportePlanificacion.setAction(actionMap.get("generarReportePlanificacion")); // NOI18N
        itemReportePlanificacion.setText(resourceMap.getString("itemReportePlanificacion.text")); // NOI18N
        menuReportes.add(itemReportePlanificacion);

        itemReporteCostoAgroquimico.setAction(actionMap.get("generarReporteCostoAgroquimicos")); // NOI18N
        itemReporteCostoAgroquimico.setText(resourceMap.getString("itemReporteCostoAgroquimico.text")); // NOI18N
        itemReporteCostoAgroquimico.setName("itemReporteCostoAgroquimico"); // NOI18N
        menuReportes.add(itemReporteCostoAgroquimico);

        itemReporteCostosCampania.setAction(actionMap.get("generarReporteCostosCampania")); // NOI18N
        itemReporteCostosCampania.setText(resourceMap.getString("itemReporteCostosCampania.text")); // NOI18N
        itemReporteCostosCampania.setName("itemReporteCostosCampania"); // NOI18N
        menuReportes.add(itemReporteCostosCampania);

        itemReporteCostosEstablecimiento.setAction(actionMap.get("generarReporteCostosPorEstablecimiento")); // NOI18N
        itemReporteCostosEstablecimiento.setText(resourceMap.getString("itemReporteCostosEstablecimiento.text")); // NOI18N
        itemReporteCostosEstablecimiento.setName("itemReporteCostosEstablecimiento"); // NOI18N
        menuReportes.add(itemReporteCostosEstablecimiento);

        itemReporteSiembra.setAction(actionMap.get("generarReporteSiembras")); // NOI18N
        itemReporteSiembra.setText(resourceMap.getString("itemReporteSiembra.text")); // NOI18N
        itemReporteSiembra.setName("itemReporteSiembra"); // NOI18N
        menuReportes.add(itemReporteSiembra);

        itemReporteComparativoInsumos.setAction(actionMap.get("generarReporteComparativoInsumos")); // NOI18N
        itemReporteComparativoInsumos.setText(resourceMap.getString("itemReporteComparativoInsumos.text")); // NOI18N
        itemReporteComparativoInsumos.setName("itemReporteComparativoInsumos"); // NOI18N
        menuReportes.add(itemReporteComparativoInsumos);

        itemReporteAgroquimicosPlanificados.setAction(actionMap.get("generarReporteAgroquimicosPlanificados")); // NOI18N
        itemReporteAgroquimicosPlanificados.setText(resourceMap.getString("itemReporteAgroquimicosPlanificados.text")); // NOI18N
        itemReporteAgroquimicosPlanificados.setName("itemReporteAgroquimicosPlanificados"); // NOI18N
        menuReportes.add(itemReporteAgroquimicosPlanificados);

        itemReporteMargenBrutoCampania.setAction(actionMap.get("generarReporteMargenBrutoCampania")); // NOI18N
        itemReporteMargenBrutoCampania.setText(resourceMap.getString("itemReporteMargenBrutoCampania.text")); // NOI18N
        itemReporteMargenBrutoCampania.setName("itemReporteMargenBrutoCampania"); // NOI18N
        menuReportes.add(itemReporteMargenBrutoCampania);

        itemReporteEmpresasDeServicio.setText(resourceMap.getString("itemReporteEmpresasDeServicio.text")); // NOI18N
        itemReporteEmpresasDeServicio.setName("itemReporteEmpresasDeServicio"); // NOI18N
        itemReporteEmpresasDeServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemReporteEmpresasDeServicioActionPerformed(evt);
            }
        });
        menuReportes.add(itemReporteEmpresasDeServicio);

        menuBar.add(menuReportes);

        helpMenu.setIcon(resourceMap.getIcon("helpMenu.icon")); // NOI18N
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setToolTipText(resourceMap.getString("helpMenu.toolTipText")); // NOI18N
        helpMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        helpMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpMenu.setName("helpMenu"); // NOI18N
        helpMenu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jMenuItemAutogestion.setAction(actionMap.get("abrirAutogestion")); // NOI18N
        jMenuItemAutogestion.setText(resourceMap.getString("jMenuItemAutogestion.text")); // NOI18N
        jMenuItemAutogestion.setName("jMenuItemAutogestion"); // NOI18N
        helpMenu.add(jMenuItemAutogestion);

        jSeparator5.setName("jSeparator5"); // NOI18N
        helpMenu.add(jSeparator5);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setIcon(resourceMap.getIcon("aboutMenuItem.icon")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        sugerenciasMenuItem.setAction(actionMap.get("enviarSugerencia")); // NOI18N
        sugerenciasMenuItem.setText(resourceMap.getString("sugerenciasMenuItem.text")); // NOI18N
        sugerenciasMenuItem.setName("sugerenciasMenuItem"); // NOI18N
        helpMenu.add(sugerenciasMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator);

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel);

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusPanel.add(statusAnimationLabel);

        progressBar.setName("progressBar"); // NOI18N
        statusPanel.add(progressBar);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        itemFormaFumigacion.setText(resourceMap.getString("itemFormaFumigacion.text")); // NOI18N
        itemFormaFumigacion.setName("itemFormaFumigacion"); // NOI18N
        itemFormaFumigacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemFormaFumigacionActionPerformed(evt);
            }
        });
        jMenu1.add(itemFormaFumigacion);

        itemTipoTrabajo.setText(resourceMap.getString("itemTipoTrabajo.text")); // NOI18N
        itemTipoTrabajo.setName("itemTipoTrabajo"); // NOI18N
        itemTipoTrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemTipoTrabajoActionPerformed(evt);
            }
        });
        jMenu1.add(itemTipoTrabajo);

        itemTipoCampania.setText(resourceMap.getString("itemTipoCampania.text")); // NOI18N
        itemTipoCampania.setName("itemTipoCampania"); // NOI18N
        itemTipoCampania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemTipoCampaniaActionPerformed(evt);
            }
        });
        jMenu1.add(itemTipoCampania);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jMenu1.add(jSeparator3);

        itemRegistrarPlaga.setAction(actionMap.get("registrarPlaga")); // NOI18N
        itemRegistrarPlaga.setText(resourceMap.getString("itemRegistrarPlaga.text")); // NOI18N
        itemRegistrarPlaga.setName("itemRegistrarPlaga"); // NOI18N
        jMenu1.add(itemRegistrarPlaga);

        itemConsultarPlagas.setAction(actionMap.get("consultarPlagas")); // NOI18N
        itemConsultarPlagas.setText(resourceMap.getString("itemConsultarPlagas.text")); // NOI18N
        itemConsultarPlagas.setName("itemConsultarPlagas"); // NOI18N
        jMenu1.add(itemConsultarPlagas);

        itemRegistrarTipoPlaga.setAction(actionMap.get("registrarTipoPlaga")); // NOI18N
        itemRegistrarTipoPlaga.setText(resourceMap.getString("itemRegistrarTipoPlaga.text")); // NOI18N
        itemRegistrarTipoPlaga.setName("itemRegistrarTipoPlaga"); // NOI18N
        jMenu1.add(itemRegistrarTipoPlaga);

        itemConsultarTiposPlaga.setAction(actionMap.get("consultarTipoPlaga")); // NOI18N
        itemConsultarTiposPlaga.setText(resourceMap.getString("itemConsultarTiposPlaga.text")); // NOI18N
        itemConsultarTiposPlaga.setName("itemConsultarTiposPlaga"); // NOI18N
        jMenu1.add(itemConsultarTiposPlaga);

        menuTipoSemilla.setIcon(resourceMap.getIcon("menuTipoSemilla.icon")); // NOI18N
        menuTipoSemilla.setText(resourceMap.getString("menuTipoSemilla.text")); // NOI18N
        menuTipoSemilla.setName("menuTipoSemilla"); // NOI18N

        itemRegistrarTipoSemilla.setAction(actionMap.get("registrarTipoSemilla")); // NOI18N
        itemRegistrarTipoSemilla.setName("itemRegistrarTipoSemilla"); // NOI18N
        menuTipoSemilla.add(itemRegistrarTipoSemilla);

        itemConsultarTipoSemilla.setAction(actionMap.get("consultarTipoSemilla")); // NOI18N
        itemConsultarTipoSemilla.setText(resourceMap.getString("itemConsultarTipoSemilla.text")); // NOI18N
        itemConsultarTipoSemilla.setName("itemConsultarTipoSemilla"); // NOI18N
        menuTipoSemilla.add(itemConsultarTipoSemilla);

        jMenu1.add(menuTipoSemilla);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void itemFormaFumigacionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_itemFormaFumigacionActionPerformed
    {//GEN-HEADEREND:event_itemFormaFumigacionActionPerformed
        FrameNamedEntity<FormaFumigacion> f = new FrameNamedEntity<FormaFumigacion>(this.getFrame(), true,
                FormaFumigacion.class);
        f.setTitle(getResourceMap().getString("formaFumigacion.title"));
        f.setVisible(true);
    }//GEN-LAST:event_itemFormaFumigacionActionPerformed

    private void itemTipoTrabajoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_itemTipoTrabajoActionPerformed
    {//GEN-HEADEREND:event_itemTipoTrabajoActionPerformed
    }//GEN-LAST:event_itemTipoTrabajoActionPerformed

    private void itemTipoCampaniaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_itemTipoCampaniaActionPerformed
    {//GEN-HEADEREND:event_itemTipoCampaniaActionPerformed
    }//GEN-LAST:event_itemTipoCampaniaActionPerformed

    private void mainPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_mainPanelComponentShown
    {//GEN-HEADEREND:event_mainPanelComponentShown
    }//GEN-LAST:event_mainPanelComponentShown

    private void itemReporteEmpresasDeServicioActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_itemReporteEmpresasDeServicioActionPerformed
    {//GEN-HEADEREND:event_itemReporteEmpresasDeServicioActionPerformed
        if (frameReporteEmpresasDeServicio == null) {
            frameReporteEmpresasDeServicio = new FrameReporteEmpresasDeServicios();
            frameReporteEmpresasDeServicio.setTitle(getResourceMap().getString("itemReporteEmpresasDeServicio.text"));
        } else {
            frameReporteEmpresasDeServicio.refreshUI();
        }
        frameReporteEmpresasDeServicio.setVisible(true);
    }//GEN-LAST:event_itemReporteEmpresasDeServicioActionPerformed
    FrameIngresoStock frameIngresoStock;
    FrameRegistrarPlanificacionAgroquimico frameRegistrarPlanificacion;
    FrameRegistrarTrabajoDeLote frameRegistrarTrabajo;
    FrameIngresoSemillaCereal frameIngresoSemillaCereal;
    FrameEgresoSemillaCereal frameEgresoSemillaCereal;
    FrameReporteEmpresasDeServicios frameReporteEmpresasDeServicio;

    @Action
    public void quit(ActionEvent e) {
        getApplication().exit();
    }
    InformacionAgroquimicoFrameListEntity infoAgroquimicoFLE;

    @Action
    public void consultarAgroquimicos() {
        if (infoAgroquimicoFLE == null) {
            InformacionAgroquimicoJpaController controller = new InformacionAgroquimicoJpaController();
            infoAgroquimicoFLE = new InformacionAgroquimicoFrameListEntity(controller,
                    getResourceMap());
            infoAgroquimicoFLE.setTitle(getResourceMap().getString("itemMenuConsultarAgroquimicos.text"));
        } else {
            infoAgroquimicoFLE.clean();
            infoAgroquimicoFLE.refreshTable();
        }
        infoAgroquimicoFLE.setVisible(true);
    }

    @Action
    public void seleccionarAgroquimicoUso() {
        DialogAgroquimico d = new DialogAgroquimico(this.getFrame());
        d.setTitle(getResourceMap().getString("agroquimico.alta.title"));
        d.setVisible(true);
    }
    AgroquimicoFrameListEntity agroquimicoFLE;

    @Action
    public void consultarAgroquimicosUso() {
        if (agroquimicoFLE == null) {
            agroquimicoFLE = new AgroquimicoFrameListEntity(getResourceMap());
            agroquimicoFLE.setTitle(getResourceMap().getString("itemConsultarAgroquimicosUso.text"));
        } else {
            agroquimicoFLE.clean();
            agroquimicoFLE.refreshUI();
        }
        agroquimicoFLE.setVisible(true);
    }

    @Action
    public void registrarSiembra() {
        DialogSiembra d = new DialogSiembra(this.getFrame());
        d.setTitle(getResourceMap().getString("siembra.alta.title"));
        d.setVisible(true);
    }
    SiembraFrameListEntity siembraFLE;

    @Action
    public void consultarSiembras() {
        if (siembraFLE == null) {
            siembraFLE = new SiembraFrameListEntity(getResourceMap(), new SiembraJpaController());
            siembraFLE.setTitle(getResourceMap().getString("siembra.list.title"));
        }
        siembraFLE.setVisible(true);
    }

    @Action
    public void ajustarInventario() {
        DialogAjusteInventario f = new DialogAjusteInventario(this.getFrame());
        f.setTitle(getResourceMap().getString("itemAjusteInventario.text"));
        f.setVisible(true);
    }

    @Action
    public void registrarCultivo() {
        DialogCultivo f = new DialogCultivo(this.getFrame());
        f.setTitle(getResourceMap().getString("cultivo.alta.title"));
        f.setVisible(true);
    }
    CultivoFrameListEntity cultivoFLE;

    @Action
    public void consultarCultivos() {
        if (cultivoFLE == null) {
            cultivoFLE = new CultivoFrameListEntity(getResourceMap());
            cultivoFLE.setTitle(getResourceMap().getString("itemConsultarCultivos.text"));
        }
        cultivoFLE.setVisible(true);
    }

    @Action
    public void registrarCampo() {
        DialogCampo f = new DialogCampo(this.getFrame(), getResourceMap());
        f.setTitle(getResourceMap().getString("campo.alta.title"));
        f.setVisible(true);
    }

    @Action
    public void registrarIngresoAgroquimicoAlStock() {
        if (frameIngresoStock == null) {
            frameIngresoStock = new FrameIngresoStock();
            frameIngresoStock.setTitle(getResourceMap().getString("itemIngresoStock.text"));
        } else {
            frameIngresoStock.refresh();
        }
        frameIngresoStock.setVisible(true);
    }

    @Action
    public void registrarProveedor() {
        DialogProveedor p = new DialogProveedor(ApplicationView.this.getFrame());
        p.setTitle(getResourceMap().getString("proveedor.alta.title"));
        p.setVisible(true);
    }
    ProveedorFrameListEntity proveedorFLE;

    @Action
    public void consultarProveedores() {
        if (proveedorFLE == null) {
            proveedorFLE = new ProveedorFrameListEntity(getResourceMap());
            proveedorFLE.setTitle(getResourceMap().getString("proveedor.list.title"));
        }
        proveedorFLE.setVisible(true);
    }

    @Action
    public void registrarPlanificacionAgroquimicos() {
        if (frameRegistrarPlanificacion == null) {
            frameRegistrarPlanificacion = new FrameRegistrarPlanificacionAgroquimico();
            frameRegistrarPlanificacion.setTitle(getResourceMap().getString(
                    "agroquimicos.registroPlanificacion"));
        }
        frameRegistrarPlanificacion.setVisible(true);
    }

    @Action
    public void registrarTrabajo() {
        if (frameRegistrarTrabajo == null) {
            frameRegistrarTrabajo = new FrameRegistrarTrabajoDeLote();
            frameRegistrarTrabajo.setTitle(getResourceMap().getString(
                    "trabajo.alta.title"));
        }
        frameRegistrarTrabajo.setVisible(true);
    }

    @Action
    public void registrarCampania() {
        DialogCampania c = new DialogCampania(ApplicationView.this.getFrame());
        c.setTitle(getResourceMap().getString("campania.alta.title"));
        c.setVisible(true);
    }
    CampaniaFrameListEntity campaniaFLE;

    @Action
    public void consultarCampanias() {
        if (campaniaFLE == null) {
            campaniaFLE = new CampaniaFrameListEntity(getResourceMap());
            campaniaFLE.setTitle(getResourceMap().getString("campania.list.title"));
        }
        campaniaFLE.setVisible(true);
    }
    CampoFrameListEntity campoFLE;

    @Action
    public void consultarCampos() {
        if (campoFLE == null) {
            campoFLE = new CampoFrameListEntity(getResourceMap());
            campoFLE.setTitle(getResourceMap().getString("campo.list.title"));
        }
        campoFLE.setVisible(true);
    }

    @Action
    public void generarReporteCostoAgroquimicos() {
        CostoAgroquimicosReportFrame f = new CostoAgroquimicosReportFrame();
        f.setTitle(getResourceMap().getString("reporte.agroquimico.costo"));
        f.setVisible(true);
    }

    @Action
    public void generarReporteStockAlmacenamientos() {
        StockAlmacenamientosReportFrame f = new StockAlmacenamientosReportFrame();
        f.setTitle(getResourceMap().getString("itemReporteStockAlmacenamientos.text"));
        f.setVisible(true);
    }

    @Action
    public void registrarDeposito() {
        DialogAlmacenamiento f = new DialogAlmacenamiento(this.getFrame());
        f.setTitle(getResourceMap().getString("itemRegistrarDeposito.text"));
        f.setVisible(true);
    }
    AlmacenamientoFrameListEntity AlmacenamientoFLE;

    @Action
    public void consultarDepositos() {
        if (AlmacenamientoFLE == null) {
            AlmacenamientoFLE = new AlmacenamientoFrameListEntity(getResourceMap());
            AlmacenamientoFLE.setTitle(getResourceMap().getString("itemConsultarDepositos.text"));
        }
        AlmacenamientoFLE.setVisible(true);
    }

    @Action
    public void registrarTipoCosto() {
        DialogTipoCosto f = new DialogTipoCosto(this.getFrame());
        f.setTitle(getResourceMap().getString("itemRegistrarTipoCosto.text"));
        f.setVisible(true);
    }
    TipoCostoFrameListEntity tipoCostoFLE;

    @Action
    public void consultarTiposCosto() {
        if (tipoCostoFLE == null) {
            tipoCostoFLE = new TipoCostoFrameListEntity(getResourceMap());
            tipoCostoFLE.setTitle(getResourceMap().getString("itemConsultarTiposCosto.text"));
        }
        tipoCostoFLE.setVisible(true);
    }

    @Action
    public void configurar() {
        DialogConfiguracion d = new DialogConfiguracion(this.getFrame());
        d.setVisible(true);
    }

    @Action
    public void registrarMovimientoDeposito() {
        DialogMovimientoDeposito d = new DialogMovimientoDeposito(this.getFrame());
        d.setTitle(getResourceMap().getString("itemRegistrarMovimientoDeposito.text"));
        d.setVisible(true);
    }

    @Action
    public Task realizarCopiaBD() {
        return new Task(this.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                DatabaseUtil.realizarCopiaBD(ApplicationView.this.getFrame());

                return "";
            }
        };
    }

    @Action
    public Task restaurarBD() {
        return new Task(this.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                DatabaseUtil.restaurarBD(ApplicationView.this.getFrame());

                return "";
            }
        };
    }

    @Action
    public Task importarBD() {
        return new Task(this.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                DatabaseUtil.importarBD(ApplicationView.this.getFrame());

                return "";
            }
        };
    }

    private class ImportarBDTask extends org.jdesktop.application.Task<Object, Void> {
        ImportarBDTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to ImportarBDTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public void registrarLluvia() {
        DialogLluvia c = new DialogLluvia(ApplicationView.this.getFrame());
        c.setTitle(getResourceMap().getString("lluvia.alta.title"));
        c.setVisible(true);
    }
    LluviaFrameListEntity lluviaFLE;

    @Action
    public void consultarLluvias() {
        if (lluviaFLE == null) {
            lluviaFLE = new LluviaFrameListEntity(getResourceMap());
            lluviaFLE.setTitle(getResourceMap().getString("lluvia.list.title"));
        }
        lluviaFLE.setVisible(true);
    }

    @Action
    public void cancelarIngresoStock() {
        DialogCancelarIngresoStock d = new DialogCancelarIngresoStock(this.getFrame());
        d.setTitle(getResourceMap().getString("itemCancelarIngresoStock.text"));
        d.setVisible(true);
    }

//    @Action
//    public void registrarTipoSemilla()
//    {
//        DialogTipoSemilla f = new DialogTipoSemilla(this.getFrame());
//        f.setTitle(getResourceMap().getString("registrarTipoSemilla.Action.text"));
//        f.setVisible(true);
//    }
//    TipoSemillaFrameListEntity tipoSemillaFLE;
//
//    @Action
//    public void consultarTipoSemilla()
//    {
//        if (tipoCostoFLE == null)
//        {
//            tipoSemillaFLE = new TipoSemillaFrameListEntity(getResourceMap());
//            tipoSemillaFLE.setTitle(getResourceMap().getString("consultarTipoSemilla.Action.text"));
//        }
//        tipoSemillaFLE.setVisible(true);
//    }

    /*@Action
    public void registrarPlaga()
    {
    DialogPlaga d = new DialogPlaga(this.getFrame());
    d.setTitle(getResourceMap().getString("plaga.alta.title"));
    d.setVisible(true);
    }
    PlagaFrameListEntity plagaFLE;

    @Action
    public void consultarPlagas()
    {
    if (plagaFLE == null)
    {
    plagaFLE = new PlagaFrameListEntity(getResourceMap());
    plagaFLE.setTitle(getResourceMap().getString("plaga.list.title"));
    }
    plagaFLE.setVisible(true);
    }

    @Action
    public void registrarTipoPlaga()
    {
    FrameNamedEntity<TipoPlaga> f =
    new FrameNamedEntity<TipoPlaga>(this.getFrame(), true, TipoPlaga.class);
    f.setTitle(getResourceMap().getString("registrarTipoPlaga.Action.text"));
    f.setVisible(true);
    }
    FrameListNamedEntity<TipoPlaga> tipoPlagaFLE;

    @Action
    public void consultarTipoPlaga()
    {
    if (tipoPlagaFLE == null)
    {
    tipoPlagaFLE = new FrameListNamedEntity<TipoPlaga>(TipoPlaga.class, "Tipo Plaga",
    TipoPlaga.TABLE_HEADERS);
    tipoPlagaFLE.setTitle(getResourceMap().getString("tipoplaga.list.title"));
    }
    tipoPlagaFLE.setVisible(true);
    }*/
    @Action
    public void registrarCierreDeCampania() {
        DialogCierreDeCampania d = new DialogCierreDeCampania(getFrame(), true, false);
        d.setTitle(getResourceMap().getString("campania.cierre.title"));
        d.setVisible(true);
    }

    @Action
    public void generarReporteLluvias() {
        LluviasReportFrame f = new LluviasReportFrame();
        f.setTitle(getResourceMap().getString("reporte.lluvias"));
        f.setVisible(true);
    }
    PlanificacionesFrameListEntity planificacionFLE;

    @Action
    public void consultarPlanificaciones() {
        if (planificacionFLE == null) {
            planificacionFLE = new PlanificacionesFrameListEntity(getResourceMap(),
                    new PlanificacionAgroquimicoJpaController());
            planificacionFLE.setTitle(getResourceMap().getString("planificacion.list.title"));
        }
        planificacionFLE.setVisible(true);
    }

    @Action
    public void generarReporteComparacionCostoAgroquimicos() {
        ComparacionCostosAgroquimicosReportFrame f = new ComparacionCostosAgroquimicosReportFrame();
        f.setTitle(getResourceMap().getString("itemReporteComparacionCostoAgroquimico.text"));
        f.setVisible(true);
    }
    TrabajoFrameListEntity trabajoFLE;

    @Action
    public void consultarTrabajos() {
        if (trabajoFLE == null) {
            trabajoFLE = new TrabajoFrameListEntity(getResourceMap(), new TrabajoLoteJpaController());
            trabajoFLE.setTitle(getResourceMap().getString("trabajo.list.title"));
        }
        trabajoFLE.setVisible(true);
    }

    @Action
    public void generarReportePlanificacion() {
        ConsultarPlanificacionesReportFrame r = new ConsultarPlanificacionesReportFrame();
        r.setTitle(getResourceMap().getString("itemReportePlanificacion.text"));
        r.setVisible(true);
    }

    @Action
    public void registrarMomentoAplicacion() {
        FrameNamedEntity<MomentoAplicacion> f =
                new FrameNamedEntity<MomentoAplicacion>(this.getFrame(), true, MomentoAplicacion.class);
        f.setTitle(getResourceMap().getString("itemRegistrarMomentoAplicacion.text"));
        f.setVisible(true);
    }
    FrameListNamedEntity<MomentoAplicacion> momentoAplicacionFLE;

    @Action
    public void consultarMomentoAplicacion() {
        if (momentoAplicacionFLE == null) {
            momentoAplicacionFLE = new FrameListNamedEntity<MomentoAplicacion>(MomentoAplicacion.class,
                    "Momento Aplicación",
                    MomentoAplicacion.TABLE_HEADERS);
            momentoAplicacionFLE.setTitle(getResourceMap().getString("itemConsultarMomentoAplicacion.text"));
        }
        momentoAplicacionFLE.setVisible(true);
    }

    @Action
    public void generarReporteSiembras() {
        SiembrasReportFrame r = new SiembrasReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteSiembra.text"));
        r.setVisible(true);
    }

    /*@Action
    public void generarReporteCostos()
    {
    CostosReportFrame r = new CostosReportFrame();
    r.setTitle(getResourceMap().getString("itemReporteCostos.text"));
    r.setVisible(true);
    }*/
    @Action
    public void generarReporteHistoricoTrabajo() {
        HistoricoTrabajoCampoLoteReportFrame r = new HistoricoTrabajoCampoLoteReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteHistoricoTrabajo.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteCostosCampania() {
        CostosDeCampaniaReportFrame r = new CostosDeCampaniaReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteCostosCampania.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteComparativoInsumos() {
        PlanificadoVSFumigadoReportFrame r = new PlanificadoVSFumigadoReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteComparativoInsumos.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteCostosPorEstablecimiento() {
        CostosPorEstablecimientoReportFrame r = new CostosPorEstablecimientoReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteCostosEstablecimiento.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteAgroquimicosPlanificados() {
        AgroquimicosPlanificadosReportFrame r = new AgroquimicosPlanificadosReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteAgroquimicosPlanificados.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteMargenBrutoCampania() {
//        MargenBrutoCampaniaReportFrame r = new MargenBrutoCampaniaReportFrame();
//        r.setTitle(getResourceMap().getString("itemReporteMargenBrutoCampania.text"));
//        r.setVisible(true);

        //SI, está bien esto
        MargenBrutoEstablecimientoReportFrame r = new MargenBrutoEstablecimientoReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteMargenBrutoCampania.text"));
        r.setVisible(true);
    }

    @Action
    public void generarReporteMargenBrutoEstablecimiento() {
        MargenBrutoEstablecimientoReportFrame r = new MargenBrutoEstablecimientoReportFrame();
        r.setTitle(getResourceMap().getString("itemReporteMargenBrutoEstablecimiento.text"));
        r.setVisible(true);
    }
    AgroquimicosTotalizer agroquimTotalizer;
    LluviasTotalizer lluviasTotalizer;

    @Action
    public void showTotalizadorAgroquimicos() {
        if (agroquimTotalizer == null) {
            agroquimTotalizer = new AgroquimicosTotalizer(this.getFrame());
        }
        agroquimTotalizer.setVisible(true);
    }

    @Action
    public void showTotalizadorLluvias() {
        if (lluviasTotalizer == null) {
            lluviasTotalizer = new LluviasTotalizer(this.getFrame());
        }
        lluviasTotalizer.setVisible(true);
    }

    @Action
    public void registrarTipoIngreso() {
        DialogTipoIngreso f = new DialogTipoIngreso(this.getFrame());
        f.setTitle(getResourceMap().getString("itemRegistrarTipoIngreso.text"));
        f.setVisible(true);
    }

    @Action
    public void consultarTiposIngreso() {
        if (tipoIngresoFLE == null) {
            tipoIngresoFLE = new TipoIngresoFrameListEntity(getResourceMap());
            tipoIngresoFLE.setTitle(getResourceMap().getString("itemConsultarTiposIngreso.text"));
        }
        tipoIngresoFLE.setVisible(true);
    }
    TipoIngresoFrameListEntity tipoIngresoFLE;

    @Action
    public void consultarIngresosDeStock() {
        if (ingresoStockFLE == null) {
            ingresoStockFLE = new IngresoStockFrameListEntity(getResourceMap());
            ingresoStockFLE.setTitle(getResourceMap().getString("consultarIngresosDeStock.Action.text"));
        }
        ingresoStockFLE.setVisible(true);
    }
    IngresoStockFrameListEntity ingresoStockFLE;

    @Action
    public void consultarMovimientosDepositos() {
        if (movimientoFLE == null) {
            movimientoFLE = new MovimientoDepositoFrameListEntity(getResourceMap());
            movimientoFLE.setTitle(getResourceMap().getString("itemConsultarMovimientosDepositos.text"));
        }
        movimientoFLE.setVisible(true);
    }
    MovimientoDepositoFrameListEntity movimientoFLE;

    @Action
    public void consultarCierreCampania() {
        DialogCierreDeCampania d = new DialogCierreDeCampania(getFrame(), true, true);
        d.setTitle(getResourceMap().getString("campania.cierre.list"));
        d.setVisible(true);
    }

    @Action
    public void registrarAgroquimico() {
        DialogAgroquimicoManual d = new DialogAgroquimicoManual(getFrame(), null, false);
        d.setTitle(getResourceMap().getString("agroquimico.registrar.title"));
        d.setVisible(true);
    }

    @Action
    public void registrarIngresoSemillaCereal() {
        if (frameIngresoSemillaCereal == null) {
            frameIngresoSemillaCereal = new FrameIngresoSemillaCereal();
            frameIngresoSemillaCereal.setTitle(getResourceMap().getString(
                    "itemIngresoSemCer.text"));
        }
        frameIngresoSemillaCereal.setVisible(true);
    }
    private IngresoSemillaCerealFrameListEntity ingresoSemillaCerealFLE;

    @Action
    public void consultarIngresoSemillaCereal() {
        if (ingresoSemillaCerealFLE == null) {
            ingresoSemillaCerealFLE = new IngresoSemillaCerealFrameListEntity(getResourceMap());
            ingresoSemillaCerealFLE.setTitle(getResourceMap().getString("itemConsultarIngresoSemCer.text"));
        }
        ingresoSemillaCerealFLE.setVisible(true);
    }

    @Action
    public void registrarEgresoSemillaCereal() {
        if (frameEgresoSemillaCereal == null) {
            frameEgresoSemillaCereal = new FrameEgresoSemillaCereal();
            frameEgresoSemillaCereal.setTitle(getResourceMap().getString(
                    "itemEgresoSemCer.text"));
        }
        frameEgresoSemillaCereal.setVisible(true);
    }
    private EgresoSemillaCerealFrameListEntity egresoSemillaCerealFLE;

    @Action
    public void consultarEgresoSemCer() {
        if (egresoSemillaCerealFLE == null) {
            egresoSemillaCerealFLE = new EgresoSemillaCerealFrameListEntity(getResourceMap());
            egresoSemillaCerealFLE.setTitle(getResourceMap().getString("itemConsultarEgresoSemCer.text"));
        }
        egresoSemillaCerealFLE.setVisible(true);
    }

    @Action
    public void enviarSugerencia()
    {
        DialogSugerencias d = new DialogSugerencias(getFrame(), true);
        d.setTitle(getResourceMap().getString("sugerenciasMenuItem.text"));
        d.setVisible(true);
    }
    

    private void OpenURI() {

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        
        if(!java.awt.Desktop.isDesktopSupported() ) {
            GUIUtility.showWarningMessage(getFrame(),"Error al abrir autogestión", "No se ha podido iniciar el navegador web. Por favor visite www.osirisagro.com.ar/gestion\nNo encuentra la opción de escritorio");
            return;
        }

        if(!desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            GUIUtility.showWarningMessage(getFrame(),"Error al abrir autogestión", "No se ha podido iniciar el navegador web. Por favor visite www.osirisagro.com.ar/gestion\nNo encuentra la opción de búsqueda.");
            return;
        }
        else
        {
            try {

                java.net.URI uri = new java.net.URI("http://www.osirisagro.com.ar/gestion");
                desktop.browse( uri );
            }
            catch ( Exception e ) {

                System.err.println( e.getMessage() );
            }
        }
    }

    @Action
    public void abrirAutogestion() {
        OpenURI();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem itemAjusteInventario;
    private javax.swing.JMenuItem itemCancelarIngresoStock;
    private javax.swing.JMenuItem itemConfiguracion;
    private javax.swing.JMenuItem itemConsultarAgroquimicosUso;
    private javax.swing.JMenuItem itemConsultarCampanias;
    private javax.swing.JMenuItem itemConsultarCampos;
    private javax.swing.JMenuItem itemConsultarCultivos;
    private javax.swing.JMenuItem itemConsultarDepositos;
    private javax.swing.JMenuItem itemConsultarEgresoSemCer;
    private javax.swing.JMenuItem itemConsultarIngresoSemCer;
    private javax.swing.JMenuItem itemConsultarIngresosAgroquimicos;
    private javax.swing.JMenuItem itemConsultarLluvias;
    private javax.swing.JMenuItem itemConsultarMomentoAplicacion;
    private javax.swing.JMenuItem itemConsultarMovimientosDepositos;
    private javax.swing.JMenuItem itemConsultarPlagas;
    private javax.swing.JMenuItem itemConsultarProveedores;
    private javax.swing.JMenuItem itemConsultarTipoSemilla;
    private javax.swing.JMenuItem itemConsultarTiposCosto;
    private javax.swing.JMenuItem itemConsultarTiposIngreso;
    private javax.swing.JMenuItem itemConsultarTiposPlaga;
    private javax.swing.JMenuItem itemConsultarTrabajo;
    private javax.swing.JMenuItem itemEgresoSemCer;
    private javax.swing.JMenuItem itemFormaFumigacion;
    private javax.swing.JMenuItem itemImportarBD;
    private javax.swing.JMenuItem itemIngresoSemCer;
    private javax.swing.JMenuItem itemIngresoStock;
    private javax.swing.JMenuItem itemMenuConsultarAgroquimicos;
    private javax.swing.JMenuItem itemMenuConsultarPlanificaciones;
    private javax.swing.JMenuItem itemMenuConsultarSiembras;
    private javax.swing.JMenuItem itemMenuRegistrarAgroquimico;
    private javax.swing.JMenuItem itemMenuRegistrarPlanificacionAgroquimico;
    private javax.swing.JMenuItem itemMenuRegistrarSiembra;
    private javax.swing.JMenuItem itemRealizarCopiaBD;
    private javax.swing.JMenuItem itemRegistrarCampania;
    private javax.swing.JMenuItem itemRegistrarCampo;
    private javax.swing.JMenuItem itemRegistrarCierreDeCampania;
    private javax.swing.JMenuItem itemRegistrarCultivos;
    private javax.swing.JMenuItem itemRegistrarDeposito;
    private javax.swing.JMenuItem itemRegistrarLluvia;
    private javax.swing.JMenuItem itemRegistrarMomentoAplicacion;
    private javax.swing.JMenuItem itemRegistrarMovimientoDeposito;
    private javax.swing.JMenuItem itemRegistrarPlaga;
    private javax.swing.JMenuItem itemRegistrarProveedor;
    private javax.swing.JMenuItem itemRegistrarTipoCosto;
    private javax.swing.JMenuItem itemRegistrarTipoIngreso;
    private javax.swing.JMenuItem itemRegistrarTipoPlaga;
    private javax.swing.JMenuItem itemRegistrarTipoSemilla;
    private javax.swing.JMenuItem itemRegistrarTrabajo;
    private javax.swing.JMenuItem itemReporteAgroquimicosPlanificados;
    private javax.swing.JMenuItem itemReporteComparacionCostoAgroquimico;
    private javax.swing.JMenuItem itemReporteComparativoInsumos;
    private javax.swing.JMenuItem itemReporteCostoAgroquimico;
    private javax.swing.JMenuItem itemReporteCostosCampania;
    private javax.swing.JMenuItem itemReporteCostosEstablecimiento;
    private javax.swing.JMenuItem itemReporteEmpresasDeServicio;
    private javax.swing.JMenuItem itemReporteHistoricoTrabajo;
    private javax.swing.JMenuItem itemReporteLluvias;
    private javax.swing.JMenuItem itemReporteMargenBrutoCampania;
    private javax.swing.JMenuItem itemReportePlanificacion;
    private javax.swing.JMenuItem itemReporteSiembra;
    private javax.swing.JMenuItem itemReporteStockAlmacenamientos;
    private javax.swing.JMenuItem itemRestaurarBD;
    private javax.swing.JMenuItem itemSeleccionarAgroquimicosUso;
    private javax.swing.JMenuItem itemTipoCampania;
    private javax.swing.JMenuItem itemTipoTrabajo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItemAutogestion;
    private javax.swing.JMenuItem jMenuItemTotalizadorAgroquim;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JLabel lblImage2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu menuAdministracion;
    private javax.swing.JMenu menuAgroquimicos;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCampania;
    private javax.swing.JMenu menuCampo;
    private javax.swing.JMenu menuDepositos;
    private javax.swing.JMenu menuLluvia;
    private javax.swing.JMenu menuMomentoAplicacion;
    private javax.swing.JMenu menuPlanificacion;
    private javax.swing.JMenu menuProveedores;
    private javax.swing.JMenu menuPulverizaciones;
    private javax.swing.JMenu menuReportes;
    private javax.swing.JMenu menuSiembra;
    private javax.swing.JMenu menuStock;
    private javax.swing.JMenu menuTipoSemilla;
    private javax.swing.JMenu menuTiposCosto;
    private javax.swing.JMenu menuTiposIngreso;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenuItem sugerenciasMenuItem;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
