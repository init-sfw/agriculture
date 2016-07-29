package ar.com.init.agros.view.reporting.helper;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.FormaFumigacion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase AbstractSuperficiesHelper
 *
 *
 * @author gmatheu
 * @version 25/04/2010 
 */
public abstract class AbstractSuperficiesHelper<T>
{

    protected Map<Campania, Map<T, Double>> superficies;

    /** Constructor por defecto de AbstractSuperficiesHelper */
    public AbstractSuperficiesHelper(List<Campania> campanias, List<T> filtros)
    {
        superficies = new HashMap<Campania, Map<T, Double>>();

        for (Campania camp : campanias) {
            Map<T, Double> supPulvCamp = new HashMap<T, Double>();

            superficies.put(camp, supPulvCamp);
            for (T object : filtros) {

                supPulvCamp.put(object, 0D);
            }
        }
    }

    public Double getSuperficie(Campania camp, Object filtro)
    {
        return superficies.get(camp).get(filtro);
    }

    public abstract Double getSuperficie(final Campania camp, final T filtro,
            final Agroquimico agroquimico);

    public abstract Double getSuperficie(final Campania camp, final T filtro,
            final Agroquimico agroquimico, final FormaFumigacion formaFumigacion);
}
