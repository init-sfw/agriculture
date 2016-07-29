package ar.com.init.agros.controller.inventario.agroquimicos;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.inventario.agroquimicos.IngresoStock;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase IngresoStockJpaController
 *
 *
 * @author gmatheu
 * @version 06/07/2009 
 */
public class IngresoStockJpaController extends BaseEntityJpaController<IngresoStock> {

    /** Constructor por defecto de IngresoStockJpaController */
    public IngresoStockJpaController() {
        super(IngresoStock.class);
    }

    @Override
    public void persist(IngresoStock entity) throws InvalidStateException, ConstraintViolationException {
        throw new UnsupportedOperationException("Usar MovimientoStockJpaController");
    }

    @Override
    protected Query createFindEntitiesQuery() {
        return em.createQuery("SELECT i FROM " + IngresoStock.class.getName() + " AS  i"
                + " INNER JOIN i.detalles d "
                + " WHERE i.status = :status "
                + " AND (d.cancelado IS NULL OR d.cancelado = false)"
                + " GROUP BY i"
                + getOrderBy()).setParameter("status", BaseEntityStateEnum.ACTIVE);
    }

    @Override
    public String orderBy() {
        return " i.fecha ";
    }
}
