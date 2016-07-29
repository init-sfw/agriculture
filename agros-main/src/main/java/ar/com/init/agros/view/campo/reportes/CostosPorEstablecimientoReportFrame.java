package ar.com.init.agros.view.campo.reportes;

import java.util.List;

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

/**
 * Clase CostosPorEstablecimientoReportFrame
 *
 *
 * @author fbobbio
 * @version 09-oct-2009 
 */
public class CostosPorEstablecimientoReportFrame extends ReportFrame
{

    private static final long serialVersionUID = -1L;
    private CampaniasReportCriteria campaniaCriteria;
    private CampoCultivoReportCriteria campoCultivoReportCriteria;
    private TipoCostoReportCriteria tipoCostoCriteria;

    /** Constructor por defecto de CostosPorEstablecimientoReportFrame */
    public CostosPorEstablecimientoReportFrame()
    {
        super();
        campaniaCriteria = new CampaniasReportCriteria();
        campoCultivoReportCriteria = new CampoCultivoReportCriteria(campaniaCriteria, true, false,
                true);
        tipoCostoCriteria = new TipoCostoReportCriteria(true, false, true, true);

        insertCriteria(0, campaniaCriteria);
        insertCriteria(1, campoCultivoReportCriteria);
        insertCriteria(2, tipoCostoCriteria);

        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport()
    {
        return new CostosPorEstablecimientoJasperReport()
        {

            @Override
            public List<Campania> getCampanias()
            {
                return campaniaCriteria.getSelected();
            }

            @Override
            public List<Campo> getCampos()
            {
                return campoCultivoReportCriteria.getCampos();
            }

            @Override
            public List<TipoCosto> getTipoCostos()
            {
                return tipoCostoCriteria.getSelected();
            }

            @Override
            public List<Cultivo> getCultivos()
            {
                return campoCultivoReportCriteria.getCultivos();
            }

            @Override
            public List<VariedadCultivo> getVariedades()
            {
                return campoCultivoReportCriteria.getVariedades();
            }
        };
    }
}
