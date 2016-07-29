package ar.com.init.agros.controller;

import ar.com.init.agros.model.BaseCampaniaTransaction;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase PlanificacionAgroquimicoJpaController
 *
 *
 * @author fbobbio
 * @version 29-jun-2009 
 */
public class PlanificacionAgroquimicoJpaController extends BaseCampaniaTransactionJpaController
{

    /** Constructor por defecto de AgroquimicoJpaController */
    public PlanificacionAgroquimicoJpaController()
    {
        super(PlanificacionAgroquimico.class);
    }

    @Override
    public void persistOrUpdate(BaseCampaniaTransaction baseCampaniaTransaction) throws InvalidStateException, ConstraintViolationException, Exception
    {
        ((PlanificacionAgroquimico)baseCampaniaTransaction).calcularCostoPlanificado();
        super.persistOrUpdate(baseCampaniaTransaction);
    }

    public List<PlanificacionAgroquimico> findPlanificacionesByCampania(Campania campania)
    {
        EntityManager em = getEntityManager();
        Query q = em.createQuery("select object(o) from " + clazz.getName() + " as o where o.status = :status and o.campania = :campania").setParameter("status", BaseEntityStateEnum.ACTIVE).setParameter("campania", campania);
        return q.getResultList();
    }
}
