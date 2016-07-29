package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.view.reporting.helper.AbstractSuperficiesHelper;
import ar.com.init.agros.view.reporting.helper.SiembrasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCampoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasVariedadCultivoHelper;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.ejb.QueryImpl;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase HistoricoTrabajoCampoLoteJasperReport
 *
 *
 * @author gmatheu
 * @version 11/09/2009 
 */
public abstract class HistoricoTrabajoCampoLoteJasperReport extends AbstractJasperReport {

    private static final Logger log = Logger.getLogger(HistoricoTrabajoCampoLoteJasperReport.class.getName());
    private static final String HEADER_CANTIDAD_VALUE = "Cant / Ha";
    public static final String REPORT_TITLE = "Reporte Consumo de Agroquímicos por Hectárea";
    private List<HistoricoTrabajoCampoLoteReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String CAMPANIA = "campania";
    private static final String FILTRO = "filtro";
    private static final String CANTIDAD = "cantidad";
    private static final String FORMA_FUMIGACION = "formaFumigacion";
    private static final String HEADER_FILTRO = "headerFiltro";
    private static final String HEADER_CANTIDAD = "headerCantidad";
    private AbstractSuperficiesHelper supPulvHelper;
    private SiembrasCultivoHelper siembrasCultivoHelper;
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    protected class HistoricoTrabajoCampoLoteReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public HistoricoTrabajoCampoLoteReportLine(String campania, String filtro, String agroquimico, String formaFumigacion, Double cantidad) {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(AGROQUIMICO, agroquimico);
            put(CANTIDAD, cantidad);
            put(FORMA_FUMIGACION, formaFumigacion);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, CAMPANIA, FILTRO, AGROQUIMICO);
        }

        @Override
        public int hashCode() {
            return this.get(CAMPANIA).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, CAMPANIA, FILTRO, AGROQUIMICO, FORMA_FUMIGACION);
        }

        public void addCantidad(Double cantidad) {            
            put(CANTIDAD,  getCantidad() + cantidad);
        }

        public Double getCantidad() {
            return (Double) get(CANTIDAD);
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public HistoricoTrabajoCampoLoteJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<HistoricoTrabajoCampoLoteReportLine>();

            String subtitle = "";

            String filtroColumn = "";
            String filtroJoin = "";
            List filtroObjects = null;

            params.put(HEADER_CANTIDAD, HEADER_CANTIDAD_VALUE);
            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            if (isByCampo()) {
                filtroColumn = "t.campo";
                filtroObjects = getCampos();

                params.put(HEADER_FILTRO, "Campo");
                subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());
            } else if (isByCultivo()) {
                filtroColumn = "s.cultivo";
                filtroObjects = getCultivos();
                filtroJoin = " INNER JOIN t.campania.siembras s ";

                params.put(HEADER_FILTRO, "Cultivo");
                subtitle += " ; " + ReportUtils.armarSubtituloCultivo(getCultivos());
            } else if (isByVariedad()) {
                filtroColumn = "s.variedadCultivo";
                filtroObjects = getVariedades();
                filtroJoin = " INNER JOIN t.campania.siembras s ";

                params.put(HEADER_FILTRO, "Cultivo");
                subtitle +=
                        " ; " + ReportUtils.armarSubtituloVariedadesCultivo(getCultivos(), getVariedades());
            }

            setReportSubTitle(subtitle, ";");

            EntityManager em = EntityManagerUtil.createEntityManager();

            calculateAuxValues(em, filtroObjects);

            if (isByCampo()) {
                String queryCantidad =
                        "SELECT t.campania, " + filtroColumn + ", t.formaFumigacion, dt.agroquimico, SUM(dt.cantidadUtilizada.valor) "
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt " + filtroJoin
                        + " WHERE t.campania IN (:campanias) "
                        + " AND dt.agroquimico IN (:agroquimicos) "
                        + " AND " + filtroColumn + " IN (:filtros) "
                        + (getFormaFumigacion() != null ? " AND t.formaFumigacion = :formaFumigacion " : "")
                        + (getMomentoAplicacion() != null ? " AND dt.momentoAplicacion = :momentoAplicacion " : "")
                        + " GROUP BY t.campania, " + filtroColumn + ", t.formaFumigacion, dt.agroquimico "
                        + " ORDER BY t.campania, " + filtroColumn + ", t.formaFumigacion, dt.agroquimico ";

                QueryImpl hibernateQueryCantidad = (QueryImpl) em.createQuery(queryCantidad);
                hibernateQueryCantidad.getHibernateQuery().setParameterList("campanias", getCampanias());
                hibernateQueryCantidad.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
                hibernateQueryCantidad.getHibernateQuery().setParameterList("filtros", filtroObjects);
                if (getFormaFumigacion() != null) {
                    hibernateQueryCantidad.setParameter("formaFumigacion", getFormaFumigacion());
                }
                if (getMomentoAplicacion() != null) {
                    hibernateQueryCantidad.setParameter("momentoAplicacion", getMomentoAplicacion());
                }

                List<Object[]> cantidades = hibernateQueryCantidad.getResultList();

                for (Object[] o : cantidades) {
                    Campania campania = (Campania) o[0];
                    Object filtro = o[1];
                    if (filtro instanceof Campo) {
                        campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                    }
                    FormaFumigacion forma = (FormaFumigacion) o[2];
                    Agroquimico agroquimico = (Agroquimico) o[3];
                    Double cantidad = (Double) o[4];

                    Double superficie = supPulvHelper.getSuperficie(campania, filtro, agroquimico, forma);
                    if (superficie == 0) {
                        continue;
                    }

                    Double cantHa = cantidad / superficie;

                    addReportLine(campania.toString(), filtro.toString(), agroquimico.toString(), forma.toString(), cantHa);
                }
            } else if (isByCultivo() || isByVariedad()) {
                String queryCostosAgroquimicos =
                        "SELECT t.campania, t, t.formaFumigacion, dt.agroquimico, SUM(dt.cantidadUtilizada.valor) "
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " AND dt.agroquimico IN (:agroquimicos) "
                        + (getFormaFumigacion() != null ? " AND t.formaFumigacion = :formaFumigacion " : "")
                        + (getMomentoAplicacion() != null ? " AND dt.momentoAplicacion = :momentoAplicacion " : "")
                        + " GROUP BY t.campania, t, t.formaFumigacion, dt.agroquimico "
                        + " ORDER BY t.campania, t, t.formaFumigacion, dt.agroquimico ";
                QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
                hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                        "campanias", getCampanias());
                hibernateQueryAgroquim.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
                if (getFormaFumigacion() != null) {
                    hibernateQueryAgroquim.setParameter("formaFumigacion", getFormaFumigacion());
                }
                if (getMomentoAplicacion() != null) {
                    hibernateQueryAgroquim.setParameter("momentoAplicacion", getMomentoAplicacion());
                }

                List<Object[]> costos = hibernateQueryAgroquim.getResultList();

                for (Iterator<Object[]> it = costos.iterator(); it.hasNext();) {
                    Object[] o = it.next();

                    Campania campania = (Campania) o[0];
                    Trabajo trabajo = (Trabajo) o[1];
                    FormaFumigacion forma = (FormaFumigacion) o[2];
                    Agroquimico agroquimico = (Agroquimico) o[3];
                    Double cantidad = (Double) o[4];

                    Siembra siembra = siembrasCultivoHelper.findSiembra(trabajo.getSuperficies(), campania);
                    if (siembra == null) {
                        it.remove();
                        continue;
                    }

                    Object cult = null;
                    if (isByCultivo()) {
                        cult = siembra.getCultivo();
                    } else if (isByVariedad()) {
                        cult = siembra.getVariedadCultivo();
                    }

                    Double superficie = supPulvHelper.getSuperficie(campania, cult, agroquimico);
                    if (superficie == 0) {
                        continue;
                    }
                    Double cantHa = cantidad / superficie;

                    addReportLine(campania.toString(), cult.toString(), agroquimico.toString(), forma.toString(), cantHa);
                }
            }

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    public void addReportLine(String campania, String filtro, String agroquimico, String formaFumigacion, Double cantidad)
    {
        HistoricoTrabajoCampoLoteReportLine rl = new HistoricoTrabajoCampoLoteReportLine(
                            campania, filtro, agroquimico, formaFumigacion,
                            cantidad);

        if(datasourceCollection.contains(rl))
        {
            int idx = datasourceCollection.indexOf(rl);
            datasourceCollection.get(idx).addCantidad(rl.getCantidad());
        }else
        {
            datasourceCollection.add(rl);
        }
    }

    private void calculateAuxValues(EntityManager em, List filtroParameters) {
        if (isByCampo()) {
            supPulvHelper = new SuperficiesPulverizadasCampoHelper(em, getCampanias(), getCampos());
        } else if (isByCultivo() || isByVariedad()) {
            siembrasCultivoHelper = new SiembrasCultivoHelper(em, getCampanias(), getCultivos());
            if (isByCultivo()) {
                supPulvHelper = new SuperficiesPulverizadasCultivoHelper(em, getCampanias(), getCultivos(),
                        siembrasCultivoHelper);
            } else if (isByVariedad()) {
                supPulvHelper = new SuperficiesPulverizadasVariedadCultivoHelper(em, getCampanias(), getVariedades(),
                        siembrasCultivoHelper);
            }
        }
    }

    private boolean isByCampo() {
        return getCampos().size() > 0;
    }

    private boolean isByCultivo() {
        return getCultivos().size() > 0 && getVariedades().isEmpty();
    }

    private boolean isByVariedad() {
        return getVariedades().size() > 0;
    }

    protected abstract List<Agroquimico> getAgroquimicos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<Campo> getCampos();

    protected abstract FormaFumigacion getFormaFumigacion();

    protected abstract MomentoAplicacion getMomentoAplicacion();

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/HistoricoTrabajoCampoLote.jrxml";
    }

    @Override
    protected void buildReport() throws JRException, Exception {
        super.buildReport();
    }

    @Override
    public JFreeChart createChart() {
        ValueAxis rangeAxis = new NumberAxis(HEADER_CANTIDAD_VALUE);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);

        Iterator<Entry<String, CategoryDataset>> it = createChartDataset().entrySet().iterator();
        DecimalFormat labelFormatter = new DecimalFormat("###0.000");

        while (it.hasNext()) {
            Entry<String, CategoryDataset> entry = it.next();

            CategoryAxis domainAxis = new CategoryAxis(entry.getKey());
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
            BarRenderer renderer = new BarRenderer();
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", labelFormatter));
            renderer.setDrawBarOutline(false);

            renderer.setBaseItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", labelFormatter));

            JFreeChartUtils.setUpRenderer(renderer);

            renderer.setLegendItemToolTipGenerator(
                    new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));

            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(
                    Math.PI / 6.0));

            CategoryPlot subplot = new CategoryPlot(entry.getValue(), domainAxis, null,
                    renderer);
            subplot.setDomainGridlinesVisible(true);

            plot.add(subplot);
        }

        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(REPORT_TITLE, plot);

        JFreeChartUtils.setSubtitles(chart, getReportSubTitle().split(";"));


        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset() {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (HistoricoTrabajoCampoLoteReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double cantidad = Double.parseDouble(rl.get(CANTIDAD).toString());
            dataset.addValue(cantidad, (String) rl.get(FILTRO) + " - " + rl.get(FORMA_FUMIGACION),
                    (String) rl.get(AGROQUIMICO));
        }

        return r;
    }
}
