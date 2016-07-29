package ar.com.init.agros.view.reporting.helper;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.ejb.QueryImpl;

/**
 * Clase SuperficiesPulverizadasVariedadCultivoHelper
 *
 *
 * @author gmatheu
 * @version 25/04/2010 
 */
public class SuperficiesPulverizadasVariedadCultivoHelper extends AbstractSuperficiesHelper<VariedadCultivo>
{

    private EntityManager em;
    private SiembrasCultivoHelper siembrasCultivoHelper;

    public SuperficiesPulverizadasVariedadCultivoHelper(EntityManager em, List<Campania> campanias,
            List<VariedadCultivo> filtros, SiembrasCultivoHelper siembrasHelper)
    {
        super(campanias, filtros);

        this.em = em;
        this.siembrasCultivoHelper = siembrasHelper;

        QueryImpl hibernateQuerySuperficiePulv =
                (QueryImpl) em.createQuery(
                "SELECT t.campania, t, t.superficieSeleccionada.valor"
                + " FROM Trabajo  AS t "
                + " WHERE t.campania IN (:campanias) ");
        hibernateQuerySuperficiePulv.getHibernateQuery().setParameterList(
                "campanias", campanias);
        List<Object[]> supPulv = hibernateQuerySuperficiePulv.getResultList();

        for (Iterator<Object[]> it = supPulv.iterator(); it.hasNext();) {
            Object[] o = it.next();

            Campania camp = (Campania) o[0];
            Trabajo trabajo = (Trabajo) o[1];
            Siembra siembra = siembrasHelper.findSiembra(trabajo.getSuperficies(), camp);

            if (siembra == null) {
                it.remove();
                continue;
            }
            VariedadCultivo cultivo = siembra.getVariedadCultivo();

            if (cultivo != null) {
                Double sup = superficies.get((Campania) o[0]).get(cultivo) + (Double) o[2];
                superficies.get((Campania) o[0]).put(cultivo, sup);
            }
        }
    }

    @Override
    public Double getSuperficie(Campania camp, VariedadCultivo variedad, Agroquimico agroquimico)
    {
        return getSuperficie(camp, variedad, agroquimico,null);
    }

    @Override
    public Double getSuperficie(Campania camp, VariedadCultivo variedad, Agroquimico agroquimico, FormaFumigacion formaFumigacion)
    {
        Double sup = 0D;

        String fumig = "";
        if (formaFumigacion != null)
        {
            fumig = " AND t.formaFumigacion = :formaFumigacion ";
        }

                Query queryPlanificado = em.createQuery(
                " SELECT DISTINCT t, t.superficieSeleccionada.valor" +
                " FROM Trabajo  AS t " +
                " INNER JOIN t.detalles dt " +
                " WHERE t.campania = :campania " +
                " AND dt.agroquimico = :agroquimico" +
                " GROUP BY t, t.superficieSeleccionada.valor ");
        queryPlanificado.setParameter("campania", camp);
        queryPlanificado.setParameter("agroquimico", agroquimico);
        if (formaFumigacion != null)
        {
            queryPlanificado.setParameter("formaFumigacion", formaFumigacion);
        }

        List<Object[]> supPulv = queryPlanificado.getResultList();

        for (Iterator<Object[]> it = supPulv.iterator(); it.hasNext();) {
            Object[] o = it.next();

            Trabajo trabajo = (Trabajo) o[0];
            Siembra siembra = siembrasCultivoHelper.findSiembra(trabajo.getSuperficies(), camp);

            if (siembra == null) {
                it.remove();
                continue;
            }
            VariedadCultivo variedadSiembra = siembra.getVariedadCultivo();

            if(variedadSiembra != null && variedadSiembra.equals(variedad))
            {
                sup += (Double) o[1];
            }
        }

        return sup;
    }
}
