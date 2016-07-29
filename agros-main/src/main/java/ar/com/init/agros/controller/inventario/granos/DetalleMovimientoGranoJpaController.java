package ar.com.init.agros.controller.inventario.granos;

import ar.com.init.agros.controller.inventario.DetalleMovimientoJpaController;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.model.inventario.granos.DetalleCancelacionEgresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleCancelacionIngresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleMovimientoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleMovimientoGrano_;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase DetalleMovimientoGranoJpaController
 * 
 * 
 * @author gmatheu
 * @version 11/12/2010
 */
public class DetalleMovimientoGranoJpaController
        extends DetalleMovimientoJpaController<DetalleMovimientoGrano> {

    private static final Logger logger = Logger.getLogger(DetalleMovimientoGranoJpaController.class.getName());

    /** Constructor por defecto de DetalleMovimientoStockJpaController */
    public DetalleMovimientoGranoJpaController() {
        super(DetalleMovimientoGrano.class);
    }

    public DetalleMovimientoGranoJpaController(Class<DetalleMovimientoGrano> clazz) {
        super(clazz);
    }

    /**
     * Usar solo este metodo para guardar todo tipo de movimientos. Pues aqui se
     * verifican los niveles minimos de stock. Pues aqui se verifican los
     * niveles minimos de stock y se actualizan los valores de stock
     *
     * @param entities
     * @throws InvalidStateException
     * @throws ConstraintViolationException
     */
    @Override
    public void persist(List<DetalleMovimientoGrano> entities, EntityManager em)
            throws InvalidStateException, ConstraintViolationException {

        List<ValorAlmacenamiento> valoresDeposito = getUniqueValorAlmacenamiento(em, entities);

        for (DetalleMovimientoGrano detalle : entities) {

            ValorAlmacenamiento vd = detalle.getValor();

            vd.sumar(detalle);

            if (detalle instanceof DetalleCancelacionIngresoGrano) {
                DetalleCancelacionIngresoGrano dcig = (DetalleCancelacionIngresoGrano) detalle;
                dcig.setDetalleIngreso(em.merge(dcig.getDetalleIngreso()));
            } else if (detalle instanceof DetalleCancelacionEgresoGrano) {
                DetalleCancelacionEgresoGrano dceg = (DetalleCancelacionEgresoGrano) detalle;
                dceg.setDetalleEgreso(em.merge(dceg.getDetalleEgreso()));
            }

            if (detalle.getTipo() != null) {
                detalle.setTipo(em.merge(detalle.getTipo()));
            }

            em.persist(detalle);
        }

        for (ValorAlmacenamiento valorDeposito : valoresDeposito) {
            em.merge(valorDeposito);
        }
    }

    @SuppressWarnings("unchecked")
    public List<DetalleMovimientoGrano> findByFecha(Date date, TipoMovimientoStockEnum tipo) {
        return findByFecha(date, tipo, false);
    }

    @SuppressWarnings("unchecked")
    public List<DetalleMovimientoGrano> findByFecha(Date date, TipoMovimientoStockEnum tipo,
            boolean includeCancelados) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<DetalleMovimientoGrano> criteria = builder.createQuery(DetalleMovimientoGrano.class);
        Root<DetalleMovimientoGrano> root = criteria.from(DetalleMovimientoGrano.class);
        criteria.select(root);

        List<Predicate> where = new ArrayList<Predicate>();
        where.add(builder.equal(root.get(DetalleMovimientoGrano_.fecha), date));
        where.add(builder.equal(root.get(DetalleMovimientoGrano_.tipo), tipo.tipo()));

        if (!includeCancelados) {
            where.add(builder.or(builder.notEqual(root.get(DetalleMovimientoGrano_.cancelado), root), builder.isNull(root.get(DetalleMovimientoGrano_.cancelado))));
        }
        criteria.where(where.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<DetalleMovimientoGrano> r = qry.getResultList();

        return r;
    }

    public <T extends DetalleMovimientoGrano> List<T> findByAlmacenamiento(Almacenamiento almacenamiento, Class<T> aClass) {

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(aClass);
        Root<T> root = criteria.from(aClass);
        criteria.select(root);

        List<Predicate> where = new ArrayList<Predicate>();
        where.add(builder.equal(root.get("valor").get("almacenamiento"), almacenamiento));
        where.add(builder.or(builder.notEqual(root.get(DetalleMovimientoGrano_.cancelado), root), builder.isNull(root.get(DetalleMovimientoGrano_.cancelado))));

        criteria.where(where.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<T> r = qry.getResultList();

        return r;
    }

    public <T extends DetalleMovimientoGrano> List<T> findDetalleSemillaByAlmacenamiento(Almacenamiento almacenamiento, List<VariedadCultivo> variedades, Class<T> aClass) {

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(aClass);
        Root<T> root = criteria.from(aClass);
        criteria.select(root);

        List<Predicate> where = new ArrayList<Predicate>();
        where.add(builder.equal(root.get("valor").get("almacenamiento"), almacenamiento));
        where.add((root.get("valor").get("semilla")).in(variedades));
        where.add(builder.or(builder.equal(root.get(DetalleMovimientoGrano_.cancelado), false), builder.isNull(root.get(DetalleMovimientoGrano_.cancelado))));

        criteria.where(where.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<T> r = qry.getResultList();

        return r;
    }

    public <T extends DetalleMovimientoGrano> List<T> findDetalleCerealByAlmacenamiento(Almacenamiento almacenamiento, List<Cultivo> cultivos, Class<T> aClass) {

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(aClass);
        Root<T> root = criteria.from(aClass);
        criteria.select(root);

        List<Predicate> where = new ArrayList<Predicate>();
        where.add(builder.equal(root.get("valor").get("almacenamiento"), almacenamiento));
        where.add((root.get("valor").get("cereal")).in(cultivos));
        where.add(builder.or(builder.equal(root.get(DetalleMovimientoGrano_.cancelado), false), builder.isNull(root.get(DetalleMovimientoGrano_.cancelado))));

        criteria.where(where.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<T> r = qry.getResultList();

        return r;
    }

    public List<DetalleEgresoGrano> findDetalleEgresoGranoByServiciosYTipo(List<Servicio> servicios, TipoServicio tipoServicio)
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select o from " + DetalleEgresoGrano.class.getName() + " as o " +
                "where o.status = :status " +
                "and o.cancelado is not true " +
                "and o.servicio IN (:servicios) " +
                "and o.servicio.tipo = :tipoServicio")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("servicios", servicios)
                .setParameter("tipoServicio", tipoServicio);
        List<DetalleEgresoGrano> l = q.getResultList();
        return l;
    }

    public List<DetalleMovimientoStockAlmacenamiento> findDetalleMovimientoStockAlmacenamientoByServiciosYTipo(List<Servicio> servicios, TipoServicio tipoServicio)
    {
        List<DetalleMovimientoStockAlmacenamiento> ret = new ArrayList<DetalleMovimientoStockAlmacenamiento>();
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select o from " + DetalleIngresoGrano.class.getName() + " as o " +
                "where o.status = :status " +
                "and o.cancelado is not true " +
                "and o.tipo = :tipoMov " +
                "and o.servicio IN (:servicios) " +
                "and o.servicio.tipo = :tipoServicio")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("tipoMov", TipoMovimientoStockEnum.INGRESO.tipo())
                .setParameter("servicios", servicios)
                .setParameter("tipoServicio", tipoServicio);
        ret.addAll(q.getResultList());
        q =
                em.createQuery(
                "select o from " + DetalleIngresoStock.class.getName() + " as o " +
                "where o.status = :status " +
                "and o.cancelado is not true " +
                "and o.tipo = :tipoMov " +
                "and o.proveedor IN (:servicios) " +
                "and o.proveedor.tipo = :tipoServicio")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("tipoMov", TipoMovimientoStockEnum.INGRESO.tipo())
                .setParameter("servicios", servicios)
                .setParameter("tipoServicio", tipoServicio);
        ret.addAll(q.getResultList());
        return ret;
    }
}
