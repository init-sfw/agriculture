package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.costo.TipoCosto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Clase TipoCostoController
 *
 *
 * @author fbobbio
 * @version 09-dic-2009 
 */
public class TipoCostoController extends NamedEntityJpaController<TipoCosto>
{
    /** Constructor por defecto de TipoCostoController */
    public TipoCostoController()
    {
        super(TipoCosto.class);
    }

    public List<TipoCosto> findCostosPulverizacion()
    {
        return findCostos(TipoCosto.TipoTipoCosto.PULVERIZACION);
    }

    public List<TipoCosto> findCostosSiembra()
    {
        return findCostos(TipoCosto.TipoTipoCosto.SIEMBRA);
    }

    public List<TipoCosto> findCostosCampania()
    {
        return findCostos(TipoCosto.TipoTipoCosto.CAMPANIA);
    }

    public List<TipoCosto> findCostosPostCosecha()
    {
        return findCostos(TipoCosto.TipoTipoCosto.POST_COSECHA);
    }

    private List<TipoCosto> findCostos(TipoCosto.TipoTipoCosto tipo)
    {
        EntityManager em = getEntityManager();
        try {
            Query q =
                    em.createQuery(
                    "SELECT object(o) FROM " + clazz.getName() + " AS o " +
                    " WHERE o.status = :status " +
                    " AND o.tipo = :tipo" +
                    getOrderBy()).setParameter("status", BaseEntityStateEnum.ACTIVE)
                    .setParameter("tipo", tipo);

            return q.getResultList();
        }
        finally {
//            em.close();
        }
    }
}
