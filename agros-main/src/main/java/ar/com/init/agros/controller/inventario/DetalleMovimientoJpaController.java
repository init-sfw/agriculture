package ar.com.init.agros.controller.inventario;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase DetalleMovimientoStockJpaController
 * 
 * 
 * @author gmatheu
 * @version 15/06/2009
 */
public abstract class DetalleMovimientoJpaController<K extends DetalleMovimientoStockAlmacenamiento>
        extends BaseEntityJpaController<K> {

    private static final Logger log = Logger.getLogger(DetalleMovimientoJpaController.class.getName());

    /** Constructor por defecto de DetalleMovimientoStockJpaController */
    public DetalleMovimientoJpaController(Class<K> clazz) {
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
    public abstract void persist(List<K> entities, EntityManager em)
            throws InvalidStateException, ConstraintViolationException;

    /**
     * Anula el efecto del detalle pasado por parametro para el valorDeposito
     * correspondiente al grano y almacenamiento.
     *
     * @param entity
     * @param em
     */
    public Set<ValorAlmacenamiento> anular(K entity, EntityManager em) {
        Set<ValorAlmacenamiento> r = new HashSet<ValorAlmacenamiento>();
        ValorAlmacenamiento vd = entity.getValor();

        vd.restar(entity);

        vd = em.merge(vd);
        r.add(vd);
        return r;
    }

    protected List<ValorAlmacenamiento> getUniqueValorAlmacenamiento(EntityManager em, List<K> entities) {
        List<ValorAlmacenamiento> valoresAlm = new ArrayList<ValorAlmacenamiento>();
        for (K detalle : entities) {
            ValorAlmacenamiento vd = detalle.getValor();
            if (!valoresAlm.contains(vd)) {
                valoresAlm.add(em.merge(vd));
            }

            int idx = valoresAlm.indexOf(vd);
            detalle.setValor(valoresAlm.get(idx));
        }
        return valoresAlm;
    }
}
