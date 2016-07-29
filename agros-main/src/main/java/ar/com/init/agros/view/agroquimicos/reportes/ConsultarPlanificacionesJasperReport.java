package ar.com.init.agros.view.agroquimicos.reportes;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.ejb.QueryImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
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
public abstract class ConsultarPlanificacionesJasperReport extends AbstractJasperReport
{

    public static final String REPORT_TITLE = "Reporte de Planificaciones";
    private static final String INOCULANTE_VALUE = "Inoculante/Curasemilla";
    private static final String SEMILLAS_VALUE = "Semillas";
    private List<PlanificacionReportLine> datasourceCollection;
    private static final String AGROQUIMICO = "agroquimico";
    private static final String COSTO = "costo";
    private static final String CAMPANIA = "campania";

    private class PlanificacionReportLine extends ReportLine
    {

        public static final long serialVersionUID = -1L;

        public PlanificacionReportLine(String agroquimico, Double costo, String campania)
        {
            super();
            put(AGROQUIMICO, agroquimico);
            put(COSTO, costo);
            put(CAMPANIA, campania);
        }

        @Override
        public int compareTo(ReportLine o)
        {
            return keyCompareTo(o, CAMPANIA, AGROQUIMICO);
        }

        @Override
        public int hashCode()
        {
            return this.get(CAMPANIA).hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return keyEquals(obj, CAMPANIA, AGROQUIMICO);
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public ConsultarPlanificacionesJasperReport()
    {
        super(REPORT_TITLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource()
    {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<PlanificacionReportLine>();

            EntityManager em = EntityManagerUtil.createEntityManager();

            String subtitle = ReportUtils.armarSubtituloCampania(getCampanias()) + " ; ";

            calculateAgroquimicos(em, subtitle);

            Collections.sort(datasourceCollection);

            calculateSemillas(em); //Esto es a proposito para que estas columnas queden al final de todo
            em.close();

            createCollectionDataSource(datasourceCollection);
        }
        return (datasourceCollection.size() > 0);
    }

    private void calculateAgroquimicos(EntityManager em, String subtitle) throws HibernateException
    {
        String query =
                "SELECT p.campania.nombre, dp.agroquimico.informacion.nombreComercial, SUM(dp.precioReferencia.monto * dp.dosisPorHectarea.valor * dp.superficiePlanificada)" + " FROM " + PlanificacionAgroquimico.class.getName() + " AS p " + " INNER JOIN p.detallesPlanificacion dp " + " WHERE p.campania IN (:campanias) " + " AND dp.agroquimico IN (:agroquimicos)";
        boolean campo = false;
        if (getCampos().size() > 0) {
            campo = true;
            query = query + " AND p.campo IN (:campos)";
        }
        else {
            query = query + " AND p.cultivo IN (:cultivos) AND p.variedadCultivo IN (:variedades)";
        }
        query = query + " GROUP BY dp.agroquimico, p.campania" + " ORDER BY dp.agroquimico, p.campania";
        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        hibernateQuery.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
        if (campo) {
            hibernateQuery.getHibernateQuery().setParameterList("campos", getCampos());
            subtitle += ReportUtils.armarSubtituloEstablecimiento(getCampos());
        }
        else {
            hibernateQuery.getHibernateQuery().setParameterList("cultivos", getCultivos());
            hibernateQuery.getHibernateQuery().setParameterList("variedades", getVariedades());
            subtitle +=
                    ReportUtils.armarSubtituloVariedadesCultivo(getCultivos(), getVariedades());
        }
        setReportSubTitle(subtitle, ";");
        List<Object[]> detalles = hibernateQuery.getResultList();
        for (Object[] dt : detalles) {
            String camp = dt[0].toString();
            String agro = dt[1].toString();
            Double costo = (Double) dt[2];
            PlanificacionReportLine rl =
                    new PlanificacionReportLine(agro, costo, camp);
            datasourceCollection.add(rl);
        }
    }

    private void calculateSemillas(EntityManager em) throws HibernateException
    {
        String query =
                "SELECT p.campania.nombre, SUM(p.semilla.costoTotalSemillas.monto), SUM(p.semilla.costoTotalInoculante.monto) " +
                " FROM " + PlanificacionAgroquimico.class.getName() + " AS p " +
                " WHERE p.campania IN (:campanias) ";
        boolean campo = false;
        if (getCampos().size() > 0) {
            campo = true;
            query = query + " AND p.campo IN (:campos)";
        }
        else {
            query = query + " AND p.cultivo IN (:cultivos) AND p.variedadCultivo IN (:variedades)";
        }
        query = query + " GROUP BY p.campania" +
                " ORDER BY p.campania";
        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
        if (campo) {
            hibernateQuery.getHibernateQuery().setParameterList("campos", getCampos());

        }
        else {
            hibernateQuery.getHibernateQuery().setParameterList("cultivos", getCultivos());
            hibernateQuery.getHibernateQuery().setParameterList("variedades", getVariedades());
        }
        List<Object[]> detalles = hibernateQuery.getResultList();
        for (Object[] dt : detalles) {
            String camp = dt[0].toString();
            Double costoSem = (Double) dt[1];
            Double costoInoc = (Double) dt[2];

            PlanificacionReportLine rlSem = new PlanificacionReportLine(SEMILLAS_VALUE, costoSem, camp);
            PlanificacionReportLine rlInoc = new PlanificacionReportLine(INOCULANTE_VALUE, costoInoc, camp);

            boolean found = false;
            int idx = datasourceCollection.size();
            for (PlanificacionReportLine reportLine : datasourceCollection) {

                boolean sameGroup = rlSem.get(CAMPANIA).equals(reportLine.get(CAMPANIA));

                if (!found && sameGroup) {
                    found = true;
                }

                if (found && !sameGroup) {
                    idx = datasourceCollection.indexOf(reportLine);
                    found = false;
                    break;
                }
            }


            datasourceCollection.add(idx, rlSem);
            datasourceCollection.add(idx + 1, rlInoc);

        }
    }

    protected abstract List<Campo> getCampos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<Agroquimico> getAgroquimicos();

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/ConsultarPlanificaciones.jrxml";
    }

    @Override
    public JFreeChart createChart()
    {
        // create the chart...
        chart = ChartFactory.createBarChart(
                REPORT_TITLE, // chart title
                "Agroquímico",
                "Costo [U$S]",
                createChartDataset(), // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        JFreeChartUtils.setSubtitles(chart, getReportSubTitle().split(";"));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        renderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());
        JFreeChartUtils.setUpRenderer(renderer);

        renderer.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("{0}"));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                Math.PI / 6.0));

        return chart;
    }
    private JFreeChart chart;

    private CategoryDataset createChartDataset()
    {
        createDataSource();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        //Configuración del gráfico que se va a mostrar en la ventana
        for (PlanificacionReportLine rl : datasourceCollection) {
            dataset.addValue(Double.parseDouble(rl.get(COSTO).toString()), rl.get(CAMPANIA).toString(),
                    (String) rl.get(AGROQUIMICO));
        }

        return dataset;
    }
}
