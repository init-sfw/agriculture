package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.CampaniaJpaController;
import ar.com.init.agros.model.Campania;
import java.util.List;

/**
 * Clase CampaniasReportCriteria
 *
 *
 * @author gmatheu
 * @version 08/07/2009 
 */
public class CampaniasReportCriteria extends DualListReportCriteria<Campania>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de CampaniasReportCriteria */
    public CampaniasReportCriteria()
    {
        this(true, true);
    }

    public CampaniasReportCriteria(boolean showCerradas, boolean showPendientesCierre)
    {
        CampaniaJpaController contr = new CampaniaJpaController();

        if (showCerradas && showPendientesCierre) {
            this.addAvailable(contr.findEntities());
        }
        else if (showCerradas) {
            this.addAvailable(contr.findCampaniasCerradas());
        }
        else if (showPendientesCierre) {
            this.addAvailable(contr.findCampaniasPendientesDeCierre());
        }
    }

    /** Constructor por defecto de CampaniasReportCriteria */
    public CampaniasReportCriteria(List<Campania> campanias)
    {
        super();

        this.addAvailable(campanias);
    }

    @Override
    public String getTabTitle()
    {
        return "Campaña";
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar por lo menos una Campaña";
    }
}
