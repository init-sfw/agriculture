package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.Iterator;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 * Clase TipoCostoReportCriteria
 *
 *
 * @author fbobbio
 * @version 20-ago-2009 
 */
public class TipoCostoReportCriteria extends DualListReportCriteria<TipoCosto>
{

    /** Constructor por defecto de TipoCostoReportCriteria */
    public TipoCostoReportCriteria(boolean useAgroquimicos, boolean useCampania, boolean usePulverizacion, boolean usePostCosecha)
    {
        super();

        try {
            NamedEntityJpaController<TipoCosto> costoJpaController = new NamedEntityJpaController<TipoCosto>(
                    TipoCosto.class);

            List<TipoCosto> tipos = costoJpaController.findEntities();

            for (Iterator<TipoCosto> it = tipos.iterator(); it.hasNext();) {
                TipoCosto tipoCosto = it.next();
                if (!useCampania && tipoCosto.getTipo().equals(TipoCosto.TipoTipoCosto.CAMPANIA)) {
                    it.remove();
                }
                if (!usePulverizacion && tipoCosto.getTipo().equals(TipoCosto.TipoTipoCosto.PULVERIZACION)) {
                    it.remove();
                }
                if (!usePostCosecha && tipoCosto.getTipo().equals(TipoCosto.TipoTipoCosto.POST_COSECHA)) {
                    it.remove();
                }
            }

            if (useAgroquimicos) {
                tipos.add(0, TipoCosto.TIPO_COSTO_AGROQUIMICO);
            }

            this.addAvailable(tipos);
        }
        catch (PersistenceException e) {
            GUIUtility.logPersistenceError(TipoCostoReportCriteria.class, e);
        }
    }

    /** Constructor de TipoCostoReportCriteria que recibe la lista de tipos de costos a mostrar */
    public TipoCostoReportCriteria(List<TipoCosto> tipos)
    {
        super();

        this.addAvailable(tipos);
    }

    @Override
    public String getTabTitle()
    {
        return "Costos";
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar al menos un tipo de costo";
    }
}
