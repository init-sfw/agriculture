package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
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
 * Clase ConsultarPlanificacionesJasperReport
 *
 *
 * @author fbobbio
 * @version 01-sep-2009 
 */
public abstract class AgroquimicosPlanificadosJasperReport extends AbstractJasperReport
{

    public static final String REPORT_TITLE = "Reporte de Agroquímicos Planificados";
    private static final String SEMILLAS_VALUE = "Semillas";
    private List<AgroquimicosPlanificadosReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String CANTIDAD = "cantidad";
    private static final String CAMPANIA = "campania";
    private static final String FILTRO = "filtro";
    private static final String UNIDAD = "unidad";
    private static final String HEADER_FILTRO = "headerFiltro";

    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    private class AgroquimicosPlanificadosReportLine extends ReportLine
    {

        public static final long serialVersionUID = -1L;

        public AgroquimicosPlanificadosReportLine(String campania, String filtro, String agroquimico, Double cantidad, String unidad)
        {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(AGROQUIMICO, agroquimico);
            put(CANTIDAD, cantidad);
            put(UNIDAD, unidad);
        }

        @Override
        public int compareTo(ReportLine o)
        {
            return keyCompareTo(o, CAMPANIA, FILTRO, AGROQUIMICO);
        }

        @Override
        public boolean equals(Object o)
        {
            return keyEquals(o, CAMPANIA, FILTRO, CANTIDAD);
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            return this.get(CAMPANIA).hashCode();
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public AgroquimicosPlanificadosJasperReport()
    {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource()
    {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<AgroquimicosPlanificadosReportLine>();

            String subtitle = "";

            String filtroColumn = "";
            List<VariedadCultivo> variedades = null;


            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());
            if (isByCampo()) {
                filtroColumn = "p.campo";

                params.put(HEADER_FILTRO, "Establecimiento");
                subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());
            }
            else if (isByCultivo()) {
                filtroColumn = "p.cultivo.nombre";

                params.put(HEADER_FILTRO, "Cultivo");
                subtitle += " ; " + ReportUtils.armarSubtituloCultivo(getCultivos());

                variedades = new ArrayList<VariedadCultivo>();
                for (Cultivo cultivo : getCultivos()) {
                    variedades.addAll(cultivo.getVariedades());
                }
            }
            else if (isByVariedad()) {
                filtroColumn = "p.cultivo.nombre || \' - \' || p.variedadCultivo.nombre";

                params.put(HEADER_FILTRO, "Cultivo - Variedad");
                subtitle +=
                        " ; " + ReportUtils.armarSubtituloVariedadesCultivo(getCultivos(), getVariedades());
                variedades = getVariedades();
            }

            setReportSubTitle(subtitle, ";");

            EntityManager em = EntityManagerUtil.createEntityManager();

            calculateAgroquimicos(filtroColumn, em, variedades);

            Collections.sort(datasourceCollection);

            calculateSemillas(filtroColumn, em, variedades); //Esto es a proposito para que estas columnas queden al final de todo

            em.close();

            createCollectionDataSource(datasourceCollection);
        }
        return (datasourceCollection.size() > 0);
    }

    private void calculateAgroquimicos(String filtroColumn, EntityManager em, List<VariedadCultivo> variedades) throws HibernateException
    {
        String query =
                "SELECT p.campania.nombre," + filtroColumn + ", dp.agroquimico, SUM(dp.dosisPorHectarea.valor * p.superficiePlanificada) " + " FROM " + PlanificacionAgroquimico.class.getName() + " AS p " + " INNER JOIN p.detallesPlanificacion dp " + " WHERE p.campania IN (:campanias) " + " AND dp.agroquimico IN (:agroquimicos)";
        if (isByCampo()) {
            query += " AND p.campo IN (:campos)";
        }
        else {
            query += " AND p.cultivo IN (:cultivos) AND p.variedadCultivo IN (:variedades)";
        }
        query +=
                " GROUP BY dp.agroquimico, p.campania.nombre, " + filtroColumn + " ORDER BY dp.agroquimico, p.campania.nombre";
        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        hibernateQuery.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
        if (isByCampo()) {
            hibernateQuery.getHibernateQuery().setParameterList("campos", getCampos());
        }
        else {
            hibernateQuery.getHibernateQuery().setParameterList("cultivos", getCultivos());
            hibernateQuery.getHibernateQuery().setParameterList("variedades", variedades);
        }
        List<Object[]> detalles = hibernateQuery.getResultList();
        for (Object[] dt : detalles) {
            String camp = dt[0].toString();
            Object filtro = dt[1];
            if (filtro instanceof Campo)
            {
                campoController.cargarCampaniasAsociadasACampo((Campo)filtro);
            }
            Agroquimico agro = (Agroquimico) dt[2];
            Double cant = (Double) dt[3];
            AgroquimicosPlanificadosReportLine rl =
                    new AgroquimicosPlanificadosReportLine(camp, filtro.toString(),
                    agro.getInformacion().getNombreComercial() + " (" + agro.getUnidad().getAbreviatura() + ")",
                    cant, agro.getUnidad().getAbreviatura());
            datasourceCollection.add(rl);
        }
    }

    private void calculateSemillas(String filtroColumn, EntityManager em, List<VariedadCultivo> variedades) throws HibernateException
    {
        String query =
                "SELECT p.campania.nombre," + filtroColumn + ", SUM(p.semilla.cantTotalSemillas.valor) " +
                " FROM " + PlanificacionAgroquimico.class.getName() + " AS p " +
                "WHERE p.campania IN (:campanias) ";
        if (isByCampo()) {
            query += " AND p.campo IN (:campos)";
        }
        else {
            query += " AND p.cultivo IN (:cultivos) AND p.variedadCultivo IN (:variedades)";
        }
        query +=
                " GROUP BY p.campania.nombre, " + filtroColumn +
                " ORDER BY p.campania.nombre";
        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        if (isByCampo()) {
            hibernateQuery.getHibernateQuery().setParameterList("campos", getCampos());
        }
        else {
            hibernateQuery.getHibernateQuery().setParameterList("cultivos", getCultivos());
            hibernateQuery.getHibernateQuery().setParameterList("variedades", variedades);
        }
        List<Object[]> detalles = hibernateQuery.getResultList();
        for (Object[] dt : detalles) {
            String camp = dt[0].toString();
            Object filtro = dt[1];
            if (filtro instanceof Campo)
            {
                campoController.cargarCampaniasAsociadasACampo((Campo)filtro);
            }
            Double cant = (Double) dt[2];
            AgroquimicosPlanificadosReportLine rl =
                    new AgroquimicosPlanificadosReportLine(camp, filtro.toString(),
                    SEMILLAS_VALUE, cant, "Kg");

            boolean found = false;
            int idx = datasourceCollection.size();
            for (AgroquimicosPlanificadosReportLine reportLine : datasourceCollection) {

                boolean sameGroup = rl.get(FILTRO).equals(reportLine.get(FILTRO)) && rl.get(CAMPANIA).equals(
                        reportLine.get(CAMPANIA));

                if (!found && sameGroup) {
                    found = true;
                }

                if (found && !sameGroup) {
                    idx = datasourceCollection.indexOf(reportLine);
                    found = false;
                    break;
                }
            }
            datasourceCollection.add(idx, rl);
        }
    }

    private boolean isByCampo()
    {
        return getCampos().size() > 0;
    }

    private boolean isByCultivo()
    {
        return getCultivos().size() > 0 && getVariedades().size() == 0;
    }

    private boolean isByVariedad()
    {
        return getVariedades().size() > 0;
    }

    protected abstract List<Campo> getCampos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<Agroquimico> getAgroquimicos();

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/AgroquimicosPlanificados.jrxml";
    }

    @Override
    public JFreeChart createChart()
    {
        ValueAxis rangeAxis = new NumberAxis("Cantidad Planificada");
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(rangeAxis);

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

    private Map<String, CategoryDataset> createChartDataset()
    {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (AgroquimicosPlanificadosReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double cantidad = Double.parseDouble(rl.get(CANTIDAD).toString());
            dataset.addValue(cantidad, (String) rl.get(FILTRO),
                    (String) rl.get(AGROQUIMICO));
        }

        return r;
    }
}
