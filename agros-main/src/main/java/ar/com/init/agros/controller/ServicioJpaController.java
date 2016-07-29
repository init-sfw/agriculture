package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.inventario.granos.DetalleMovimientoGranoJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.Servicio_;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.view.servicios.model.ContratistaPostCosechaTableModelLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author gmatheu
 */
public class ServicioJpaController extends BaseEntityJpaController<Servicio> {

    public ServicioJpaController(boolean keepEntityManager) {
        super(Servicio.class, keepEntityManager);
    }

    public ServicioJpaController() {
        super(Servicio.class);
    }

    public List<Servicio> findByTipo(TipoServicio... tipos) {
        List<Servicio> r = new ArrayList<Servicio>();

        EntityManager em = getEntityManager();
        try {

            CriteriaBuilder builder = getCriteriaBuilder();
            CriteriaQuery<Servicio> qry = builder.createQuery(Servicio.class);
            Root<Servicio> root = qry.from(Servicio.class);
            qry.select(root);
            qry.where(root.get(Servicio_.tipo).in(Arrays.asList(tipos)));

            Query q = em.createQuery(qry);
            r.addAll(q.getResultList());

        } finally {
//            em.close();
        }


        return r;
    }

    //Buscar todos los costos que sean de tipo siembra o pulverización asociados a una lista de servicios y a un tipo
    public List<ContratistaPostCosechaTableModelLine> getSiembrasYPulverizacionesPorCampaniaYTipoServicio(Campania campania,List<Servicio> servicios, TipoServicio tipoServ)
    {
        List<ContratistaPostCosechaTableModelLine> ret = new ArrayList<ContratistaPostCosechaTableModelLine>();
        
        ret.addAll(getSiembrasPorCampaniaYTipoServicio(campania, servicios, tipoServ));
        ret.addAll(getPulverizacionesPorCampaniaYTipoServicio(campania, servicios, tipoServ));
        
        return ret;
    }

    public List<ContratistaPostCosechaTableModelLine> getPulverizacionesPorCampaniaYTipoServicio(Campania campania,List<Servicio> servicios, TipoServicio tipoServ)
    {
        TrabajoLoteJpaController trabajoController = new TrabajoLoteJpaController();
        List<ContratistaPostCosechaTableModelLine> trabajos = new ArrayList<ContratistaPostCosechaTableModelLine>();
        List<ContratistaPostCosechaTableModelLine> ret = new ArrayList<ContratistaPostCosechaTableModelLine>();

        trabajos = trabajoController.findTrabajosPorCampaniaTipoServicio(campania,servicios, tipoServ);
        return trabajos;
    }

    public List<ContratistaPostCosechaTableModelLine> getSiembrasPorCampaniaYTipoServicio(Campania campania,List<Servicio> servicios, TipoServicio tipoServ)
    {
        SiembraJpaController siembraController = new SiembraJpaController();
        List<ContratistaPostCosechaTableModelLine> siembras = new ArrayList<ContratistaPostCosechaTableModelLine>();

        siembras = siembraController.findSiembrasPorCampaniaTipoServicio(campania,servicios, tipoServ);

        return siembras;
    }

    public List<DetalleEgresoGrano> getDetallesEgresoGrano(List<Servicio> servicios,TipoServicio tipoServ)
    {
        DetalleMovimientoGranoJpaController controller = new DetalleMovimientoGranoJpaController();
        return controller.findDetalleEgresoGranoByServiciosYTipo(servicios,tipoServ);
    }

    public List<DetalleMovimientoStockAlmacenamiento> getDetallesIngresoPorServicio(List<Servicio> servicios,TipoServicio tipoServ)
    {
        DetalleMovimientoGranoJpaController controller = new DetalleMovimientoGranoJpaController();
        return controller.findDetalleMovimientoStockAlmacenamientoByServiciosYTipo(servicios,tipoServ);
    }
}
