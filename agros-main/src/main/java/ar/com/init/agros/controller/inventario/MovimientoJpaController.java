package ar.com.init.agros.controller.inventario;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.email.EmailManager;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.MovimientoStockAlmacenamiento;

import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.view.configuracion.model.AlertasTableModel.Alertas;
import java.util.logging.Level;
import javax.persistence.Query;

/**
 * Clase MovimientoStockJpaController
 * 
 * 
 * @author gmatheu
 * @version 06/07/2009
 */
public class MovimientoJpaController<K extends MovimientoStockAlmacenamiento> extends BaseEntityJpaController<K> {

    private static final Logger logger = Logger.getLogger(MovimientoJpaController.class.getName());

    /** Constructor por defecto de MovimientoStockJpaController */
    public MovimientoJpaController(Class<K> clazz) {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    protected void persist(DetalleMovimientoJpaController detalleController,
            MovimientoStockAlmacenamiento movimiento, Alertas... alertas) throws Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            persist(em, detalleController, movimiento, alertas);

            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Exception while persisting", ex);
            findKnownExceptions(ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void persist(EntityManager em,
            DetalleMovimientoJpaController detalleController,
            MovimientoStockAlmacenamiento movimiento, Alertas... alertas) throws Exception {
        detalleController.persist(movimiento.getCastedDetalles(), em);

        em.persist(movimiento);

        if (alertas != null) {
            for (int i = 0; i < alertas.length; i++) {
                Alertas alerta = alertas[i];
                if (movimiento instanceof Emailable && ConfMgr.getInstance().getController().findBooleanByKey(
                        alerta.toString())) {
                    Emailable emailable = (Emailable) movimiento;
                    EmailManager.getInstance().postMail(
                            emailable.createEmailMessage());
                }
            }
        }
    }

    protected Set<ValorAlmacenamiento> anular(DetalleMovimientoJpaController detalleController,
            MovimientoStockAlmacenamiento<? extends DetalleMovimientoStockAlmacenamiento> movimiento,
            EntityManager em) {
        Set<ValorAlmacenamiento> r = new HashSet<ValorAlmacenamiento>();

        for (DetalleMovimientoStockAlmacenamiento detalleMovimiento : movimiento.getDetalles()) {
            r.addAll(detalleController.anular(detalleMovimiento, em));
        }

        return r;
    }

    @Override
    protected Query createFindEntitiesQuery() {
        return getEntityManager().createQuery(String.format("SELECT %1$s FROM " + clazz.getName() + " AS  %1$s"
                + " INNER JOIN %1$s.detalles d "
                + " WHERE %1$s.status = :status "
                + " AND (d.cancelado IS NULL OR d.cancelado = false)"
                + " GROUP BY %1$s"
                + getOrderBy(),alias()))
                .setParameter("status", BaseEntityStateEnum.ACTIVE);
    }

    @Override
    public String orderBy() {
        return String.format(" %s.fecha ", alias());
    }
}
