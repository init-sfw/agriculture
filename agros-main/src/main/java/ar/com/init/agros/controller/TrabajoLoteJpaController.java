package ar.com.init.agros.controller;

import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.controller.exceptions.transacciones.CampaniaCerradaException;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.costo.Costo;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleMovimientoStock;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.view.servicios.model.ContratistaPostCosechaTableModelLine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase TrabajoLoteJpaController
 * 
 * 
 * @author fbobbio
 * @version 02-ago-2009
 */
public class TrabajoLoteJpaController extends
		BaseCampaniaTransactionJpaController<Trabajo> {

	private Logger log = Logger.getLogger(BaseEntityJpaController.class
			.getName());

	/** Constructor por defecto de AgroquimicoJpaController */
	public TrabajoLoteJpaController() {
		super(Trabajo.class);
	}

	@Override
	public void persistOrUpdate(Trabajo trabajo) throws InvalidStateException,
			ConstraintViolationException, Exception {
		EntityManager em = null;
		Set<ValorAgroquimico> valoresDeposito = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();

			List<DetalleMovimientoStockAlmacenamiento> toRemove = null;
			if (trabajo.getIdTransaccion() == null) {
				long nextId = getMaxValue("idTransaccion") + 1;
				trabajo.setIdTransaccion(nextId);
			} else {
				Trabajo old = find(trabajo.getId());

				MovimientoStockJpaController movController = new MovimientoStockJpaController();
				valoresDeposito = movController.anular(
						old.getMovimientoStock(), em);
				toRemove = new ArrayList<DetalleMovimientoStockAlmacenamiento>(old
						.getMovimientoStock().getDetalles());
			}

                        trabajo.setDetallesPersistidos();
			trabajo.realizarTrabajo();

			// Reemplazar valoresDeposito originales con valoresDepositos
			// anulados
			if (valoresDeposito != null) {
				for (Iterator<DetalleMovimientoStock> iterator = trabajo.getMovimientoStock()
						.getDetalles().iterator(); iterator.hasNext();) {
					DetalleMovimientoStock dms =  iterator.next();

					if(valoresDeposito.contains(dms.getValorDeposito()))
					{
						for (Iterator<ValorAgroquimico> iterator2 = valoresDeposito.iterator(); iterator2
								.hasNext();) {
							ValorAgroquimico vd = iterator2.next();
							
							if(vd.equals(dms.getValorDeposito()))
							{
								vd = em.merge(vd);
								dms.setValorDeposito(vd);
							}
							
						}
					}
				}
			}
			//

			MovimientoStockJpaController movimientoJpaController = new MovimientoStockJpaController();
			movimientoJpaController.persist(em, trabajo.getMovimientoStock());

			trabajo = em.merge(trabajo);			

			em.getTransaction().commit();
			
//			em.getTransaction().begin();
//			if (toRemove != null) {
//				for (DetalleMovimientoStock dms : toRemove) {
//					dms = em.merge(dms);
//					em.remove(dms);
//				}
//			}
//			em.getTransaction().commit();
			
			UpdatableSubject.notifyListeners();
			log.fine(trabajo.toString());
		} catch (InvalidStateException ise) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw ise;
		} catch (Exception ex) {
			log.log(Level.WARNING, "Exception while persisting Trabajo", ex);
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			String msg = ex.getLocalizedMessage();
			findKnownExceptions(ex);
			if (msg == null || msg.length() == 0) {
				UUID id = trabajo.getUUID();
				if (find(id) == null) {
					throw new NonExistentEntityException("The "
							+ clazz.getName() + " with id " + id
							+ " no longer exists.");
				}
			}
			throw ex;
		} finally {
			if (em != null) {
				// em.close();
			}
		}
	}

	@Override
	public void delete(Trabajo entity) throws NonExistentEntityException,
			InvalidStateException, ConstraintViolationException,
			CampaniaCerradaException, Exception {
		if (!entity.getCampania().isCerrada()) {
			EntityManager em = null;
			try {
				em = getEntityManager();
				em.getTransaction().begin();

				entity = em.merge(entity);
				MovimientoStockJpaController movController = new MovimientoStockJpaController();
				movController.anular(entity.getMovimientoStock(), em);

				em.remove(entity);

				em.getTransaction().commit();
				UpdatableSubject.notifyListeners();
				log.fine(entity.toString());
			} catch (Exception ex) {

				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				log.log(Level.INFO, null, ex);
			} finally {
				if (em != null) {
					// em.close();
				}
			}
		} else {
			throw new CampaniaCerradaException(
					"Imposible eliminar transacción, la campaña asociada ha sido cerrada");
		}
	}

    public List<Trabajo> findTrabajosPorCampania(Campania campania)
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status and o.campania = :campania").setParameter(
                "status", BaseEntityStateEnum.ACTIVE).setParameter("campania", campania);
        return q.getResultList();
    }

    public List<ContratistaPostCosechaTableModelLine> findTrabajosPorCampaniaTipoServicio(Campania campania,List<Servicio> servicios, TipoServicio tipoServicio)
    {
        String costos = "costos";
        if (tipoServicio.equals(TipoServicio.PROVEEDOR_POST_COSECHA))
        {
            costos = "costosPostCosecha";
        }
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select o,c from " + clazz.getName() + " as o " +
                "inner join o." + costos + "  c " +
                "where o.status = :status " +
                "and o.campania = :campania " +
                "and c.servicio IN (:servicios) " +
                "and c.servicio.tipo = :tipoServicio")
                .setParameter("status", BaseEntityStateEnum.ACTIVE)
                .setParameter("campania", campania)
                .setParameter("servicios", servicios)
                .setParameter("tipoServicio", tipoServicio);
        List result = q.getResultList();

        List<ContratistaPostCosechaTableModelLine> ret = new ArrayList<ContratistaPostCosechaTableModelLine>();
        for (int i = 0; i < result.size(); i++)
        {
            Trabajo t = (Trabajo)((Object[])result.get(i))[0];
            Costo c = (Costo)((Object[])result.get(i))[1];
            ContratistaPostCosechaTableModelLine l = new ContratistaPostCosechaTableModelLine(
                    t,
                    c);
            ret.add(l);
        }
        return ret;
    }
}
