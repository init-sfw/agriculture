package ar.com.init.agros.view.campanias;

import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.RendimientoSuperficie;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.costo.Costo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.ingreso.Ingreso;
import ar.com.init.agros.reporting.dj.AbstractDJReport.Template;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.ExportFileChooser;
import ar.com.init.agros.util.gui.table.TableExporter;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.Application;
import ar.com.init.agros.view.campanias.editor.CierreCampaniaTableCellEditor;
import ar.com.init.agros.view.campanias.model.SeleccionSuperficiesTableModel;
import ar.com.init.agros.view.components.editors.EditableColumnHighlighter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/*
 * Clase GUI DialogCierreDeCampania
 *
 * @author fbobbio 
 * @version 23-jul-2009
 */
public class DialogCierreDeCampania extends javax.swing.JDialog implements UpdatableListener {

    private static final long serialVersionUID = -1L;
    private static final Logger logger = Logger.getLogger(DialogCierreDeCampania.class.getName());
    private EventControl evt;
    private SiembraJpaController siembraJpaController;
    public final static UnidadMedida UNIDAD_MEDIDA_RINDE = UnidadMedida.getQuintal();//MagnitudEnum.PESO.patron();
    private SeleccionSuperficiesTableModel seleccionSuperficiesTableModel;
    private boolean consulta;

    /** Creates new form DialogCierreDeCampania */
    public DialogCierreDeCampania(java.awt.Frame parent, boolean modal, boolean consulta) {
        super(parent, modal);
        this.consulta = consulta;
        GUIUtility.initWindow(this);
        initComponents();
        panelValorMonedaIngresosBrutos.setFrameNotifier(frameNotifier1);
        panelValorMonedaIngresosBrutos.setEnabled(false);
        seleccionSuperficiesTableModel = new SeleccionSuperficiesTableModel();
        jXTableSiembras.setModel(seleccionSuperficiesTableModel);
        jXTableSiembras.setDefaultEditor(Double.class, new CierreCampaniaTableCellEditor(frameNotifier1,
                seleccionSuperficiesTableModel,
                new DecimalInputVerifier(frameNotifier1, false)));
        siembraJpaController = new SiembraJpaController();
        panelCostosCampania1.setFrameNotifier(frameNotifier1);
        panelIngresos1.setFrameNotifier(frameNotifier1);
        panelBonificaciones1.setFrameNotifier(frameNotifier1);
        panelCostosComercializacion1.setFrameNotifier(frameNotifier1);
        evt = new EventControl();
        oKCancelCleanPanel1.getBtnAceptar().setEnabled(false);
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
        jPanelExportaciones.setVisible(false);
        if (consulta) {
            disableFieldAndButtons();
            jPanelExportaciones.setVisible(true);
        }

        jXTableSiembras.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                calcularRindeYRendimientoTotalCampania();
            }
        });
        refreshUI();
        jXTableSiembras.packAll();
    }

    private void calcularRindeYRendimientoTotalCampania() {
        double acumRendimiento = 0;
        for (RendimientoSuperficie ren : seleccionSuperficiesTableModel.getRendimientoSuperficies()) {
            if (ren.isComplete()) {
                acumRendimiento += ren.calcularRendimientoTotal();
            }
        }
        panelValorMonedaIngresosBrutos.setValorMoneda(new ValorMoneda(acumRendimiento));
    }

    private void fillTable(Campania campania) {
        if (campania != null) {
            seleccionSuperficiesTableModel.setSiembras(campania.getSiembras());

            //Poner foco en la celda para editar siempre que no sea consulta
            if (seleccionSuperficiesTableModel.getRowCount() > 0 && !consulta) {
                jXTableSiembras.changeSelection(0, SeleccionSuperficiesTableModel.RINDE_COLUMN_IDX, false,
                        false);
                jXTableSiembras.requestFocus();
                jXTableSiembras.setColumnSelectionInterval(0, SeleccionSuperficiesTableModel.RINDE_COLUMN_IDX);
                jXTableSiembras.setRowSelectionInterval(0, 0);
                jXTableSiembras.editCellAt(0, SeleccionSuperficiesTableModel.RINDE_COLUMN_IDX);
            }
        } else {
            seleccionSuperficiesTableModel.clear();
        }
        setHighlighters();
    }

    private Campania getCampania() {
        if (jComboBoxCampania.getSelectedItem() instanceof Campania) {
            Campania c = (Campania) jComboBoxCampania.getSelectedItem();
            panelCostosCampania1.setCostosCargados(c.getCostos());
            panelIngresos1.setIngresosCargados(c.getIngresos());
            return c;
        } else {
            panelCostosCampania1.clear();
            panelIngresos1.clear();
            return null;
        }
    }

    private void setHighlighters() {
        jXTableSiembras.setHighlighters(new EditableColumnHighlighter(
                SeleccionSuperficiesTableModel.VALORPORQUINTAL_COLUMN_IDX,
                SeleccionSuperficiesTableModel.RINDE_COLUMN_IDX));
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
        jLabel1 = new javax.swing.JLabel();
        jComboBoxCampania = new javax.swing.JComboBox();
        jPanelExportaciones = new javax.swing.JPanel();
        jButtonPDF = new javax.swing.JButton();
        jButtonExcel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableSiembras = new org.jdesktop.swingx.JXTable();
        jLabel2 = new javax.swing.JLabel();
        panelValorMonedaIngresosBrutos = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        panelCostosComercializacion1 = new ar.com.init.agros.view.costos.PanelCostosComercializacion();
        panelBonificaciones1 = new ar.com.init.agros.view.components.panels.PanelBonificaciones();
        panelIngresos1 = new ar.com.init.agros.view.components.panels.PanelIngresos();
        panelCostosCampania1 = new ar.com.init.agros.view.components.panels.PanelCostosCampania();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogCierreDeCampania.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jComboBoxCampania.setRenderer(listableComboBoxRenderer1);
        jComboBoxCampania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampaniaActionPerformed(evt);
            }
        });

        jButtonPDF.setIcon(resourceMap.getIcon("jButtonPDF.icon")); // NOI18N
        jButtonPDF.setText(resourceMap.getString("jButtonPDF.text")); // NOI18N
        jButtonPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPDFActionPerformed(evt);
            }
        });
        jPanelExportaciones.add(jButtonPDF);

        jButtonExcel.setIcon(resourceMap.getIcon("jButtonExcel.icon")); // NOI18N
        jButtonExcel.setText(resourceMap.getString("jButtonExcel.text")); // NOI18N
        jButtonExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcelActionPerformed(evt);
            }
        });
        jPanelExportaciones.add(jButtonExcel);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxCampania, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 354, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 283, Short.MAX_VALUE)
                .add(jPanelExportaciones, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel1)
                .add(jComboBoxCampania, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanelExportaciones, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N

        jXTableSiembras.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableSiembras);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelValorMonedaIngresosBrutos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(panelValorMonedaIngresosBrutos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelCostosComercializacion1.TabConstraints.tabTitle"), panelCostosComercializacion1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelBonificaciones1.TabConstraints.tabTitle"), panelBonificaciones1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelIngresos1.TabConstraints.tabTitle"), panelIngresos1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelCostosCampania1.TabConstraints.tabTitle"), panelCostosCampania1); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 877, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCampaniaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampaniaActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampaniaActionPerformed
        Campania c = getCampania();
        oKCancelCleanPanel1.getBtnAceptar().setEnabled(c instanceof Campania);
        fillTable(c);
        panelBonificaciones1.setCampania(c);
        panelCostosComercializacion1.setCampania(c);
    }//GEN-LAST:event_jComboBoxCampaniaActionPerformed

    private void jButtonPDFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonPDFActionPerformed
    {//GEN-HEADEREND:event_jButtonPDFActionPerformed
        exportToPDF().execute();
    }//GEN-LAST:event_jButtonPDFActionPerformed

    private void jButtonExcelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonExcelActionPerformed
    {//GEN-HEADEREND:event_jButtonExcelActionPerformed
        exportToExcel().execute();
    }//GEN-LAST:event_jButtonExcelActionPerformed

    @Action
    public Task exportToPDF() {
        return new Task(Application.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    String file = ExportFileChooser.showPDFFileChooser(DialogCierreDeCampania.this);

                    Campania c = getCampania();
                    if (file == null || file.length() > 0 && c != null) {
                        TableExporter te = createTableExporter(c);
                        te.exportToPDF(c.toString(), file);
                    }
                } catch (Exception ex) {
                    logger.log(Level.INFO, "Exportando tabla a pdf", ex);
                }
                return "";
            }
        };
    }

    @Action
    public Task exportToExcel() {
        return new Task(Application.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    String file = ExportFileChooser.showExcelFileChooser(DialogCierreDeCampania.this);

                    Campania c = getCampania();
                    if (file == null || file.length() > 0 && c != null) {
                        TableExporter te = createTableExporter(c);
                        te.exportToExcel(c.toString(), file);
                    }
                } catch (Exception ex) {

                    logger.log(Level.INFO, "Exportando tabla a Excel", ex);

                }
                return "";
            }
        };
    }

    private TableExporter createTableExporter(Campania c) {
        TableExporter r = new TableExporter();

        r.setTemplate(Template.LANDSCAPE);
        r.setReportTitle("Cierre de Campaña");
        r.setReportSubtitle(String.format("Campaña: %s", c.toString()));
        r.setMainCategory(c.toString());
        r.setMainCategoryLabel("Campaña:");

        TableExporter.Table supTable = new TableExporter.Table("Superficies", jXTableSiembras.getModel());
        supTable.setStartOnNewPage(false);
        r.addTable(supTable);

        List<String[]> ingBrutosValues = new ArrayList<String[]>();
        ingBrutosValues.add(new String[]{c.toString(), panelValorMonedaIngresosBrutos.getValorMoneda().toString()});
        TableExporter.Table ingBrutosTable = new TableExporter.Table("Ingresos Brutos", new String[]{"Campaña", "Ingresos Brutos"}, ingBrutosValues);
        ingBrutosTable.setWidths(new int[]{200, 85});
        ingBrutosTable.setStartOnNewPage(false);
        r.addTable(ingBrutosTable);

        TableExporter.Table costosComercTable = new TableExporter.Table("Costos de Comercialización", panelCostosComercializacion1.getModel());
        costosComercTable.setWidths(new int[]{100, 100, 100, 100, 60, 60, 75, 60, 60});
        r.addTable(costosComercTable);

        List<String[]> values = calcularCostosComercializacionCultivo(c);
        TableExporter.Table costosComercCultivoTable = new TableExporter.Table("Costos de Comercialización por Cultivo", new String[]{"Cultivo", "Tipo de Costo", "Importe", "Un."}, values);
        costosComercCultivoTable.setWidths(new int[]{120, 100, 85, 60});
        r.addTable(costosComercCultivoTable);

        TableExporter.Table bonifTable = new TableExporter.Table("Bonificaciones", panelBonificaciones1.getModel());
        bonifTable.setWidths(new int[]{120, 120, 85});
        r.addTable(bonifTable);

        TableExporter.Table ingExtraTable = new TableExporter.Table("Ingresos Extras", c.getIngresos(), Ingreso.TABLE_HEADERS);
        ingExtraTable.setWidths(new int[]{120, 85, 60});
        r.addTable(ingExtraTable);

        TableExporter.Table costoCampTable = new TableExporter.Table("Costos de Campaña", c.getCostos(), Costo.TABLE_HEADERS);
        costoCampTable.setWidths(new int[]{120, 85, 60, 100});
        r.addTable(costoCampTable);

        return r;
    }

    private List<String[]> calcularCostosComercializacionCultivo(Campania c) {
        Map<Cultivo, Costo> costos = new HashMap<Cultivo, Costo>();
        for (Siembra siembra : c.getSiembras()) {
            List<Costo> costosAux = new ArrayList<Costo>();
            costosAux.addAll(siembra.getCostos());
            costosAux.addAll(siembra.getCostosPostCosecha());
            for (Costo costo : costosAux) {
                if (!costos.containsKey(siembra.getCultivo())) {
                    Costo aux = new Costo();
                    aux.setTipoCosto(costo.getTipoCosto());
                    aux.setImporte(new ValorMoneda(costo.getImporte().getMonto()));
                    aux.setServicio(costo.getServicio());
                    costos.put(siembra.getCultivo(), aux);
                } else {
                    costos.get(siembra.getCultivo()).getImporte().sumar(costo.getImporte());
                }
            }
        }
        List<String[]> values = new ArrayList<String[]>();
        for (Map.Entry<Cultivo, Costo> entry : costos.entrySet()) {
            Costo costo = entry.getValue();
            values.add(new String[]{entry.getKey().toString(), costo.getTipoCosto().getNombre(), String.format("%.2f", costo.getImporte().getMonto()), costo.getImporte().getDivisa().getAbreviatura()});
        }
        return values;
    }

    @Override
    public void refreshUI() {
        if (consulta) // si es consulta trabajo con las campañas cerradas
        {
            GUIUtility.refreshComboBox(siembraJpaController.findCampaniasCerradas(), jComboBoxCampania);
        } else {
            GUIUtility.refreshComboBox(siembraJpaController.findCampaniasPendientesDeCierre(), jComboBoxCampania);
        }
    }

    @Override
    public boolean isNowVisible() {
        return this.isVisible();
    }

    private void disableFieldAndButtons() {
        jXTableSiembras.setEnabled(false);
        panelBonificaciones1.setEnabled(false);
        panelCostosCampania1.disableFields();
        panelIngresos1.disableFields();
        panelCostosComercializacion1.disableFields();
        oKCancelCleanPanel1.disableForList();
    }

    /** Clase de control de eventos que maneja los eventos de la GUI DialogCierreDeCampania y las validaciones de la misma */
    public class EventControl extends AbstractEventControl implements ActionListener {

        /** Mï¿½todo que maneja los eventos de la GUI DialogCierreDeCampania
         *  @param e el evento de acciï¿½n lanzado por algï¿½n componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {
                if (checkRendimientosCargados()) {
                    boolean confirmResult;
                    Campania c = getCampania();
                    c.setCostos(panelCostosCampania1.getCostosCargados());
                    c.setIngresos(panelIngresos1.getIngresosCargados());
                    c.setBonificaciones(panelBonificaciones1.getBonificaciones());
                    fillCostosComercializacion(c);
                    //List<Siembra> siembras = seleccionSuperficiesTableModel.getSiembras();
                    if (c.preparadaParaCerrar()) {
                        confirmResult = GUIUtility.confirmWarningData((DialogCierreDeCampania.this), "Una vez cerrada la campaña no podrá ser modificada, ¿Está seguro que desea cerrar la campaña?", "Cerrando Campaña");
                    } else {
                        confirmResult = GUIUtility.confirmData(DialogCierreDeCampania.this);
                    }
                    if (confirmResult) {
                        try {
                            String msg;
                            c = siembraJpaController.persistOrUpdateWithCampania(c);
                            if (c.isCerrada()) {
                                msg = "Se cargaron los rendimientos de las siembras y los datos de la campaña con éxito, la campaña ha sido cerrada";
                            } else {
                                msg = "Se cargaron parcialmente los rendimientos de las siembras y los datos de la campaña con éxito";
                            }
                            frameNotifier1.showInformationMessage(msg);
                            clear();
                            refreshUI();
                        } catch (InvalidStateException ex) {
                            frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogCierreDeCampania.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        } catch (ConstraintViolationException ex) {
                            frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogCierreDeCampania.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        } catch (Exception ex) {
                            frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                            Logger.getLogger(DialogCierreDeCampania.class.getName()).log(Level.SEVERE, null,
                                    ex);
                        }
                    }
                } else {
                    frameNotifier1.showErrorMessage("Existen siembras incompletas, cargue todos los valores para cerrar la campaña");
                }
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
                DialogCierreDeCampania.this.dispose();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                clear();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier() {
            return frameNotifier1;
        }

        private void fillCostosComercializacion(Campania c) {
            for (Siembra s : c.getSiembras()) {
                Siembra aux = panelCostosComercializacion1.getSiembras().get(panelCostosComercializacion1.getSiembras().indexOf(s));
                s.setCostosPostCosecha(aux.getCostosPostCosecha());
            }
        }
    }

    /** Checkeo si hay filas que tengan cargados datos parciales
     *
     * @return false si hay datos incompletos
     */
    private boolean checkRendimientosCargados() {
        for (RendimientoSuperficie r : seleccionSuperficiesTableModel.getRendimientoSuperficies()) {
            if (!r.isValid()) {
                return false;
            }
        }
        return true;
    }

    private void clear() {
        jComboBoxCampania.setSelectedIndex(0);
        panelCostosCampania1.clear();
        panelIngresos1.clear();
        panelCostosComercializacion1.clear();
        panelValorMonedaIngresosBrutos.setValorMoneda(new ValorMoneda(0.0));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JButton jButtonExcel;
    private javax.swing.JButton jButtonPDF;
    private javax.swing.JComboBox jComboBoxCampania;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelExportaciones;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXTable jXTableSiembras;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelBonificaciones panelBonificaciones1;
    private ar.com.init.agros.view.components.panels.PanelCostosCampania panelCostosCampania1;
    private ar.com.init.agros.view.costos.PanelCostosComercializacion panelCostosComercializacion1;
    private ar.com.init.agros.view.components.panels.PanelIngresos panelIngresos1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaIngresosBrutos;
    // End of variables declaration//GEN-END:variables
}
