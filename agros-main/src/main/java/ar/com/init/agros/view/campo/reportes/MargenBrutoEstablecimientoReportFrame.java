package ar.com.init.agros.view.campo.reportes;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.CampoReportCriteria;
import ar.com.init.agros.view.reporting.CultivosReportCriteria;
import java.util.List;

/**
 * Clase CostosPorEstablecimientoReportFrame
 *
 *
 * @author fbobbio
 * @version 09-oct-2009 
 */
public class MargenBrutoEstablecimientoReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;
    private CampoReportCriteria campoCriteria;
    private CultivosReportCriteria cultivoCriteria;

    /** Constructor por defecto de CostosPorEstablecimientoReportFrame */
    public MargenBrutoEstablecimientoReportFrame()
    {
        super();
        campaniaCriteria = new CampaniasReportCriteria();
        campoCriteria = new CampoReportCriteria(campaniaCriteria, true, false, true);
        cultivoCriteria = new CultivosReportCriteria(false, campaniaCriteria, true, false, true);
        cultivoCriteria.setVariedadesVisible(false);
        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, campoCriteria);
        insertCriteria(2, cultivoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new MargenBrutoEstablecimientoJasperReport()
        {

            @Override
            public List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }

            @Override
            public List<Campo> getCampos()
            {
                return campoCriteria.getSelected();
            }

            @Override
            public List<Cultivo> getCultivos()
            {
                return cultivoCriteria.getSelectedCultivos();
            }

//            @Override
//            public List<VariedadCultivo> getVariedadesCultivos()
//            {
//                return cultivoCriteria.getSelectedVariedades();
//            }
        };
    }
}
