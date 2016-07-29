package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.controller.base.ReadOnlyBaseEntityJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.ValorUnidad_;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico_;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.almacenamiento.ValorCereal_;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.almacenamiento.ValorSemilla_;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author gmatheu
 */
public class ValorAlmacenamientoJpaController<T extends ValorAlmacenamiento> extends ReadOnlyBaseEntityJpaController<T> {

    public ValorAlmacenamientoJpaController(Class<T> clazz) {
        super(clazz);
    }

    public ValorAgroquimico find(Deposito deposito, Agroquimico agroquimico) {
        List<ValorAgroquimico> r = find(deposito, new Agroquimico[]{agroquimico});

        if (!r.isEmpty()) {
            return r.get(0);
        }

        return null;
    }

    public List<ValorAgroquimico> find(Deposito deposito, Agroquimico... agroquimicos) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ValorAgroquimico> criteria = builder.createQuery(ValorAgroquimico.class);
        Root<ValorAgroquimico> root = criteria.from(ValorAgroquimico.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(ValorAgroquimico_.almacenamiento), deposito));
        predicates.add(root.get(ValorAgroquimico_.agroquimico).in(Arrays.asList(agroquimicos)));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<ValorAgroquimico> r = qry.getResultList();

        return r;
    }

    public ValorCereal find(Silo silo, Cultivo cultivo) {
        List<ValorCereal> r = find(silo, new Cultivo[]{cultivo});

        if (!r.isEmpty()) {
            return r.get(0);
        }

        return new ValorCereal(cultivo, silo, MagnitudEnum.PESO.patron());
    }

    public List<ValorCereal> find(Silo silo, Cultivo... cultivos) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ValorCereal> criteria = builder.createQuery(ValorCereal.class);
        Root<ValorCereal> root = criteria.from(ValorCereal.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(ValorCereal_.almacenamiento), silo));
        predicates.add(root.get(ValorCereal_.cereal).in(Arrays.asList(cultivos)));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<ValorCereal> r = qry.getResultList();

        return r;
    }

    public <T extends Almacenamiento> ValorSemilla<T> find(T almacenamiento, VariedadCultivo variedad) {
        List<ValorSemilla<T>> r = find(almacenamiento, new VariedadCultivo[]{variedad});

        if (!r.isEmpty()) {
            return r.get(0);
        }

        return new ValorSemilla<T>(variedad, almacenamiento, MagnitudEnum.PESO.patron());
    }

    public <T extends Almacenamiento> List<ValorSemilla<T>> find(T almacenamiento, VariedadCultivo... variedades) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ValorSemilla> criteria = builder.createQuery(ValorSemilla.class);
        Root<ValorSemilla> root = criteria.from(ValorSemilla.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(ValorSemilla_.almacenamiento), almacenamiento));
        predicates.add(root.get(ValorSemilla_.semilla).in(Arrays.asList(variedades)));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = createQuery(criteria);
        List<ValorSemilla<T>> r = qry.getResultList();

        return r;
    }

    public ValorUnidad calcularExistenciaTotal(Cultivo cultivo) {
        ValorUnidad vu = null;

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Double> criteria = builder.createQuery(Double.class);
        Root<ValorCereal> root = criteria.from(ValorCereal.class);
        criteria.select(builder.sum(root.get(ValorCereal_.stockActual).get(ValorUnidad_.valor)));

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(ValorCereal_.cereal), cultivo));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = getEntityManager().createQuery(criteria);
        Double existencia = (Double) qry.getSingleResult();

        vu = new ValorUnidad(existencia, MagnitudEnum.PESO.patron());

        return vu;
    }

    public ValorUnidad calcularExistenciaTotal(VariedadCultivo variedad) {
        ValorUnidad vu = null;

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Double> criteria = builder.createQuery(Double.class);
        Root<ValorSemilla> root = criteria.from(ValorSemilla.class);
        criteria.select(builder.sum(root.get(ValorSemilla_.stockActual).get(ValorUnidad_.valor)));

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(ValorSemilla_.semilla), variedad));

        criteria.where(predicates.toArray(new Predicate[0]));

        Query qry = getEntityManager().createQuery(criteria);
        Double existencia = (Double) qry.getSingleResult();

        vu = new ValorUnidad(existencia, MagnitudEnum.PESO.patron());
        return vu;
    }
}
