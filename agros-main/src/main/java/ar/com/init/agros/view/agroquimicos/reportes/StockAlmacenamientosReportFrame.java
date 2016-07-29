package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.reporting.AbstractReport;
import ar.com.init.agros.reporting.components.ReportFrame;
import ar.com.init.agros.view.reporting.ValoresAlmacenamientoReportCriteria;
import java.util.List;

/**
 * Clase StockAgroquimicoReportFrame
 *
 *
 * @author gmatheu
 * @version 26/07/2009 
 */
public class StockAlmacenamientosReportFrame extends ReportFrame {

    private static final long serialVersionUID = -1L;
    private ValoresAlmacenamientoReportCriteria valoresAlmacenamientoReportCriteria;

    /** Constructor por defecto de StockAgroquimicoReportFrame */
    public StockAlmacenamientosReportFrame() {

        valoresAlmacenamientoReportCriteria = new ValoresAlmacenamientoReportCriteria();

        insertCriteria(0, valoresAlmacenamientoReportCriteria);


        jTabbedPane.setSelectedIndex(0);
        validate = true; //Indica que se deben validar todos los paneles.
        generateChart = true; //Esto es un workaround, necesita estar al final del constructor si se quiere generar un grafico
    }

    @Override
    protected AbstractReport createReport() {
        return new StockAlmacenamientosJasperReport() {

            @Override
            protected List<Almacenamiento> getAlmacenamientos() {
                return valoresAlmacenamientoReportCriteria.getAlmacenamientos();
            }

            @Override
            protected List<Agroquimico> getAgroquimicos() {
                return valoresAlmacenamientoReportCriteria.getAgroquimicos();
            }

            @Override
            protected List<Cultivo> getCereales() {
                return valoresAlmacenamientoReportCriteria.getCereales();
            }

            @Override
            protected List<VariedadCultivo> getSemillas() {
                return valoresAlmacenamientoReportCriteria.getSemillas();
            }
        };
    }
}
