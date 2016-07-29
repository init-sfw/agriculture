package ar.com.init.agros.view.campo.reportes;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Bonificacion;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.DetalleTrabajo;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.model.terreno.SubLote;
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
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Clase CostosPorEstablecimientoJasperReport
 * 
 * 
 * @author fbobbio
 * @version 09-oct-2009
 */
public abstract class MargenBrutoEstablecimientoJasperReport extends AbstractJasperReport {

    public static final String COSTOS_DE_PULVERIZACIONES = "Costos de Pulverizaciones";
    public static final String COSTOS_DE_SIEMBRA = "Costo de Plan de Siembras";
    public static final String COSTO_DE_AGROQUIMICOS = "Costo de Agroquímicos";
    public static final String BONIFICACIONES = "Bonificaciones";
    public static final String INGRESOS_EXTRAS = "Ingresos Extras";
    public static final String LABORES_Y_COSTOS = "Labores y Costos";
    public static final String RENDIMIENTOS = "Rendimientos";
    public static final String COSTO_DE_PULVERIZACION = "Costo de Pulverización";
    public static final String COSTO_DE_SIEMBRAS = "Costo de Plan de Siembras";
    public static final String COSTO_DE_COMERCIALIZACION = "Costo de Comercialización";
    public static final String COSTO_DE_CAMPANIAS = "Costo de Campañas";
    public static final String RENDIMIENTO = "Rendimiento";
    public static final String REPORT_TITLE = "Reporte Margen Bruto de Campaña";
    private static final String CAMPANIA = "campania";
    private static final String CULTIVO = "cultivo";
    private static final String TIPO = "tipo";
    private static final String ESTABLECIMIENTO = "establecimiento";
    private static final String EGRESO = "egreso";
    private static final String INGRESO = "ingreso";
    private static final String HEADER_MARGEN = "Margen [U$S/ha]";
    private static final String HEADER_INGRESOS = "Ingresos";
    private static final String HEADER_EGRESOS = "Egresos";
    private List<MargenBrutoEstablecimientoReportLine> datasourceCollection;
    private HashMap<Campania, Double> superficiesCampania;
    private HashMap<Siembra, Double> superficiesSiembras;
    private HashMap<Campania, Double> quintalesCampania;
    private HashMap<Siembra, Double> proporcionSiembras;
    private HashMap<Siembra, Double> proporcionQuintalesSiembras;
    private Map<Campania, Map<Campo, Map<Cultivo, Double>>> superficiesSembradas;
    private List<Siembra> siembras;
    private CampoJpaController campoController; //XXX: arreglar esta forma de cargar las campañas asociadas

    /** Constructor por defecto de CostosDeCampaniaJasperReport */
    public MargenBrutoEstablecimientoJasperReport() {
        super(REPORT_TITLE);
        campoController = new CampoJpaController();
    }

    private class MargenBrutoEstablecimientoReportLine extends ReportLine {

        public static final long serialVersionUID = -1L;

        public MargenBrutoEstablecimientoReportLine(String campania,
                String establecimiento, String cultivo, String tipo,
                Double ingreso, Double egreso) {
            super();
            put(CAMPANIA, campania);
            put(ESTABLECIMIENTO, establecimiento);
            put(CULTIVO, cultivo);
            put(TIPO, tipo);
            put(INGRESO, (ingreso == null ? 0D : ingreso));
            put(EGRESO, (egreso == null ? 0D : egreso));
        }

        public void addIngreso(Double ingreso) {
            Double i = (Double) this.get(INGRESO);

            if (i == null) {
                i = 0D;
            }

            i += ingreso;

            this.put(INGRESO, i);
        }

        public void addEgreso(Double egreso) {
            Double e = (Double) this.get(EGRESO);

            if (e == null) {
                e = 0D;
            }

            e += egreso;

            this.put(EGRESO, e);
        }

        @Override
        public int compareTo(ReportLine o) {
            return keyCompareTo(o, CAMPANIA, ESTABLECIMIENTO, CULTIVO, TIPO);
        }

        @Override
        public int hashCode() {
            return this.get(CAMPANIA).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return keyEquals(obj, CAMPANIA, ESTABLECIMIENTO, CULTIVO, TIPO);
        }
    }

    @Override
    protected String getJasperDefinitionPath() {
        return "ar/com/init/agros/reporting/reports/MargenBrutoPorEstablecimiento.jrxml";
    }

    public abstract List<Campania> getCampanias();

    public abstract List<Campo> getCampos();

    public abstract List<Cultivo> getCultivos();

    private boolean isByCultivo() {
        return getCultivos().size() > 0;

    }

    @Override
    protected boolean createDataSource() {
        params.put("headerMargen", HEADER_MARGEN);
        params.put("headerIngresos", HEADER_INGRESOS);
        params.put("headerEgresos", HEADER_EGRESOS);

        if (datasourceCollection == null) {
            datasourceCollection = new ArrayList<MargenBrutoEstablecimientoReportLine>();

            String subtitle = ReportUtils.armarSubtituloCampania(getCampanias()) + "; ";
            subtitle += ReportUtils.armarSubtituloEstablecimiento(getCampos()) + "; ";
            if (isByCultivo()) {
                subtitle += ReportUtils.armarSubtituloCultivo(getCultivos());
            }

            setReportSubTitle(subtitle, ";");

            // Formula:
            // Bonificaciones + Rindes + Ingresos Extras
            // -
            // Costos de Agroquimicos, Siembras, Pulverizaciones, PostCosecha y
            // Campañas
            // -------------------------
            // Margen Bruto

            EntityManager em = EntityManagerUtil.createEntityManager();

            calculateAuxiliarValues(em);

            // INGRESOS
            calculateRendimiento(em);
            calculateBonificaciones(em);
            calculateIngresosExtras(em);
            // EGRESOS
            calculateCostosAgroquimicos(em);
            calculateCostosSiembras(em);
            calculateCostosPulverizaciones(em);
            calculateCostosPostCosecha(em);
            calculateCostosCampania(em);

            em.close();

            Collections.sort(datasourceCollection);
            createCollectionDataSource(datasourceCollection);
        }

        return (datasourceCollection.size() > 0);
    }

    private void calculateAuxiliarValues(EntityManager em) {
        superficiesCampania = new HashMap<Campania, Double>();
        QueryImpl hibernateQuerySuperficie = (QueryImpl) em.createNamedQuery(
                Campania.SUPERFICIE_SIEMBRA_QUERY_NAME);
        hibernateQuerySuperficie.getHibernateQuery().setParameterList(
                "campanias", getCampanias());

        List<Object[]> sup = hibernateQuerySuperficie.getResultList();
        for (Object[] objects : sup) {
            superficiesCampania.put((Campania) objects[0], (Double) objects[1]);
        }

        quintalesCampania = new HashMap<Campania, Double>();
        QueryImpl hibernateQQ = (QueryImpl) em.createNamedQuery(Campania.QUINTALES_QUERY_NAME);
        hibernateQQ.getHibernateQuery().setParameterList("campanias",
                getCampanias());

        List<Object[]> qq = hibernateQQ.getResultList();
        for (Object[] objects : qq) {

            Double quintales = (Double) objects[1];
            if (quintales == null) {
                quintales = 0D;
            }

            quintalesCampania.put((Campania) objects[0], quintales);
        }

        superficiesSiembras = new HashMap<Siembra, Double>();
        proporcionSiembras = new HashMap<Siembra, Double>();
        QueryImpl hibernateQuerySuperficieSiembras = (QueryImpl) em.createNamedQuery(
                Siembra.SUPERFICIE_QUERY_NAME);
        hibernateQuerySuperficieSiembras.getHibernateQuery().setParameterList(
                "campanias", getCampanias());

        List<Campo> campos = getCampos();
        List<Object[]> supSiembras = hibernateQuerySuperficieSiembras.getResultList();
        for (Object[] objects : supSiembras) {
            Siembra s = (Siembra) objects[0];
            Double supSiembra = (Double) objects[1];
            Double supCampania = superficiesCampania.get(s.getCampania());
            Double prop = supSiembra / supCampania;

            if (campos.contains(s.getCampo())) {
                superficiesSiembras.put(s, supSiembra);
                proporcionSiembras.put(s, prop);
            }
        }

        proporcionQuintalesSiembras = new HashMap<Siembra, Double>();
        QueryImpl hibernateQueryQQSiembras = (QueryImpl) em.createNamedQuery(Siembra.QUINTALES_QUERY_NAME);
        hibernateQueryQQSiembras.getHibernateQuery().setParameterList(
                "campanias", getCampanias());

        List<Object[]> qqSiembras = hibernateQueryQQSiembras.getResultList();
        for (Object[] objects : qqSiembras) {
            Siembra s = (Siembra) objects[0];
            Double qqSiembra = (Double) objects[1];
            if (qqSiembra == null) {
                qqSiembra = new Double(0.0);
            }
            if (campos.contains(s.getCampo())) {
                Double qqCampania = quintalesCampania.get(s.getCampania());

                if (qqCampania > 0D) {
                    Double prop = qqSiembra / qqCampania;
                    proporcionQuintalesSiembras.put(s, prop);
                }
            }
        }

        QueryImpl hibernateQuerySiembras = (QueryImpl) em.createNamedQuery(
                Siembra.FIND_BY_CAMPANIA_CAMPO_CULTIVO_NAME);
        hibernateQuerySiembras.getHibernateQuery().setParameterList(
                "campanias", getCampanias());
        hibernateQuerySiembras.getHibernateQuery().setParameterList("cultivos",
                getCultivos());
        hibernateQuerySiembras.getHibernateQuery().setParameterList("campos",
                getCampos());

        siembras = hibernateQuerySiembras.getResultList();
        for (Iterator<Siembra> it = siembras.iterator(); it.hasNext();) {
            Siembra siembra = it.next();
            if (!campos.contains(siembra.getCampo())) {
                it.remove();
            }
        }

        superficiesSembradas = new HashMap<Campania, Map<Campo, Map<Cultivo, Double>>>();
        for (Campania camp : getCampanias()) {
            Map<Campo, Map<Cultivo, Double>> supCampoCamp = new HashMap<Campo, Map<Cultivo, Double>>();
            superficiesSembradas.put(camp, supCampoCamp);

            for (Campo campo : campos) {
                Map<Cultivo, Double> supCultCamp = new HashMap<Cultivo, Double>();
                supCampoCamp.put(campo, supCultCamp);

                for (Cultivo cult : getCultivos()) {
                    supCultCamp.put(cult, 0D);
                }
            }
        }

        QueryImpl hibernateQuerySuperficieSemb = (QueryImpl) em.createNamedQuery(
                Campania.SUPERFICIE_CAMPO_CULTIVO_QUERY_NAME);
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList("campanias", getCampanias());
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList("campos", getCampos());
        hibernateQuerySuperficieSemb.getHibernateQuery().setParameterList("cultivos", getCultivos());

        List<Object[]> supSemb = hibernateQuerySuperficieSemb.getResultList();

        for (Object[] o : supSemb) {
            superficiesSembradas.get((Campania) o[0]).get((Campo) o[1]).put((Cultivo) o[2], (Double) o[3]);
        }
    }

    /** Aca puede haber error, estoy tratando de asociar Siembra a Trabajos a partir
     *  de las superficies que tiene cada uno... Y en Trabajo puede darse que haya más de uno
     *  sobre la misma superficie, por lo que podría elegirse mal la siembra al hacer esta comparación.
     *
     *  PARA SOLUCIONARLO LE CLAVE QUE CONTROLE TAMBIÉN POR CAMPAÑA, veremos qué onda
     */
    private Siembra findSiembra(List<Superficie> superficies, Campania camp) {
        for (Siembra s : siembras) {
            if (s.getCampania().equals(camp)) {
                for (Superficie supPulv : superficies) {
                    if (s.getSuperficies().contains(supPulv)) {
                        return s;
                    }
                    if (supPulv instanceof SubLote) {
                        SubLote sl = (SubLote) supPulv;
                        if (s.getSuperficies().contains(sl.getPadre())) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void calculateCostosAgroquimicos(EntityManager em)
            throws HibernateException {

        //Tengo que traer todos los detalles de agroquímicos de cada campo, agrupado por campania
        String queryCostosAgroquimicos =
                "SELECT t.campania, t.campo, t, dt, 0 "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt "
                + " WHERE t.campania IN (:campanias) "
                + " GROUP BY t.campania, t.campo, t, dt"
                + " ORDER BY t.campania, t.campo, t";

        QueryImpl hibernateQueryAgroquim = (QueryImpl) em.createQuery(queryCostosAgroquimicos);
        hibernateQueryAgroquim.getHibernateQuery().setParameterList("campanias",
                getCampanias());

        List<Object[]> costos = hibernateQueryAgroquim.getResultList();

        //Traigo los campos seleccionados en el reporte
        List<Campo> campos = getCampos();
        //Cargo los nombres de esos campos
        List<String> nombresCampos = new ArrayList<String>();
        for (Campo campo : campos) {
            nombresCampos.add(campo.toString());
        }
        //Borro los costos que no correspondan a campos seleccionados //PENDING: esto no se puede hacer mediante SQL por algo? (quizás por algún group)
        for (Iterator<Object[]> it = costos.iterator(); it.hasNext();) {
            String nombreCampo = it.next()[1].toString();
            if (!nombresCampos.contains(nombreCampo)) {
                it.remove();
            }
        }

        /** Sumo todos los productos de U$$/ha por superficie pulverizada
         * agrupados según el array costos[] sin dividirlos y los guardo */
        List<Object[]> costosSumarizados = sumarizarCostoAgroquimicos(costos);

        /** Seteo dentro de cada costo sumarizado, el nombre de la campaña, el cultivo
         * y la superficie total sembrada por la cual se dividirá en el insertReportLine */
        for (Iterator<Object[]> it = costosSumarizados.iterator(); it.hasNext();) {
            Object[] objects = it.next();

            Campania camp = (Campania) objects[0];
            objects[0] = camp.getNombre();
            Campo campo = (Campo) objects[1];
            objects[1] = campoController.cargarCampaniasAsociadasACampo(campo).toString();
            Cultivo c = (Cultivo) objects[2];
            objects[2] = c.getNombre();
            objects[4] = superficiesSembradas.get(camp).get(campo).get(c);
        }

        insertReportLine(datasourceCollection, costosSumarizados, COSTO_DE_AGROQUIMICOS,
                false);
    }

    private List<Object[]> sumarizarCostoAgroquimicos(List<Object[]> pulverizados) {
        SortedMap<Campania, SortedMap<Campo, SortedMap<Cultivo, Double>>> sumarizador = new TreeMap<Campania, SortedMap<Campo, SortedMap<Cultivo, Double>>>();
        for (Object[] o : pulverizados) {
            Campania c = (Campania) o[0];
            Campo campo = (Campo) o[1];
            Trabajo f = (Trabajo) o[2];
            DetalleTrabajo dt = (DetalleTrabajo) o[3];
            Double fumigado = dt.calcularCostoPorHectarea().getMonto() * dt.getSuperficiePlanificada();
            Siembra siembra = findSiembra(f.getSuperficies(), c);
            Cultivo cultivo = null;
            if (siembra != null) {
                cultivo = siembra.getCultivo();
            }

            if (!sumarizador.containsKey(c)) {
                sumarizador.put(c, new TreeMap<Campo, SortedMap<Cultivo, Double>>());
            }

            SortedMap<Campo, SortedMap<Cultivo, Double>> campos = sumarizador.get(c);
            if (!campos.containsKey(campo)) {
                campos.put(campo, new TreeMap<Cultivo, Double>());
            }

            if (cultivo != null) {
                SortedMap<Cultivo, Double> cultivos = campos.get(campo);
                if (!cultivos.containsKey(cultivo)) {
                    cultivos.put(cultivo, fumigado);
                } else {
                    cultivos.put(cultivo, fumigado + cultivos.get(cultivo));
                }
            }
        }

        List<Object[]> sumarizado = new ArrayList<Object[]>();
        for (Campania c : sumarizador.keySet()) {
            for (Entry<Campo, SortedMap<Cultivo, Double>> campos : sumarizador.get(c).entrySet()) {

                for (Entry<Cultivo, Double> cultivos : campos.getValue().entrySet()) {

                    sumarizado.add(new Object[]{c, campos.getKey(), cultivos.getKey(), cultivos.getValue(), 0D});
                }
            }
        }
        return sumarizado;
    }

    private void calculateRendimiento(EntityManager em) {
        String queryCostosRindes =
                " SELECT s, s.campo, s.cultivo.nombre, SUM(rend.rinde.valor * rend.valorPorQuintal.monto * rend.superficie.superficie.valor), 0"
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s.rendimientoSuperficies rend "
                + " WHERE s.campania IN (:campanias) "
                + " AND s.campo IN (:campos) "
                + " AND s.cultivo IN (:cultivos) "
                + " GROUP BY s.campania, s, s.campo, s.cultivo"
                + " ORDER BY s.campania, s, s.campo, s.cultivo";

        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosRindes);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("campos",
                getCampos());
        hibernateQuerySiembra.getHibernateQuery().setParameterList("cultivos",
                getCultivos());

        List<Object[]> rindes = hibernateQuerySiembra.getResultList();
        for (Object[] objects : rindes) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }
        insertReportLine(datasourceCollection, rindes, RENDIMIENTO, true);
    }

    private void calculateBonificaciones(EntityManager em) {
        String subQuery =
                "SELECT s1" + " FROM " + Bonificacion.class.getName() + " AS b1 " + " INNER JOIN b1.agrupacionSiembras s1 " + " WHERE b1 IN (b) ";

        String query =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(b.importe.monto), 0 " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s.campania.bonificaciones b " + " INNER JOIN s.superficies sup " + " WHERE s.campania IN (:campanias) " + " AND s IN (" + subQuery + ") " + " AND s.campo IN (:campos) " + " AND s.cultivo IN (:cultivos) " + " GROUP BY s.campania, s.campo, s.cultivo, sup" + " ORDER BY s.campania, s.campo, s.cultivo, sup";

        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);
        hibernateQuery.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQuery.getHibernateQuery().setParameterList("campos",
                getCampos());
        hibernateQuery.getHibernateQuery().setParameterList("cultivos",
                getCultivos());

        List<Object[]> result = hibernateQuery.getResultList();
        for (Object[] objects : result) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }
        insertReportLine(datasourceCollection, result, BONIFICACIONES, true);
    }

    private void calculateIngresosExtras(EntityManager em) {
        calculateIngresosExtrasDolar(em);
        calculateIngresosExtrasDolarHa(em);
    }

    private void calculateIngresosExtrasDolar(EntityManager em) {
        // INGRESOS EN DOLARES
        String queryDolar =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(ing.importe.monto) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s.campania.ingresos ing " + " WHERE s IN (:siembras) " + " AND ing.tipoIngreso.dolarPorHectarea = false " + " GROUP BY s, s.campo, s.cultivo" + " ORDER BY s, s.campo, s.cultivo";

        QueryImpl hibernateQueryDolar = (QueryImpl) em.createQuery(queryDolar);
        hibernateQueryDolar.getHibernateQuery().setParameterList("siembras",
                siembras);

        List<Object[]> dolar = hibernateQueryDolar.getResultList();

        for (Object[] objs : dolar) {
            Siembra siembra = (Siembra) objs[0];
            objs[0] = siembra.getCampania().getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double ingRend = ((Double) objs[3]);
            Double supSiembra = superficiesSiembras.get(siembra);
            Double prop = proporcionSiembras.get(siembra);
            Double ingreso = (ingRend / supSiembra) * prop;
            objs[3] = ingreso;
        }

        insertReportLine(datasourceCollection, dolar, INGRESOS_EXTRAS, true);
    }

    private void calculateIngresosExtrasDolarHa(EntityManager em) {
        // INGRESOS EN DOLARES POR HA
        String queryDolarHa =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(ing.importe.monto) " + " FROM " + Siembra.class.getName() + " AS s " + " INNER JOIN s.rendimientoSuperficies rend " + " INNER JOIN s.campania.ingresos ing " + " WHERE s IN (:siembras) " + " AND ing.tipoIngreso.dolarPorHectarea = true " + " GROUP BY s, s.campo, s.cultivo" + " ORDER BY s, s.campo, s.cultivo";

        QueryImpl hibernateDolarHa = (QueryImpl) em.createQuery(queryDolarHa);
        hibernateDolarHa.getHibernateQuery().setParameterList("siembras",
                siembras);

        List<Object[]> dolarHa = hibernateDolarHa.getResultList();
        for (Object[] objs : dolarHa) {
            Siembra siembra = (Siembra) objs[0];
            Campania camp = (Campania) siembra.getCampania();
            objs[0] = camp.getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double ingRend = ((Double) objs[3]);
            Double propSiembra = proporcionSiembras.get(siembra);
            Double ingreso = ingRend * propSiembra;
            objs[3] = ingreso;
        }

        insertReportLine(datasourceCollection, dolarHa, INGRESOS_EXTRAS, true);
    }

    private void calculateCostosPulverizaciones(EntityManager em) {
        // DOLARES POR HA
        String queryCostosTrabajo =
                "SELECT t.campania, t.campo, t, SUM(cos.importe.monto * sup.superficie.valor), 0 "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.costos cos "
                + " INNER JOIN t.superficies sup "
                + " WHERE t.campania IN (:campanias) "
                + " GROUP BY t.campania, t.campo, t"
                + " ORDER BY t.campania, t.campo, t";

        QueryImpl hibernateQueryTrabajo = (QueryImpl) em.createQuery(queryCostosTrabajo);
        hibernateQueryTrabajo.getHibernateQuery().setParameterList("campanias",
                getCampanias());

        List<Object[]> costos = hibernateQueryTrabajo.getResultList();
        for (Iterator<Object[]> it = costos.iterator(); it.hasNext();) {
            Object[] objects = it.next();

            Campania camp = (Campania) objects[0];
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            Trabajo t = (Trabajo) objects[2];
            Siembra s = findSiembra(t.getSuperficies(), camp);
            if (s == null) {
                it.remove();
                continue;
            }
            objects[2] = s.getCultivo().getNombre();
            objects[4] = superficiesSembradas.get(camp).get(s.getCampo()).get(s.getCultivo());
        }

        insertReportLine(datasourceCollection, costos,
                COSTOS_DE_PULVERIZACIONES, false);
    }

    private void calculateCostosSiembras(EntityManager em) {
        String prop = "costos";
        calculateCostosSiembrasDolar(em, prop, COSTO_DE_SIEMBRAS);
        calculateCostosSiembrasDolarHa(em, prop, COSTO_DE_SIEMBRAS);
        calculateCostosSiembrasDolarQQ(em, prop, COSTO_DE_SIEMBRAS);
        calculateCostosSiembrasDolarTn(em, prop, COSTO_DE_SIEMBRAS);

    }

    private void calculateCostosPostCosecha(EntityManager em) {

        String prop = "costosPostCosecha";
        calculateCostosSiembrasDolar(em, prop, COSTO_DE_COMERCIALIZACION);
        calculateCostosSiembrasDolarHa(em, prop, COSTO_DE_COMERCIALIZACION);
        calculateCostosSiembrasDolarQQ(em, prop, COSTO_DE_COMERCIALIZACION);
        calculateCostosSiembrasDolarTn(em, prop, COSTO_DE_COMERCIALIZACION);
    }

    private void calculateCostosSiembrasDolar(EntityManager em,
            String propiedadCosto, String title) {
        String queryCostosSiembra =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto), 0 "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " WHERE s IN (:siembras) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NOT NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad IS NULL "
                + " GROUP BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre "
                + " ORDER BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("siembras",
                siembras);
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] objects : costosSiembra) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }

        insertReportLine(datasourceCollection, costosSiembra, title, false);
    }

    private void calculateCostosSiembrasDolarHa(EntityManager em,
            String propiedadCosto, String title) {
        String queryCostosSiembra =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto * sup.superficie.valor), 0 "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " WHERE s IN (:siembras) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre"
                + " ORDER BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("siembras",
                siembras);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorHa());
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] objects : costosSiembra) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }

        insertReportLine(datasourceCollection, costosSiembra, title, false);
    }

    private void calculateCostosSiembrasDolarQQ(EntityManager em,
            String propiedadCosto, String title) {
        String queryCostosSiembra =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto * rend.rinde.valor), 0 "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " INNER JOIN s.rendimientoSuperficies rend "
                + " WHERE s IN (:siembras) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre"
                + " ORDER BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("siembras",
                siembras);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorQuintal());

        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] objects : costosSiembra) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }
        insertReportLine(datasourceCollection, costosSiembra, title, false);
    }

    private void calculateCostosSiembrasDolarTn(EntityManager em,
            String propiedadCosto, String title) {
        String queryCostosSiembra =
                "SELECT s, s.campo, s.cultivo.nombre, SUM((cos.importe.monto / 10) * rend.rinde.valor), 0 "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " INNER JOIN s." + propiedadCosto + " cos "
                + " INNER JOIN s.superficies sup "
                + " INNER JOIN s.rendimientoSuperficies rend "
                + " WHERE s IN (:siembras) "
                + " AND cos.tipoCosto.unidadMedida.divisa IS NULL "
                + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto "
                + " GROUP BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre"
                + " ORDER BY s.campania.nombre, s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQuerySiembra = (QueryImpl) em.createQuery(queryCostosSiembra);
        hibernateQuerySiembra.getHibernateQuery().setParameterList("siembras",
                siembras);
        hibernateQuerySiembra.setParameter("unTipoCosto", UnidadMedida.getDolarPorTonelada());
        List<Object[]> costosSiembra = hibernateQuerySiembra.getResultList();
        for (Object[] objects : costosSiembra) {
            Siembra s = (Siembra) objects[0];
            Campania camp = s.getCampania();
            objects[0] = camp.getNombre();
            objects[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objects[1]).toString();

            objects[4] = superficiesSiembras.get(s);
        }
        insertReportLine(datasourceCollection, costosSiembra, title, false);
    }

    private void calculateCostosCampania(EntityManager em) {
        calculateCostosCampaniaDolar(em);
        calculateCostosCampaniaDolarHa(em);
        calculateCostosCampaniaDolarQQ(em);
        calculateCostosCampaniaDolarTn(em);
    }

    private void calculateCostosCampaniaDolar(EntityManager em) {
        // DOLAR
        // Si una campaña tiene dos siembras, supongamos una de 30 hectáreas y
        // otra de 70 hectáreas,
        // y en esta campaña se ingresa un costo (en la pestaña de Labores y
        // Costos) de 100 U$S,
        // estos costos se distribuyen 30 U$S para la siembra de 30 hectáreas y
        // 70 U$S para la siembra de 70 hectáreas.
        // Como la unidad de medida del costo es U$S, se lo pasa a U$S/ha
        // dividiéndolo por la cantidad de hectáreas de cada siembra.
        // Así tendríamos 30/30 U$S/ha y 70/70 U$S/ha.
        String queryDolar =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto) " + " FROM " + Campania.class.getName() + " AS c " + " INNER JOIN c.costos cos " + " INNER JOIN c.siembras s" + " WHERE c IN (:campanias) " + " AND s IN (:siembras) " + " AND cos.tipoCosto.unidadMedida.divisa IS NOT NULL " + " AND cos.tipoCosto.unidadMedida.unidad IS NULL " + " GROUP BY s, s.campo.nombre, s.cultivo.nombre" + " ORDER BY s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQueryDolar = (QueryImpl) em.createQuery(queryDolar);
        hibernateQueryDolar.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolar.getHibernateQuery().setParameterList("siembras", siembras);
        List<Object[]> dolar = hibernateQueryDolar.getResultList();
        for (Object[] objs : dolar) {
            Siembra siembra = (Siembra) objs[0];
            Campania camp = siembra.getCampania();
            objs[0] = camp.getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double costosSum = (Double) objs[3];
            Double propSiembra = proporcionSiembras.get(siembra);
            Double supSiembra = superficiesSiembras.get(siembra);
            Double costo = (costosSum / supSiembra) * propSiembra;
            objs[3] = costo;
        }

        insertReportLine(datasourceCollection, dolar, COSTO_DE_CAMPANIAS, false);
    }

    private void calculateCostosCampaniaDolarHa(EntityManager em) {
        // DOLAR POR HECTAREA
        // Si una campaña tiene dos siembras, supongamos una de 30 hectáreas y
        // otra de 70 hectáreas,
        // y en esta campaña se ingresa un costo (en la pestaña de Labores y
        // Costos) de 100 U$S/ha,
        // estos costos se distribuyen 30 U$S/ha para la siembra de 30 hectáreas
        // y 70 U$S/ha para la siembra de 70 hectáreas.
        String queryDolarHa =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto) " + " FROM " + Campania.class.getName() + " AS c " + " INNER JOIN c.costos cos " + " INNER JOIN c.siembras s" + " WHERE c IN (:campanias) " + " AND s IN (:siembras) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s, s.campo.nombre, s.cultivo.nombre " + " ORDER BY s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQueryDolarHa = (QueryImpl) em.createQuery(queryDolarHa);
        hibernateQueryDolarHa.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarHa.getHibernateQuery().setParameterList("siembras", siembras);
        hibernateQueryDolarHa.setParameter("unTipoCosto", UnidadMedida.getDolarPorHa());
        List<Object[]> dolarHa = hibernateQueryDolarHa.getResultList();
        for (Object[] objs : dolarHa) {
            Siembra siembra = (Siembra) objs[0];
            Campania camp = siembra.getCampania();
            objs[0] = camp.getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double propSiembra = proporcionSiembras.get(siembra);
            Double costosSum = (Double) objs[3];
            Double costo = costosSum * propSiembra;
            objs[3] = costo;
        }

        insertReportLine(datasourceCollection, dolarHa, COSTO_DE_CAMPANIAS,
                false);
    }

    private void calculateCostosCampaniaDolarQQ(EntityManager em) {
        // DOLAR POR QUINTAL
        // Si una campaña tiene dos siembras, supongamos que en una siembra el
        // Rinde Promedio es de 30 quintales
        // y la otra siembra tiene un rinde promedio de 70 quintales, y en esta
        // campaña se ingresa un costo
        // (en la pestaña de Labores y Costos) de 100 U$S/quintal, estos costos
        // se distribuyen 30 U$S/quintal
        // para la siembra de 30 quintales y 70 U$S/quintal para la siembra de
        // 70 quintales.
        // Como la unidad de medida del costo es U$S/quintal, se lo pasa a U$S
        // multiplicándolo por la cantidad de quintales
        // de la siembra, obteniendo el costo en U$S. Con esto se lo divide por
        // la cantidad de hectáreas
        // de la siembra para obtener el costo en U$S/ha.
        String queryDolarQQ =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto) " + " FROM " + Campania.class.getName() + " AS c " + " INNER JOIN c.costos cos " + " INNER JOIN c.siembras s" + " WHERE c IN (:campanias) " + " AND s IN (:siembras) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s, s.campo.nombre, s.cultivo.nombre " + " ORDER BY s, s.campo.nombre, s.cultivo.nombre";
        QueryImpl hibernateQueryDolarQQ = (QueryImpl) em.createQuery(queryDolarQQ);
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarQQ.getHibernateQuery().setParameterList("siembras", siembras);
        hibernateQueryDolarQQ.setParameter("unTipoCosto", UnidadMedida.getDolarPorQuintal());
        List<Object[]> dolarQQ = hibernateQueryDolarQQ.getResultList();
        for (Object[] objs : dolarQQ) {
            Siembra siembra = (Siembra) objs[0];
            Campania camp = siembra.getCampania();
            objs[0] = camp.getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double costosSum = (Double) objs[3];
            Double propQQSiembra = proporcionQuintalesSiembras.get(siembra);
            Double qqCamp = quintalesCampania.get(camp);
            Double supSiembra = superficiesSiembras.get(siembra);
            Double costo = ((costosSum * qqCamp) / supSiembra) * propQQSiembra;
            objs[3] = costo;
        }

        insertReportLine(datasourceCollection, dolarQQ, COSTO_DE_CAMPANIAS,
                false);
    }

    private void calculateCostosCampaniaDolarTn(EntityManager em) {
        // DOLAR POR TONELADA
        // Si una campaña tiene dos siembras, supongamos que en una siembra el
        // Rinde Promedio es de 30 quintales
        // y la otra siembra tiene un rinde promedio de 70 quintales, y en esta
        // campaña se ingresa un costo
        // (en la pestaña de Labores y Costos) de 1000 U$S/Ton, o lo que es lo
        // mismo 100 U$S/quintal,
        // estos costos se distribuyen 30 U$S/quintal para la siembra de 30
        // quintales y 70 U$S/quintal para la siembra de 70 quintales.
        // Como la unidad de medida del costo es U$S/Ton, primero se lo pasa a
        // U$S/quintal dividiéndolo por 10,
        // luego multiplicándolo por la cantidad de quintales de la siembra se
        // obtiene el costo en U$S.
        // Con esto se lo divide por la cantidad de hectáreas de la siembra para
        // obtener el costo en U$S/ha.
        String queryDolarTn =
                "SELECT s, s.campo, s.cultivo.nombre, SUM(cos.importe.monto) " + " FROM " + Campania.class.getName() + " AS c " + " INNER JOIN c.costos cos " + " INNER JOIN c.siembras s" + " WHERE c IN (:campanias) " + " AND s IN (:siembras) " + " AND cos.tipoCosto.unidadMedida.divisa IS NULL " + " AND cos.tipoCosto.unidadMedida.unidad = :unTipoCosto " + " GROUP BY s, s.campo.nombre, s.cultivo.nombre " + " ORDER BY s, s.campo.nombre, s.cultivo.nombre ";
        QueryImpl hibernateQueryDolarTn = (QueryImpl) em.createQuery(queryDolarTn);
        hibernateQueryDolarTn.getHibernateQuery().setParameterList("campanias",
                getCampanias());
        hibernateQueryDolarTn.getHibernateQuery().setParameterList("siembras",
                siembras);
        hibernateQueryDolarTn.setParameter("unTipoCosto", UnidadMedida.getDolarPorTonelada());
        List<Object[]> dolarTn = hibernateQueryDolarTn.getResultList();
        for (Object[] objs : dolarTn) {
            Siembra siembra = (Siembra) objs[0];
            Campania camp = siembra.getCampania();
            objs[0] = camp.getNombre();
            objs[1] = campoController.cargarCampaniasAsociadasACampo((Campo) objs[1]).toString();

            Double costosSum = (Double) objs[3];
            Double qqCamp = quintalesCampania.get(camp);
            Double propQQSiembra = proporcionQuintalesSiembras.get(siembra);
            Double supSiembra = superficiesSiembras.get(siembra);
            Double costo = (((costosSum / 10) * qqCamp) / supSiembra) * propQQSiembra;
            objs[3] = costo;
        }

        insertReportLine(datasourceCollection, dolarTn, COSTO_DE_CAMPANIAS,
                false);
    }

    private void insertReportLine(
            List<MargenBrutoEstablecimientoReportLine> ds, String campania,
            String establecimiento, String cultivo, String tipo,
            Double ingreso, Double egreso) {
        MargenBrutoEstablecimientoReportLine r = new MargenBrutoEstablecimientoReportLine(
                campania, establecimiento, cultivo, tipo, ingreso, egreso);

        if (ds.contains(r)) {
            int idx = ds.indexOf(r);
            MargenBrutoEstablecimientoReportLine existing = ds.get(idx);

            existing.addEgreso(egreso);
            existing.addIngreso(ingreso);
        } else {
            ds.add(r);
        }
    }

    private void insertReportLine(
            List<MargenBrutoEstablecimientoReportLine> ds,
            List<Object[]> result, String tipo, boolean ingreso) {
        for (Object[] o : result) {
            String campania = o[0].toString();
            Object campo = o[1];
            if (campo instanceof Campo) {
                campoController.cargarCampaniasAsociadasACampo((Campo) campo);
            }
            String cultivo = o[2].toString();
            Double monto = (Double) o[3];
            if (o.length == 5) {
                Double sup = (Double) o[4];

                if (monto == null) {
                    monto = new Double(0.0);
                }
                if (sup == null) {
                    sup = new Double(0.0);
                }

                if (sup > 0) {
                    monto /= sup;
                }
            }

            Double i = new Double(0);
            Double e = new Double(0);

            if (ingreso) {
                i = monto;
            } else {
                e = monto;

            }
            insertReportLine(ds, campania, campo.toString(), cultivo, tipo, i, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JFreeChart createChart() {
        ValueAxis rangeAxis = new NumberAxis(HEADER_MARGEN);
        CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot(
                rangeAxis);

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
        rangeAxis.setLowerMargin(0.10);

        return chart;
    }
    private JFreeChart chart;

    private Map<String, CategoryDataset> createChartDataset() {
        Map<String, CategoryDataset> r = new HashMap<String, CategoryDataset>();

        createDataSource();

        DefaultCategoryDataset dataset = null;

        for (MargenBrutoEstablecimientoReportLine rl : datasourceCollection) {

            String campania = rl.get(CAMPANIA).toString();
            dataset = (DefaultCategoryDataset) r.get(campania);

            if (dataset == null) {
                dataset = new DefaultCategoryDataset();
                r.put(campania, dataset);
            }

            Double ingreso = 0D;
            if (rl.get(INGRESO) != null) {
                ingreso = Double.parseDouble(rl.get(INGRESO).toString());
            }

            Double egreso = 0D;
            if (rl.get(EGRESO) != null) {
                egreso = Double.parseDouble(rl.get(EGRESO).toString());
            }

            Double margen = ingreso - egreso;

            try {
                Double oldValue = (Double) dataset.getValue((String) rl.get(CULTIVO), (String) rl.get(
                        ESTABLECIMIENTO));

                if (oldValue != null) {
                    margen += oldValue;
                }
            } catch (UnknownKeyException uke) {
            }

            dataset.addValue(margen, (String) rl.get(CULTIVO), (String) rl.get(ESTABLECIMIENTO));
        }

        return r;
    }

    public static void main(String[] args) {
        MargenBrutoEstablecimientoReportFrame frame = new MargenBrutoEstablecimientoReportFrame();
        frame.setVisible(true);
    }
}
