package ar.com.init.agros.controller.base;

import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.ejb.QueryImpl;
import org.hibernate.exception.ConstraintViolationException;

/**
 * Clase base para el manejo de operaciones de solo lectura de entidades heredadas de BaseEntity.
 *
 * @author gmatheu
 */
public class ReadOnlyBaseEntityJpaController<T extends BaseEntity> {

    protected static final Logger logger = Logger.getLogger(ReadOnlyBaseEntityJpaController.class.getName());
    protected Class<? extends BaseEntity> clazz;
    protected boolean keepEntityManager;
    protected EntityManager em;

    /**
     *
     *
     * @param clazz Objeto correspondiente a la clase a la que hace referencia.
     */
    public ReadOnlyBaseEntityJpaController(Class<T> clazz) {
        this(clazz, false);
    }

    /**
     *
     *
     * @param clazz Objeto correspondiente a la clase a la que hace referencia.
     * @param keepEntityManager determina si se va a crear un solo entityManager para la instancia
     */
    public ReadOnlyBaseEntityJpaController(Class<T> clazz, boolean keepEntityManager) {
        this.clazz = clazz;
        this.keepEntityManager = keepEntityManager;
    }

    /**
     * Crea un entityManager para ser utilizado libremente.
     *
     * @return
     */
    public EntityManager getEntityManager() {
        if (keepEntityManager) {
            if (em == null) {
                em = EntityManagerUtil.createEntityManager();
                logger.info("Using a single EntityManager");
            }
            return em;
        } else {
            return EntityManagerUtil.createEntityManager();
        }
    }

    public void close() {
        if (em != null) {
            em.close();
            logger.info("Closing EntityManager");
        }
    }

    public void setKeepEntityManager(boolean keepEntityManager) {
        this.keepEntityManager = keepEntityManager;
    }

    protected void findKnownExceptions(Exception ex) throws ConstraintViolationException {
        Throwable current = ex;
        do {
            if (current instanceof ConstraintViolationException) {
                throw (ConstraintViolationException) current;
            }

            current = current.getCause();

        } while (current != null);

        if (ex instanceof PersistenceException) {
            throw (PersistenceException) ex;
        }
    }

    /**
     * Encuentra todas las entidades sin diferenciar estado
     *
     * @return
     */
    public List<T> findEntities() {
        return findEntities(true, -1, -1);
    }

    /**
     * Encuentra todas las entidades con estado ACTIVE
     *
     * @return
     */
    public List<T> findAllEntities() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT object(" + alias() + ") FROM " + clazz.getName() + " AS " + alias() + " " + getOrderBy());

            return q.getResultList();
        } finally {
//            em.close();
        }
    }

    /**
     * Encuentra todas las entidades con estado ACTIVE
     *
     * @param maxResults cantidad maxima de resultados
     * @param firstResult desplazamiento del primer resultado
     * @return
     */
    public List<T> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    /**
     *
     *
     * @param all
     * @param maxResults
     * @param firstResult
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<T> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        em.clear();
        try {
            Query q = createFindEntitiesQuery();

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
//            em.close();
        }
    }

    protected Query createFindEntitiesQuery() {
        return getEntityManager().createQuery(
                "SELECT object(" + alias() + ") FROM " + clazz.getName() + " AS " + alias()
                + " WHERE " + alias() + ".status = :status "
                + getOrderBy()).setParameter("status", BaseEntityStateEnum.ACTIVE);
    }

    /**
     * Devuelve la entidad de la base de datos correspondiente al id.
     * @param id
     * @return
     */
    public T find(UUID id) {
        return find(id.toString());
    }

    /**
     * Metodo que crea la clausula ORDER BY de acuerdo a las propiedades indicadas en el metodo orderBy para ser usado en las consultas JPA.
     * @return la clausula ORDER BY incluyendo las propiedades y sentidos
     */
    protected String getOrderBy() {
        if (orderBy().length() == 0) {
            return "";
        } else {
            return " ORDER BY " + orderBy();
        }
    }

    /**
     * Indica cuales propiedades seran usadas para ordenar el resultado de los metodos findXXX
     * @return la lista de propiedades separadas por comas, con el sentido (ASC, DESC) opcionalmente
     */
    public String orderBy() {
        return "";
    }

    /**
     * Indica cual es el alias que se va a usar en las consultas genericas
     * @return el valor del alias de la consulta HQL (as o)
     */
    public String alias() {
        return "o";
    }

    @SuppressWarnings("unchecked")
    public T find(String id) {
        EntityManager em = getEntityManager();
        try {
            return (T) em.find(clazz, id.toString());
        } finally {
//            em.close();
        }
    }

    /**
     * Devuelve el maximo valor de la columna pasada por parametro (incluyendo todos los estados).
     *
     * @param  property columna cuyo valor se quiere conocer.
     * @return Valor maximo de la columna especificada.
     */
    protected long getMaxValue(String property) {
        EntityManager em = getEntityManager();
        try {
            return ((Long) em.createQuery("select max(" + property + ") from " + clazz.getName() + " as o").getSingleResult()).longValue();
        } catch (NullPointerException ex) {
            return 0;
        } finally {
//            em.close();
        }
    }

    /**
     * Devuelve la cantidad de instancias persistidas en estado ACTIVE
     * de la clase a la que hace referencia.
     *
     * @return Cantidad de instancias persistidas de la Clase.
     */
    public long getCount() {
        EntityManager em = getEntityManager();
        try {
            return ((Long) em.createQuery(
                    "select count(o) from " + clazz.getName() + " as o where o.status = :status").setParameter(
                    "status", BaseEntityStateEnum.ACTIVE).getSingleResult()).longValue();
        } finally {
//            em.close();
        }
    }

    /**     
     * Metodo que devuelve una consulta en base a la cadena pasada por parametro para ser ejecutada directamente.
     *
     * //TODO: Verificar que el em no quede abierto.
     *
     * @param query Consulta HQL
     * @return JPA Query para ser ejecutada directamente
     */
    public Query createQuery(String query) {
        return getEntityManager().createQuery(query);
    }

    public <T extends BaseEntity> Query createQuery(CriteriaQuery<T> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    /**
     * Devuelve una Consulta DE LA IMPLEMENTACION DE HIBERNATE con el parametro pasado.
     * Permite pasar listas como parametros.
     * @param query
     * @return
     */
    public QueryImpl createHibernateQuery(String query) {
        return (QueryImpl) createQuery(query);
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return EntityManagerUtil.getCriteriaBuilder();
    }

    /** Método que equipara los valores del objeto de entidad a los de la base de datos
     *
     * @param entity la entidad a ser actualizada
     */
    public void refreshEntity(T entity) {
        if (this.getEntityManager().contains(entity)) {
            this.getEntityManager().refresh(entity);
        }
    }

    public void clear() {
        getEntityManager().clear();
    }
}
