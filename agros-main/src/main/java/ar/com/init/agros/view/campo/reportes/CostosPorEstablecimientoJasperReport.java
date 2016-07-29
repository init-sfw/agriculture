package ar.com.init.agros.view.campo.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.JFreeChartUtils;
import ar.com.init.agros.reporting.ReportUtils;
import ar.com.init.agros.reporting.jasper.AbstractJasperReport;
import ar.com.init.agros.view.reporting.helper.SiembrasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.AbstractSuperficiesHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCampoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasCultivoHelper;
import ar.com.init.agros.view.reporting.helper.SuperficiesPulverizadasVariedadCultivoHelper;
import java.awt.Color;
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
 * Clase CostosPorEstablecimientoJasperReport
 * 
 * 
 * @author fbobbio
 * @version 09-oct-2009
 */
public abstract class CostosPorEstablecimientoJasperReport extends AbstractJasperReport {

    private List<CostosPorEstablecimientoReportLine> datasourceCollection;
    public static final String REPORT_TITLE = "Reporte de Costos por Establecimiento/Cultivo";
    private static final String CAMPANIA = "campania";
    private static final String COSTO = "costo";
    private static final String TIPO_COSTO = "tipoCosto";
    private static final String FILTRO_HEADER = "headerFiltro";
    private static final String FILTRO = "filtro";
    public static final String HEADER_COSTO_VALUE = "Costo [U$S/ha]";
    private Map<Campania, Map<Object, Double>> superficiesSembradas;
    private SiembrasCultivoHelper siembrasHelper;
    private AbstractSuperficiesHelper superficiesPulvHelper;
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    /** Constructor por defecto de CostosDeCampaniaJasperReport */
    public CostosPorEstablecimientoJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    private void insertDataToCollection(String campania, String filtro,
            String tipoCosto, Double costo) {
        boolean update = false;
        for (CostosPorEstablecimientoReportLine rl : datasourceCollection) {
            if (rl.size() > 0 && rl.get(CAMPANIA).equals(campania) && rl.get(FILTRO).equals(filtro) && rl.get(
                    TIPO_COSTO).equals(tipoCosto)) {
                update = true;
                rl.updateCosto(costo);
            }
        }
        if (!update) {
            CostosPorEstablecimientoReportLine rl = new CostosPorEstablecimientoReportLine(
                    campania, filtro, tipoCosto, costo);
            datasourceCollection.add(rl);
        }
    }

    protected class CostosPorEstablecimientoReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public CostosPorEstablecimientoReportLine(String campania,
                String filtro, String tipoCosto, Double costo) {
            super();
            put(CAMPANIA, campania);
            put(FILTRO, filtro);
            put(TIPO_COSTO, tipoCosto);
            put(COSTO, costo);
        }

        public void updateCosto(Double costo) {
            Double aux = (Double) this.get(COSTO);
            aux = new Double(aux + costo);
            this.put(COSTO, aux);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, CAMPANIA, FILTRO, TIPO_COSTO);
        }

        @Override
        public int hashCode() {
            return this.get(CAMPANIA).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, CAMPANIA, FILTRO, TIPO_COSTO);
        }
    }

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/CostosPorEstablecimiento.jrxml";
    }

    public abstract List<Campania> getCampanias();

    public abstract List<Campo> getCampos();

    public abstract List<TipoCosto> getTipoCostos();

    public abstract List<Cultivo> getCultivos();

    public abstract List<VariedadCultivo> getVariedades();

    private boolean isByCampo() {
        return getCampos().size() > 0;
    }

    private boolean isByCultivo() {
        return getCultivos().size() > 0 && getVariedades().isEmpty();
    }

    private boolean isByVariedad() {
        return getVariedades().size() > 0;
    }

    @Override
    protected boolean createDataSource() {
        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<CostosPorEstablecimientoReportLine>();

            String subtitle = ReportUtils.armarSubtituloCampania(getCampanias());

            String filtroSelect = "";
            boolean includeSiembra = false;
            List filtroParameters = null;
            if (isByCampo()) {
                subtitle += ";" + ReportUtils.armarSubtituloEstablecimiento(getCampos());
                params.put(FILTRO_HEADER, "Establecimiento");
                filtroSelect = "t.campo";
                filtroParameters = getCampos();
            } else if (isByCultivo()) {
                includeSiembra = true;
                subtitle += ";" + ReportUtils.armarSubtituloCultivo(getCultivos());
                params.put(FILTRO_HEADER, "Cultivo");
                filtroSelect = "s.cultivo";
                filtroParameters = getCultivos();
            } else {
            }
            if (isByVariedad()) {
                includeSiembra = true;
                subtitle += ";" + ReportUtils.armarSubtituloVariedadesCultivo(
                        getCultivos(), getVariedades());
                params.put(FILTRO_HEADER, "Variedad Cultivo");
                filtroSelect = "s.variedadCultivo";
                filtroParameters = getVariedades();
            }

            setReportSubTitle(subtitle, ";");

            /**
             * Se hacen 4 querys: 1 para costos de agroquímicos, otra para
             * costos de trabajo y otra para costos de siembra y postCosecha
             */
            EntityManager em = EntityManagerUtil.createEntityManager();

            calculateAuxValues(em, filtroParameters);

            List<TipoCosto> tiposCostos = getTipoCostos();

            if (getTipoCostos().contains(TipoCosto.TIPO_COSTO_AGROQUIMICO)) {
                calculateAgroquimicos(tiposCostos, filtroSelect,
                        includeSiembra, em, filtroParameters);
            }
            if (tiposCostos.size() > 0) {
                calculateSiembras(filtroSelect, em, filtroParameters,
                        tiposCostos);
                calculatePulverizacion(filtroSelect, includeSiembra, em,
                        filtroParameters, tiposCostos);
                calculatePostCosecha(filtroSelect, em, filtroParameters,
                        tiposCostos);
            }

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private void calculateAuxValues(EntityManager em, List filtroParameters) {

        superficiesSembradas = new HashMap<Campania, Map<Object, Double>>();

        for (Campania camp : getCampanias()) {
            Map<Object, Double> supSembCamp = new HashMap<Object, Double>();

            superficiesSembradas.put(camp, supSembCamp);
            for (Object object : filtroParameters) {
                supSembCamp.put(object, 0D);
            }
        }

        if (isByCultivo() || isByVariedad()) {
            siembrasHelper = new SiembrasCultivoHelper(em, getCampanias(), getCultivos());
        }

        if (isByCampo()) {
            superficiesPulvHelper = new SuperficiesPulverizadasCampoHelper(em, getCampanias(), getCampos());
        } else if (isByCultivo()) {
            superficiesPulvHelper = new SuperficiesPulverizadasCultivoHelper(em, getCampanias(),
                    getCultivos(), siembrasHelper);
        } else if (isByVariedad()) {
            superficiesPulvHelper = new SuperficiesPulverizadasVariedadCultivoHelper(em, getCampanias(),
                    getVariedades(), siembrasHelper);
        }

        String namedQuery =
                (isByCampo() ? Campania.SUPERFICIE_SIEMBRA_CAMPO_QUERY_NAME
                : (isByCultivo() ? Campania.SUPERFICIE_SIEMBRA_CULTIVO_QUERY_NAME : Campania.SUPERFICIE_SIEMBRA_VAR_CULTIVO_QUERY_NAME));

        QueryImpl hibernateQuerySuperficieSemb = (QueryImpl) em.createNamedQuery(namedQuery);
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList(
                "campanias", getCampanias());
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList(
                "filtros", filtroParameters);
        List<Object[]> supSemb = hibernateQuerySuperficieSemb.getResultList();
        for (Object[] objects : supSemb) {
            superficiesSembradas.get((Campania) objects[0]).put(objects[1], (Double) objects[2]);
        }
    }

    private void calculateAgroquimicos(List<TipoCosto> tiposCostos,
            String filtroSelect, boolean includeSiembra, EntityManager em,
            List filtroParameters) throws HibernateException {
        tiposCostos.remove(TipoCosto.TIPO_COSTO_AGROQUIMICO);
        // COSTO DE AGROQUIMICOS
        if (isByCampo()) {
            String queryCostosAgroquimicos =
                    "SELECT t.campania, " + filtroSelect + ", dt"
                    + " FROM " + Trabajo.class.getName() + " AS t "
                    + " INNER JOIN t.detalles dt "
                    + " WHERE t.campania IN (:campanias) "
                    + " AND " + filtroSelect + " IN (:filtros) "
                    + " GROUP BY t.campania, " + filtroSelect + ", dt"
                    + " ORDER BY t.campania, " + filtroSelect;
            QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
            hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                    "campanias", getCampanias());
            hibernateQueryAgroquim.getHibernateQuery().setParameterList("filtros",
                    filtroParameters);
            List<Object[]> costos = hibernateQueryAgroquim.getResultList();

            List<Object[]> sumarizados = sumarizarCostoAgroquimicos(costos);
            for (Object[] o : sumarizados) {
                Campania campania = (Campania) o[0];
                Campo campo = campoController.cargarCampaniasAsociadasACampo((Campo) o[1]);
                Double monto = (Double) o[2];
                Double superficie = superficiesPulvHelper.getSuperficie(campania, campo);
                if (superficie == 0) {
                    continue;
                }
                Double costo = monto / superficie;
                insertDataToCollection(campania.getNombre(), campo.toString(),
                        TipoCosto.TIPO_COSTO_AGROQUIMICO.getNombre(), costo);
            }
        } else if (isByCultivo() || isByVariedad()) {
            String queryCostosAgroquimicos =
                    "SELECT t.campania, t, dt"
                    + " FROM " + Trabajo.class.getName() + " AS t "
                    + " INNER JOIN t.detalles dt "
                    + " WHERE t.campania IN (:campanias) "
                    + " GROUP BY t.campania, t, dt "
                    + " ORDER BY t.campania, t, dt ";
            QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
            hibernateQueryAgroquim.getHibernateQuery().setParameterList(
                    "campanias", getCampanias());

            List<Object[]> costos = hibernateQueryAgroquim.getResultList();
            List<Object[]> sumarizados = sumarizarCostoAgroquimicos(costos, siembrasHelper, isByVariedad());

            for (Iterator<Object[]> it = sumarizados.iterator(); it.hasNext();) {
                Object[] o = it.next();

                Campania campania = (Campania) o[0];
                Object filtro =  o[1];    
                Double monto = (Double) o[2];
                
                Double superficie = superficiesPulvHelper.getSuperficie(campania, filtro);
                if (superficie == 0) {
                    continue;
                }
                Double costo = monto / superficie;
                insertDataToCollection(campania.getNombre(), filtro.toString(),
                        TipoCosto.TIPO_COSTO_AGROQUIMICO.getNombre(), costo);
            }
        }
    }

    private List<Object[]> sumarizarCostoAgroquimicos(List<Object[]> pulverizados) {
        return sumarizarCostoAgroquimicos(pulverizados, null, false);
    }

    private List<Object[]> sumarizarCostoAgroquimicos(List<Object[]> pulverizados, SiembrasCultivoHelper siembrasHelper, boolean useVariedadCultivo) {
        SortedMap<Campania, SortedMap<Object, Double>> sumarizador = new TreeMap<Campania, SortedMap<Object, Double>>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];
            Object f = o[1];
            DetalleTrabajo dt = (DetalleTrabajo) o[2];
            Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();

            if (siembrasHelper != null && f instanceof Trabajo) {

                if (useVariedadCultivo) {
                    f = siembrasHelper.findVariedadCultivo((Trabajo) f, c);
                } else {
                    f = siembrasHelper.findCultivo((Trabajo) f, c);
                }
            }

            if (!sumarizador.containsKey(c)) {
                sumarizador.put(c, new TreeMap<Object, Double>());
            }

            if (f != null) {
                if (!sumarizador.get(c).containsKey(f)) {
                    sumarizador.get(c).put(f, fumigado);
                } else {
                    sumarizador.get(c).put(f, fumigado + sumarizador.get(c).get(f));
                }
            }
        }
        List<Object[]> sumarizado = new ArrayList<Object[]>();
        for (Campania c : sumarizador.keySet()) {
            for (Entry<Object, Double> e : sumarizador.get(c).entrySet()) {
                sumarizado.add(new Object[]{c, e.getKey(), e.getValue()});
            }
        }
        return sumarizado;
    }

    private void calculatePulverizacion(String filtroSelect,
            boolean includeSiembra, EntityManager em, List filtroParameters,
            List<TipoCosto> tiposCostos) throws HibernateException {
        // COSTOS DE PULVERIZACION
        if (isByCampo()) {
            String queryCostosTrabajo =
                    "SELECT t.campania, " + filtroSelect + ", cos.tipoCosto, SUM(cos.importe.monto * t.superficieSeleccionada.valor) "
                    + " FROM " + Trabajo.class.getName() + " AS t "
                    + " INNER JOIN t.costos cos "
                    + " WHERE t.campania IN (:campanias) "
                    + " AND " + filtroSelect + " IN (:filtros) "
                    + " AND cos.tipoCosto IN (:tiposCosto) "
                    + " GROUP BY t.campania, " + filtroSelect + ", cos.tipoCosto"
                    + " ORDER BY t.campania, " + filtroSelect + ", cos.tipoCosto";
            QueryImpl hibernateQueryTrabajo = (QueryImpl) em.createQuery(queryCostosTrabajo);
            hibernateQueryTrabajo.getHibernateQuery().setParameterList("campanias",
                    getCampanias());
            hibernateQueryTrabajo.getHibernateQuery().setParameterList("filtros",
                    filtroParameters);
            hibernateQueryTrabajo.getHibernateQuery().setParameterList(
                    "tiposCosto", tiposCostos);
            List<Object[]> costosTrabajo = hibernateQueryTrabajo.getResultList();
            for (Object[] o : costosTrabajo) {
                Campania campania = (Campania) o[0];
                Object filtro = o[1];
                if (filtro instanceof Campo) {
                    campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
                }
                TipoCosto tipoCosto = (TipoCosto) o[2];
                Double monto = (Double) o[3];
                Double superficie = superficiesPulvHelper.getSuperficie(campania, filtro);
                if (superficie == 0) {
                    continue;
                }
                Double costo = monto / superficie;
                insertDataToCollection(campania.getNombre(), filtro.toString(),
                        tipoCosto.getNombre(), costo);
            }
        } else if (isByCultivo() || isByVariedad()) {
            String queryCostosTrabajo =
                    "SELECT t.campania, t, cos.tipoCosto, SUM(cos.importe.monto * t.superficieSeleccionada.valor) "
                    + " FROM " + Trabajo.class.getName() + " AS t "
                    + " INNER JOIN t.costos cos "
                    + " WHERE t.campania IN (:campanias) "
                    + " AND cos.tipoCosto IN (:tiposCosto) "
                    + " GROUP BY t.campania, t, cos.tipoCosto"
                    + " ORDER BY t.campania, t, cos.tipoCosto";
            QueryImpl hibernateQueryTrabajo = (QueryImpl) em.createQuery(queryCostosTrabajo);
            hibernateQueryTrabajo.getHibernateQuery().setParameterList("campanias",
                    getCampanias());
            hibernateQueryTrabajo.getHibernateQuery().setParameterList(
                    "tiposCosto", tiposCostos);
            List<Object[]> costosTrabajo = hibernateQueryTrabajo.getResultList();

            for (Iterator<Object[]> it = costosTrabajo.iterator(); it.hasNext();) {
                Object[] o = it.next();

                Campania campania = (Campania) o[0];
                Trabajo trabajo = (Trabajo) o[1];
                Siembra siembra = siembra = siembrasHelper.findSiembra(trabajo.getSuperficies(), campania);
                if (siembra == null) {
                    it.remove();
                    continue;
                }

                Object filtro = null;
                if (isByCultivo()) {
                    Cultivo cultivo = siembra.getCultivo();
                    filtro = cultivo;
                } else if (isByVariedad()) {
                    VariedadCultivo variedad = siembra.getVariedadCultivo();
                    filtro = variedad;
                } else {
                    continue;
                }
                TipoCosto tipoCosto = (TipoCosto) o[2];
                Double monto = (Double) o[3];
                Double superficie = superficiesPulvHelper.getSuperficie(campania, filtro);
                if (superficie == 0) {
                    continue;
                }
                Double costo = monto / superficie;
                insertDataToCollection(campania.getNombre(), filtro.toString(),
                        tipoCosto.getNombre(), costo);
            }
        }
    }

    private void calculateSiembras(String filtroSelect, EntityManager em,
            List filtroParameters, List<TipoCosto> tiposCostos)
            throws HibernateException {
        // COSTOS DE SIEMBRAS
        String f = filtroSelect.replace("t.", "s.");
        String propiedadCosto = "costos";

        calculateSiembrasDolar(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarHa(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarQQ(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarTn(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
    }

    private void calculatePostCosecha(String filtroSelect, EntityManager em,
            List filtroParameters, List<TipoCosto> tiposCostos)
            throws HibernateException {
        String f = filtroSelect.replace("t.", "s.");
        String propiedadCosto = "costosPostCosecha";

        calculateSiembrasDolar(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarHa(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarQQ(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
        calculateSiembrasDolarTn(f, em, filtroParameters, tiposCostos,
                propiedadCosto);
    }

    private void calculateSiembrasDolar(String filtroSelect, EntityManager em,
            List filtroParameters, List<TipoCosto> tiposCostos,
            String propiedadCosto) throws HibernateException {
        String queryCostosSiembra =
                "SELECT s.campania, " + filtroSelect + ", cos.tipoCosto, SUM(cos.importe.monto) "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " WHERE s.campania IN (:campanias) "
                + " AND " + filtroSelect + " IN (:filtros) "
                + " AND cos.tipoCosto IN (:tiposCosto) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NOT NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad IS NULL "
                + " GROUP BY s.campania, " + filtroSelect + ", cos.tipoCosto"
                + " ORDER BY s.campania, " + filtroSelect + ", cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("filtros",
                filtroParameters);
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            Object filtro = o[1];
            if (filtro instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
            }
            TipoCosto tipoCosto = (TipoCosto) o[2];
            Double monto = (Double) o[3];
            Double superficie = superficiesSembradas.get(campania).get(filtro);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), filtro.toString(),
                    tipoCosto.getNombre(), costo);
        }
    }

    private void calculateSiembrasDolarHa(String filtroSelect,
            EntityManager em, List filtroParameters,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        String queryCostosSiembra =
                "SELECT s.campania, " + filtroSelect + ", cos.tipoCosto, SUM(cos.importe.monto * sup.superficie.valor) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s." + propiedadCosto + " cos " + " INNER JOIN s.superficies sup " + " WHERE s.campania IN (:campanias) " + " AND " + filtroSelect + " IN (:filtros) " + " AND cos.tipoCosto IN (:tiposCosto) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s.campania, " + filtroSelect + ", cos.tipoCosto" + " ORDER BY s.campania, " + filtroSelect + ", cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("filtros",
                filtroParameters);
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorHa());
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            Object filtro = o[1];
            if (filtro instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
            }
            TipoCosto tipoCosto = (TipoCosto) o[2];
            Double monto = (Double) o[3];
            Double superficie = superficiesSembradas.get(campania).get(filtro);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), filtro.toString(),
                    tipoCosto.getNombre(), costo);
        }
    }

    private void calculateSiembrasDolarQQ(String filtroSelect,
            EntityManager em, List filtroParameters,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        String queryCostosSiembra =
                "SELECT s.campania, " + filtroSelect + ", cos.tipoCosto, SUM(cos.importe.monto * rend.rinde.valor) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s." + propiedadCosto + " cos " + " INNER JOIN s.superficies sup " + " INNER JOIN s.rendimientoSuperficies rend " + " WHERE s.campania IN (:campanias) " + " AND " + filtroSelect + " IN (:filtros) " + " AND cos.tipoCosto IN (:tiposCosto) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s.campania, " + filtroSelect + ", cos.tipoCosto" + " ORDER BY s.campania, " + filtroSelect + ", cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("filtros",
                filtroParameters);
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorQuintal());
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            Object filtro = o[1];
            if (filtro instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
            }
            TipoCosto tipoCosto = (TipoCosto) o[2];
            Double monto = (Double) o[3];
            Double superficie = superficiesSembradas.get(campania).get(filtro);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), filtro.toString(),
                    tipoCosto.getNombre(), costo);
        }
    }

    private void calculateSiembrasDolarTn(String filtroSelect,
            EntityManager em, List filtroParameters,
            List<TipoCosto> tiposCostos, String propiedadCosto)
            throws HibernateException {
        String queryCostosSiembra =
                "SELECT s.campania, " + filtroSelect + ", cos.tipoCosto, SUM((cos.importe.monto / 10) * rend.rinde.valor) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s." + propiedadCosto + " cos " + " INNER JOIN s.superficies sup " + " INNER JOIN s.rendimientoSuperficies rend " + " WHERE s.campania IN (:campanias) " + " AND " + filtroSelect + " IN (:filtros) " + " AND cos.tipoCosto IN (:tiposCosto) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s.campania, " + filtroSelect + ", cos.tipoCosto" + " ORDER BY s.campania, " + filtroSelect + ", cos.tipoCosto";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("filtros",
                filtroParameters);
        hibernateQuerySiembra.getHibernateQuery().setParameterList(
                "tiposCosto", tiposCostos);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorTonelada());
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] o : costosSiembra) {
            Campania campania = (Campania) o[0];
            Object filtro = o[1];
            if (filtro instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) filtro);
            }
            TipoCosto tipoCosto = (TipoCosto) o[2];
            Double monto = (Double) o[3];
            Double superficie = superficiesSembradas.get(campania).get(filtro);
            if (superficie == 0) {
                continue;
            }
            Double costo = monto / superficie;
            insertDataToCollection(campania.getNombre(), filtro.toString(),
                    tipoCosto.getNombre(), costo);
        }
    }

    @Override
    public JFreeChart createChart() {
        ValueAxis rangeAxis = new NumberAxis(HEADER_COSTO_VALUE);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);
        rangeAxis.setUpperMargin(0.10); // leave some space for item labels

        Iterator<Entry<String, CategoryDataset>> it = createChartDataset().entrySet().iterator();
        DecimalFormat labelFormatter = new DecimalFormat("##,##0.00");

        while (it.hasNext()) {
            Entry<String, CategoryDataset> entry = it.next();

            CategoryAxis domainAxis = new CategoryAxis(entry.getKey());
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setMaximumCategoryLabelWidthRatio(5.0f);
            BarRenderer renderer = new BarRenderer();
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                    "{2}", labelFormatter));
            renderer.setDrawBarOutline(false);
            renderer.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator(
                    "{0}"));

            JFreeChartUtils.setUpRenderer(renderer);

            CategoryPlot subplot = new CategoryPlot(entry.getValue(),
                    domainAxis, null, renderer);
            subplot.setDomainGridlinesVisible(true);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(
                    Math.PI / 6.0));

            plot.add(subplot);
        }

        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(REPORT_TITLE, plot);

        JFreeChartUtils.setSubtitles(chart, getReportSubTitle().split(";"));

        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);
        rangeAxis.setLowerBound(0);

        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset() {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (CostosPorEstablecimientoReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double value = Double.parseDouble(rl.get(COSTO).toString());

            dataset.addValue(value, (String) rl.get(TIPO_COSTO), (String) rl.get(FILTRO));
        }

        return r;
    }
}
