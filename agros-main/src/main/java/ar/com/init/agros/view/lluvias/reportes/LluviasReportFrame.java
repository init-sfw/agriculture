package ar.com.init.agros.view.lluvias.reportes;

import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.util.gui.Periodo;
import ar.com.init.agros.view.reporting.PeriodosReportCriteria;
import ar.com.init.agros.view.reporting.SuperficiesSinSubLoteReportCriteria;
import java.util.List;

/**
 * Clase LluviasReportFrame
 *
 *
 * @author fbobbio
 * @version 21-jul-2009 
 */
public class LluviasReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private SuperficiesSinSubLoteReportCriteria camposReportCriteria;
    private PeriodosReportCriteria periodosReportCriteria;

    /** Constructor por defecto de LluviasReportFrame */
    public LluviasReportFrame()
    {
        super();

        camposReportCriteria = new SuperficiesSinSubLoteReportCriteria(getFrameNotifier());
        periodosReportCriteria = new PeriodosReportCriteria();

        insertCriteria(0, periodosReportCriteria);
        insertCriteria(1, camposReportCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true;
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new LluviasJasperReport()
        {

            @Override
            protected List<Periodo> getPeriodos()
            {
                return periodosReportCriteria.getPeriodos();
            }

            @Override
            protected List<Lote> getSuperficies()
            {
                return camposReportCriteria.getSelectedSuperficies();
            }
        };
    }
}
