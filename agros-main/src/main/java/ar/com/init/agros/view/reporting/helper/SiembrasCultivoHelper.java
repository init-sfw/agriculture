package ar.com.init.agros.view.reporting.helper;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.Trabajo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.model.terreno.SubLote;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.ejb.QueryImpl;

/**
 * Clase SiembrasCultivoHelper
 *
 *
 * @author gmatheu
 * @version 24/04/2010 
 */
public class SiembrasCultivoHelper {

    private List<Siembra> siembras;

    /** Constructor por defecto de SiembrasCultivoHelper */
    public SiembrasCultivoHelper(EntityManager em, List<Campania> campanias, List<Cultivo> cultivos) {
        QueryImpl hibernateQuerySiembras = (QueryImpl) em.createNamedQuery(
                Siembra.FIND_BY_CAMPANIA_CULTIVO_NAME);
        hibernateQuerySiembras.getHibernateQuery().setParameterList(
                "campanias", campanias);
        hibernateQuerySiembras.getHibernateQuery().setParameterList("cultivos",
                cultivos);

        siembras = hibernateQuerySiembras.getResultList();
    }

    public Siembra findSiembra(List<Superficie> superficies, Campania camp) {
        for (Siembra s : siembras) {
            if (s.getCampania().equals(camp)) {
                for (Superficie supPulv : superficies) {
                    if (s.getSuperficies().contains(supPulv)) {
                        return s;
                    }
                    if (supPulv instanceof SubLote) {
                        SubLote sl = (SubLote) supPulv;
                        if (s.getSuperficies().contains(sl.getPadre())) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Cultivo findCultivo(Trabajo trabajo, Campania camp) {
        Cultivo r = null;

        Siembra siembra = findSiembra(trabajo.getSuperficies(), camp);
        Cultivo cultivo = null;
        if (siembra != null) {
            cultivo = siembra.getCultivo();
            r = cultivo;
        }

        return r;
    }

    public VariedadCultivo findVariedadCultivo(Trabajo trabajo, Campania camp) {
        VariedadCultivo r = null;

        Siembra siembra = findSiembra(trabajo.getSuperficies(), camp);
        VariedadCultivo vc = null;
        if (siembra != null) {
            vc = siembra.getVariedadCultivo();
            r = vc;
        }

        return r;
    }
}
