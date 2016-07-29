package ar.com.init.agros.controller.inventario.agroquimicos;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.EmailManager;
import ar.com.init.agros.model.util.EMailMessage;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico_;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleCancelacionIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoDeposito;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock_;
import ar.com.init.agros.view.configuracion.model.AlertasTableModel.Alertas;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Clase DetalleMovimientoStockJpaController
 * 
 * 
 * @author gmatheu
 * @version 15/06/2009
 */
public class DetalleMovimientoStockJpaController<K extends DetalleMovimientoStock>
        extends BaseEntityJpaController<K> {

    private static final Logger log = Logger.getLogger(DetalleMovimientoStockJpaController.class.getName());

    /** Constructor por defecto de DetalleMovimientoStockJpaController */
    public DetalleMovimientoStockJpaController(Class<K> clazz) {
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
    public void persist(List<K> entities, EntityManager em)
            throws InvalidStateException, ConstraintViolationException {

        List<ValorAgroquimico> valoresDeposito = getUniqueValorAgroquimico(entities, em);

        Set<ValorAgroquimico> valoresDepositoMinimo = new HashSet<ValorAgroquimico>();
        for (K detalle : entities) {

            ValorAgroquimico vd = detalle.getValor();

            vd.sumar(detalle);

            if (!vd.isMayorMinimo()) {
                valoresDepositoMinimo.add(vd);
            }

            if (detalle instanceof DetalleMovimientoDeposito) {
                DetalleMovimientoDeposito dmd = (DetalleMovimientoDeposito) detalle;
                int idx2 = valoresDeposito.indexOf(dmd.getValorDepositoOrigen());
                ValorAgroquimico vdOrigen = valoresDeposito.get(idx2);//dmd.getValorDepositoOrigen();
                vdOrigen.restar(detalle);

                if (!vdOrigen.isMayorMinimo()) {
                    valoresDepositoMinimo.add(vdOrigen);
                }
            } else if (detalle instanceof DetalleCancelacionIngresoStock) {
                DetalleCancelacionIngresoStock dcsi = (DetalleCancelacionIngresoStock) detalle;
                dcsi.setDetalleIngreso(em.merge(dcsi.getDetalleIngreso()));
            }

            if (detalle.getTipo() != null) {
                detalle.setTipo(em.merge(detalle.getTipo()));
            }

            em.persist(detalle);
        }

        for (ValorAgroquimico valorDeposito : valoresDeposito) {
            em.merge(valorDeposito);
        }

        if (valoresDepositoMinimo.size() > 0 && ConfMgr.getInstance().getController().findBooleanByKey(
                Alertas.ALERTA_STOCK_MINIMO.toString())) {
            EmailManager.getInstance().postMail(
                    createAlertaStockMinimo(valoresDepositoMinimo));
        }
    }

    protected List<ValorAgroquimico> getUniqueValorAgroquimico(List<K> entities, EntityManager em) {
        List<ValorAgroquimico> valoresDeposito = new ArrayList<ValorAgroquimico>();
        for (K detalle : entities) {
            ValorAgroquimico vd = detalle.getValor();
            if (!valoresDeposito.contains(vd)) {
                vd = em.merge(vd);
                valoresDeposito.add(vd);
            }
            int idx = valoresDeposito.indexOf(vd);
            detalle.setValor(valoresDeposito.get(idx));
            if (detalle instanceof DetalleMovimientoDeposito) {
                DetalleMovimientoDeposito dmd = (DetalleMovimientoDeposito) detalle;
                ValorAgroquimico vdOrigen = dmd.getValorDepositoOrigen();
                if (!valoresDeposito.contains(vdOrigen)) {
                    vdOrigen = em.merge(vdOrigen);
                    valoresDeposito.add(vdOrigen);
                }
                int idxOrigen = valoresDeposito.indexOf(vdOrigen);
                dmd.setValorDepositoOrigen(valoresDeposito.get(idxOrigen));
            }
        }
        return valoresDeposito;
    }

    /**
     * Anula el efecto del detalle pasado por parametro para el valorDeposito
     * correspondiente al agroquimico y deposito.
     *
     * @param entity
     * @param em
     */
    public Set<ValorAgroquimico> anular(K entity, EntityManager em) {
        Set<ValorAgroquimico> r = new HashSet<ValorAgroquimico>();
        ValorAgroquimico vd = entity.getValorDeposito();

        vd.restar(entity);

        if (entity instanceof DetalleMovimientoDeposito) {
            DetalleMovimientoDeposito dmd = (DetalleMovimientoDeposito) entity;
            ValorAgroquimico vdOrigen = dmd.getValorDepositoOrigen();
            vdOrigen.sumar(entity);
        }

        vd = em.merge(vd);
        r.add(vd);
        return r;
    }

//    public void ajustarAutomaticamente()
//    {
//        log.info("Realizando Ajustes Automaticos");
//        long t = System.currentTimeMillis();
//
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            @SuppressWarnings("unchecked")
//            List<ValorAgroquimico> valores = (List<ValorAgroquimico>) em.createQuery(
//                    "FROM " + ValorAgroquimico.class.getName()).getResultList();
//
//            HashMap<ValorAgroquimico, ValorUnidad> valoresDetalles = new HashMap<ValorAgroquimico, ValorUnidad>();
//
//            for (ValorAgroquimico vd : valores) {
//                ValorUnidad valorDetalles = new ValorUnidad(0D, vd.getStockActual().getUnidad());
//                valoresDetalles.put(vd, valorDetalles);
//            }
//
//            for (ValorAgroquimico vd : valores) {
//                ValorUnidad valorDetalles = valoresDetalles.get(vd);
//
//                for (DetalleMovimientoStock dm : vd.getMovimientos()) {
//                    valorDetalles.sumar(dm.getCantidadRelativa(valorDetalles.getUnidad()));
//
//                    if (dm instanceof DetalleMovimientoDeposito) {
//                        DetalleMovimientoDeposito dmd = (DetalleMovimientoDeposito) dm;
//                        ValorAgroquimico vdOrigen = dmd.getValorDepositoOrigen();
//
//                        ValorUnidad valorDetallesOrigen = valoresDetalles.get(vdOrigen);
//
//                        valorDetallesOrigen.sumar(-dmd.getCantidadRelativa(valorDetalles.getUnidad()));
//                    }
//                }
//            }
//
//            for (Entry<ValorAgroquimico, ValorUnidad> entry : valoresDetalles.entrySet()) {
//                ValorAgroquimico vd = entry.getKey();
//                ValorUnidad valorDetalles = entry.getValue();
//
//                if (!vd.getStockActual().equals(valorDetalles)) {
//                    log.log(
//                            Level.INFO, "Diferencia de Inventario: Actual {0} || Calculado: {1}", new Object[]{vd.getStockActual().toString(), valorDetalles.toString()});
//                }
//            }
//
//            em.getTransaction().commit();
//        }
//        catch (Exception ex) {
//
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            log.log(Level.INFO, null, ex);
//        }
//        finally {
//            if (em != null) {
//                // em.close();
//            }
//        }
//
//        log.log(Level.INFO, "Ajustes automaticos realizados: {0} ms", (System.currentTimeMillis() - t));
//    }
    public EMailMessage createAlertaStockMinimo(
            Set<ValorAgroquimico> valoresDeposito) {
        Calendar date = Calendar.getInstance();

        EmailHelper helper = new EmailHelper();
        helper.writeLogo();
        helper.writeTitle("Alertas de Stock");
        helper.writeBlankLine();
        helper.writeLine("Se han detectado niveles de stock inferiores a los permitidos.");
        helper.writeBlankLine();
        helper.writeLine("Fecha: " + EmailHelper.formatDate(date.getTime()));
        helper.writeBlankLine();

        helper.writeLine("Detalle:");
        helper.writeBlankLine();

        helper.writeTable(ValorAgroquimico.TABLE_HEADERS, valoresDeposito.iterator());

        helper.writeBlankLine();
        helper.writeBlankLine();

        EMailMessage r = new EMailMessage(
                "Alertas de Stock: Nivel Mínimo Alcanzado - " + EmailHelper.formatDate(date.getTime()),
                helper.buildContent());

        return r;
    }

    @SuppressWarnings("unchecked")
    public List<K> findByFecha(Date date, TipoMovimientoStockEnum tipo) {
        return createQuery(
                "FROM " + DetalleMovimientoStock.class.getName()
                + " WHERE fecha = :fecha AND tipo = :tipo AND (cancelado <> :cancelado OR cancelado IS NULL)").setParameter("fecha", date).setParameter("tipo", tipo.tipo()).setParameter("cancelado", true).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<K> findByFecha(Date date, TipoMovimientoStockEnum tipo,
            boolean cancelados) {
        return createQuery(
                "FROM " + DetalleMovimientoStock.class.getName()
                + " WHERE fecha = :fecha AND tipo = :tipo").setParameter("fecha", date).setParameter("tipo", tipo.tipo()).getResultList();
    }

    public List<DetalleMovimientoDeposito> find(ValorAgroquimico vd) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<DetalleMovimientoStock> criteria = builder.createQuery(DetalleMovimientoStock.class);
        Root<DetalleMovimientoStock> root = criteria.from(DetalleMovimientoStock.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(DetalleMovimientoStock_.valor).get(ValorAgroquimico_.agroquimico), vd.getAgroquimico()));
        predicates.add(builder.equal(root.get(DetalleMovimientoStock_.valor).get(ValorAgroquimico_.almacenamiento), vd.getAlmacenamiento()));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);

//        String hql = String.format("SELECT d FROM %s AS d ", DetalleMovimientoStock.class.getName());
//        hql += " WHERE d.valor = :valor ";
//
//        Query qry = createQuery(hql);
//        qry.setParameter("valor", vd);
        List<DetalleMovimientoDeposito> r = qry.getResultList();

        return r;
    }
}
