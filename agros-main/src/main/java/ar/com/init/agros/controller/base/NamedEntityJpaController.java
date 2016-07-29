package ar.com.init.agros.controller.base;

import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.model.base.NamedEntity;
import javax.persistence.NoResultException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase base para el manejo de persistencia basico de entidades que heredan de BaseEntity r implementan NamedEntity.
 *
 * @author gmatheu
 * @version 07/06/2009 
 */
public class NamedEntityJpaController<T extends NamedEntity> extends BaseEntityJpaController<T>
{

    /** Constructor por defecto de NamedEntityJpaController */
    public NamedEntityJpaController(Class<T> clazz)
    {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    public T findByNombre(String nombre) throws NoResultException
    {
        return (T) createQuery("FROM " + clazz.getName() + " WHERE nombre = :nombre").setParameter("nombre", nombre).getSingleResult();
    }

    public boolean existsNombre(String nombre)
    {
        try {
            return (findByNombre(nombre) != null);
        }
        catch (NoResultException nre) {
            return false;
        }
    }

    @Override
    public void persist(T entity) throws InvalidStateException
    {
        super.persist(entity);
    }

    @Override
    public void update(T entity) throws NonExistentEntityException, Exception
    {
        super.update(entity);
    }

    @Override
    public void refreshEntity(T entity)
    {
        super.refreshEntity(entity);
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public String orderBy()
    {
        return "nombre";
    }
}
