package ar.com.init.agros.view.costos.reportes;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase CostosJasperReport
 *
 *
 * @author fbobbio
 * @version 09-sep-2009 
 */
public abstract class CostosJasperReport extends AbstractJasperReport
{
    public static final String REPORT_TITLE = "Reporte de Costos";
    private List<CostoReportLine> datasourceCollection;
    private static final String CAMPANIA = "campania";
    private static final String CAMPO = "campo";
    private static final String CULTIVO = "cultivo";
    private static final String TIPO_COSTO = "tipoCosto";
    private static final String COSTO = "costo";

    static class CostoReportLine extends HashMap<String, Object>
    {
        public static final long serialVersionUID = -1L;

        public CostoReportLine(String campania, String campo, String cultivo, String tipoCosto, Double costo)
        {
            super();
            put(CAMPANIA,campania);
            put(CAMPO,campo);
            put(CULTIVO,cultivo);
            put(TIPO_COSTO,tipoCosto);
            put(COSTO,costo);
        }
    }

    /** Constructor por defecto de CostosJasperReport */
    public CostosJasperReport()
    {
        super(REPORT_TITLE);
    }

    protected abstract List<Campo> getCampos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<TipoCosto> getTiposDeCosto();

    @Override
    public JFreeChart createChart()
    {
        ValueAxis rangeAxis = new NumberAxis("Costos [U$S] por Hectárea");
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);

        Iterator<Entry<String, CategoryDataset>> it = createChartDataset().entrySet().iterator();
        //DecimalFormat labelFormatter = new DecimalFormat("##,###.00");

        while (it.hasNext()) {
            Entry<String, CategoryDataset> entry = it.next();

            CategoryAxis domainAxis = new CategoryAxis(entry.getKey());
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
            BarRenderer renderer = new BarRenderer();
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
            //renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2} %", labelFormatter));

            CategoryPlot subplot = new CategoryPlot(entry.getValue(), domainAxis, null,
                    renderer);
            subplot.setDomainGridlinesVisible(true);

            plot.add(subplot);
        }

        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(REPORT_TITLE, plot);

        chart.addSubtitle(new TextTitle(getReportSubTitle()));


        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);
        rangeAxis.setUpperBound(100);
        rangeAxis.setLowerBound(0);


        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset()
    {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (CostoReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPO).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            dataset.addValue(Double.parseDouble(rl.get(COSTO).toString()), (String) rl.get(CULTIVO),
                    (String) rl.get(TIPO_COSTO));
        }

        return r;
    }

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/Costos.jrxml";
    }

    @Override
    protected boolean createDataSource()
    {
        if (datasourceCollection == null)
        {
            datasourceCollection = new ArrayList<CostoReportLine>();

//            EntityManager em = EntityManagerUtil.createEntityManager();
//
//            String query = "SELECT s.campania, s.campo, s.cultivo, dp.agroquimico, SUM(dp.costoPlanificado.monto), p.campania " +
//                    " FROM " + Costo.class.getName() + " AS c " +
//                    ", " + Siembra.class.getName() + " AS s " +
//                    ", " + Trabajo.class.getName() + " AS t " +
//                    ", " + Campania.class.getName() + " AS camp " +
//
//                    " INNER JOIN p.detallesPlanificacion dp " +
//                    " WHERE p.campania IN (:campanias) " +
//                    " AND dp.agroquimico IN (:agroquimicos)";
//
//            boolean campo = false;
//            if (getCampos().size() > 0)
//            {
//                campo = true;
//                query = query + " AND p.campo IN (:campos)";
//            }
//            else
//            {
//                query = query + " AND p.cultivo IN (:cultivos) AND p.variedadCultivo IN (:variedades)";
//            }
//            query = query + " GROUP BY dp.agroquimico, p.campania" +
//                    " ORDER BY dp.agroquimico, p.campania";
//            QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
//            hibernateQuery.getHibernateQuery().setParameterList("campanias", getCampanias());
//            hibernateQuery.getHibernateQuery().setParameterList("agroquimicos", getAgroquimicos());
//            if (campo)
//            {
//                hibernateQuery.getHibernateQuery().setParameterList("campos", getCampos());
//            }
//            else
//            {
//                hibernateQuery.getHibernateQuery().setParameterList("cultivos", getCultivos());
//                hibernateQuery.getHibernateQuery().setParameterList("variedades", getVariedades());
//            }
//            List<Object[]> detalles = hibernateQuery.getResultList();
//
//            for (Object[] dt : detalles)
//            {
//                PlanificacionReportLine rl = new PlanificacionReportLine(((Agroquimico)dt[0]).getInformacion().getNombreComercial(),(Double)dt[1],dt[2].toString());
//                datasourceCollection.add(rl);
//            }
//            em.close();

            datasourceCollection.add(new CostoReportLine("campaña1", "campo1", "cult1", "tipo1", 10D));
            datasourceCollection.add(new CostoReportLine("campaña1", "campo1", "cult2", "tipo1", 12.2D));
            datasourceCollection.add(new CostoReportLine("campaña1", "campo2", "cult2", "tipo3", 20D));
            datasourceCollection.add(new CostoReportLine("campaña2", "campo2", "cult1", "tipo1", 30D));
            datasourceCollection.add(new CostoReportLine("campaña3", "campo1", "cult2", "tipo3", 15D));
            datasourceCollection.add(new CostoReportLine("campaña3", "campo1", "cult1", "tipo3", 50D));

            createCollectionDataSource(datasourceCollection);
        }
        return (datasourceCollection.size() > 0);
    }
}
