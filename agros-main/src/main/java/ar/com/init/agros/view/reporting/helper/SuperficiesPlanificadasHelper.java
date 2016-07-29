package ar.com.init.agros.view.reporting.helper;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import org.hibernate.ejb.QueryImpl;

import javax.persistence.EntityManager;
import java.util.List;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: gmatheu
 * Date: 30/04/2010
 * Time: 10:37:34
 * To change this template use File | Settings | File Templates.
 */
public class SuperficiesPlanificadasHelper extends AbstractSuperficiesHelper<Object>
{

    private EntityManager em;
    private String filtroCol;

    /**
     * Constructor por defecto de AbstractSuperficiesHelper
     */
    public SuperficiesPlanificadasHelper(EntityManager em, String filtroCol,
            List<Campania> campanias, List filtros)
    {
        super(campanias, filtros);

        this.em = em;
        this.filtroCol = filtroCol;

        String queryPlanificado = "SELECT t.campania, " + filtroCol + ", SUM(t.superficiePlanificada) "
                + " FROM " + PlanificacionAgroquimico.class.getName() + " AS t "
                + " WHERE t.campania IN (:campanias) "
                + " AND " + filtroCol + " IN (:filtros) "
                + " GROUP BY t.campania, " + filtroCol
                + " ORDER BY t.campania, " + filtroCol;

        QueryImpl hibernateQueryPlanificado = (QueryImpl) em.createQuery(queryPlanificado);
        hibernateQueryPlanificado.getHibernateQuery().setParameterList("campanias", campanias);
        hibernateQueryPlanificado.getHibernateQuery().setParameterList("filtros", filtros);
        List<Object[]> supPlanif = hibernateQueryPlanificado.getResultList();

        for (Object[] objects : supPlanif) {
            superficies.get((Campania) objects[0]).put(objects[1], (Double) objects[2]);
        }
    }

    @Override
    public Double getSuperficie(Campania camp, Object filtro, Agroquimico agroquimico)
    {
        Double sup = 0D;

        String hqlPlanificado = "SELECT t.superficiePlanificada "
                + " FROM " + PlanificacionAgroquimico.class.getName() + " AS t "
                + " INNER JOIN t.detallesPlanificacion dt "
                + " WHERE t.campania = :campania "
                + " AND " + filtroCol + " = :filtro "
                + " AND dt.agroquimico = :agroquimico"
                + " GROUP BY t, t.superficiePlanificada ";

        Query queryPlanificado = em.createQuery(hqlPlanificado);
        queryPlanificado.setParameter("campania", camp);
        queryPlanificado.setParameter("filtro", filtro);
        queryPlanificado.setParameter("agroquimico", agroquimico);
        List<Double> supPlanif = queryPlanificado.getResultList();

        for (Double d : supPlanif) {
            sup += d;
        }

        return sup;
    }

    @Override
    public Double getSuperficie(Campania camp, Object filtro, Agroquimico agroquimico, FormaFumigacion formaFumigacion)
    {
        //Sin implementación real
        return getSuperficie(camp, filtro, agroquimico);
    }
}
