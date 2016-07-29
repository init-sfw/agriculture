package ar.com.init.agros.view.costos.reportes;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.CampoCultivoReportCriteria;
import ar.com.init.agros.view.reporting.TipoCostoReportCriteria;
import java.util.List;

/**
 * Clase CostosReportFrame
 *
 *
 * @author fbobbio
 * @version 09-sep-2009 
 */
public class CostosReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;
    private CampoCultivoReportCriteria campoCultivoCriteria;
    private TipoCostoReportCriteria tipoCostoCriteria;

    /** Constructor por defecto de CostosReportFrame */
    public CostosReportFrame()
    {
        campaniaCriteria = new CampaniasReportCriteria();
        campoCultivoCriteria = new CampoCultivoReportCriteria(campaniaCriteria, true, false, true);
        tipoCostoCriteria = new TipoCostoReportCriteria(false, true, false, false);

        campaniaCriteria.setFrameNotifier(getFrameNotifier());
        campoCultivoCriteria.setFrameNotifier(getFrameNotifier());
        tipoCostoCriteria.setFrameNotifier(getFrameNotifier());

        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, campoCultivoCriteria);
        insertCriteria(2, tipoCostoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new CostosJasperReport()
        {

            @Override
            protected List<Campo> getCampos()
            {
                return campoCultivoCriteria.getCampos();
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
            protected List<TipoCosto> getTiposDeCosto()
            {
                return tipoCostoCriteria.getSelected();
            }
        };
    }
}
