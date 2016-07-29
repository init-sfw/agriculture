package ar.com.init.agros.reporting.jasper;

import net.sf.jasperreports.engine.JRException;
import org.jfree.chart.JFreeChart;

/**
 * Clase AbstractChartImageReport
 *
 *
 * @author gmatheu
 * @version 02/08/2009 
 */
public abstract class AbstractChartImageReport extends AbstractJasperReport
{

    private JFreeChart chart;

    /** Constructor por defecto de AbstractChartImageReport */
    public AbstractChartImageReport(String title)
    {
        super(title);
    }

    @Override
    protected String getJasperDefinitionPath()
    {
        return "ar/com/init/agros/reporting/reports/ImageReport.jrxml";
    }

    @Override
    protected void buildReport() throws JRException, Exception
    {
        if (chart != null) {
            params.put("", chart.createBufferedImage(500, 400));

            super.buildReport();
        }
    }
}
