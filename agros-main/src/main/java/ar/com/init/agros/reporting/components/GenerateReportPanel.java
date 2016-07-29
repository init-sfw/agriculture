/*
 * GenerateReportPanel.java
 *
 * Created on 06/07/2009, 22:44:29
 */
package ar.com.init.agros.reporting.components;

import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.ExportTypeEnum;
import ar.com.init.agros.reporting.JFreeChartExporter;
import ar.com.init.agros.util.gui.components.ExportFileChooser;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.Application;
import com.lowagie.text.pdf.DefaultFontMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * Clase para ser usada para generar y mostrar los reportes.
 * Es el ultimo tab en un ReportFrame
 * 
 * @author gmatheu
 */
public class GenerateReportPanel extends javax.swing.JPanel
{

    private static final long serialVersionUID = -1L;
    private static final Logger logger = Logger.getLogger(GenerateReportPanel.class.getName());
    private AbstractReport report;
    private String tabTitle;
    private FrameNotifier frameNotifier;
    private JFreeChart chart;

    /** Creates new form GenerateReportPanel */
    public GenerateReportPanel()
    {
        initComponents();
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
    }

    public String getTabTitle()
    {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle)
    {
        this.tabTitle = tabTitle;
    }

    public AbstractReport getReport()
    {
        return report;
    }

    public void setReport(AbstractReport report)
    {
        this.report = report;
        createChart();
    }

    public void addContent(JComponent content)
    {
        jPanelContent.add(content);
    }

    private void clearContent()
    {
        jPanelContent.removeAll();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelContent = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonExportChartToPdf = new javax.swing.JButton();
        jButtonExportToPdf = new javax.swing.JButton();
        jButtonExportToExcel = new javax.swing.JButton();
        jButtonView = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jPanelContent.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelContent.setName("jPanelContent"); // NOI18N
        jPanelContent.setLayout(new java.awt.BorderLayout());

        jPanelButton.setName("jPanelButton"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(GenerateReportPanel.class, this);
        jButtonExportChartToPdf.setAction(actionMap.get("exportChartToPdf")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(GenerateReportPanel.class);
        jButtonExportChartToPdf.setIcon(resourceMap.getIcon("jButtonExportChartToPdf.icon")); // NOI18N
        jButtonExportChartToPdf.setText(resourceMap.getString("jButtonExportChartToPdf.text")); // NOI18N
        jButtonExportChartToPdf.setName("jButtonExportChartToPdf"); // NOI18N
        jPanelButton.add(jButtonExportChartToPdf);

        jButtonExportToPdf.setAction(actionMap.get("exportToPdf")); // NOI18N
        jButtonExportToPdf.setIcon(resourceMap.getIcon("jButtonExportToPdf.icon")); // NOI18N
        jButtonExportToPdf.setText(resourceMap.getString("jButtonExportToPdf.text")); // NOI18N
        jButtonExportToPdf.setName("jButtonExportToPdf"); // NOI18N
        jButtonExportToPdf.setPreferredSize(new java.awt.Dimension(90, 40));
        jPanelButton.add(jButtonExportToPdf);

        jButtonExportToExcel.setAction(actionMap.get("exportToExcel")); // NOI18N
        jButtonExportToExcel.setIcon(resourceMap.getIcon("jButtonExportToExcel.icon")); // NOI18N
        jButtonExportToExcel.setText(resourceMap.getString("jButtonExportToExcel.text")); // NOI18N
        jButtonExportToExcel.setName("jButtonExportToExcel"); // NOI18N
        jButtonExportToExcel.setPreferredSize(new java.awt.Dimension(90, 40));
        jPanelButton.add(jButtonExportToExcel);

        jButtonView.setAction(actionMap.get("view")); // NOI18N
        jButtonView.setIcon(resourceMap.getIcon("jButtonView.icon")); // NOI18N
        jButtonView.setText(resourceMap.getString("jButtonView.text")); // NOI18N
        jButtonView.setName("jButtonView"); // NOI18N
        jButtonView.setPreferredSize(new java.awt.Dimension(90, 40));
        jPanelButton.add(jButtonView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
            .addComponent(jPanelContent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void print()
    {
    }

    @Action
    public Task exportToPdf()
    {
        frameNotifier.showOkMessage();
        final String f = ExportFileChooser.showPDFFileChooser(GenerateReportPanel.this);

        return new Task(Application.getApplication())
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                try {

                    if (f == null || f.length() > 0) {
                        getReport().exportReport(ExportTypeEnum.PDF, f);
                        frameNotifier.showInformationMessage("Reporte exportado correctamente a PDF");

                        return "ok";
                    }
                }
                catch (Exception ex) {
                    frameNotifier.showErrorMessage("NO se pudo exportar el reporte a PDF");
                    logger.log(Level.INFO, "Exportando reporte a pdf", ex);
                }

                return "error";
            }
        };

    }

    @Action
    public Task exportToExcel()
    {
        frameNotifier.showOkMessage();
        final String file = ExportFileChooser.showExcelFileChooser(this);
        
        return new Task(Application.getApplication())
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                try {
                    if (file == null || file.length() > 0) {
                        getReport().exportReport(ExportTypeEnum.XLS, file);
                        frameNotifier.showInformationMessage("Reporte exportado correctamente a Excel");

                        return "ok";
                    }
                }
                catch (Exception ex) {
                    frameNotifier.showErrorMessage("NO se pudo exportar el reporte a Excel");
                    logger.log(Level.INFO, "Exportando reporte a Excel", ex);

                }

                return "error";
            }
        };
    }

    @Action
    public Task view()
    {
        frameNotifier.showOkMessage();
        return new Task(Application.getApplication())
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                try {
                    getReport().showReport();
                    return "ok";
                }
                catch (Exception ex) {
                    frameNotifier.showErrorMessage("NO se pudo generar el reporte.");
                    logger.log(Level.INFO, "Generando el reporte", ex);
                }
                return "error";
            }
        };

    }

    private void createChart()
    {
        chart = getReport().createChart();
        if (chart != null) {
            clearContent();
            addContent(new ChartPanel(chart));
            getFrameNotifier().showOkMessage();
        }
        else {
            getFrameNotifier().showInformationMessage("No hay gr�fico disponible para este reporte");
        }
    }

    @Action
    public Task exportChartToPdf() throws IOException
    {
        frameNotifier.showOkMessage();
        String aux = ExportFileChooser.showFileChooser(this, new FileNameExtensionFilter("Archivos PDF",
                "pdf"),JFileChooser.SAVE_DIALOG);

        if (aux == null || aux.length() > 0) {
            if (!aux.endsWith(".pdf")) {
                aux += ".pdf";
            }

            final String file = aux;

            return new Task(Application.getApplication())
            {

                @Override
                protected Object doInBackground() throws Exception
                {

                    JFreeChartExporter.saveChartAsPDF(new File(file), chart, 710, 520, new DefaultFontMapper());
                    frameNotifier.showInformationMessage("Grafico exportado correctamente a PDF");
                    return "ok";
                }
            };
        }
        return null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExportChartToPdf;
    private javax.swing.JButton jButtonExportToExcel;
    private javax.swing.JButton jButtonExportToPdf;
    private javax.swing.JButton jButtonView;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelContent;
    // End of variables declaration//GEN-END:variables
}
