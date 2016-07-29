package ar.com.init.agros.view.campanias.reportes;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
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
 * Clase CostosDeCampaniaJasperReport
 * 
 * 
 * @author fbobbio
 * @version 18-sep-2009
 */
public abstract class CostosDeCampaniaJasperReport extends AbstractJasperReport {

    private static final String COSTO_DE_AGROQUIMICOS = "Costo de Agroquímicos";
    private List<CostosDeCampaniaReportLine> datasourceCollection;
    public static final String REPORT_TITLE = "Reporte de Costos de Campaña Agrícola";
    private static final String CAMPANIA = "campania";
    private static final String COSTO = "costo";
    private static final String TIPO_COSTO = "tipoCosto";
    private HashMap<Campania, Double> superficiesSembradas;
    private HashMap<Campania, Double> superficiesPulverizadas;
    private HashMap<Campania, Double> quintales;

    /** Constructor por defecto de CostosDeCampaniaJasperReport */
    public CostosDeCampaniaJasperReport() {
        super(REPORT_TITLE);
    }

    private void insertDataToCollection(String campania, String tipoCosto,
            Double costo) {
        boolean update = false;
        for (CostosDeCampaniaReportLine rl : datasourceCollection) {
            if (rl.size() > 0 && rl.get(CAMPANIA).equals(campania) && rl.get(TIPO_COSTO).equals(tipoCosto)) {
                update = true;
                rl.updateCosto(costo);
            }
        }
        if (!update) {
            CostosDeCampaniaReportLine rl = new CostosDeCampaniaReportLine(
                    campania, tipoCosto, costo);
            datasourceCollection.add(rl);
        }
    }

    protected class CostosDeCampaniaReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public CostosDeCampaniaReportLine(String campania, String tipoCosto,
                Double costo) {
            super();
            put(CAMPANIA, campania);
            put(TIPO_COSTO, tipoCosto);
            put(COSTO, costo);
        }

        public void updateCosto(Double costo) {
            this.put(COSTO, (Double) this.get(COSTO) + costo);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, CAMPANIA, TIPO_COSTO);
        }

        @Override
        public boolean equals(Object o) {
            return keyEquals(o, CAMPANIA, TIPO_COSTO);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            return this.get(CAMPANIA).hashCode();
        }
    }

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/CostosDeCampania.jrxml";
    }

    public abstract List<Campania> getCampanias();

    public abstract List<TipoCosto> getTipoCostos();

    @Override
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<CostosDeCampaniaReportLine>();

            String subtitle = "";

            subtitle += ReportUtils.armarSubtituloCampania(getCampanias());

            subtitle +=
                    ";Recordar que este reporte muestra los costos de campaña, sin discriminarlos por establecimiento.";
            setReportSubTitle(subtitle, ";");

            /**
             * Se hacen 5 querys: 1 para costos de agroquímicos, otra para
             * costos de trabajo, otra para costos de siembra, costos
             * PostCosecha y otra para costos de campaña
             */
            EntityManager em = EntityManagerUtil.createEntityManager();
            calulateAuxValues(em);

            List<TipoCosto> tiposCostos = getTipoCostos();

            if (tiposCostos.contains(TipoCosto.TIPO_COSTO_AGROQUIMICO)) {
                calculateAgroquimicos(tiposCostos, em);
            }

            if (tiposCostos.size() > 0) {
                calculateSiembras(em, tiposCostos);
                calculatePulverizacion(em, tiposCostos);
                calculatePostCosecha(em, tiposCostos);
                calculateCampania(em, tiposCostos);
            }

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private void calulateAuxValues(EntityManager em) throws HibernateException {
        superficiesPulverizadas = new HashMap<Campania, Double>();
        QueryImpl hibernateQuerySuperficiePulv =
                (QueryImpl) em.createNamedQuery(Campania.SUPERFICIE_TRABAJO_QUERY_NAME);
        hibernateQuerySuperficiePulv.getHibernateQuery().setParameterList("campanias", getCampanias());
        List<Object[]> supPulv = hibernateQuerySuperficiePulv.getResultList();
        for (Object[] objects : supPulv) {
            superficiesPulverizadas.put((Campania) objects[0], (Double) objects[1]);
        }

        superficiesSembradas = new HashMap<Campania, Double>();
        QueryImpl hibernateQuerySuperficieSemb =
                (QueryImpl) em.createNamedQuery(Campania.SUPERFICIE_SIEMBRA_QUERY_NAME);
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList("campanias", getCampanias());
        List<Object[]> supSemb = hibernateQuerySuperficieSemb.getResultList();
        for (Object[] objects : supSemb) {
            superficiesSembradas.put((Campania) objects[0], (Double) objects[1]);
        }

        quintales = new HashMap<Campania, Double>();
        QueryImpl hibernateQQ =
                (QueryImpl) em.createNamedQuery(Campania.QUINTALES_QUERY_NAME);
        hibernateQQ.getHibernateQuery().setParameterList("campanias", getCampanias());
        List<Object[]> qq = hibernateQQ.getResultList();
        for (Object[] objects : qq) {
            quintales.put((Campania) objects[0], (Double) objects[1]);
        }
    }

    private void calculateAgroquimicos(List<TipoCosto> tiposCostos,
            EntityManager em) throws HibernateException {
        tiposCostos.remove(TipoCosto.TIPO_COSTO_AGROQUIMICO);
        // COSTO DE AGROQUIMICOS
        String queryCostosAgroquimicos =
                "SELECT t.campania, dt "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt "
                + " WHERE t.campania IN (:campanias) "
                + " GROUP BY t.campania, dt"
                + " ORDER BY t.campania";
        QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
        hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                "campanias", getCampanias());
        List<Object[]> costos = hibernateQueryAgroquim.getResultList();
        List<Object[]> costosSumarizados = sumarizarTotales(costos);
        for (Object[] o : costosSumarizados) {
            Campania campania = (Campania) o[0];
            Double monto = (Double) o[1];
            Double superficie = superficiesPulverizadas.get(campania);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), COSTO_DE_AGROQUIMICOS,
                    costo);
        }
    }

    private List<Object[]> sumarizarTotales(List<Object[]> pulverizados) {
        SortedMap<Campania, Double> sumarizador = new TreeMap<Campania, Double>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];           
            DetalleTrabajo dt = (DetalleTrabajo) o[1];
            Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();

            if (!sumarizador.containsKey(c)) {
                sumarizador.put(c, fumigado);
            } else {
                sumarizador.put(c, sumarizador.get(c) + fumigado);
            }

        }
        List<Object[]> sumarizado = new ArrayList<Object[]>();
        for (Entry<Campania, Double> e : sumarizador.entrySet()) {
            sumarizado.add(new Object[]{e.getKey(), e.getValue()});

        }
        return sumarizado;
    }

    private void calculateCampania(EntityManager em, List<TipoCosto> tiposCostos) {
        // COSTOS CAMPAÑA
        List<Object[]> costosCampania = new ArrayList<Object[]>();
        List<Object[]> dolar = calculateCostosCampaniaDolar(em, tiposCostos);
        costosCampania.addAll(dolar);
        List<Object[]> dolarHa = calculateCostosCampaniaDolarHa(em, tiposCostos);
        costosCampania.addAll(dolarHa);
        List<Object[]> dolarQQ = calculateCostosCampaniaDolarQQ(em, tiposCostos);
        costosCampania.addAll(dolarQQ);
        List<Object[]> dolarTn = calculateCostosCampaniaDolarTn(em, tiposCostos);
        costosCampania.addAll(dolarTn);
        for (Object[] o : costosCampania) {
            String campania = (String) o[0];
            TipoCosto tipoCosto = (TipoCosto) o[1];
            Double costo = (Double) o[2];
            insertDataToCollection(campania, tipoCosto.getNombre(), costo);
        }
    }

    private List<Object[]> calculateCostosCampaniaDolar(EntityManager em,
            List<TipoCosto> tiposCostos) {
        // DOLAR (C)
        String queryDolar =
                "SELECT c, cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Campania.class.getName() + " AS c "
                + " INNER JOIN c.costos cos "
                + " WHERE c IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NOT NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad IS NULL "
                + " GROUP BY c, cos.tipoCosto"
                + " ORDER BY c, cos.tipoCosto";
        QueryImpl hibernateQueryDolar = (QueryImpl) em.createQuery(queryDolar);
        hibernateQueryDolar.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolar.getHibernateQuery().setParameterList("tiposCosto",
                tiposCostos);
        List<Object[]> dolar = hibernateQueryDolar.getResultList();
        for (Object[] objs : dolar) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
            Double costosSum = (Double) objs[2];
            Double supCamp = superficiesSembradas.get(camp);
            Double costo = costosSum / supCamp;
            objs[2] = costo;
        }
        return dolar;
    }

    private List<Object[]> calculateCostosCampaniaDolarHa(EntityManager em,
            List<TipoCosto> tiposCostos) {
        // DOLAR/HA (C)
        String queryDolarHa =
                "SELECT c, cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Campania.class.getName() + " AS c "
                + " INNER JOIN c.costos cos "
                + " WHERE c IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY c, cos.tipoCosto"
                + " ORDER BY c, cos.tipoCosto";
        QueryImpl hibernateQueryDolarHa = (QueryImpl) em.createQuery(queryDolarHa);
        hibernateQueryDolarHa.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarHa.setParameter("unTipoCosto", UnidadMedida.getDolarPorHa());
        hibernateQueryDolarHa.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> dolarHa = hibernateQueryDolarHa.getResultList();
        for (Object[] objs : dolarHa) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
        }

        return dolarHa;
    }

    private List<Object[]> calculateCostosCampaniaDolarTn(EntityManager em,
            List<TipoCosto> tiposCostos) {
        // DOLAR POR TONELADA (C)
        String queryDolarTn =
                "SELECT c, cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Campania.class.getName() + " AS c "
                + " INNER JOIN c.costos cos "
                + " WHERE c IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY c, cos.tipoCosto"
                + " ORDER BY c, cos.tipoCosto";
        QueryImpl hibernateQueryDolarTn = (QueryImpl) em.createQuery(queryDolarTn);
        hibernateQueryDolarTn.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarTn.setParameter("unTipoCosto", UnidadMedida.getDolarPorTonelada());
        hibernateQueryDolarTn.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> dolarTn = hibernateQueryDolarTn.getResultList();
        for (Object[] objs : dolarTn) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
            Double costosSum = ((Double) objs[2]);
            Double qqCamp = quintales.get(camp);
            Double supCamp = superficiesSembradas.get(camp);
            Double costo = (costosSum / 10) * qqCamp / supCamp;
            objs[2] = costo;
        }
        return dolarTn;
    }

    private List<Object[]> calculateCostosCampaniaDolarQQ(EntityManager em,
            List<TipoCosto> tiposCostos) {
        // DOLAR POR QUINTAL (C)
        String queryDolarQQ =
                "SELECT c, cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Campania.class.getName() + " AS c "
                + " INNER JOIN c.costos cos "
                + " WHERE c IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY c, cos.tipoCosto"
                + " ORDER BY c, cos.tipoCosto";
        QueryImpl hibernateQueryDolarQQ = (QueryImpl) em.createQuery(queryDolarQQ);
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarQQ.setParameter("unTipoCosto", UnidadMedida.getDolarPorQuintal());
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> dolarQQ = hibernateQueryDolarQQ.getResultList();
        for (Object[] objs : dolarQQ) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
            Double costosSum = (Double) objs[2];
            Double qqCamp = quintales.get(camp);
            Double supCamp = superficiesSembradas.get(camp);
            Double costo = costosSum * qqCamp / supCamp;
            objs[2] = costo;
        }
        return dolarQQ;
    }

    private void calculateSiembras(EntityManager em, List<TipoCosto> tiposCostos)
            throws HibernateException {
        // COSTOS SIEMBRAS
        List<Object[]> costosSiembras = new ArrayList<Object[]>();

        String propCosto = "costos";
        List<Object[]> dolar = calculateSiembraDolar(em, tiposCostos, propCosto);
        costosSiembras.addAll(dolar);
        List<Object[]> dolarHa = calculateSiembraDolarHa(em, tiposCostos,
                propCosto);
        costosSiembras.addAll(dolarHa);
        List<Object[]> dolarQQ = calculateSiembraDolarQQ(em, tiposCostos,
                propCosto);
        costosSiembras.addAll(dolarQQ);
        List<Object[]> dolarTn = calculateSiembraDolarTn(em, tiposCostos,
                propCosto);
        costosSiembras.addAll(dolarTn);
        for (Object[] o : costosSiembras) {
            String campania = (String) o[0];
            TipoCosto tipoCosto = (TipoCosto) o[1];
            Double costo = (Double) o[2];
            insertDataToCollection(campania, tipoCosto.getNombre(), costo);
        }
    }

    private void calculatePostCosecha(EntityManager em,
            List<TipoCosto> tiposCostos) throws HibernateException {
        // COSTOS POST COSECHA
        List<Object[]> costosPostCosecha = new ArrayList<Object[]>();

        String propCosto = "costosPostCosecha";
        List<Object[]> dolar = calculateSiembraDolar(em, tiposCostos, propCosto);
        costosPostCosecha.addAll(dolar);
        List<Object[]> dolarHa = calculateSiembraDolarHa(em, tiposCostos,
                propCosto);
        costosPostCosecha.addAll(dolarHa);
        List<Object[]> dolarQQ = calculateSiembraDolarQQ(em, tiposCostos,
                propCosto);
        costosPostCosecha.addAll(dolarQQ);
        List<Object[]> dolarTn = calculateSiembraDolarTn(em, tiposCostos,
                propCosto);
        costosPostCosecha.addAll(dolarTn);
        for (Object[] o : costosPostCosecha) {
            String campania = (String) o[0];
            TipoCosto tipoCosto = (TipoCosto) o[1];
            Double costo = (Double) o[2];
            insertDataToCollection(campania, tipoCosto.getNombre(), costo);
        }
    }

    private List<Object[]> calculateSiembraDolar(EntityManager em,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        // DOLAR (D)
        String queryCostosSiembra =
                "SELECT s.campania, cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " WHERE s.campania IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NOT NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad IS NULL "
                + " GROUP BY s.campania, cos.tipoCosto"
                + " ORDER BY s.campania, cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            o[0] = campania.getNombre();
            Double monto = (Double) o[2];
            Double superficie = superficiesSembradas.get(campania);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            o[2] = costo;
        }

        return costosSiembra;
    }

    private List<Object[]> calculateSiembraDolarHa(EntityManager em,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        // DOLAR POR HECTAREA (C)
        String queryCostosSiembra =
                "SELECT s.campania, cos.tipoCosto, SUM(cos.importe.monto * sup.superficie.valor) "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " WHERE s.campania IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY s.campania, cos.tipoCosto"
                + " ORDER BY s.campania, cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorHa());
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            o[0] = campania.getNombre();
            Double monto = (Double) o[2];
            Double superficie = superficiesSembradas.get(campania);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            o[2] = costo;
        }

        return costosSiembra;
    }

    private List<Object[]> calculateSiembraDolarQQ(EntityManager em,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        // DOLAR POR QUINTAL (D)
        String queryDolarQQ =
                "SELECT s.campania, cos.tipoCosto, SUM(cos.importe.monto * rend.rinde.valor) "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s.rendimientoSuperficies rend "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " WHERE s.campania IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY s.campania, cos.tipoCosto"
                + " ORDER BY s.campania, cos.tipoCosto";
        QueryImpl hibernateQueryDolarQQ = (QueryImpl) em.createQuery(queryDolarQQ);
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarQQ.setParameter("unTipoCosto", UnidadMedida.getDolarPorQuintal());
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> dolarQQ = hibernateQueryDolarQQ.getResultList();
        for (Object[] objs : dolarQQ) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
            Double rend = (Double) objs[2];
            Double superficie = superficiesSembradas.get(camp);
            Double costo = rend / superficie;
            objs[2] = costo;
        }
        return dolarQQ;
    }

    private List<Object[]> calculateSiembraDolarTn(EntityManager em,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        // DOLAR POR TONELADA
        String queryDolarTn =
                "SELECT s.campania, cos.tipoCosto, SUM(cos.importe.monto * rend.rinde.valor) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s.rendimientoSuperficies rend " + " INNER JOIN s." + propiedadCosto + " cos " + " WHERE s.campania IN (:campanias) " + " AND cos.tipoCosto IN (:tiposCosto) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s.campania, cos.tipoCosto" + " ORDER BY s.campania, cos.tipoCosto";
        QueryImpl hibernateQueryDolarTn = (QueryImpl) em.createQuery(queryDolarTn);
        hibernateQueryDolarTn.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarTn.setParameter("unTipoCosto", UnidadMedida.getDolarPorTonelada());
        hibernateQueryDolarTn.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> dolarTn = hibernateQueryDolarTn.getResultList();
        for (Object[] objs : dolarTn) {
            Campania camp = (Campania) objs[0];
            objs[0] = camp.getNombre();
            Double rend = ((Double) objs[2]) / 10;
            Double superficie = superficiesSembradas.get(camp);
            Double costo = rend / superficie;
            objs[2] = costo;
        }
        return dolarTn;
    }

    private void calculatePulverizacion(EntityManager em,
            List<TipoCosto> tiposCostos) throws HibernateException {
        // COSTOS DE PULVERIZACION
        String queryCostosTrabajo =
                "SELECT t.campania, cos.tipoCosto, SUM(cos.importe.monto * t.superficieSeleccionada.valor) "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.costos cos "
                + " WHERE t.campania IN (:campanias) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " GROUP BY t.campania, cos.tipoCosto "
                + " ORDER BY t.campania, cos.tipoCosto ";
        QueryImpl hibernateQueryTrabajo = (QueryImpl) em.createQuery(queryCostosTrabajo);
        hibernateQueryTrabajo.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryTrabajo.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> costosTrabajo = hibernateQueryTrabajo.getResultList();
        for (Object[] o : costosTrabajo) {
            Campania campania = (Campania) o[0];
            TipoCosto tipoCosto = (TipoCosto) o[1];
            Double monto = (Double) o[2];
            Double superficie = superficiesPulverizadas.get(campania);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), tipoCosto.getNombre(),
                    costo);
        }
    }
    private JFreeChart chart;

    @Override
    public JFreeChart createChart() {
        // create the chart...
        chart = ChartFactory.createBarChart(REPORT_TITLE, // chart title
                "", // domain axis label
                "Costo [U$S/ha]", // range axis label
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

        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        JFreeChartUtils.setUpRenderer(renderer);

        renderer.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator(
                "{0}"));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(
                Math.PI / 6.0));

        return chart;
    }

    private CategoryDataset createChartDataset() {
        createDataSource();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Configuración del gráfico que se va a mostrar en la ventana
        for (CostosDeCampaniaReportLine rl : datasourceCollection) {
            dataset.addValue(Double.parseDouble(rl.get(COSTO).toString()),
                    (String) rl.get(TIPO_COSTO), (String) rl.get(CAMPANIA));
        }

        return dataset;
    }
}
