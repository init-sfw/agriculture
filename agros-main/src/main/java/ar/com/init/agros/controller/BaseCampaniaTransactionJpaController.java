package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.controller.exceptions.transacciones.CampaniaCerradaException;
import ar.com.init.agros.model.BaseCampaniaTransaction;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.terreno.Campo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase BaseCampaniaTransactionJpaController
 *
 *
 * @author fbobbio
 * @version 02-ago-2009 
 */
public class BaseCampaniaTransactionJpaController<T extends BaseCampaniaTransaction> extends BaseEntityJpaController<T>
{

    /** Constructor por defecto de BaseCampaniaTransactionJpaController */
    public BaseCampaniaTransactionJpaController(Class<T> clazz)
    {
        super(clazz);
    }

    @Override
    public void delete(T entity) throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, CampaniaCerradaException, Exception
    {
      delete(entity, null);
    }

    protected void delete(T entity, EntityManager em)  throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, CampaniaCerradaException, Exception
    {
          if (!entity.getCampania().isCerrada()) {
            super.delete(entity);
        }
        else {
            throw new CampaniaCerradaException(
                    "Imposible eliminar transacción, la campaña asociada ha sido cerrada");
        }
    }

    /** Método que checkea si hay transacciones abiertas para un campo y devuelve la instancia que corresponda
     *
     * @param entity el campo que se quiere modificar
     * @return null si el campo posee transacciones asociadas a una campaña abierta (no se puede modificar); la misma entidad enviada por parámetro si no posee transacciones asociadas o una copia de toda la configuración de campo si posee transacciones asociadas a una campaña cerrada
     */
    public Campo checkTransactionOpenedForCampo(Campo entity)
    {
        boolean ban = false;
        //Checkeo si el Establecimiento está en la base de datos
        if (!isCampoPersistido(entity))
        {
            // Si no está persistido lo devuelvo como vino
            return entity;
        }
        List<BaseCampaniaTransaction> list = findTransaccionesAsociadasACampo(entity);
        for (BaseCampaniaTransaction transaction : list) {
            if (transaction.getCampo().equals(entity)) {
                if (transaction.getCampania().isCerrada()) {
                    ban = true; // seteo la bandera a true si encuentro transacciones cerradas
                }
                else
                {
                    ban = false;
                    return null;
                }
            }
        }
        if (ban) // Si llegó como true quiere decir que TODAS las campañas de las transacciones asociadas al campo están CERRADAS
        {
            return new Campo(entity);
        }
        else
        {
            return entity;
        }

    }

    @Override
    public void persistOrUpdate(T baseCampaniaTransaction) throws InvalidStateException, ConstraintViolationException, Exception
    {
        if (baseCampaniaTransaction.getIdTransaccion() == null) {
            long nextId = getMaxValue("idTransaccion") + 1;
            baseCampaniaTransaction.setIdTransaccion(nextId);
        }

        super.persistOrUpdate(baseCampaniaTransaction);
    }

    public boolean exists(T baseCampaniaTransaction)
    {
        List<T> list = findEntities();
        for (T p : list) {
            if (baseCampaniaTransaction.equalToEntity(p)) {
                return true;
            }
        }
        return false;
    }

    /** Método que lista las campanias que tiene asociadas un campo x a través de una o varias transacciones
     *  
     */
    public Set<Campania> findCampaniasAsociadasACampo(Campo campo)
    {
        List<BaseCampaniaTransaction> transacciones = null;
        Set<Campania> campanias = new HashSet<Campania>();
        //Checkeo si el Establecimiento está en la base de datos
        if (!isCampoPersistido(campo))
        {
            return campanias;
        }
        //Traigo las transacciones asociadas al campo
        transacciones = findTransaccionesAsociadasACampo(campo);
        //Agrego las campanias al set
        for (BaseCampaniaTransaction tr : transacciones)
        {
            campanias.add(tr.getCampania());
        }
        return campanias;
    }

    /**  
     * Pregunto si el campo existe en la base
     * @param campo
     * @return true si el campo está persistido, false si no lo está
     */
    private boolean isCampoPersistido(Campo campo)
    {
        CampoJpaController controller = new CampoJpaController();
        return (controller.find(campo.getId()) != null);
    }

    public List<BaseCampaniaTransaction> findTransaccionesAsociadasACampo(Campo campo)
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status " +
                "and o.campo = :campo")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("campo", campo);
        return q.getResultList();
    }
}
