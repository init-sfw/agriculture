package ar.com.init.agros.view.siembras.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.EntityManager;
import org.hibernate.ejb.QueryImpl;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase SiembrasJasperReport
 *
 *
 * @author fbobbio
 * @version 09-sep-2009 
 */
public abstract class SiembrasJasperReport extends AbstractJasperReport
{

    public static final String REPORT_TITLE = "Reporte Plan de Siembra";
    private List<SiembraReportLine> datasourceCollection;
    private static final String CAMPANIA = "campania";
    private static final String PORCENTAJE = "porcentaje";
    private static final String CAMPO = "campo";
    private static final String CULTIVO = "cultivo";
    private static final String TIPOCOSTO = "tipoCosto";
    private static final String COSTO = "costo";
    private static final String TOTAL = "total";
    public static final String HEADER_PORCENTAJE_VALUE = "%";
    private static final String HEADER_COSTO_VALUE = "Porcentaje";
    private static final String HEADER_FILTRO = "headerFiltro";
    private static final String HEADER_PORCENTAJE = "headerPorcentaje";
    private static final String HEADER_COSTO = "headerCosto";
    private Set<String> cultivos;
    private Set<String> tiposCosto;
    /** Controller para llamar al llenado de campañas */
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    private void fillSets()
    {
        cultivos = new HashSet<String>();
        tiposCosto = new HashSet<String>();
        for (SiembraReportLine rl : datasourceCollection) {
            cultivos.add(rl.get(CULTIVO).toString());
            tiposCosto.add(rl.get(TIPOCOSTO).toString());
        }
    }

    private class SiembraReportLine extends ReportLine
    {

        public static final long serialVersionUID = -1L;

        public SiembraReportLine(String campania, String campo, String cultivo, String tipoCosto, Double costo)
        {
            super();
            put(CAMPANIA, campania);
            put(CAMPO, campo);
            put(CULTIVO, cultivo);
            put(TIPOCOSTO, tipoCosto);
            put(COSTO, costo);
        }

        private void addTotal(Double total)
        {
            Double acum = (Double) get(TOTAL);
            if (acum == null) {
                acum = new Double(0);
            }
            acum += total;

            put(TOTAL, acum);
        }

        private void calcularPorcentaje()
        {
            Double total = (Double) get(TOTAL);
            Double costo = (Double) get(COSTO);
            Double pct = (costo / total) * 100;

            put(PORCENTAJE, pct);
        }

        @Override
        public int compareTo(ReportLine o)
        {
            return keyCompareTo(o, CAMPANIA, CAMPO, CULTIVO, TIPOCOSTO);
        }

        @Override
        public boolean equals(Object o)
        {
            return keyEquals(o, CAMPANIA, CAMPO, CULTIVO, TIPOCOSTO);
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            return this.get(CAMPANIA).hashCode();
        }
    }

    /** Constructor por defecto de SiembrasJasperReport */
    public SiembrasJasperReport()
    {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    protected abstract List<Campo> getCampos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<TipoCosto> getTiposDeCosto();

    @Override
    public JFreeChart createChart()
    {
        ValueAxis rangeAxis = new NumberAxis(HEADER_COSTO_VALUE);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);
        rangeAxis.setLowerBound(0);
        rangeAxis.setAutoRange(true);

        Iterator<Entry<String, CategoryDataset>> it = createChartDataset().entrySet().iterator();
        DecimalFormat labelFormatter = new DecimalFormat("###,000");

        while (it.hasNext()) {
            Entry<String, CategoryDataset> entry = it.next();

            CategoryAxis domainAxis = new CategoryAxis(entry.getKey());
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
            StackedBarRenderer renderer = new StackedBarRenderer();
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2} %", labelFormatter));
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

    private Map<String, CategoryDataset> createChartDataset()
    {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (SiembraReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            //TODO: ver por qué razón llegan los porcentajes con null acá
            Double value = Double.parseDouble(rl.get(PORCENTAJE).toString());

            dataset.addValue(value, (String) rl.get(TIPOCOSTO),
                    (String) rl.get(CULTIVO) + " - " + (String) rl.get(CAMPO));
        }

        return r;
    }

    private boolean isByCampo()
    {
        return getCampos().size() > 0;
    }

    private LegendItemCollection createLegendItems()
    {
        LegendItemCollection result = new LegendItemCollection();
        for (String tipoCosto : tiposCosto) {
            LegendItem item1 = new LegendItem(tipoCosto, tipoCosto, tipoCosto, tipoCosto,
                    new Rectangle(10, 10), new GradientPaint(0.0f, 0.0f, new Color(16, 89, 172), 0.0f, 0.0f,
                    new Color(201, 201, 244)));
            result.add(item1);
        }

        return result;
    }

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/Siembras.jrxml";
    }

    @Override
    protected boolean createDataSource()
    {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<SiembraReportLine>();

            String subtitle = "";

            String filtroColumn = "";

            params.put(HEADER_PORCENTAJE, HEADER_PORCENTAJE_VALUE);
            params.put(HEADER_COSTO, HEADER_COSTO_VALUE);
            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            if (isByCampo()) {
                filtroColumn = "AND s.campo IN (:campos)";

                params.put(HEADER_FILTRO, "Establecimiento");
                subtitle += " ; " + ReportUtils.armarSubtituloEstablecimiento(getCampos());
            }

            setReportSubTitle(subtitle, ";");

            EntityManager em = EntityManagerUtil.createEntityManager();

            String queryCostos = "SELECT s.campania, s.campo, s.cultivo, cos.tipoCosto, SUM(cos.importe.monto * sup.superficie.valor)" +
                    " FROM " + Siembra.class.getName() + " AS s " +
                    " INNER JOIN s.costos cos " +
                    " INNER JOIN s.superficies sup " +
                    " WHERE s.campania IN (:campanias) " +
                    " AND cos.tipoCosto IN (:tiposCosto) " +
                    filtroColumn +
                    " GROUP BY s.campania, s.campo, s.cultivo, cos.tipoCosto" +
                    " ORDER BY s.campania, s.campo, s.cultivo, cos.tipoCosto";

            QueryImpl hibernateQueryImportes = (QueryImpl) em.createQuery(queryCostos);
            hibernateQueryImportes.getHibernateQuery().setParameterList("campanias", getCampanias());
            hibernateQueryImportes.getHibernateQuery().setParameterList("tiposCosto", getTiposDeCosto());
            if (isByCampo()) {
                hibernateQueryImportes.getHibernateQuery().setParameterList("campos", getCampos());
            }

            List<Object[]> costos = hibernateQueryImportes.getResultList();


            for (Object[] o : costos) {
                Campania campania = (Campania) o[0];
                Campo campo = campoController.cargarCampaniasAsociadasACampo((Campo) o[1]);
                Cultivo cultivo = (Cultivo) o[2];
                TipoCosto tipoCosto = (TipoCosto) o[3];
                Double costo = (Double) o[4];

                SiembraReportLine rl = new SiembraReportLine(
                        campania.getNombre(),
                        campo.toString(),
                        cultivo.getNombre(),
                        tipoCosto.getNombre(),
                        costo);

                datasourceCollection.add(rl);
            }

            String queryTotales = "SELECT s.campania, s.campo, s.cultivo, SUM(cos.importe.monto * sup.superficie.valor)" +
                    " FROM " + Siembra.class.getName() + " AS s " +
                    " INNER JOIN s.costos cos " +
                    " INNER JOIN s.superficies sup " +
                    " WHERE s.campania IN (:campanias) " +
                    filtroColumn +
                    " GROUP BY s.campania, s.campo, s.cultivo" +
                    " ORDER BY s.campania, s.campo, s.cultivo";

            QueryImpl hibernateQueryTotales = (QueryImpl) em.createQuery(queryTotales);
            hibernateQueryTotales.getHibernateQuery().setParameterList("campanias", getCampanias());
            if (isByCampo()) {
                hibernateQueryTotales.getHibernateQuery().setParameterList("campos", getCampos());
            }

            List<Object[]> totales = hibernateQueryTotales.getResultList();

            for (SiembraReportLine rl : datasourceCollection) {
                for (Object[] o : totales) {
                    Campania campania = (Campania) o[0];
                    Campo campo = campoController.cargarCampaniasAsociadasACampo((Campo) o[1]);
                    Cultivo cultivo = (Cultivo) o[2];

                    if (rl.get(CAMPANIA).equals(campania.getNombre()) && rl.get(CAMPO).equals(
                            campo.toString()) && rl.get(CULTIVO).equals(cultivo.getNombre())) {
                        rl.addTotal((Double) o[3]);
                        rl.calcularPorcentaje();

                        break;
                    }
                }
            }

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }
        return (datasourceCollection.size() > 0);
    }
}
