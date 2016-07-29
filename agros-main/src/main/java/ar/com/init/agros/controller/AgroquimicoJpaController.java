package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico_;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.ejb.HibernateQuery;
import org.hibernate.ejb.QueryImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase AgroquimicoJpaController
 *
 *
 * @author gmatheu
 * @version 12/06/2009 
 */
public class AgroquimicoJpaController extends BaseEntityJpaController<Agroquimico> {

    private Logger log = Logger.getLogger(AgroquimicoJpaController.class.getName());

    /** Constructor por defecto de AgroquimicoJpaController */
    public AgroquimicoJpaController() {
        super(Agroquimico.class);
    }

    @Override
    public void persistOrUpdate(Agroquimico agroquimico) throws InvalidStateException, ConstraintViolationException, Exception {
        DepositoJpaController depoCont = new DepositoJpaController();

        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InformacionAgroquimico ia = em.merge(agroquimico.getInformacion());
            agroquimico = em.merge(agroquimico);
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            log.fine(agroquimico.toString());
        } catch (InvalidStateException ise) {
            throw ise;
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            findKnownExceptions(ex);
            if (msg == null || msg.length() == 0) {
                UUID id = agroquimico.getUUID();
                if (find(id) == null) {
                    throw new NonExistentEntityException(
                            "The " + clazz.getName() + " with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
//                em.close();
            }
        }

        for (Deposito deposito : depoCont.findAllEntities()) {
            getEntityManager().merge(agroquimico.getValorDeposito(deposito));
            getEntityManager().merge(deposito);
        }
    }

    public List<Agroquimico> findByCampanias(List<Campania> campanias, boolean useTrabajos, boolean usePlanificaciones) {
        Set<Agroquimico> agroquimicos = new HashSet<Agroquimico>();

        if (useTrabajos) {
            agroquimicos.addAll(findByTrabajos(campanias));
        }
        if (usePlanificaciones) {
            agroquimicos.addAll(findByPlanificaciones(campanias));
        }

        return new ArrayList<Agroquimico>(agroquimicos);
    }

    @SuppressWarnings("unchecked")
    public List<Agroquimico> findByTrabajos(List<Campania> campanias) {
        String query =
                " SELECT DISTINCT dt.agroquimico "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt "
                + " WHERE t.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Agroquimico>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    @SuppressWarnings("unchecked")
    public List<Agroquimico> findByPlanificaciones(List<Campania> campanias) {
        String query = "SELECT DISTINCT dp.agroquimico "
                + " FROM " + PlanificacionAgroquimico.class.getName() + " AS p "
                + " INNER JOIN p.detallesPlanificacion dp "
                + " WHERE p.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Agroquimico>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<Agroquimico> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
//            Query q = em.createQuery(" "+
//                    "FROM " + Agroquimico.class.getName() + " AS a " +
//                    "INNER JOIN FETCH a.informacion AS ia " +
//                    "LEFT JOIN FETCH a.unidad " +
//                    "WHERE a.status = :status " +
//                    getOrderBy())
            Query q = em.createNamedQuery("agroquimico.findAll").setParameter("status",
                    BaseEntityStateEnum.ACTIVE);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
//            em.close();
        }
    }

    public List<Agroquimico> findByAlmacenamiento(List<Almacenamiento> almacenamientos) {

        List<Agroquimico> r = null;

        if (!almacenamientos.isEmpty()) {
            EntityManager em = getEntityManager();
            try {

                CriteriaBuilder builder = getCriteriaBuilder();
                CriteriaQuery<Agroquimico> criteria = builder.createQuery(Agroquimico.class);
                Root<ValorAgroquimico> root = criteria.from(ValorAgroquimico.class);
                criteria.select(root.get(ValorAgroquimico_.agroquimico));
                criteria.where(root.get(ValorAgroquimico_.almacenamiento).in(Arrays.asList(almacenamientos)));

                Query q = em.createQuery(criteria);
                r = q.getResultList();

                r = filterNoUsados(r, almacenamientos);

            } finally {
//            em.close();
            }
        } else {
            r = new ArrayList<Agroquimico>();
        }

        return r;
    }

    private List<Agroquimico> filterNoUsados(List<Agroquimico> agros, List<Almacenamiento> almacenamientos) throws HibernateException {
        List<Agroquimico> r = new ArrayList<Agroquimico>();

        if (!agros.isEmpty()) {
            String hql = "SELECT DISTINCT(dis.valor.agroquimico) "
                    + " FROM " + DetalleMovimientoStock.class.getName() + " dis"
                    + " WHERE (dis.cancelado = false OR dis.cancelado IS NULL) "
                    + " AND dis.valor.almacenamiento IN (:almacenamientos)"
                    + " AND dis.valor.agroquimico IN (:agros)";
            HibernateQuery qry = createHibernateQuery(hql);
            qry.getHibernateQuery().setParameterList("almacenamientos", almacenamientos);
            qry.getHibernateQuery().setParameterList("agros", agros);
            r = qry.getResultList();
        }
        return r;
    }

    @Override
    public String orderBy() {
        return "o.informacion.nombreComercial";
    }
}
