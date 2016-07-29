package ar.com.init.agros.view.reporting.helper;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.terreno.Campo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.ejb.QueryImpl;

/**
 * Clase SuperficiesPulverizadasCampoHelper
 *
 *
 * @author gmatheu
 * @version 25/04/2010 
 */
public class SuperficiesPulverizadasCampoHelper extends AbstractSuperficiesHelper<Campo>
{

    private EntityManager em;

    public SuperficiesPulverizadasCampoHelper(EntityManager em, List<Campania> campanias,
            List<Campo> filtros)
    {
        super(campanias, filtros);

        this.em = em;

        QueryImpl hibernateQuerySuperficiePulv =
                (QueryImpl) em.createNamedQuery(Campania.SUPERFICIE_TRABAJO_CAMPO_QUERY_NAME);
        hibernateQuerySuperficiePulv.getHibernateQuery().setParameterList(
                "campanias", campanias);
        hibernateQuerySuperficiePulv.getHibernateQuery().setParameterList(
                "filtros", filtros);
        List<Object[]> supPulv = hibernateQuerySuperficiePulv.getResultList();
        for (Object[] objects : supPulv) {
            superficies.get((Campania) objects[0]).put((Campo) objects[1], (Double) objects[2]);
        }
    }

    @Override
    public Double getSuperficie(Campania camp, Campo campo, Agroquimico agroquimico)
    {
        return getSuperficie(camp, campo, agroquimico,null);
    }

    @Override
    public Double getSuperficie(final Campania camp, final Campo campo,
            final Agroquimico agroquimico, final FormaFumigacion formaFumigacion)
    {
        Double sup = 0D;

        String fumig = "";
        if (formaFumigacion != null)
        {
            fumig = " AND t.formaFumigacion = :formaFumigacion ";
        }

        Query queryPlanificado = em.createQuery("SELECT t.superficieSeleccionada.valor "
                + " FROM Trabajo  AS t "
                + " INNER JOIN t.detalles dt "
                + " WHERE t.campania = :campania "
                + " AND dt.agroquimico = :agroquimico"
                + " AND t.campo = :campo "
                + fumig
                + " GROUP BY t, t.superficieSeleccionada.valor");
        queryPlanificado.setParameter("campania", camp);
        queryPlanificado.setParameter("campo", campo);
        queryPlanificado.setParameter("agroquimico", agroquimico);
        if (formaFumigacion != null)
        {
            queryPlanificado.setParameter("formaFumigacion", formaFumigacion);
        }
        List<Double> supPulv = queryPlanificado.getResultList();

        for (Double d : supPulv) {
            sup += d;
        }

        return sup;
    }
}
