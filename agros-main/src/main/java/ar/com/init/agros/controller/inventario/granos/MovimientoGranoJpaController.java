package ar.com.init.agros.controller.inventario.granos;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

import ar.com.init.agros.controller.inventario.MovimientoJpaController;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.MovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.model.inventario.agroquimicos.MovimientoStock;
import ar.com.init.agros.model.inventario.granos.CancelacionEgresoGrano;
import ar.com.init.agros.model.inventario.granos.CancelacionIngresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleMovimientoGrano;
import ar.com.init.agros.model.inventario.granos.EgresoGrano;
import ar.com.init.agros.model.inventario.granos.IngresoGrano;
import ar.com.init.agros.model.inventario.granos.MovimientoGrano;
import ar.com.init.agros.view.configuracion.model.AlertasTableModel.Alertas;
import java.util.Set;

/**
 * Clase MovimientoGranoJpaController
 * 
 * 
 * @author gmatheu
 * @version 11/12/2010
 */
public class MovimientoGranoJpaController extends MovimientoJpaController<MovimientoGrano> {

    private static final Logger logger = Logger.getLogger(MovimientoGranoJpaController.class.getName());

    /** Constructor por defecto de MovimientoGranoJpaController */
    public MovimientoGranoJpaController() {
        super(MovimientoGrano.class);
    }

    public MovimientoGranoJpaController(Class clazz) {
        super(clazz);
    }

    /**
     * Usar solo este metodo para guardar Ingresos de Granos. Pues aqui
     * se actualizan los valores de
     * stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(IngresoGrano entity) throws InvalidStateException,
            ConstraintViolationException, Exception {
        DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(detalleController, entity, Alertas.ALERTA_INGRESO_GRANO);
    }

    /**
     * Usar solo este metodo para guardar Ingresos de Granos. Pues aqui
     * se actualizan los valores de
     * stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(EgresoGrano entity) throws InvalidStateException,
            ConstraintViolationException, Exception {
        DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(detalleController, entity, Alertas.ALERTA_EGRESO_GRANO);
    }

    /**
     * Usar solo este metodo para guardar Cancelacion de Ingresos de granos.
     * Pues aqui se actualizan los
     * valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    public void persist(CancelacionIngresoGrano entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(detalleController, entity, Alertas.ALERTA_CANCELACION_INGRESO_GRANO);
    }

    public void persist(CancelacionEgresoGrano entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
                DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(detalleController, entity, Alertas.ALERTA_CANCELACION_EGRESO_GRANO);
    }

    /**
     * Usar solo este metodo para guardar Movimientos "Genericos" de granos,
     * en general de tipo OTRO. Pues aqui se actualizan los valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    protected void persist(MovimientoStockAlmacenamiento<DetalleMovimientoStockAlmacenamiento> entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(detalleController, entity);
    }

    /**
     * Usar solo este metodo para guardar Movimientos "Genericos" de granos,
     * en general de tipo OTRO. Pues aqui
     * se actualizan los valores de stock. Esta version recibe el em por
     * parametro para realizar las operaciones en ese contexto.
     *
     * @param entity
     * @param em
     *            instanciado, se puede usar con una transaccion iniciada.
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    protected void persist(EntityManager em,
            MovimientoGrano<DetalleMovimientoGrano> entity)
            throws InvalidStateException, ConstraintViolationException,
            Exception {
        DetalleMovimientoGranoJpaController detalleController = new DetalleMovimientoGranoJpaController();

        persist(em, detalleController, entity);
    }

    protected Set<ValorAlmacenamiento> anular(
            MovimientoStock<DetalleMovimientoStock> movimientoStock,
            EntityManager em) {

        return super.anular(new DetalleMovimientoGranoJpaController(), movimientoStock, em);
    }
}
