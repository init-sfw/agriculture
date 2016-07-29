package ar.com.init.agros.controller.inventario.agroquimicos;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.ValorMonedaMedida;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.email.EmailManager;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.MovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.agroquimicos.AjusteInventario;
import ar.com.init.agros.model.inventario.agroquimicos.CancelacionIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleAjusteInventario;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleCancelacionIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoDeposito;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.model.inventario.agroquimicos.IngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.MovimientoDeposito;
import ar.com.init.agros.model.inventario.agroquimicos.MovimientoStock;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.view.configuracion.model.AlertasTableModel.Alertas;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Clase MovimientoStockJpaController
 * 
 * 
 * @author gmatheu
 * @version 06/07/2009
 */
public class MovimientoStockJpaController extends BaseEntityJpaController<MovimientoStock> {

    private Logger log = Logger.getLogger(MovimientoStockJpaController.class.getName());

    /** Constructor por defecto de MovimientoStockJpaController */
    public MovimientoStockJpaController() {
        super(MovimientoStock.class);
    }

    @SuppressWarnings("unchecked")
    private void persist(DetalleMovimientoStockJpaController detalleController,
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
            log.log(Level.SEVERE, "Exception while persisting", ex);
            findKnownExceptions(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void persist(EntityManager em,
            DetalleMovimientoStockJpaController detalleController,
            MovimientoStockAlmacenamiento movimiento, Alertas... alertas) throws Exception {
        detalleController.persist(movimiento.getCastedDetalles(), em);

        em.merge(movimiento);

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

    /**
     * Usar solo este metodo para guardar Ingresos de Stock. Pues aqui se
     * verifican los niveles minimos de stock y se actualizan los valores de
     * stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(IngresoStock entity) throws InvalidStateException,
            ConstraintViolationException, Exception {
        DetalleMovimientoStockJpaController<DetalleIngresoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleIngresoStock>(
                DetalleIngresoStock.class);

        persist(detalleController, entity, Alertas.ALERTA_INGRESO_STOCK);
    }

    /**
     * Usar solo este metodo para guardar Movimientos de Stock entre Deposito.
     * Pues aqui se verifican los niveles minimos de stock y se actualizan los
     * valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(MovimientoDeposito entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoStockJpaController<DetalleMovimientoDeposito> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoDeposito>(
                DetalleMovimientoDeposito.class);

        persist(detalleController, entity,
                Alertas.ALERTA_MOVIMIENTO_ENTRE_DEPOSITO);
    }

    /**
     * Usar solo este metodo para guardar Movimientos de Stock entre Deposito.
     * Pues aqui se verifican los niveles minimos de stock y se actualizan los
     * valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(CancelacionIngresoStock entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoStockJpaController<DetalleMovimientoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoStock>(
                DetalleMovimientoStock.class);

        persist(detalleController, entity, Alertas.ALERTA_CANCELACION_INGRESO);

        EntityManager em = getEntityManager();
        em.clear();

        Query qry = em.createQuery("SELECT d FROM " + DetalleIngresoStock.class.getName() + " AS d "
                + " WHERE d.valor.agroquimico = :agroquimico "
                + " AND d.cancelado = false "
                + " ORDER BY d.fecha DESC");
        qry.setMaxResults(1);

        for (DetalleCancelacionIngresoStock d : entity.getCastedDetalles()) {
            ValorAgroquimico vd = d.getValorDeposito();
            Agroquimico agro = vd.getAgroquimico();

            qry.setParameter("agroquimico", agro);
            try {
                DetalleIngresoStock ingreso = (DetalleIngresoStock) qry.getSingleResult();
                agro = ingreso.getAgroquimico();
                agro.setCostoActual((ValorMonedaMedida) ingreso.getCostoUnitario().clone());
            } catch (NoResultException nre) {
                agro.setCostoActual(null);
                continue;
            }
            em.merge(agro);
        }
        em.close();
    }

    /**
     * Usar solo este metodo para guardar Ajustes de Inventario. Pues aqui se
     * verifican los niveles minimos de stock y se actualizan los valores de
     * stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(AjusteInventario entity) throws InvalidStateException,
            ConstraintViolationException, Exception {
        DetalleMovimientoStockJpaController<DetalleAjusteInventario> detalleController = new DetalleMovimientoStockJpaController<DetalleAjusteInventario>(
                DetalleAjusteInventario.class);

        persist(detalleController, entity, Alertas.ALERTA_AJUSTE_INVENTARIO);
    }

    /**
     * Usar solo este metodo para guardar Movimientos "Genericos" de inventario,
     * en general de tipo OTRO. Pues aqui se verifican los niveles minimos de
     * stock y se actualizan los valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(MovimientoStockAlmacenamiento<DetalleMovimientoStockAlmacenamiento> entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoStockJpaController<DetalleMovimientoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoStock>(
                DetalleMovimientoStock.class);

        persist(detalleController, entity);
    }

    /**
     * Usar solo este metodo para guardar Movimientos "Genericos" de inventario,
     * en general de tipo OTRO. Pues aqui se verifican los niveles minimos de
     * stock y se actualizan los valores de stock. Esta version recibe el em por
     * parametro para realizar las operaciones en ese contexto.
     *
     * @param entity
     * @param em
     *            instanciado, se puede usar con una transaccion iniciada.
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(EntityManager em,
            MovimientoStock<DetalleMovimientoStock> entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoStockJpaController<DetalleMovimientoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoStock>(
                DetalleMovimientoStock.class);

        persist(em, detalleController, entity);
    }

    public Set<ValorAgroquimico> anular(
            MovimientoStock<DetalleMovimientoStock> movimientoStock,
            EntityManager em) {
        Set<ValorAgroquimico> r = new HashSet<ValorAgroquimico>();

        DetalleMovimientoStockJpaController<DetalleMovimientoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoStock>(
                DetalleMovimientoStock.class);
        for (DetalleMovimientoStock detalleMovimientoStock : movimientoStock.getDetalles()) {
            r.addAll(detalleController.anular(detalleMovimientoStock, em));
        }

        return r;
    }
}
