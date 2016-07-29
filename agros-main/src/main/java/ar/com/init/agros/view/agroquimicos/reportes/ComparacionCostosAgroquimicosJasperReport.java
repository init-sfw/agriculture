package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.view.reporting.helper.SiembrasCultivoHelper;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
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
 * Clase CostoAgroquimicosJasperReport
 *
 *
 * @author gmatheu
 * @version 30/07/2009 
 */
public abstract class ComparacionCostosAgroquimicosJasperReport extends AbstractJasperReport {

    private static final Logger log = Logger.getLogger(
            ComparacionCostosAgroquimicosJasperReport.class.getName());
    public static final String HEADER_PORCENTAJE_VALUE = "%";
    private static final String HEADER_IMPORTE_VALUE = "Importe (U$S)";
    public static final String REPORT_TITLE = "Reporte de Costos Relativos de Agroquímicos";
    private List<ComparacionCostosAgroquimicosReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String CAMPANIA = "campania";
    private static final String TOTAL = "total";
    private static final String FILTRO = "filtro";
    private static final String IMPORTE = "importe";
    private static final String PORCENTAJE = "porcentaje";
    private static final String HEADER_FILTRO = "headerFiltro";
    private static final String HEADER_PORCENTAJE = "headerPorcentaje";
    private static final String HEADER_IMPORTE = "headerImporte";
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    protected class ComparacionCostosAgroquimicosReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public ComparacionCostosAgroquimicosReportLine(String campania, String filtro, String agroquimico, Double importe, Double total) {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(AGROQUIMICO, agroquimico);
            put(IMPORTE, importe);
            put(TOTAL, total);
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
            return keyEquals(obj, CAMPANIA, FILTRO, AGROQUIMICO);
        }

        private void addImporte(Double importe) {
            Double acum = (Double) get(IMPORTE);
            acum += importe;

            put(IMPORTE, acum);
        }

        private void addTotal(Double total) {
            Double acum = (Double) get(TOTAL);
            acum += total;

            put(TOTAL, acum);
        }

        private void calcularPorcentaje() {
            Double total = (Double) get(TOTAL);
            Double importe = (Double) get(IMPORTE);
            Double pct = importe / total;

            put(PORCENTAJE, pct);
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public ComparacionCostosAgroquimicosJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<ComparacionCostosAgroquimicosReportLine>();

            String subtitle = "";

            String filtroColumn = "";
            List filtroObjects = null;

            params.put(HEADER_PORCENTAJE, HEADER_PORCENTAJE_VALUE);
            params.put(HEADER_IMPORTE, HEADER_IMPORTE_VALUE);
            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            EntityManager em = EntityManagerUtil.createEntityManager();

            if (isByCampo()) {
                filtroColumn = "t.campo";
                filtroObjects = getCampos();

                params.put(HEADER_FILTRO, "Establecimiento");
                subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());

                String queryImportes = "SELECT t.campania, " + filtroColumn + ", dt.agroquimico, dt"
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " AND dt.agroquimico IN (:agroquimicos) "
                        + " AND " + filtroColumn + " IN (:filtros) "
                        + " GROUP BY t.campania, " + filtroColumn + ", dt.agroquimico, dt  "
                        + " ORDER BY t.campania, " + filtroColumn + ", dt.agroquimico  ";

                QueryImpl hibernateQueryImportes = (QueryImpl) em.createQuery(queryImportes);
                hibernateQueryImportes.getHibernateQuery().setParameterList("campanias", getCampanias());
                hibernateQueryImportes.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
                hibernateQueryImportes.getHibernateQuery().setParameterList("filtros", filtroObjects);

                List<Object[]> importes = hibernateQueryImportes.getResultList();
                List<Object[]> importesSumarizados = sumarizarCostosAgroquimicos(importes);

                for (Object[] o : importesSumarizados) {
                    Campania campania = (Campania) o[0];
                    Object filtro = o[1];
                    if (filtro instanceof Campo) {
                        campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                    }
                    Agroquimico agroquimico = (Agroquimico) o[2];
                    Double importe = (Double) o[3];

                    ComparacionCostosAgroquimicosReportLine rl = new ComparacionCostosAgroquimicosReportLine(
                            campania.toString(),
                            filtro.toString(), agroquimico.toString(),
                            importe, 0D);

                    datasourceCollection.add(rl);
                }

                String queryTotales = "SELECT t.campania, " + filtroColumn + ", dt"
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " AND " + filtroColumn + " IN (:filtros) "
                        + " GROUP BY t.campania, " + filtroColumn + ", dt "
                        + " ORDER BY t.campania," + filtroColumn;

                QueryImpl hibernateQueryTotales = (QueryImpl) em.createQuery(queryTotales);
                hibernateQueryTotales.getHibernateQuery().setParameterList("campanias", getCampanias());
                hibernateQueryTotales.getHibernateQuery().setParameterList("filtros", filtroObjects);

                List<Object[]> totales = hibernateQueryTotales.getResultList();
                List<Object[]> totalesSumarizados = sumarizarTotales(totales);

                for (ComparacionCostosAgroquimicosReportLine rl : datasourceCollection) {
                    for (Object[] o : totalesSumarizados) {

                        Campania campania = (Campania) o[0];
                        Object filtro = o[1];
                        if (filtro instanceof Campo) {
                            campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                        }

                        if (rl.get(CAMPANIA).equals(campania.toString()) && rl.get(FILTRO).equals(
                                filtro.toString())) {
                            rl.addTotal((Double) o[2]);
                            rl.calcularPorcentaje();

                            break;
                        }
                    }
                }
            } else if (isByCultivo()) {

                SiembrasCultivoHelper siembrasHelper = new SiembrasCultivoHelper(em, getCampanias(), getCultivos());

                filtroObjects = getCultivos();

                params.put(HEADER_FILTRO, "Cultivo");
                subtitle += " ; " + ReportUtils.armarSubtituloCultivo(getCultivos());

                String queryCostosAgroquimicos =
                        "SELECT t.campania, t, dt.agroquimico, dt"
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " GROUP BY t.campania, t, dt.agroquimico, dt "
                        + " ORDER BY t.campania, t, dt.agroquimico ";
                QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
                hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                        "campanias", getCampanias());

                List<Object[]> costos = hibernateQueryAgroquim.getResultList();
                List<Object[]> costosSumarizados = sumarizarCostosAgroquimicos(costos, siembrasHelper);

                for (Iterator<Object[]> it = costosSumarizados.iterator(); it.hasNext();) {
                    Object[] o = it.next();

                    Campania campania = (Campania) o[0];
                    Cultivo cultivo = (Cultivo) o[1];
                    Agroquimico agroquimico = (Agroquimico) o[2];
                    Double monto = (Double) o[3];

                    ComparacionCostosAgroquimicosReportLine rl = new ComparacionCostosAgroquimicosReportLine(
                            campania.toString(),
                            cultivo.toString(), agroquimico.toString(),
                            monto, 0D);

                    if (!datasourceCollection.contains(rl)) {
                        datasourceCollection.add(rl);
                    } else {
                        int idx = datasourceCollection.indexOf(rl);
                        datasourceCollection.get(idx).addImporte(monto);
                    }
                }

                String queryTotales = "SELECT t.campania, t, dt"
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " GROUP BY t.campania, t, dt  "
                        + " ORDER BY t.campania, t";

                QueryImpl hibernateQueryTotales = (QueryImpl) em.createQuery(queryTotales);
                hibernateQueryTotales.getHibernateQuery().setParameterList("campanias", getCampanias());

                List<Object[]> totales = hibernateQueryTotales.getResultList();
                List<Object[]> totalesSumarizados = sumarizarTotales(totales, siembrasHelper);

                for (ComparacionCostosAgroquimicosReportLine rl : datasourceCollection) {
                    for (Iterator<Object[]> it = totalesSumarizados.iterator(); it.hasNext();) {
                        Object[] o = it.next();

                        Campania campania = (Campania) o[0];
                        Cultivo cultivo = (Cultivo) o[1];

                        if (rl.get(CAMPANIA).equals(campania.toString()) && rl.get(FILTRO).equals(cultivo.toString())) {
                            rl.addTotal((Double) o[2]);
                            rl.calcularPorcentaje();
                        }
                    }
                }
            }

            setReportSubTitle(subtitle, ";");

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private List<Object[]> sumarizarCostosAgroquimicos(List<Object[]> pulverizados) {
        return sumarizarCostosAgroquimicos(pulverizados, null);
    }

    private List<Object[]> sumarizarCostosAgroquimicos(List<Object[]> pulverizados, SiembrasCultivoHelper siembrasHelper) {
        SortedMap<Campania, SortedMap<Object, SortedMap<Agroquimico, Double>>> sumarizador = new TreeMap<Campania, SortedMap<Object, SortedMap<Agroquimico, Double>>>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];
            Object f = o[1];
            Agroquimico a = (Agroquimico) o[2];
            DetalleTrabajo dt = (DetalleTrabajo) o[3];
            Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();

            if (siembrasHelper != null && f instanceof Trabajo) {
                f = siembrasHelper.findCultivo((Trabajo) f, c);
            }

            if (!sumarizador.containsKey(c)) {
                sumarizador.put(c, new TreeMap<Object, SortedMap<Agroquimico, Double>>());
            }
            if (f != null) {
                if (!sumarizador.get(c).containsKey(f)) {
                    sumarizador.get(c).put(f, new TreeMap<Agroquimico, Double>());
                }

                if (!sumarizador.get(c).get(f).containsKey(a)) {
                    sumarizador.get(c).get(f).put(a, fumigado);
                } else {
                    sumarizador.get(c).get(f).put(a, fumigado + sumarizador.get(c).get(f).get(a));
                }
            }
        }
        List<Object[]> sumarizado = new ArrayList<Object[]>();
        for (Campania c : sumarizador.keySet()) {
            for (Object f : sumarizador.get(c).keySet()) {
                for (Entry<Agroquimico, Double> e : sumarizador.get(c).get(f).entrySet()) {
                    sumarizado.add(new Object[]{c, f, e.getKey(), e.getValue()});
                }
            }
        }
        return sumarizado;
    }

    private List<Object[]> sumarizarTotales(List<Object[]> pulverizados) {
        return sumarizarTotales(pulverizados, null);
    }

    private List<Object[]> sumarizarTotales(List<Object[]> pulverizados, SiembrasCultivoHelper siembrasHelper) {
        SortedMap<Campania, SortedMap<Object, Double>> sumarizador = new TreeMap<Campania, SortedMap<Object, Double>>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];
            Object f = o[1];
            DetalleTrabajo dt = (DetalleTrabajo) o[2];
            Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();

            if (siembrasHelper != null && f instanceof Trabajo) {
                Trabajo trabajo = (Trabajo) f;

                Siembra siembra = siembrasHelper.findSiembra(trabajo.getSuperficies(), c);
                Cultivo cultivo = null;
                if (siembra != null) {
                    cultivo = siembra.getCultivo();
                    f = cultivo;
                } else {
                    f = null;
                }
            }

            if (!sumarizador.containsKey(c)) {
                sumarizador.put(c, new TreeMap<Object, Double>());
            }

            if (f != null) {
                if (!sumarizador.get(c).containsKey(f)) {
                    sumarizador.get(c).put(f, fumigado);
                } else {
                    sumarizador.get(c).put(f, fumigado + sumarizador.get(c).get(f));
                }
            }
        }
        List<Object[]> sumarizado = new ArrayList<Object[]>();
        for (Campania c : sumarizador.keySet()) {
            for (Entry<Object, Double> e : sumarizador.get(c).entrySet()) {
                sumarizado.add(new Object[]{c, e.getKey(), e.getValue()});
            }
        }
        return sumarizado;
    }

    private boolean isByCampo() {
        return getCampos().size() > 0;
    }

    private boolean isByCultivo() {
        return getCultivos().size() > 0;
    }

    protected abstract List<Agroquimico> getAgroquimicos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<Campo> getCampos();

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/ComparacionCostosAgroquimicos.jrxml";
    }

    @Override
    protected void buildReport() throws JRException, Exception {
        super.buildReport();
    }

    @Override
    public JFreeChart createChart() {
        ValueAxis rangeAxis = new NumberAxis(HEADER_PORCENTAJE_VALUE);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);

        Iterator<Entry<String, CategoryDataset>> it = createChartDataset().entrySet().iterator();
        DecimalFormat labelFormatter = new DecimalFormat("##,##0.00");

        while (it.hasNext()) {
            Entry<String, CategoryDataset> entry = it.next();

            CategoryAxis domainAxis = new CategoryAxis(entry.getKey());
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
            BarRenderer renderer = new BarRenderer();
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", labelFormatter));
            renderer.setDrawBarOutline(false);
            JFreeChartUtils.setUpRenderer(renderer);

            renderer.setBaseItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", labelFormatter));

            renderer.setLegendItemToolTipGenerator(
                    new StandardCategorySeriesLabelGenerator("{0}"));

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
        rangeAxis.setLowerBound(0);

        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset() {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (ComparacionCostosAgroquimicosReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double pct = Double.parseDouble(rl.get(PORCENTAJE).toString()) * 100;
            dataset.addValue(pct, (String) rl.get(FILTRO),
                    (String) rl.get(AGROQUIMICO));
        }

        return r;
    }
}
