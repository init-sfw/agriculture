package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.util.EMailMessage;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 * Clase EMailMessageJpaController
 *
 *
 * @author gmatheu
 * @version 13/07/2009 
 */
public class EMailMessageJpaController extends BaseEntityJpaController<EMailMessage>
{

    /** Constructor por defecto de EMailMessageJpaController */
    public EMailMessageJpaController()
    {
        super(EMailMessage.class);
    }

    @SuppressWarnings("unchecked")
    public List<EMailMessage> findNotSent()
    {
        return createQuery("FROM " + EMailMessage.class.getName() + " WHERE sentTimes = :sentTimes").setParameter(
                "sentTimes", 0).getResultList();
    }

    public void purge()
    {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, -6);

        int deleted = em.createQuery(
                "DELETE FROM " + EMailMessage.class.getName() + " WHERE lastSent < :lastSent").setParameter(
                "lastSent", date.getTime()).executeUpdate();

        Logger.getLogger(EMailMessageJpaController.class.getName()).info(deleted + " emails were purged");
        em.getTransaction().commit();
    }
}
