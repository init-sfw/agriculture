package ar.com.init.agros.view.campanias.reportes;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.CampaniasReportCriteria;
import java.util.List;

/**
 * Clase CostosDeCampaniaReportFrame
 *
 *
 * @author fbobbio
 * @version 18-sep-2009 
 */
public class MargenBrutoCampaniaReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;   

    /** Constructor por defecto de CostosDeCampaniaReportFrame */
    public MargenBrutoCampaniaReportFrame()
    {
        super();
        campaniaCriteria = new CampaniasReportCriteria();       

        insertCriteria(0, campaniaCriteria);      

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new MargenBrutoCampaniaJasperReport()
        {

            @Override
            public List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }           
        };
    }
}
