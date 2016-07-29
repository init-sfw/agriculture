package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import java.util.List;

/**
 * Clase UnidadMedidaJpaController
 *
 *
 * @author gmatheu
 * @version 05/06/2009 
 */
public class UnidadMedidaJpaController extends BaseEntityJpaController<UnidadMedida>
{

    /** Constructor por defecto de UnidadMedidaJpaController */
    public UnidadMedidaJpaController()
    {
        super(UnidadMedida.class);
    }

    @SuppressWarnings("unchecked")
    public List<UnidadMedida> findByMagnitud(MagnitudEnum... magnitudes)
    {        
        return createHibernateQuery("FROM UnidadMedida WHERE magnitud IN (:magnitudes) AND status = :status")
                .getHibernateQuery()
                .setParameterList("magnitudes", magnitudes)
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .list();
    }

     @SuppressWarnings("unchecked")
    public List<UnidadMedida> findByMagnitud(MagnitudEnum magnitud)
    {
        return createQuery("FROM UnidadMedida WHERE magnitud = :magnitud AND status = :status")
                .setParameter("magnitud", magnitud)
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .getResultList();
    }
}
