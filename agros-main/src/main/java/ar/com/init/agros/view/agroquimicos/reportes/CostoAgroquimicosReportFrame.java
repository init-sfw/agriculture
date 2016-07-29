package ar.com.init.agros.view.agroquimicos.reportes;

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
 * Clase CostoAgroquimicosReportFrame
 *
 *
 * @author gmatheu
 * @version 07/07/2009 
 */
public class CostoAgroquimicosReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private AgroquimicosReportCriteria agroquimicosReportCriteria;
    private CampoCultivoReportCriteria campoCultivoReportCriteria;
    private CampaniasReportCriteria campaniasReportCriteria;

    /** Constructor por defecto de CostoAgroquimicosReportFrame */
    public CostoAgroquimicosReportFrame()
    {
        super();

        campaniasReportCriteria = new CampaniasReportCriteria();
        campoCultivoReportCriteria = new CampoCultivoReportCriteria(campaniasReportCriteria, true, false,
                false);
        campoCultivoReportCriteria.setFrameNotifier(getFrameNotifier());
        campoCultivoReportCriteria.setVariedadesVisible(false);
        agroquimicosReportCriteria = new AgroquimicosReportCriteria(campaniasReportCriteria, true, false);
        agroquimicosReportCriteria.setFiltroMomentoDeAplicacionVisible(false);

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
        return new CostoAgroquimicosJasperReport()
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
        };
    }
}
