package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.AgroquimicosReportCriteria;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.CampoCultivoReportCriteria;
import java.util.List;

/**
 * Clase HistoricoTrabajoCampoLoteReportFrame
 *
 *
 * @author gmatheu
 * @version 11/09/2009 
 */
public class HistoricoTrabajoCampoLoteReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private AgroquimicosReportCriteria agroquimicosReportCriteria;
    private CampoCultivoReportCriteria campoCultivoReportCriteria;
    private CampaniasReportCriteria campaniasReportCriteria;

    /** Constructor por defecto de CostoAgroquimicosReportFrame */
    public HistoricoTrabajoCampoLoteReportFrame()
    {
        super();

        campaniasReportCriteria = new CampaniasReportCriteria();
        campoCultivoReportCriteria = new CampoCultivoReportCriteria(campaniasReportCriteria, true, false,
                false);
        campoCultivoReportCriteria.setFrameNotifier(getFrameNotifier());
        campoCultivoReportCriteria.setFormaFumigacionVisible(true);
        agroquimicosReportCriteria = new AgroquimicosReportCriteria(campaniasReportCriteria, true, false);
        agroquimicosReportCriteria.setFiltroMomentoDeAplicacionVisible(true);


        insertCriteria(0, campaniasReportCriteria);
        insertCriteria(1, campoCultivoReportCriteria);
        insertCriteria(2, agroquimicosReportCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new HistoricoTrabajoCampoLoteJasperReport()
        {

            @Override
            protected List<Agroquimico> getAgroquimicos()
            {
                return agroquimicosReportCriteria.getSelectedAgroquimicos();
            }

            @Override
            protected List<Campania> getCampanias()
            {
                return campaniasReportCriteria.getSelected();
            }

            @Override
            protected List<Cultivo> getCultivos()
            {
                return campoCultivoReportCriteria.getCultivos();
            }

            @Override
            protected List<VariedadCultivo> getVariedades()
            {
                return campoCultivoReportCriteria.getVariedades();
            }

            @Override
            protected List<Campo> getCampos()
            {
                return campoCultivoReportCriteria.getCampos();
            }

            @Override
            protected FormaFumigacion getFormaFumigacion()
            {
                return campoCultivoReportCriteria.getSelectedFormaFumigacion();
            }

            @Override
            protected MomentoAplicacion getMomentoAplicacion()
            {
                return agroquimicosReportCriteria.getSelectedMomento();
            }
        };
    }
}

