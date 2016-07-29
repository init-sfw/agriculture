package ar.com.init.agros.view.lluvias;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.DialogTotalizer;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hibernate.ejb.QueryImpl;

/**
 * Clase LluviasTotalizer
 * 
 * 
 * @author fbobbio
 * @version 20-nov-2009
 */
public class LluviasTotalizer extends DialogTotalizer {

	public static final long serialVersionUID = -1L;
	private static final int CAMPO_COLUMN = 0;
	private static final int MES_ANIO_COLUMN = 1;
	private static final int VALOR_COLUMN = 2;

	/** Constructor por defecto de LluviasTotalizer */
	public LluviasTotalizer(Frame parent) {
		super(parent, "Cantidad Total de Lluvias por Establecimiento");
		title = "Cantidad Total de Lluvias por Establecimiento";
	}

	private class LluviasTotalizerTableModel extends DefaultTableModel {

		public static final long serialVersionUID = -1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 2) {
				return Double.class;
			}
			return Object.class;
		}
	}

	@Override
	protected TableModel createTableModel() {
		String[] headers = { "Establecimiento", "Mes - Año", "Lluvia (mm)" };
		LluviasTotalizerTableModel lluviasModel = new LluviasTotalizerTableModel();
		lluviasModel.setColumnIdentifiers(headers);
		return lluviasModel;
	}

	@Override
	protected void findData() {
		EntityManager em = EntityManagerUtil.createEntityManager();

		String hql = "SELECT ll.campo, ll.fecha, MAX(ll.cantidad.valor) "
				+ "FROM " + Lluvia.class.getName() + " AS ll "
				+ " INNER JOIN ll.lotes l " + " WHERE ll.status = :status"
				+ " GROUP BY ll.campo, ll.fecha" + " ORDER BY ll.fecha ASC";

		QueryImpl hibernateQuery = (QueryImpl) em.createQuery(hql);
		hibernateQuery.setParameter("status", BaseEntityStateEnum.ACTIVE);

		List<Object[]> result = hibernateQuery.getResultList();
		List<LluviaSummarization> sum = new ArrayList<LluviaSummarization>();

		for (Object[] objects : result) {
			String campo = objects[0].toString();
			Date fecha = (Date) objects[1];
			Double cant = (Double) objects[2];

			Calendar cal = Calendar.getInstance();
			cal.setTime(fecha);
			
			LluviaSummarization ll = new LluviaSummarization(campo,cal.get(Calendar.MONTH) + 1 ,cal.get(Calendar.YEAR), cant);
			
			if(!sum.contains(ll))
			{
				sum.add(ll);
			}else
			{
				int idx = sum.indexOf(ll);
				sum.get(idx).addCantidad(cant);					
			}
		}

		LluviasTotalizerTableModel lluviasModel = (LluviasTotalizerTableModel) model;
		lluviasModel.setRowCount(sum.size());
		for (int i = 0; i < sum.size(); i++) {
			LluviaSummarization ll = sum.get(i);
			lluviasModel.setValueAt(ll.getCampo(), i, CAMPO_COLUMN);
			lluviasModel.setValueAt(GUIUtility.convertMonth(ll.getMes()) + " " + ll.getAnio(), i, MES_ANIO_COLUMN);
			lluviasModel.setValueAt(new ValorUnidad(ll.getCantidad(), MagnitudEnum.LLUVIA_CAIDA.patron()), i, VALOR_COLUMN);
		}			

		em.close();
	}

	private class LluviaSummarization {
		String campo;
		int mes;
		int anio;
		double cantidad;

		public LluviaSummarization(String campo, int mes, int anio, Double cantidad) {
			super();
			this.campo = campo;
			this.mes = mes;
			this.anio = anio;
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

		public int getMes() {
			return mes;
		}

		public int getAnio() {
			return anio;
		}

		public void addCantidad(double cantidad) {
			this.cantidad += cantidad;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + anio;
			result = prime * result + ((campo == null) ? 0 : campo.hashCode());
			result = prime * result + mes;
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
			LluviaSummarization other = (LluviaSummarization) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (anio != other.anio)
				return false;
			if (campo == null) {
				if (other.campo != null)
					return false;
			} else if (!campo.equals(other.campo))
				return false;
			if (mes != other.mes)
				return false;
			return true;
		}

		private LluviasTotalizer getOuterType() {
			return LluviasTotalizer.this;
		}	
	}

	@Override
	public boolean isNowVisible() {
		return this.isVisible();
	}
}
