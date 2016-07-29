package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.almacenamiento.ValorCereal_;
import ar.com.init.agros.model.inventario.cereales.DetalleIngresoCereal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Clase CultivoJpaController
 *
 *
 * @author gmatheu
 * @version 30/06/2009
 */
public class CultivoJpaController extends NamedEntityJpaController<Cultivo> {

    /** Constructor por defecto de CultivoJpaController */
    public CultivoJpaController() {
        super(Cultivo.class);
    }

    @Override
    public void persistOrUpdate(Cultivo entity) throws InvalidStateException, ConstraintViolationException, Exception {
        super.persistOrUpdate(entity);

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        String hql = String.format("SELECT vc FROM %s AS vc WHERE vc.cultivo = :cultivo", VariedadCultivo.class.getName());
        Query qry = em.createQuery(hql);
        qry.setParameter("cultivo", entity);

        try {
            List<VariedadCultivo> vars = qry.getResultList();
            vars.removeAll(entity.getVariedades());

            for (VariedadCultivo var : vars) {
                em.remove(var);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }

        em.close();
    }

    public List<Cultivo> findByCampanias(List<Campania> campanias, boolean useTrabajos, boolean usePlanificaciones, boolean useSiembras) {
        Set<Cultivo> cultivos = new HashSet<Cultivo>();

        if (useTrabajos) {
            cultivos.addAll(findByTrabajos(campanias));
        }
        if (usePlanificaciones) {
            cultivos.addAll(findByPlanificaciones(campanias));
        }
        if (useSiembras) {
            cultivos.addAll(findBySiembras(campanias));
        }

        return new ArrayList<Cultivo>(cultivos);
    }

    @SuppressWarnings("unchecked")
    public List<Cultivo> findByTrabajos(List<Campania> campanias) {
        String query =
                " SELECT DISTINCT s.cultivo "
                + " FROM " + Trabajo.class.getName() + " AS t "
                + " INNER JOIN t.detalles dt " + " INNER JOIN t.campania.siembras s "
                + " WHERE t.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Cultivo>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    @SuppressWarnings("unchecked")
    public List<Cultivo> findByPlanificaciones(List<Campania> campanias) {
        String query = " SELECT DISTINCT p.cultivo "
                + " FROM " + PlanificacionAgroquimico.class.getName() + " AS p "
                + " WHERE p.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Cultivo>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    @SuppressWarnings("unchecked")
    public List<Cultivo> findBySiembras(List<Campania> campanias) {
        String query = " SELECT DISTINCT s.cultivo "
                + " FROM " + Siembra.class.getName() + " AS s "
                + " WHERE s.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Cultivo>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    public List<Cultivo> findByAlmacenamiento(List<Almacenamiento> almacenamientos) {

        List<Cultivo> r = null;

        if (!almacenamientos.isEmpty()) {
            EntityManager em = getEntityManager();
            try {

                CriteriaBuilder builder = getCriteriaBuilder();
                CriteriaQuery<Cultivo> criteria = builder.createQuery(Cultivo.class);
                Root<ValorCereal> root = criteria.from(ValorCereal.class);
                criteria.select(root.get(ValorCereal_.cereal));
                criteria.where(root.get(ValorCereal_.almacenamiento).in(Arrays.asList(almacenamientos)));

                Query q = em.createQuery(criteria);
                r = q.getResultList();

                r = filterNoUsados(r, almacenamientos);

            } finally {
//            em.close();
            }
        } else {
            r = new ArrayList<Cultivo>();
        }

        return r;
    }

    private List<Cultivo> filterNoUsados(List<Cultivo> cultivos, List<Almacenamiento> almacenamientos) throws HibernateException {
        List<Cultivo> r = new ArrayList<Cultivo>();

        if (!cultivos.isEmpty()) {
            String hql = "SELECT DISTINCT(dic.valor.cereal) "
                    + " FROM " + DetalleIngresoCereal.class.getName() + " dic"
                    + " WHERE (dic.cancelado = false OR dic.cancelado IS NULL) "
                    + " AND dic.valor.almacenamiento IN (:almacenamientos)"
                    + " AND dic.valor.cereal IN (:cultivos)";
            HibernateQuery qry = createHibernateQuery(hql);
            qry.getHibernateQuery().setParameterList("almacenamientos", almacenamientos);
            qry.getHibernateQuery().setParameterList("cultivos", cultivos);
            r = qry.getResultList();
        }
        return r;
    }
}
