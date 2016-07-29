package ar.com.init.agros.controller.base;

import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase base para el manejo de persistencia basico de entidades heredadas de BaseEntity.
 *
 * @author gmatheu
 */
public class BaseEntityJpaController<T extends BaseEntity> extends ReadOnlyBaseEntityJpaController<T> {

    /**
     *
     *
     * @param clazz Objeto correspondiente a la clase a la que hace referencia.
     */
    public BaseEntityJpaController(Class<T> clazz) {
        super(clazz, false);
    }

    /**
     *
     *
     * @param clazz Objeto correspondiente a la clase a la que hace referencia.
     * @param keepEntityManager determina si se va a crear un solo entityManager para la instancia
     */
    public BaseEntityJpaController(Class<T> clazz, boolean keepEntityManager) {
        super(clazz);
        this.clazz = clazz;
        this.keepEntityManager = keepEntityManager;
    }
   
    /**
     * Actualiza la entidad si esta existe previamente, en caso contrario la persiste.
     *
     * @param entity
     * @throws org.hibernate.validator.InvalidStateException
     */
    public void persistOrUpdate(T entity) throws InvalidStateException, ConstraintViolationException, Exception {
        try {
            update(entity);
            logger.log(Level.FINE, "updated {0}", entity.toString());
        } catch (NonExistentEntityException ex) {
            persist(entity);
            logger.log(Level.FINE, "persisted {0}", entity.toString());
        }
    }

    /**
     * Para cada entidad de la listaActualiza la entidad si esta existe previamente, en caso contrario la persiste.
     *
     * @param entities
     * @throws org.hibernate.validator.InvalidStateException
     */
    public void persistOrUpdate(List<T> entities) throws InvalidStateException, ConstraintViolationException, Exception {
        for (T t : entities) {
            persistOrUpdate(t);
        }
    }

    /**
     * Guarda la entidades  en la base de datos como transaccion.
     *
     * @param entity
     * @throws org.hibernate.validator.InvalidStateException
     */
    public void persist(T entity) throws InvalidStateException, ConstraintViolationException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            logger.fine(entity.toString());
        } catch (Exception ex) {
            findKnownExceptions(ex);
        } finally {
            if (em != null) {
//                em.close();
            }
        }
    }

    /**
     * Guarda la entidad  en la base de datos como transaccion.
     *
     * @param entity
     * @throws org.hibernate.validator.InvalidStateException
     */
    public void persist(List<T> entities) throws InvalidStateException, ConstraintViolationException {
        if (entities.isEmpty()) {
            return;
        }

        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (BaseEntity baseEntity : entities) {
                em.persist(baseEntity);
            }
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
        } catch (Exception ex) {
            findKnownExceptions(ex);

            logger.severe(ex.getMessage());
        } finally {
            if (em != null) {
//                em.close();
            }
        }
    }

    /**
     *
     *
     * @param entity
     * @throws ar.com.init.agros.controller.exceptions.NonExistentEntityException
     * @throws java.lang.Exception
     * @throws org.hibernate.validator.InvalidStateException
     */
    //TODO: arreglar el logueo de la excepción de ConstraintViolation
    public void update(T entity) throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            logger.fine(entity.toString());
        } catch (InvalidStateException ise) {
            throw ise;
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            findKnownExceptions(ex);
            if (msg == null || msg.length() == 0) {
                UUID id = entity.getUUID();
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
    }
    

    /**
     * Pone la entidad pasada por parametro en contexto de persistencia. Para poder usar relaciones
     * con lazy load.
     *
     * Cerrar em, al terminar la tarea.
     *
     * @param entity
     * @return
     */
    public EntityManager merge(T entity) {
        EntityManager em = getEntityManager();
        entity = em.merge(entity);
        logger.fine(entity.toString());
        return em;
    }

    /**
     * Remueve la entidad correspondiente al id de la base datos.
     * Borrado fisico de la entidad
     *
     * @param id
     * @throws ar.com.init.agros.controller.exceptions.NonExistentEntityException
     */
    public void destroy(UUID id) throws NonExistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BaseEntity entity;
            try {
                entity = em.getReference(clazz, id.toString());
                entity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonExistentEntityException(
                        "The " + clazz.getName() + " with id " + id.toString() + " no longer exists.", enfe);
            }
            em.remove(entity);
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            logger.fine(entity.toString());
        } finally {
            if (em != null) {
//                em.close();
            }
        }
    }

    /**
     * Remueve la entidad correspondiente de la base datos. Borrado fisico de la entidad
     *
     * @param entity
     * @throws ar.com.init.agros.controller.exceptions.NonExistentEntityException
     */
    public void destroy(T entity) throws NonExistentEntityException {
        destroy(entity.getUUID());
    }

    /**
     * Borrado físico de la entidad
     *
     * @param entity
     * @throws ar.com.init.agros.controller.exceptions.NonExistentEntityException
     * @throws org.hibernate.validator.InvalidStateException
     * @throws org.hibernate.exception.ConstraintViolationException
     * @throws java.lang.Exception
     */
    public void delete(T entity) throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, Exception {
        try {
            destroy(entity);
        } catch (RollbackException ex) {
            findKnownExceptions(ex);
            throw ex;
        }
        logger.fine(entity.toString());
    }   
}
