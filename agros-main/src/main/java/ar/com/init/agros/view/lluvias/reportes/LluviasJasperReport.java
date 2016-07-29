package ar.com.init.agros.view.lluvias.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
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

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.util.gui.Periodo;

/**
 * Clase LluviasJasperReport
 * 
 * 
 * @author fbobbio
 * @version 26-ago-2009
 */
public abstract class LluviasJasperReport extends AbstractJasperReport {

	public static final String REPORT_TITLE = "Reporte de Lluvias";
	private List<LluviasReportLine> datasourceCollection;
	private static final String CANTIDAD = "cantidad";
	private static final String UNIDAD = "unidad";
	private static final String PERIODO = "periodo";
	private static final String CAMPO = "campo";
	private DefaultCategoryDataset dataset;
	private List<Periodo> periodos;
        /** Controller para llamar al llenado de campañas */
        private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

	private List<List<PeriodLine>> getPeriodosCompletos() {
		periodos = getPeriodos();
		Collections.sort(periodos); // ordeno los períodos para acomodarlos en
									// el gráfico

		List<List<PeriodLine>> data = new ArrayList<List<PeriodLine>>(periodos
				.size());

		EntityManager em = EntityManagerUtil.createEntityManager();

		for (int i = 0; i < periodos.size(); i++) {
			Periodo p = periodos.get(i);
			List<PeriodLine> periodLines = new ArrayList<PeriodLine>();

			String hql = "SELECT ll.campo, MAX(ll.cantidad.valor) FROM " + Lluvia.class.getName() + " AS ll "
					+ " INNER JOIN ll.lotes l "
					+ " WHERE ll.status = :status"					
					+ " AND l IN (:lotes) "
					+ " AND ll.fecha BETWEEN :from AND :to "
					+ " GROUP BY ll.campo, ll.fecha"
					+ " ORDER BY ll.fecha ASC";

			List<Lote> superficies = getSuperficies();
			
			QueryImpl query = (QueryImpl) em.createQuery(hql);
			query.getHibernateQuery().setParameterList("lotes", superficies);
			query.setParameter("status", BaseEntityStateEnum.ACTIVE);
			query.setParameter("from", p.getFromDate());
			query.setParameter("to", p.getToDate());
			
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				String campo = (campoController.cargarCampaniasAsociadasACampo((Campo)o[0])).toString();
				Double cantidad = (Double) o[1];

				PeriodLine pl = new PeriodLine(campo, cantidad);
				
				if(!periodLines.contains(pl))
				{
					periodLines.add(pl);
				}else
				{
					int idx = periodLines.indexOf(pl);
					periodLines.get(idx).addCantidad(cantidad);					
				}
			}
			
			data.add(periodLines);
		}

		return data;
	}

	private class PeriodLine {
		String campo;
		double cantidad;

		public PeriodLine(String campo, double cantidad) {
			this.campo = campo;
			this.cantidad = cantidad;
		}

		public String getCampo() {
			return campo;
		}

		public void setCampo(String campo) {
			this.campo = campo;
		}

		public double getCantidad() {
			return cantidad;
		}

		public void setCantidad(double cantidad) {
			this.cantidad = cantidad;
		}

		public void addCantidad(double cantidad) {		
				this.cantidad += cantidad;			
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((campo == null) ? 0 : campo.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeriodLine other = (PeriodLine) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (campo == null) {
				if (other.campo != null)
					return false;
			} else if (!campo.equals(other.campo))
				return false;
			return true;
		}

		private LluviasJasperReport getOuterType() {
			return LluviasJasperReport.this;
		}
	}

	protected class LluviasReportLine extends HashMap<String, Object> {

		public static final long serialVersionUID = -1L;

		public LluviasReportLine(Periodo periodo, String campo,
				Double cantidadTot) {
			super();
			put(PERIODO, periodo.toShortString());
			put(CAMPO, campo);
			put(CANTIDAD, cantidadTot);
			put(UNIDAD, MagnitudEnum.LLUVIA_CAIDA.patron().toString());
		}
	}

	/** Constructor por defecto de LluviasJasperReport */
	public LluviasJasperReport() {
		super(REPORT_TITLE);
                campoController = new CampoJpaController();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean createDataSource() {

		List<List<PeriodLine>> data = getPeriodosCompletos();

		for (int i = 0; i < data.size(); i++) {
			for (PeriodLine l : data.get(i)) {
				datasourceCollection.add(new LluviasReportLine(periodos.get(i),
						l.getCampo(), l.getCantidad()));
				dataset.addValue(l.getCantidad(), l.getCampo(), periodos.get(i)
						.toShortString());
			}
		}
		createCollectionDataSource(datasourceCollection);

		return (datasourceCollection.size() > 0);
	}

	protected abstract List<Periodo> getPeriodos();

	protected abstract List<Lote> getSuperficies();

	@Override
	protected String getJasperDefinitionPath() {
		return "ar/com/init/agros/reporting/reports/Lluvias.jrxml";
	}

	@Override
	public JFreeChart createChart() {
		// create the chart...
		chart = ChartFactory.createBarChart(REPORT_TITLE, // chart title
				"Período", // range axis label
				"Lluvia [Milímetros]", // domain axis label
				createChartDataset(), // data
				PlotOrientation.VERTICAL, true, // include legend
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

		renderer
				.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		JFreeChartUtils.setUpRenderer(renderer);

		renderer
				.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator(
						"{0}"));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}

	private JFreeChart chart;

	private CategoryDataset createChartDataset() {

		dataset = new DefaultCategoryDataset();
		datasourceCollection = new ArrayList<LluviasReportLine>();

		createDataSource();

		return dataset;
	}
}
