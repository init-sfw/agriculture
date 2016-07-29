package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.controller.CampaniaJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.AgroquimicosReportCriteria;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.CampoCultivoReportCriteria;
import java.util.List;

/**
 * Clase PlanificadoVSFumigadoReportFrame
 *
 *
 * @author fbobbio
 * @version 21-sep-2009 
 */
public class PlanificadoVSFumigadoReportFrame extends ReportFrame
{

    private CampaniasReportCriteria campaniaCriteria;
    private CampoCultivoReportCriteria campoCultivoCriteria;
    private AgroquimicosReportCriteria agroquimicoCriteria;

    /** Constructor por defecto de PlanificadoVSFumigadoReportFrame */
    public PlanificadoVSFumigadoReportFrame()
    {
        super();
        campaniaCriteria = new CampaniasReportCriteria();
        campoCultivoCriteria = new CampoCultivoReportCriteria(campaniaCriteria, true, true, false);
        agroquimicoCriteria = new AgroquimicosReportCriteria(campaniaCriteria, true, true);
        agroquimicoCriteria.setFiltroMomentoDeAplicacionVisible(false);

        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, campoCultivoCriteria);
        insertCriteria(2, agroquimicoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new PlanificadoVSFumigadoJasperReport()
        {

            @Override
            protected List<Agroquimico> getAgroquimicos()
            {
                return agroquimicoCriteria.getSelectedAgroquimicos();
            }

            @Override
            protected List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }

            @Override
            protected List<Cultivo> getCultivos()
            {
                return campoCultivoCriteria.getCultivos();
            }

            @Override
            protected List<VariedadCultivo> getVariedades()
            {
                return campoCultivoCriteria.getVariedades();
            }

            @Override
            protected List<Campo> getCampos()
            {
                return campoCultivoCriteria.getCampos();
            }
        };
    }
}
