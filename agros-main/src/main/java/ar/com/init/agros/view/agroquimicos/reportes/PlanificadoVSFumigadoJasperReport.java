package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.view.reporting.helper.AbstractSuperficiesHelper;
import ar.com.init.agros.view.reporting.helper.SiembrasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPlanificadasHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCampoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasVariedadCultivoHelper;
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
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase PlanificadoVSFumigadoJasperReport
 *
 *
 * @author fbobbio
 * @version 21-sep-2009 
 */
public abstract class PlanificadoVSFumigadoJasperReport extends AbstractJasperReport {

    public static final String HEADER_COSTO_VALUE = "Costo por hectárea [U$S/ha]";
    public static final String REPORT_TITLE = "Reporte Comparativo de Insumos";
    private List<PlanificadoVSFumigadoReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String CAMPANIA = "campania";
    private static final String FILTRO = "filtro";
    private static final String PLANIFICADO = "planificado";
    private static final String FUMIGADO = "fumigado";
    private static final String HEADER_FILTRO = "headerFiltro";
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    /** Constructor por defecto de PlanificadoVSFumigadoJasperReport */
    public PlanificadoVSFumigadoJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/PlanificadoVsFumigado.jrxml";
    }

    @Override
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<PlanificadoVSFumigadoReportLine>();

            String subtitle = "";

            String filtroColumn = "";
            String filtroColumnPlanif = "";
            List filtroObjects = null;

            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            EntityManager em = EntityManagerUtil.createEntityManager();

            if (isByCampo()) {
                SuperficiesPulverizadasCampoHelper supPulvHelper = new SuperficiesPulverizadasCampoHelper(em,
                        getCampanias(), getCampos());
                filtroColumn = "t.campo";
                filtroColumnPlanif = filtroColumn;
                filtroObjects = getCampos();

                params.put(HEADER_FILTRO, "Establecimiento");
                subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());

                String queryPulverizado = "SELECT t.campania, " + filtroColumn + ", dt.agroquimico, dt"
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " AND dt.agroquimico IN (:agroquimicos) "
                        + " AND " + filtroColumn + " IN (:filtros) "
                        + " GROUP BY t.campania, " + filtroColumn + ", dt.agroquimico, dt  "
                        + " ORDER BY t.campania, " + filtroColumn + ", dt.agroquimico  ";

                QueryImpl hibernateQueryPulverizado = (QueryImpl) em.createQuery(queryPulverizado);
                hibernateQueryPulverizado.getHibernateQuery().setParameterList("campanias", getCampanias());
                hibernateQueryPulverizado.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
                hibernateQueryPulverizado.getHibernateQuery().setParameterList("filtros", filtroObjects);

                List<Object[]> pulverizados = hibernateQueryPulverizado.getResultList();

                List<Object[]> sumarizado = sumarizar(pulverizados);

                for (Object[] o : sumarizado) {
                    Campania campania = (Campania) o[0];
                    Object filtro = o[1];
                    if (filtro instanceof Campo) {
                        campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                    }
                    Agroquimico agroquimico = (Agroquimico) o[2];
                    Double fumigado = (Double) o[3];

                    Double superficie = supPulvHelper.getSuperficie(campania, (Campo) filtro, agroquimico);

                    if (superficie == null) {
                        continue;
                    }

                    Double costo = fumigado / superficie;

                    PlanificadoVSFumigadoReportLine rl = new PlanificadoVSFumigadoReportLine(
                            campania.toString(),
                            filtro.toString(), agroquimico.toString(),
                            0D, costo);

                    datasourceCollection.add(rl);
                }
            } else if (isByCultivo() || isByVariedad()) {

                SiembrasCultivoHelper siembrasHelper = new SiembrasCultivoHelper(em, getCampanias(),
                        getCultivos());
                AbstractSuperficiesHelper supPulvHelper = null;

                if (isByCultivo()) {
                    filtroColumnPlanif = "t.cultivo";
                    filtroObjects = getCultivos();
                    params.put(HEADER_FILTRO, "Cultivo");
                    subtitle += " ; " + ReportUtils.armarSubtituloCultivo(getCultivos());
                    supPulvHelper = new SuperficiesPulverizadasCultivoHelper(em, getCampanias(), getCultivos(),
                            siembrasHelper);
                } else if (isByVariedad()) {
                    filtroColumnPlanif = "t.variedadCultivo";
                    filtroObjects = getVariedades();
                    params.put(HEADER_FILTRO, "Cultivo - Variedad");
                    subtitle += " ; " + ReportUtils.armarSubtituloVariedadesCultivo(getCultivos(), getVariedades());
                    supPulvHelper = new SuperficiesPulverizadasVariedadCultivoHelper(em, getCampanias(), getVariedades(),
                            siembrasHelper);
                }

                String queryCostosAgroquimicos =
                        "SELECT t.campania, t, dt.agroquimico, dt "
                        + " FROM " + Trabajo.class.getName() + " AS t "
                        + " INNER JOIN t.detalles dt "
                        + " WHERE t.campania IN (:campanias) "
                        + " AND dt.agroquimico IN (:agroquimicos) "
                        + " GROUP BY t.campania, t, t.formaFumigacion, dt.agroquimico, dt "
                        + " ORDER BY t.campania, t, t.formaFumigacion, dt.agroquimico ";
                QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
                hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                        "campanias", getCampanias());
                hibernateQueryAgroquim.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());

                List<Object[]> costos = hibernateQueryAgroquim.getResultList();
                List<Object[]> sumarizados = sumarizar(costos, siembrasHelper, isByVariedad());

                for (Iterator<Object[]> it = sumarizados.iterator(); it.hasNext();) {
                    Object[] o = it.next();

                    Campania campania = (Campania) o[0];
                    Object cult = o[1];
                    Agroquimico agroquimico = (Agroquimico) o[2];
                    Double cantidad = (Double) o[3];

                    if (cult == null) {
                        continue;
                    }

                    Double superficie = supPulvHelper.getSuperficie(campania, cult, agroquimico);
                    if (superficie == 0) {
                        continue;
                    }
                    Double cantHa = cantidad / superficie;

                    PlanificadoVSFumigadoReportLine rl = new PlanificadoVSFumigadoReportLine(
                            campania.toString(),
                            cult.toString(), agroquimico.toString(),
                            0D, cantHa);

                    datasourceCollection.add(rl);
                }
            }

            SuperficiesPlanificadasHelper supPlanificacadasHelper = new SuperficiesPlanificadasHelper(em,
                    filtroColumnPlanif, getCampanias(), filtroObjects);

            String queryPlanificado = "SELECT t.campania, " + filtroColumnPlanif + ", dt.agroquimico, SUM(dt.costoPlanificado.monto) "
                    + " FROM " + PlanificacionAgroquimico.class.getName() + " AS t "
                    + " INNER JOIN t.detallesPlanificacion dt "
                    + " WHERE t.campania IN (:campanias) "
                    + " AND dt.agroquimico IN (:agroquimicos) "
                    + " AND " + filtroColumnPlanif + " IN (:filtros) "
                    + " GROUP BY t.campania, " + filtroColumnPlanif + ", dt.agroquimico  "
                    + " ORDER BY t.campania, " + filtroColumnPlanif + ", dt.agroquimico  ";

            QueryImpl hibernateQueryPlanificado = (QueryImpl) em.createQuery(queryPlanificado);
            hibernateQueryPlanificado.getHibernateQuery().setParameterList("campanias", getCampanias());
            hibernateQueryPlanificado.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
            hibernateQueryPlanificado.getHibernateQuery().setParameterList("filtros", filtroObjects);
            List<Object[]> planificados = hibernateQueryPlanificado.getResultList();

            for (Object[] dt : planificados) {

                Campania campania = (Campania) dt[0];
                Object filtro = dt[1];
                if (filtro instanceof Campo) {
                    campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                }
                Agroquimico agroquimico = (Agroquimico) dt[2];
                Double planificado = (Double) dt[3];

                Double superficie = supPlanificacadasHelper.getSuperficie(campania, filtro, agroquimico);

                Double planificadoHa = planificado / superficie;

                PlanificadoVSFumigadoReportLine rl = new PlanificadoVSFumigadoReportLine(
                        campania.toString(),
                        filtro.toString(), agroquimico.toString(),
                        planificadoHa,
                        0D);

                int idx = datasourceCollection.indexOf(rl);
                if (idx == -1) {
                    datasourceCollection.add(rl);
                } else {
                    rl = datasourceCollection.get(idx);
                    rl.setCostoPlanificado(planificadoHa);
                }
            }

            setReportSubTitle(subtitle, ";");

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private List<Object[]> sumarizar(List<Object[]> pulverizados) {
        return sumarizar(pulverizados, null, false);
    }

    private List<Object[]> sumarizar(List<Object[]> pulverizados, SiembrasCultivoHelper siembrasHelper, boolean useVariedad) {
        SortedMap<Campania, SortedMap<Object, SortedMap<Agroquimico, Double>>> sumarizador = new TreeMap<Campania, SortedMap<Object, SortedMap<Agroquimico, Double>>>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];
            Object f = o[1];
            if (f instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) f);
            } else if (siembrasHelper != null && f instanceof Trabajo) {
                if (useVariedad) {
                    f = siembrasHelper.findVariedadCultivo((Trabajo) f, c);
                } else {
                    f = siembrasHelper.findCultivo((Trabajo) f, c);
                }
            }

            if (f != null) {
                Agroquimico a = (Agroquimico) o[2];
                DetalleTrabajo dt = (DetalleTrabajo) o[3];
                Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();

                if (!sumarizador.containsKey(c)) {
                    sumarizador.put(c, new TreeMap<Object, SortedMap<Agroquimico, Double>>());
                }
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

    private class PlanificadoVSFumigadoReportLine extends ReportLine {

        public PlanificadoVSFumigadoReportLine(String campania, String filtro, String agroquimico, Double planificado, Double fumigado) {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(AGROQUIMICO, agroquimico);
            put(PLANIFICADO, planificado);
            put(FUMIGADO, fumigado);
        }

        public void setCostoPlanificado(Double costo) {
            put(PLANIFICADO, costo);
        }

        public void setCostoFumigado(Double costo) {
            put(FUMIGADO, costo);
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
            renderer.setLegendItemToolTipGenerator(
                    new StandardCategorySeriesLabelGenerator("{0}"));
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

        for (PlanificadoVSFumigadoReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double planif = 0D;
            Double fumig = 0D;
            try {
                planif = (Double) dataset.getValue((String) rl.get(FILTRO), "Planificado");
            } catch (UnknownKeyException uke) {
            }
            try {
                fumig = (Double) dataset.getValue((String) rl.get(FILTRO), "Fumigado");
            } catch (UnknownKeyException uke) {
            }

            planif += Double.parseDouble(rl.get(PLANIFICADO).toString());
            fumig += Double.parseDouble(rl.get(FUMIGADO).toString());

            dataset.addValue(planif, (String) rl.get(FILTRO), "Planificado");
            dataset.addValue(fumig, (String) rl.get(FILTRO), "Fumigado");
        }

        return r;
    }
}
