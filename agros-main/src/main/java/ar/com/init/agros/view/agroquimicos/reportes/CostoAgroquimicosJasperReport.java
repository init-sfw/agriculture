package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.view.reporting.helper.SiembrasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCampoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCultivoHelper;
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
import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.ejb.QueryImpl;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
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
public abstract class CostoAgroquimicosJasperReport extends AbstractJasperReport {

    public static final String HEADER_COSTO_VALUE = "U$S / ha";
    public static final String REPORT_TITLE = "Reporte de Costos de Agroquímicos";
    private List<CostoAgroquimicosReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String CAMPANIA = "campania";
    private static final String COSTO = "costo";
    private static final String FILTRO = "filtro";
    private static final String HEADER_FILTRO = "headerFiltro";
    private static final String HEADER_COSTO = "headerCosto";
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    private String createCampoDatasource(EntityManager em, String subtitle, String filtroJoin, String filtroColumn, List filtroObjects) throws HibernateException {

        List<Campo> campos = getCampos();

        params.put(HEADER_FILTRO, "Establecimiento");
        filtroColumn = "t.campo";
        filtroObjects = getCampos();
        subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());

        String condition = " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt " + filtroJoin
                + " WHERE t.campania IN (:campanias) "
                + " AND dt.agroquimico IN (:agroquimicos) "
                + " AND " + filtroColumn + " IN (:filtros) "
                + " GROUP BY t.campania, " + filtroColumn + ", dt.agroquimico, dt "
                + " ORDER BY t.campania, "
                + filtroColumn + ", dt.agroquimico ";

        String query = "SELECT t.campania, " + filtroColumn + ", dt.agroquimico, dt, 0 " + condition;

        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        hibernateQuery.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
        hibernateQuery.getHibernateQuery().setParameterList("filtros", filtroObjects);

        SuperficiesPulverizadasCampoHelper supPulvHelper = new SuperficiesPulverizadasCampoHelper(em, getCampanias(), campos);

        List<Object[]> costos = hibernateQuery.getResultList();
        List<Object[]> costosSumarizados = sumarizarCostoAgroquimicos(costos);

        for (Object[] o : costosSumarizados) {
            Campania campania = (Campania) o[0];
            Object filtro = o[1];
            if (filtro instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
            }
            Agroquimico agroquimico = (Agroquimico) o[2];
            Double monto = (Double) o[3];
            Double superficie = supPulvHelper.getSuperficie(campania, (Campo) filtro, agroquimico);
            if (superficie == null) {
                continue;
            }
            Double costo = monto / superficie;
            CostoAgroquimicosReportLine rl = new CostoAgroquimicosReportLine(campania.toString(), filtro.toString(), agroquimico.toString(), costo);
            datasourceCollection.add(rl);
        }

        return subtitle;
    }

    private List<Object[]> sumarizarCostoAgroquimicos(List<Object[]> pulverizados) {
        return sumarizarCostoAgroquimicos(pulverizados, null);
    }

    private List<Object[]> sumarizarCostoAgroquimicos(List<Object[]> pulverizados, SiembrasCultivoHelper siembrasHelper) {
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
                if (a != null) {
                    if (!sumarizador.get(c).get(f).containsKey(a)) {
                        sumarizador.get(c).get(f).put(a, fumigado);
                    } else {
                        sumarizador.get(c).get(f).put(a, fumigado + sumarizador.get(c).get(f).get(a));
                    }
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

    protected class CostoAgroquimicosReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public CostoAgroquimicosReportLine(String campania, String filtro, String agroquimico, Double costo) {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(AGROQUIMICO, agroquimico);
            put(COSTO, costo);
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

        private void addCosto(Double monto) {
            Double costo = (Double) get(COSTO);
            costo += monto;

            put(COSTO, costo);
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public CostoAgroquimicosJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<CostoAgroquimicosReportLine>();

            String subtitle = "";

            String filtroColumn = "";
            String filtroJoin = "";
            List filtroObjects = null;

            params.put(HEADER_COSTO, HEADER_COSTO_VALUE);
            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            EntityManager em = EntityManagerUtil.createEntityManager();

            if (isByCampo()) {
                subtitle = createCampoDatasource(em, subtitle, filtroJoin, filtroColumn, filtroObjects);
            } else if (isByCultivo()) {
                subtitle = creaCultivoDatasource(em, subtitle);

            }
            setReportSubTitle(subtitle, ";");

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private String creaCultivoDatasource(EntityManager em, String subtitle) throws HibernateException {
        String filtroJoin;
        String filtroColumn;
        List filtroObjects;
        SiembrasCultivoHelper siembrasHelper = new SiembrasCultivoHelper(em, getCampanias(), getCultivos());
        SuperficiesPulverizadasCultivoHelper supPulvHelper = new SuperficiesPulverizadasCultivoHelper(em, getCampanias(), getCultivos(), siembrasHelper);
        filtroColumn = "s.cultivo";
        filtroObjects = getCultivos();
        filtroJoin = " INNER JOIN t.campania.siembras s ";
        params.put(HEADER_FILTRO, "Cultivo");
        subtitle += " ; " + ReportUtils.armarSubtituloCultivo(getCultivos());
        String queryCostosAgroquimicos = "SELECT t.campania, t, dt.agroquimico, dt"
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt "
                + " WHERE t.campania IN (:campanias) "
                + " GROUP BY t.campania, t, dt.agroquimico, dt "
                + " ORDER BY t.campania, t, dt.agroquimico ";
        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        List<Object[]> costos = hibernateQuery.getResultList();
        List<Object[]> costosSumarizados = sumarizarCostoAgroquimicos(costos, siembrasHelper);
        for (Iterator<Object[]> it = costosSumarizados.iterator(); it.hasNext();) {
            Object[] o = it.next();
            Campania campania = (Campania) o[0];
            Cultivo cultivo = (Cultivo) o[1];
            Agroquimico agroquimico = (Agroquimico) o[2];            
            Double monto = (Double) o[3];
            
            Double superficie = supPulvHelper.getSuperficie(campania, cultivo, agroquimico);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            CostoAgroquimicosReportLine rl = new CostoAgroquimicosReportLine(campania.toString(), cultivo.toString(), agroquimico.toString(), costo);
            datasourceCollection.add(rl);
        }
        return subtitle;
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
        return "ar/com/init/agros/reporting/reports/CostoAgroquimicos.jrxml";
    }

    @Override
    public JFreeChart createChart() {
        ValueAxis rangeAxis = new NumberAxis(HEADER_COSTO_VALUE);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);
        rangeAxis.setUpperMargin(0.10);  // leave some space for item labels

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

            CategoryPlot subplot = new CategoryPlot(entry.getValue(), domainAxis, null,
                    renderer);
            subplot.setDomainGridlinesVisible(true);
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(
                    Math.PI / 6.0));

            plot.add(subplot);
        }

        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(REPORT_TITLE, plot);

        JFreeChartUtils.setSubtitles(chart, getReportSubTitle().split(";"));

        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset() {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (CostoAgroquimicosReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double value = Double.parseDouble(rl.get(COSTO).toString());

            dataset.addValue(value, (String) rl.get(FILTRO),
                    (String) rl.get(AGROQUIMICO));
        }

        return r;
    }
}
