package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.almacenamiento.ValorAlmacenamientoJpaController;
import ar.com.init.agros.controller.inventario.agroquimicos.DetalleMovimientoStockJpaController;
import ar.com.init.agros.controller.inventario.granos.DetalleMovimientoGranoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.almacenamiento.ValorGrano;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoDeposito;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.model.inventario.cereales.DetalleEgresoCereal;
import ar.com.init.agros.model.inventario.cereales.DetalleIngresoCereal;
import ar.com.init.agros.model.inventario.granos.DetalleOperacionGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleEgresoSemilla;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.ejb.HibernateQuery;
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
 * Clase StockAlmacenamientosJasperReport
 *
 *
 * @author gmatheu
 * @version 12/12/2010
 */
public abstract class StockAlmacenamientosJasperReport extends AbstractJasperReport {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String REPORT_TITLE = "Reporte de Stock de Almacenamientos";
    private List<StockAlmacenamientosReportLine> datasourceCollection;
    private static final String ALMACENAMIENTO = "almacenamiento";
    private static final String DETALLE = "detalle";
    private static final String CANTIDAD = "cantidad";
    private static final String EXISTENCIA = "existencia";
    private static final String EXISTENCIA_TOTAL = "existenciaTotal";
    private static final String FECHA = "fecha";
    private static final String UNIDAD = "unidad";
    private static final String INGRESO = "ingreso";
    private static final String EGRESO = "egreso";
    private static final String DESDE = "desde";
    private static final String HASTA = "hasta";
    private static final String AJUSTE = "ajuste";
    private static final String AGROQUIMICOS = "agroquimicos";
    private static final String SEMILLAS = "semillas";
    private static final String CEREALES = "cereales";
    private static final String INGRESOS_SEMILLAS = "ingresosSemillas";
    private static final String EGRESOS_SEMILLAS = "egresosSemillas";
    private static final String INGRESOS_CEREALES = "ingresosCereales";
    private static final String EGRESOS_CEREALES = "egresosCereales";

    private class StockAlmacenamientosReportLine extends ReportLine {

        public StockAlmacenamientosReportLine(Almacenamiento alm, List<StockAgroquimicosReportLine> agros, List<StockGranosReportLine> semis,
                List<StockGranosReportLine> cereales,
                List<OperacionGranosReportLine> ingresoSemis, List<OperacionGranosReportLine> egresoSemis,
                List<OperacionGranosReportLine> ingresoCereales, List<OperacionGranosReportLine> egresoCereales) {

            put(ALMACENAMIENTO, alm.toString());
            put(AGROQUIMICOS, agros);
            put(SEMILLAS, semis);
            put(CEREALES, cereales);
            put(INGRESOS_SEMILLAS, ingresoSemis);
            put(EGRESOS_SEMILLAS, egresoSemis);
            put(INGRESOS_CEREALES, ingresoCereales);
            put(EGRESOS_CEREALES, egresoCereales);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, ALMACENAMIENTO);
        }

        @Override
        public int hashCode() {
            return this.get(ALMACENAMIENTO).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, ALMACENAMIENTO);
        }
    }

    private class StockAgroquimicosReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public StockAgroquimicosReportLine(ValorAgroquimico vd) {
            super();
            put(DETALLE,
                    vd.getAgroquimico().toString() + " (" + vd.getStockActual().getUnidad().getAbreviatura() + ")");
            put(EXISTENCIA, vd.getStockActual().getValor());
            put(UNIDAD, vd.getStockActual().getUnidad().getAbreviatura());
        }

        public StockAgroquimicosReportLine(String agroquimico, Double ingreso, Double egreso, Double cantidad, Double desde, Double hasta, Double ajuste, String unidad) {
            super();
            put(DETALLE, agroquimico);
            put(EXISTENCIA, cantidad);
            put(UNIDAD, unidad);
            put(INGRESO, ingreso);
            put(EGRESO, egreso);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, DETALLE);
        }

        @Override
        public int hashCode() {
            return this.get(DETALLE).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, DETALLE);
        }
    }

    private class StockGranosReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public StockGranosReportLine(ValorGrano vg, Double existenciaTotal) {
            super();
            put(DETALLE, vg.getDetalle().toString());
            put(EXISTENCIA, vg.getStockActual().getValor());
            put(EXISTENCIA_TOTAL, existenciaTotal);
            put(UNIDAD, vg.getStockActual().getUnidad().getAbreviatura());
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, DETALLE);
        }

        @Override
        public int hashCode() {
            return this.get(DETALLE).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, DETALLE);
        }
    }

    private class OperacionGranosReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public OperacionGranosReportLine(DetalleOperacionGrano dog) {
            super();
            put(DETALLE, dog.getDetalle().toString());
            put(CANTIDAD, dog.getCantidad().getValor());
            put(UNIDAD, dog.getCantidad().getUnidad().getAbreviatura());
            put(FECHA, DATE_FORMAT.format(dog.getFecha()));
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, FECHA);
        }

        @Override
        public int hashCode() {
            return this.get(DETALLE).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, DETALLE);
        }
    }

    /** Constructor por defecto de StockAgroquimicosJasperReport */
    public StockAlmacenamientosJasperReport() {
        super(REPORT_TITLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<StockAlmacenamientosReportLine>();

            for (Almacenamiento almacenamiento : getAlmacenamientos()) {

                List<StockAgroquimicosReportLine> agros = createStockAgroquimicosReportLines(almacenamiento);
                List<StockGranosReportLine> semis = createStockSemillasReportLine(almacenamiento);
                List<StockGranosReportLine> cereales = createStockCerealesReportLine(almacenamiento);
                List<OperacionGranosReportLine> ingresoSemis = createOperacionSemillaReportLine(almacenamiento, DetalleIngresoSemilla.class);
                List<OperacionGranosReportLine> egresoSemis = createOperacionSemillaReportLine(almacenamiento, DetalleEgresoSemilla.class);
                List<OperacionGranosReportLine> ingresoCereales = createOperacionCerealReportLine(almacenamiento, DetalleIngresoCereal.class);
                List<OperacionGranosReportLine> egresoCereales = createOperacionCerealReportLine(almacenamiento, DetalleEgresoCereal.class);

                if (!(agros.isEmpty() && semis.isEmpty() && cereales.isEmpty())) {
                    StockAlmacenamientosReportLine rl = new StockAlmacenamientosReportLine(almacenamiento, agros, semis,
                            cereales, ingresoSemis, egresoSemis,
                            ingresoCereales, egresoCereales);
                    datasourceCollection.add(rl);
                }
            }

//            String subtitle = ReportUtils.armarSubtituloDepositos(getDepositos());
//            setReportSubTitle(subtitle);

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private List<StockAgroquimicosReportLine> createStockAgroquimicosReportLines(Almacenamiento almacenamiento) {

        List<StockAgroquimicosReportLine> r = new ArrayList<StockAgroquimicosReportLine>();
        if (almacenamiento instanceof Deposito) {
            Deposito depo = (Deposito) almacenamiento;

            EntityManager em = EntityManagerUtil.createEntityManager();
            List<Agroquimico> agroquimicos = getAgroquimicos();

            if (!agroquimicos.isEmpty()) {
                ValorAlmacenamientoJpaController almacenamientoController = new ValorAlmacenamientoJpaController(ValorAgroquimico.class);
                List<ValorAgroquimico> valoresAgroquimicos = almacenamientoController.find(depo, agroquimicos.toArray(new Agroquimico[0]));
                DetalleMovimientoStockJpaController detalleController = new DetalleMovimientoStockJpaController(DetalleMovimientoDeposito.class);

                String desdeHQL =
                        " SELECT SUM(dms.cantidad.valor) " + " FROM " + DetalleMovimientoDeposito.class.getName() + " as dms "
                        + " WHERE valorDepositoOrigen = :valorDeposito";

                HibernateQuery desdeQuery = (HibernateQuery) em.createQuery(desdeHQL);

                for (ValorAgroquimico vd : valoresAgroquimicos) {

                    List<DetalleMovimientoStock> movimientos = detalleController.find(vd);
                    if (movimientos == null) {
                        continue;
                    }

                    StockAgroquimicosReportLine sarl = new StockAgroquimicosReportLine(vd);
                    r.add(sarl);

                    Double ingreso = 0D;
                    Double egreso = 0D;
                    Double desde = 0D;
                    Double hasta = 0D;
                    Double ajuste = 0D;
                    for (DetalleMovimientoStock d : movimientos) {
                        final TipoMovimientoStock tipo = d.getTipo();

                        if (d.isCancelado() == null || !d.isCancelado()) {
                            Double cantidad = d.getCantidad().getValor(d.getDetalle().getUnidad());
                            if (tipo.equals(TipoMovimientoStockEnum.INGRESO.tipo())) {
                                ingreso += cantidad;
                            } else if (tipo.equals(TipoMovimientoStockEnum.OTRO.tipo())) {
                                egreso += cantidad;
                            } else if (tipo.equals(TipoMovimientoStockEnum.AJUSTE.tipo())) {
                                ajuste += cantidad;
                            } else if (tipo.equals(TipoMovimientoStockEnum.MOVIMIENTO_DEPOSITO.tipo())) {
                                hasta += cantidad;
                            }
                        }
                    }

                    try {
                        desdeQuery.setParameter("valorDeposito", vd);
                        desde = (Double) desdeQuery.getSingleResult();
                    } catch (NoResultException nre) {
                        desde = 0D;
                    }
                    if (desde == null) {
                        desde = 0D;
                    }

                    sarl.put(INGRESO, ingreso);
                    sarl.put(EGRESO, egreso);
                    sarl.put(DESDE, desde);
                    sarl.put(HASTA, hasta);
                    sarl.put(AJUSTE, ajuste);
                }
            }

            em.close();
        }

        return r;
    }

    private List<StockGranosReportLine> createStockSemillasReportLine(Almacenamiento almacenamiento) {

        List<VariedadCultivo> variedades = getSemillas();
        List<StockGranosReportLine> r = new ArrayList<StockGranosReportLine>();

        if (!variedades.isEmpty()) {
            ValorAlmacenamientoJpaController controller = new ValorAlmacenamientoJpaController(ValorSemilla.class);
            List<ValorSemilla> valoresSemillas = controller.find(almacenamiento, variedades.toArray(new VariedadCultivo[0]));

            for (ValorSemilla vs : valoresSemillas) {

                ValorUnidad vu = controller.calcularExistenciaTotal(vs.getDetalle());

                r.add(new StockGranosReportLine(vs, vu.getValor()));
            }
        }
        return r;
    }

    private List<StockGranosReportLine> createStockCerealesReportLine(Almacenamiento almacenamiento) {

        List<StockGranosReportLine> r = new ArrayList<StockGranosReportLine>();

        if (almacenamiento instanceof Silo) {
            Silo silo = (Silo) almacenamiento;
            List<Cultivo> cultivos = getCereales();

            if (!cultivos.isEmpty()) {
                ValorAlmacenamientoJpaController controller = new ValorAlmacenamientoJpaController(ValorSemilla.class);
                List<ValorCereal> valoresCereales = controller.find(silo, cultivos.toArray(new Cultivo[0]));

                for (ValorCereal vc : valoresCereales) {

                    ValorUnidad vu = controller.calcularExistenciaTotal(vc.getDetalle());

                    r.add(new StockGranosReportLine(vc, vu.getValor()));
                }
            }
        }

        return r;
    }

    private List<OperacionGranosReportLine> createOperacionSemillaReportLine(Almacenamiento almacenamiento, Class<? extends DetalleOperacionGrano> aClass) {
        List<OperacionGranosReportLine> r = new ArrayList<OperacionGranosReportLine>();
        List<VariedadCultivo> variedades = getSemillas();

        if (!variedades.isEmpty()) {
            DetalleMovimientoGranoJpaController controller = new DetalleMovimientoGranoJpaController();
            List<? extends DetalleOperacionGrano> movs = controller.findDetalleSemillaByAlmacenamiento(almacenamiento, variedades, aClass);

            for (DetalleOperacionGrano mov : movs) {
                r.add(new OperacionGranosReportLine(mov));
            }
        }

        return r;
    }

    private List<OperacionGranosReportLine> createOperacionCerealReportLine(Almacenamiento almacenamiento, Class<? extends DetalleOperacionGrano> aClass) {
        List<OperacionGranosReportLine> r = new ArrayList<OperacionGranosReportLine>();
        List<Cultivo> cultivos = getCereales();

        if (!cultivos.isEmpty()) {
            DetalleMovimientoGranoJpaController controller = new DetalleMovimientoGranoJpaController();
            List<? extends DetalleOperacionGrano> movs = controller.findDetalleCerealByAlmacenamiento(almacenamiento, cultivos, aClass);

            for (DetalleOperacionGrano mov : movs) {
                r.add(new OperacionGranosReportLine(mov));
            }
        }

        return r;
    }

    protected abstract List<Almacenamiento> getAlmacenamientos();

    protected abstract List<Agroquimico> getAgroquimicos();

    protected abstract List<Cultivo> getCereales();

    protected abstract List<VariedadCultivo> getSemillas();

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/StockAlmacenamientos.jrxml";
    }

    @Override
    public JFreeChart createChart() {
        // create the chart...
        chart = ChartFactory.createBarChart(
                REPORT_TITLE, // chart title
                "", // domain axis label
                "Existencia", // range axis label
                createChartDataset(), // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        JFreeChartUtils.setSubtitles(chart, getReportSubTitle().split(";"));

        CategoryPlot plot =
                (CategoryPlot) chart.getPlot();
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
    private JFreeChart chart;

    private CategoryDataset createChartDataset() {
        createDataSource();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        //Configuración del gráfico que se va a mostrar en la ventana
        for (StockAlmacenamientosReportLine almRL : datasourceCollection) {
            String alm = (String) almRL.get(ALMACENAMIENTO);
            List<StockAgroquimicosReportLine> agros = (List<StockAgroquimicosReportLine>) almRL.get(AGROQUIMICOS);
            List<StockGranosReportLine> semillas = (List<StockGranosReportLine>) almRL.get(SEMILLAS);
            List<StockGranosReportLine> cereales = (List<StockGranosReportLine>) almRL.get(CEREALES);

            List<ReportLine> aux = new ArrayList<ReportLine>();
            aux.addAll(agros);
            aux.addAll(semillas);
            aux.addAll(cereales);

            for (ReportLine rl : aux) {
                dataset.addValue(Double.parseDouble(rl.get(EXISTENCIA).toString()), alm,
                        (String) rl.get(DETALLE));
            }
        }

        return dataset;
    }

    public static void main(String[] args) {
        StockAlmacenamientosReportFrame frame = new StockAlmacenamientosReportFrame();
        frame.setVisible(true);
    }
}
