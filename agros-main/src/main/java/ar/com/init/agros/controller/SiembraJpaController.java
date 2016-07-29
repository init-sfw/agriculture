package ar.com.init.agros.controller;

import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.controller.exceptions.transacciones.CampaniaCerradaException;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.RendimientoSuperficie;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.costo.Costo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.view.servicios.model.ContratistaPostCosechaTableModelLine;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase SiembraJpaController
 * 
 *
 * @author fbobbio
 * @version 06-jul-2009 
 */
public class SiembraJpaController extends BaseCampaniaTransactionJpaController<Siembra>
{

    private Logger log = Logger.getLogger(SiembraJpaController.class.getName());

    /** Constructor por defecto de AgroquimicoJpaController */
    public SiembraJpaController()
    {
        super(Siembra.class);
    }

    /** Método que setea la siembra a cada una de los rendimientos */
    public void updateRendimientosSuperficies(Siembra siembra)
    {
        for (RendimientoSuperficie s : siembra.getRendimientoSuperficies())
        {
            s.setSiembra(siembra);
        }
    }

    @Override
    public void delete(Siembra entity) throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, CampaniaCerradaException, Exception
    {
        Campania c = entity.getCampania();
        super.delete(entity);
        c.actualizarBonificaciones();
    }

    @Override
    protected void delete(Siembra entity, EntityManager em) throws NonExistentEntityException, InvalidStateException, ConstraintViolationException, CampaniaCerradaException, Exception
    {
        Campania c = entity.getCampania();
        super.delete(entity, em);
        c.actualizarBonificaciones();
    }

    @Override
    public void persistOrUpdate(Siembra siembra) throws InvalidStateException, ConstraintViolationException, Exception
    {
        updateRendimientosSuperficies(siembra);
        siembra.calcularCostosDeSiembra();

        super.persistOrUpdate(siembra);
        siembra.getCampania().actualizarBonificaciones();
    }

    public Campania persistOrUpdateWithCampania(Campania c) throws InvalidStateException, ConstraintViolationException, Exception
    {
        EntityManager em = getEntityManager();
        try
        {
            em.getTransaction().begin();
            for (Siembra s : c.getSiembras())
            {
                s = em.merge(s);
            }
            c = em.merge(c);
            c.cerrar();
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            return c;
        }
        catch (Exception ex)
        {
            if (em != null && em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
                log.info("Exception while persisting");
                ex.printStackTrace();
            }
            throw ex;
        }
    }

    public Cultivo findCultivoDeSuperficieEnCampania(Superficie sup, Campania campania)
    {
        List<Siembra> siembras = findSiembrasPorCampania(campania);
        for (Siembra s : siembras)
        {
            for (Superficie superficie : s.getSuperficies())
            {
                if (superficie.contiene(sup))
                {
                    return s.getCultivo();
                }
            }
        }
        return null;
    }

    public List<TipoCosto> findTipoCostosDeSiembras()
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select cos.tipoCosto from " + Siembra.class.getName() + " as s " +
                " INNER JOIN s.costos cos WHERE s.status = :status AND cos.status = :status").setParameter(
                "status", BaseEntityStateEnum.ACTIVE);
        return q.getResultList();
    }

    public List<Siembra> findSiembrasPorCampania(Campania campania)
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(o) from " + clazz.getName() + " as o where o.status = :status and o.campania = :campania").setParameter(
                "status", BaseEntityStateEnum.ACTIVE).setParameter("campania", campania);
        return q.getResultList();
    }

    public List<ContratistaPostCosechaTableModelLine> findSiembrasPorCampaniaTipoServicio(Campania campania,List<Servicio> servicios, TipoServicio tipoServicio)
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
            Siembra s = (Siembra)((Object[])result.get(i))[0];
            Costo c = (Costo)((Object[])result.get(i))[1];
            ContratistaPostCosechaTableModelLine l = new ContratistaPostCosechaTableModelLine(
                    s,
                    c);
            ret.add(l);
        }
        return ret;
    }

    public List<Campania> findCampaniasPendientesDeCierre()
    {
        CampaniaJpaController campaniaController = new CampaniaJpaController();
        return campaniaController.findCampaniasPendientesDeCierre();
    }

    public List<Campania> findCampaniasCerradas()
    {
        CampaniaJpaController campaniaController = new CampaniaJpaController();
        return campaniaController.findCampaniasCerradas();
    }

    @Override
    public boolean exists(Siembra siembra)
    {
        List<Siembra> list = findEntities();
        for (Siembra p : list)
        {
            if (siembra.equalToEntity(p))
            {
                return true;
            }
        }
        return false;
    }
}