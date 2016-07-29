package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Clase CampaniaJpaController
 *
 *
 * @author fbobbio
 * @version 30-jul-2009 
 */
public class CampaniaJpaController extends BaseEntityJpaController<Campania>
{
    /** Constructor por defecto de CampaniaJpaController */
    public CampaniaJpaController()
    {
        super(Campania.class);
    }

    public List<Campania> findCampaniasPendientesDeCierre()
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status and o.campaniaState = :campaniaState")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("campaniaState", Campania.CampaniaState.ABIERTA);
        return q.getResultList();
    }

    public List<Campania> findCampaniasCerradas()
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status and o.campaniaState = :campaniaState")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("campaniaState", Campania.CampaniaState.CERRADA);
        return q.getResultList();
    }
}
