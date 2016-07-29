package ar.com.init.agros.reporting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;
import net.sf.jasperreports.view.JasperViewer;
import org.jfree.chart.JFreeChart;

/**
 * Clase AbstractReport
 *
 *
 * @author gmatheu
 * @version 26/07/2009 
 */
public abstract class AbstractReport {

    protected static final String TEMPLATE_FOLDER = "templates";
    protected static final String DEFAULT_THEME = "basic";
    protected static final String PORTRAIT_TEMPLATE_FILE = "PortraitTemplate.jrxml";
    protected static final String FIRST_PAGE_BANNER = "first_page_banner.jpg";
    protected static final String PAGE_BANNER = "page_banner.jpg";
    protected JRDataSource dataSource;
    protected JasperPrint jasperPrint;
    protected JasperReport jasperReport;
    protected Map params;

    /** Constructor por defecto de AbstractReport */
    public AbstractReport() {
        params = new HashMap();
    }

    public void showReport() throws JRException, Exception {
        buildReport();

        JasperViewer.viewReport(jasperPrint, false); //finally display the report 
    }

    /**
     * Debe ser redefinido con la creacion de la tabla para mostrar como resultado del grafico.
     * @return
     */
    public JFreeChart createChart() {
        return null;
    }

    /**
     * Define como se debe construir el reporte.
     * En general crea el jasperReport y luego llama al metodo fillReport
     * @throws JRException
     * @throws Exception
     */
    protected abstract void buildReport() throws JRException, Exception;

    protected void fillReport() throws JRException, Exception {
        try {
            if (jasperPrint == null) {
                if (dataSource != null && jasperReport != null) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
                } else {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, params);
                }
            }
        } catch (JRException ex) {
            Logger.getLogger(AbstractReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportReport(final ExportTypeEnum type, String exportPath) throws JRException, Exception {
        if (exportPath.length() == 0) {
            return;
        }

        if (!exportPath.endsWith(type.extension())) {
            exportPath += type.extension();
        }

        buildReport();

        switch (type) {
            case PDF:
                ReportExporter.exportReportPDF(jasperPrint, exportPath);
                break;
            case PLAIN_XLS:
                ReportExporter.exportReportPlainXLS(jasperPrint, exportPath);
                break;
            case XLS:
                ReportExporter.exportReportXLS(jasperPrint, exportPath);
                break;
            default:
                ;
        }
    }

    protected void createCollectionDataSource(List coll) {
        params.remove(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION);
        dataSource = new JRBeanCollectionDataSource(coll);
    }

    /**
     * Define la creacion de la fuente de datos del reporte.
     * Si es en base a una coleccion, puede usar el metodo {@link AbstractReport#createCollectionDataSource(java.util.Collection)} para crearla.
     * Si es en base a una consulta HQL, puede usar el metoddo {@link AbstractReport#createHQLDataSource(java.lang.String)} para crearla.
     *
     *
     * @see AbstractReport#createCollectionDataSource(java.util.Collection)
     * @see AbstractReport#createHQLDataSource(java.lang.String)
     * @return si la fuente de datos contiene datos
     */
    protected abstract boolean createDataSource();

    public JasperReport getJasperReport() {
        return jasperReport;
    }

    public Map getParams() {
        return params;
    }    

    protected abstract class ReportLine extends HashMap<String, Object> implements Comparable<ReportLine> {

        public static final long serialVersionUID = -1L;

        protected void add(String key, Double valor) {
            Double existing = (Double) get(key);

            if (existing == null) {
                existing = 0D;
            }
            existing += valor;

            put(key, existing);
        }

        protected int keyCompareTo(ReportLine o, String... keys) {
            int r = 0;

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];

                if (!this.get(key).toString().equals(o.get(key).toString())) {
                    return this.get(key).toString().compareTo(o.get(key).toString());
                }
            }

            return r;
        }

        protected boolean keyEquals(Object obj, String... keys) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ReportLine other = (ReportLine) obj;

            boolean r = true;

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                r = r && this.get(key).equals(other.get(key));
            }

            return r;
        }
    }

}
