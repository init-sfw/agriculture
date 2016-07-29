package ar.com.init.agros.view.campanias.reportes;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.costo.TipoCosto;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import ar.com.init.agros.view.reporting.TipoCostoReportCriteria;
import java.util.List;

/**
 * Clase CostosDeCampaniaReportFrame
 *
 *
 * @author fbobbio
 * @version 18-sep-2009 
 */
public class CostosDeCampaniaReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;
    private TipoCostoReportCriteria tipoCostoCriteria;

    /** Constructor por defecto de CostosDeCampaniaReportFrame */
    public CostosDeCampaniaReportFrame()
    {
        super();
        campaniaCriteria = new CampaniasReportCriteria();
        tipoCostoCriteria = new TipoCostoReportCriteria(true, true, true, true);

        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, tipoCostoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new CostosDeCampaniaJasperReport()
        {

            @Override
            public List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }

            @Override
            public List<TipoCosto> getTipoCostos()
            {
                return tipoCostoCriteria.getSelected();
            }
        };
    }
}
