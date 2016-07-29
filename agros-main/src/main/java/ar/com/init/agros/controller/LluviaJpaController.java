package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.exceptions.lluvias.FechaDeLluviaRepetidaParaSuperficieException;
import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase LluviaJpaController
 *
 *
 * @author fbobbio
 * @version 03-sep-2009 
 */
public class LluviaJpaController extends BaseEntityJpaController<Lluvia>
{

    /** Constructor por defecto de LluviaJpaController */
    public LluviaJpaController()
    {
        super(Lluvia.class);
    }

    @Override
    public void persistOrUpdate(Lluvia entity) throws FechaDeLluviaRepetidaParaSuperficieException, InvalidStateException, ConstraintViolationException, Exception
    {
        if (checkDates(entity))
        {
            super.persistOrUpdate(entity);
        }
    }

    /** Método que checkea que no se esté intentando ingresar una combinación de lote-fecha repetida */
    private boolean checkDates(Lluvia l) throws FechaDeLluviaRepetidaParaSuperficieException
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status and o.fecha = :fecha and o.id <> :id")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("fecha", l.getFecha())
                .setParameter("id", l.getId());
        List<Lluvia> lluvias = (List<Lluvia>) q.getResultList();
        for (Lluvia lluvia : lluvias)
        {
            for (Lote lot : l.getLotes())
            {
                if (lluvia.getLotes().contains(lot))
                {
                    throw new FechaDeLluviaRepetidaParaSuperficieException("Ya existe una lluvia registrada para el lote " + lot.getNombre() + " en la fecha " + GUIUtility.toMediumDate(lluvia.getFecha()));
                }
            }
        }
        return true;
    }
}
