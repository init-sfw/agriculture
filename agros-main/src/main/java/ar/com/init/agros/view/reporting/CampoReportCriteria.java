package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.terreno.Campo;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 * Clase CampoReportCriteria
 *
 *
 * @author fbobbio
 * @version 06-sep-2009 
 */
public class CampoReportCriteria extends DualListReportCriteria<Campo>
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaReportCriteria;
    private boolean useTrabajos;
    private boolean usePlanificaciones;
    private boolean useSiembras;

    /** Constructor por defecto de CampoReportCriteria */
    public CampoReportCriteria()
    {
        super();
        try {
            CampoJpaController campoJpaController = new CampoJpaController();
            this.addAvailable(campoJpaController.findEntities());
        }
        catch (PersistenceException ex) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(ex.getLocalizedMessage());
            }
        }
    }

    public CampoReportCriteria(final CampaniasReportCriteria campaniaReportCriteria, final boolean useTrabajos, final boolean usePlanificaciones, final boolean useSiembras)
    {
        this();
        this.campaniaReportCriteria = campaniaReportCriteria;
        this.usePlanificaciones = usePlanificaciones;
        this.useTrabajos = useTrabajos;

        this.addComponentListener(new ComponentAdapter()
        {

            @Override
            public void componentShown(ComponentEvent e)
            {
                if (campaniaReportCriteria != null && campaniaReportCriteria.validateSelection()) {
                    List<Campania> campanias = campaniaReportCriteria.getSelected();

                    CampoReportCriteria.this.clearAvailable();
                    CampoJpaController campController = new CampoJpaController();

                    CampoReportCriteria.this.addAvailable(campController.findByCampanias(campanias, useTrabajos,
                    usePlanificaciones, useSiembras));
                }
            }
        });
    }

    @Override
    public String getTabTitle()
    {
        return "Establecimientos";
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar por lo menos un establecimiento.";
    }
}
