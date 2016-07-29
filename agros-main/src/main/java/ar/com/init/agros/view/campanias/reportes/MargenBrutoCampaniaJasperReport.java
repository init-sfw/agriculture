package ar.com.init.agros.view.campanias.reportes;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
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
 * Clase CostosDeCampaniaJasperReport
 *
 *
 * @author fbobbio
 * @version 18-sep-2009 
 */
public abstract class MargenBrutoCampaniaJasperReport extends AbstractJasperReport
{

    private List<CostosDeCampaniaReportLine> datasourceCollection;
    public static final String REPORT_TITLE = "Reporte de Margen Bruto por Campaña";
    private static final String CAMPANIA = "campania";
    private static final String COSTO = "costo";
    private static final String TIPO_COSTO = "tipoCosto";

    /** Constructor por defecto de CostosDeCampaniaJasperReport */
    public MargenBrutoCampaniaJasperReport()
    {
        super(REPORT_TITLE);
    }

    private void insertDataToCollection(String campania, String tipoCosto, Double costo)
    {
        boolean update = false;
        for (CostosDeCampaniaReportLine rl : datasourceCollection) {
            if (rl.size() > 0 && rl.get(CAMPANIA).equals(campania) && rl.get(TIPO_COSTO).equals(tipoCosto)) {
                update = true;
                rl.updateCosto(costo);
            }
        }
        if (!update) {
            CostosDeCampaniaReportLine rl = new CostosDeCampaniaReportLine(campania, tipoCosto, costo);
            datasourceCollection.add(rl);
        }
    }

    protected class CostosDeCampaniaReportLine extends HashMap<String, Object>
    {

        public static final long serialVersionUID = -1L;

        public CostosDeCampaniaReportLine(String campania, String tipoCosto, Double costo)
        {
            super();
            put(CAMPANIA, campania);
            put(TIPO_COSTO, tipoCosto);
            put(COSTO, costo);
        }

        public void updateCosto(Double costo)
        {
            this.put(COSTO, (Double) this.get(COSTO) + costo);
        }
    }

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/MargenBrutoPorCampania.jrxml";
    }

    public abstract List<Campania> getCampanias();

    @Override
    protected boolean createDataSource()
    {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<CostosDeCampaniaReportLine>();

            String subtitle = "";

            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());
            setReportSubTitle(subtitle);

            /**
             * Se hacen 4 querys: 1 para costos de agroquímicos, otra para costos de trabajo, otra para costos de siembra y otra para costos de campaña
             */
            EntityManager em = EntityManagerUtil.createEntityManager();

            String queryCostosAgroquimicos = "SELECT t.campania, SUM(dt.costoHectarea.monto * dt.superficiePlanificada), SUM(dt.superficiePlanificada) " +
                    " FROM " + Trabajo.class.getName() + " AS t " +
                    " INNER JOIN t.detalles dt " +
                    " WHERE t.campania IN (:campanias) " +
                    " GROUP BY t.campania" +
                    " HAVING SUM(dt.superficiePlanificada) > 0" +
                    " ORDER BY t.campania";

            QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
            hibernateQueryAgroquim.getHibernateQuery().setParameterList("campanias", getCampanias());

            List<Object[]> costos = hibernateQueryAgroquim.getResultList();

            for (Object[] o : costos) {
                Campania campania = (Campania) o[0];
                Double monto = (Double) o[1];
                Double superficie = (Double) o[2];

                if (superficie == 0) {
                    continue;
                }

                Double costo = monto / superficie;

                insertDataToCollection(campania.getNombre(), "Costo de Agroquímicos", costo);

            }

            String queryCostosSiembra = "SELECT s.campania, cos.tipoCosto, SUM(cos.importe.monto) " +
                    " FROM " + Siembra.class.getName() + " AS s " +
                    " INNER JOIN s.costos cos " +
                    " WHERE s.campania IN (:campanias) " +
                    " AND cos.tipoCosto IN (:tiposCosto) " +
                    " GROUP BY s.campania, cos.tipoCosto" +
                    " ORDER BY s.campania, cos.tipoCosto";

            QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
            hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias", getCampanias());

            List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();

            for (Object[] o : costosSiembra) {
                Campania campania = (Campania) o[0];
                TipoCosto tipoCosto = (TipoCosto) o[1];
                Double costo = (Double) o[2];

                insertDataToCollection(campania.getNombre(), tipoCosto.getNombre(), costo);

            }

            String queryCostosTrabajo = "SELECT t.campania, cos.tipoCosto, SUM(cos.importe.monto) " +
                    " FROM " + Trabajo.class.getName() + " AS t " +
                    " INNER JOIN t.costos cos " +
                    " WHERE t.campania IN (:campanias) " +
                    " AND cos.tipoCosto IN (:tiposCosto) " +
                    " GROUP BY t.campania, cos.tipoCosto" +
                    " ORDER BY t.campania, cos.tipoCosto";

            QueryImpl hibernateQueryTrabajo = (QueryImpl) em.createQuery(queryCostosTrabajo);
            hibernateQueryTrabajo.getHibernateQuery().setParameterList("campanias", getCampanias());

            List<Object[]> costosTrabajo = hibernateQueryTrabajo.getResultList();

            for (Object[] o : costosTrabajo) {
                Campania campania = (Campania) o[0];
                TipoCosto tipoCosto = (TipoCosto) o[1];
                Double costo = (Double) o[2];

                insertDataToCollection(campania.getNombre(), tipoCosto.getNombre(), costo);
            }

            String queryCostosCampania = "SELECT c.nombre, cos.tipoCosto, SUM(cos.importe.monto) " +
                    " FROM " + Campania.class.getName() + " AS c " +
                    " INNER JOIN c.costos cos " +
                    " WHERE c IN (:campanias) " +
                    " AND cos.tipoCosto IN (:tiposCosto) " +
                    " GROUP BY c.nombre, cos.tipoCosto" +
                    " ORDER BY c.nombre, cos.tipoCosto";

            QueryImpl hibernateQueryCampania = (QueryImpl) em.createQuery(queryCostosCampania);
            hibernateQueryCampania.getHibernateQuery().setParameterList("campanias", getCampanias());

            List<Object[]> costosCampania = hibernateQueryCampania.getResultList();

            for (Object[] o : costosCampania) {
                String campania = (String) o[0];
                TipoCosto tipoCosto = (TipoCosto) o[1];
                Double costo = (Double) o[2];

                insertDataToCollection(campania, tipoCosto.getNombre(), costo);
            }

            em.close();

            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }
    private JFreeChart chart;

    @Override
    public JFreeChart createChart()
    {
        // create the chart...
        chart = ChartFactory.createBarChart(
                REPORT_TITLE, // chart title
                "", // domain axis label
                "Costo [U$S/ha]", // range axis label
                createChartDataset(), // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        List subtitles = JFreeChartUtils.makeSubtitles(getReportSubTitle().split(";"));
        subtitles.addAll(chart.getSubtitles());
        chart.setSubtitles(subtitles);

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
                new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                Math.PI / 6.0));

        return chart;
    }

    private CategoryDataset createChartDataset()
    {
        createDataSource();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        //Configuración del gráfico que se va a mostrar en la ventana
        for (CostosDeCampaniaReportLine rl : datasourceCollection) {
            dataset.addValue(Double.parseDouble(rl.get(COSTO).toString()), (String) rl.get(TIPO_COSTO),
                    (String) rl.get(CAMPANIA));
        }

        return dataset;
    }
}
