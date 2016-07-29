package ar.com.init.agros.view.siembras.reportes;

import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.CampoCultivoReportCriteria;
import ar.com.init.agros.view.reporting.CampoReportCriteria;
import ar.com.init.agros.view.reporting.TipoCostoReportCriteria;
import java.util.List;

/** 
 * Clase SiembrasReportFrame
 *
 *
 * @author fbobbio
 * @version 09-sep-2009 
 */
public class SiembrasReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;
    private CampoReportCriteria campoCriteria;
    private TipoCostoReportCriteria tipoCostoCriteria;

    /** Constructor por defecto de SiembrasReportFrame */
    public SiembrasReportFrame()
    {
        SiembraJpaController siembraController = new SiembraJpaController();
        campaniaCriteria = new CampaniasReportCriteria();
        campoCriteria = new CampoReportCriteria(campaniaCriteria, false, false, true);
        tipoCostoCriteria = new TipoCostoReportCriteria(siembraController.findTipoCostosDeSiembras());

        campaniaCriteria.setFrameNotifier(getFrameNotifier());
        campoCriteria.setFrameNotifier(getFrameNotifier());
        tipoCostoCriteria.setFrameNotifier(getFrameNotifier());

        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, campoCriteria);
        insertCriteria(2, tipoCostoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new SiembrasJasperReport()
        {

            @Override
            protected List<Campo> getCampos()
            {
                return campoCriteria.getSelected();
            }

            @Override
            protected List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }

            @Override
            protected List<TipoCosto> getTiposDeCosto()
            {
                return tipoCostoCriteria.getSelected();
            }
        };
    }
}
