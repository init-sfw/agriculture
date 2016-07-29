package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.almacenamiento.ValorSemilla_;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.ejb.HibernateQuery;

/**
 * Clase VariedadCultivoJpaController
 *
 *
 * @author fbobbio
 * @version 18-jul-2009 
 */
public class VariedadCultivoJpaController extends BaseEntityJpaController<VariedadCultivo> {

    /** Constructor por defecto de VariedadCultivoJpaController */
    public VariedadCultivoJpaController() {
        super(VariedadCultivo.class);
    }

    public boolean exists(VariedadCultivo var) {
        List<VariedadCultivo> list = findEntities();
        for (VariedadCultivo d : list) {
            if (var.equalToEntity(d)) {
                return true;
            }
        }
        return false;
    }

    public List<VariedadCultivo> findByAlmacenamiento(List<Almacenamiento> almacenamientos) {

        List<VariedadCultivo> r = null;
        if (!almacenamientos.isEmpty()) {
            EntityManager em = getEntityManager();
            try {

                CriteriaBuilder builder = getCriteriaBuilder();
                CriteriaQuery<VariedadCultivo> criteria = builder.createQuery(VariedadCultivo.class);
                Root<ValorSemilla> root = criteria.from(ValorSemilla.class);
                criteria.select(root.get(ValorSemilla_.semilla));
                criteria.where(root.get(ValorSemilla_.almacenamiento).in(Arrays.asList(almacenamientos)));

                Query q = em.createQuery(criteria);
                r = q.getResultList();

                r = filterNoUsados(r, almacenamientos);

            } finally {
//            em.close();
            }
        } else {
            r = new ArrayList<VariedadCultivo>();
        }
        return r;
    }

    private List<VariedadCultivo> filterNoUsados(List<VariedadCultivo> variedades, List<Almacenamiento> almacenamientos) throws HibernateException {
        List<VariedadCultivo> r = new ArrayList<VariedadCultivo>();

        if (!variedades.isEmpty()) {
            String hql = "SELECT DISTINCT(dis.valor.semilla) "
                    + " FROM " + DetalleIngresoSemilla.class.getName() + " dis"
                    + " WHERE (dis.cancelado = false OR dis.cancelado IS NULL) "
                    + " AND dis.valor.almacenamiento IN (:almacenamientos)"
                    + " AND dis.valor.semilla IN (:variedades)";
            HibernateQuery qry = createHibernateQuery(hql);
            qry.getHibernateQuery().setParameterList("almacenamientos", almacenamientos);
            qry.getHibernateQuery().setParameterList("variedades", variedades);
            r = qry.getResultList();
        }
        return r;
    }
}
