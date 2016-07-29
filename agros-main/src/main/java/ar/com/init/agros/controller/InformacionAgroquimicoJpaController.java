package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.Accion;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase InformacionAgroquimicoJpaController
 *
 *
 * @author gmatheu
 * @version 28/07/2009 
 */
public class InformacionAgroquimicoJpaController extends BaseEntityJpaController<InformacionAgroquimico>
{

    /** Constructor por defecto de InformacionAgroquimicoJpaController */
    public InformacionAgroquimicoJpaController()
    {
        super(InformacionAgroquimico.class);
    }

    @Override
    public String orderBy()
    {
        return "nombreComercial";
    }

    public List<Accion> findAllAcciones()
    {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT DISTINCT a "+
                    "FROM " + Accion.class.getName() + " AS a " +
                    "WHERE a.status = :status ").setParameter("status",
                    BaseEntityStateEnum.ACTIVE);
            return q.getResultList();
        }
        finally {
//            em.close();
            }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<InformacionAgroquimico> findEntities(boolean all, int maxResults, int firstResult)
    {
        EntityManager em = getEntityManager();
        try {
//            Query q = em.createQuery("FROM " + clazz.getName() + " AS ia " +
//                    "LEFT JOIN FETCH ia.agroquimico AS a " +
//                    "LEFT JOIN FETCH a.unidad " +
//                    "LEFT JOIN FETCH ia.acciones " +
//                    "WHERE ia.status = :status " +
//                    getOrderBy()).setParameter("status",
//                    BaseEntityStateEnum.ACTIVE);

            Query q = em.createNamedQuery("informacionAgroquimico.findAll")
                    .setParameter("status", BaseEntityStateEnum.ACTIVE);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }

            List<InformacionAgroquimico> temp = q.getResultList();
            List<InformacionAgroquimico> r = new ArrayList<InformacionAgroquimico>();

            for (InformacionAgroquimico informacionAgroquimico : temp) {
                if (!r.contains(informacionAgroquimico)) {
                    r.add(informacionAgroquimico);
                }
            }

            return r;
        }
        finally {
//            em.close();
        }
    }

    @Override
    public void persist(List<InformacionAgroquimico> entities) throws InvalidStateException, ConstraintViolationException
    {
        if (entities.size() == 0) {
            return;
        }

        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (InformacionAgroquimico ia : entities) {
                em.merge(ia);
            }
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
        }
        catch (Exception ex) {
            findKnownExceptions(ex);
        }
        finally {
            if (em != null) {
//                em.close();
            }
        }
    }
}
