package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.BaseCampaniaTransaction;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.PlanificacionAgroquimico;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.base.BaseEntityStateEnum;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.DivisionCampo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.ejb.QueryImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase CampoJpaController
 *
 *
 * @author fbobbio
 * @version 19-ago-2009 
 */
public class CampoJpaController extends BaseEntityJpaController<Campo>
{

    private Logger log = Logger.getLogger(CampoJpaController.class.getName());

    /** Constructor por defecto de CampoJpaController */
    public CampoJpaController()
    {
        super(Campo.class);
    }

    public boolean isNombreRepetido(Campo entity)
    {
        EntityManager em = getEntityManager();
        Query q =
                em.createQuery(
                "select object(c) from " + Campo.class.getName() + " as c " +
                "WHERE c.status = :status " +
                " AND c.id != (:id) " +
                " AND c.nombre = (:nombre)").setParameter("status", BaseEntityStateEnum.ACTIVE).setParameter(
                "id", entity.getId()).setParameter("nombre", entity.getNombre());
        return q.getResultList().size() > 0;
    }

    @Override
    public void persistOrUpdate(Campo entity) throws InvalidStateException, ConstraintViolationException, Exception
    {
        super.persistOrUpdate(entity);
    }

    /** Método que marca como inactivo el campo viejo y guarda el nuevo
     * 
     * @param oldCampo
     * @param newCampo
     */
    public void modifyCampo(Campo oldCampo, Campo newCampo)
    {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            oldCampo.setStatus(BaseEntityStateEnum.INACTIVE);
             // A partir de la versión 1.2 se empieza a registrar esta fecha de modificación del campo
            oldCampo.setFechaModificacion(new Date());
            for (Lote l : oldCampo.getLotes()) {
                for (SubLote s : l.getSubLotes()) {
                    s.setStatus(BaseEntityStateEnum.INACTIVE);
                }
                l.setStatus(BaseEntityStateEnum.INACTIVE);
            }
            oldCampo = em.merge(oldCampo);
            em.persist(newCampo);
            em.getTransaction().commit();
            UpdatableSubject.notifyListeners();
            log.fine(newCampo.toString());
        }
        catch (Exception ex) {
            findKnownExceptions(ex);
        }
        finally {
            if (em != null) {
//                em.close();
            }
        }
    }

    public List<Campo> findByCampanias(List<Campania> campanias, boolean useTrabajos, boolean usePlanificaciones, boolean useSiembras)
    {
        Set<Campo> campos = new HashSet<Campo>();

        if (useTrabajos) {
            campos.addAll(findByTrabajos(campanias));
        }
        if (usePlanificaciones) {
            campos.addAll(findByPlanificaciones(campanias));
        }
        if (useSiembras) {
            campos.addAll(findBySiembras(campanias));
        }

        return cargarCampaniasAsociadasACampos(new ArrayList<Campo>(campos));
    }

    @SuppressWarnings("unchecked")
    public List<Campo> findByTrabajos(List<Campania> campanias)
    {
        String query =
                " SELECT DISTINCT t.campo " +
                " FROM " + Trabajo.class.getName() + " AS t " +
                " WHERE t.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Campo>) hibernateQuery
        .getHibernateQuery().setParameterList("campanias", campanias)
        .list();
    }

    @SuppressWarnings("unchecked")
    public List<Campo> findByPlanificaciones(List<Campania> campanias)
    {
        String query = "SELECT DISTINCT p.campo " +
                " FROM " + PlanificacionAgroquimico.class.getName() + " AS p " +
                " WHERE p.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Campo>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    @SuppressWarnings("unchecked")
    public List<Campo> findBySiembras(List<Campania> campanias)
    {
        String query = " SELECT DISTINCT s.campo " +
                " FROM " + Siembra.class.getName() + " AS s " +
                " WHERE s.campania IN (:campanias) ";

        QueryImpl hibernateQuery = (QueryImpl) createQuery(query);
        return (List<Campo>) hibernateQuery.getHibernateQuery().setParameterList("campanias", campanias).list();
    }

    /** Método que carga las campanias asociadas a varios campo */
    public List<Campo> cargarCampaniasAsociadasACampos(List<Campo> campos)
    {
        for (Campo c: campos)
        {
            cargarCampaniasAsociadasACampo(c);
        }
        return campos;
    }

    /** Método que carga las campanias asociadas a un campo */
    public Campo cargarCampaniasAsociadasACampo(Campo campo)
    {
        campo.setInfo(this.listarCampaniasAsociadasACampo(campo));
        return campo;
    }

    /** Método que lista las campañas asociadas a un campo */
    private String listarCampaniasAsociadasACampo(Campo campo)
    {
        StringBuffer buf = new StringBuffer();
        BaseCampaniaTransactionJpaController cont = new BaseCampaniaTransactionJpaController(BaseCampaniaTransaction.class);
        Set<Campania> camps = cont.findCampaniasAsociadasACampo(campo);
        List<Campania> campanias = new ArrayList<Campania>(camps);
        for (Campania c : campanias)
        {
            if (c.equals(campanias.get(0)))
            {
                buf.append(c.getNombre());
            }
            else
            {
                buf.append(" - " + c.getNombre());
            }
        }
        return buf.toString();
    }

    @Override
    public List<Campo> findAllEntities()
    {
        return cargarCampaniasAsociadasACampos(super.findAllEntities());
    }

    @Override
    protected List<Campo> findEntities(boolean all, int maxResults, int firstResult)
    {
        return cargarCampaniasAsociadasACampos(super.findEntities(all, maxResults, firstResult));
    }

    public void refreshEntity(DivisionCampo division)
    {
        this.getEntityManager().refresh(division);
    }
}
