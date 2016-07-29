package ar.com.init.agros.reporting.jasper;

import ar.com.init.agros.reporting.AbstractReport;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Clase AbstractJasperReport
 * Para correr reportes definidos en archivos .jrxml
 *
 * @author gmatheu
 * @version 30/07/2009 
 */
public abstract class AbstractJasperReport extends AbstractReport
{

    private static final String REPORT_TITLE_PARAMETER = "reportTitle";
    private static final String REPORT_SUBTITLE_PARAMETER = "reportSubtitle";
    private String subtitle;

    /** Constructor por defecto de AbstractJasperReport */
    public AbstractJasperReport(String title)
    {
        super();

        setReportTitle(title);
        setReportSubTitle("");
    }

    protected abstract String getJasperDefinitionPath();

    @Override
    protected void buildReport() throws JRException, Exception
    {
        try {
            if (jasperReport == null) {
                InputStream is = ClassLoader.getSystemResourceAsStream(getJasperDefinitionPath());

                JasperDesign jd = JRXmlLoader.load(is);

                InputStream templateInputStream = ClassLoader.getSystemResourceAsStream(
                        "ar/com/init/agros/reporting/templates/basic/PortraitTemplate.jrxml");

                if (templateInputStream != null) {
                    JasperDesign templateJasperDesign = JRXmlLoader.load(templateInputStream);

                    jd.setTopMargin(templateJasperDesign.getTopMargin());
                    jd.setLeftMargin(templateJasperDesign.getLeftMargin());
                    jd.setRightMargin(templateJasperDesign.getRightMargin());
                    jd.setBottomMargin(templateJasperDesign.getBottomMargin());

                    JRDesignBand titleBand = (JRDesignBand) jd.getTitle();
                    JRDesignBand templateTitleBand = (JRDesignBand) templateJasperDesign.getTitle();

                    int tempHeight = templateTitleBand.getHeight();

                    templateTitleBand.setHeight(titleBand.getHeight());

                    for (JRElement jRElement : titleBand.getElements()) {
                        JRDesignElement clone = (JRDesignElement) jRElement.clone();

//                        clone.setPositionType(JRElement.POSITION_TYPE_FLOAT);
//                        clone.setX(tempHeight + jRElement.getX());

                        templateTitleBand.addElement(clone);
                    }

                    jd.setTitle(templateTitleBand);
                    jd.setLastPageFooter(templateJasperDesign.getPageFooter());
                    jd.setPageHeader(templateJasperDesign.getPageHeader());
                    jd.setPageFooter(templateJasperDesign.getPageFooter());
                }

                jasperReport = JasperCompileManager.compileReport(jd);
            }

            fillReport();
        }
        catch (JRException ex) {
            Logger.getLogger(AbstractJasperReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void setReportTitle(String title)
    {
        params.put(REPORT_TITLE_PARAMETER, title);
    }

    @SuppressWarnings("unchecked")
    protected void setReportSubTitle(String title)
    {
        subtitle = title;
        params.put(REPORT_SUBTITLE_PARAMETER, title);
    }

    @SuppressWarnings("unchecked")
    protected void setReportSubTitle(String title, String splitRegex)
    {
        setReportSubTitle(title);

        String[] titles = title.split(splitRegex);

        for (int i = 0; i < titles.length; i++) {
            String string = titles[i].trim();
            if (i > 0) {
                params.put(REPORT_SUBTITLE_PARAMETER + i, string);
            }
            else {
                params.put(REPORT_SUBTITLE_PARAMETER, string);
            }
        }
    }

    protected String getReportTitle()
    {
        Object o = params.get(REPORT_TITLE_PARAMETER);

        if (o != null) {
            return o.toString();
        }
        else {
            return "";
        }
    }

    protected String getReportSubTitle()
    {
//        Object o = params.get(REPORT_SUBTITLE_PARAMETER);
//
//        if (o != null) {
//            return o.toString();
//        }
//        else {
//            return "";
//        }

        return subtitle;
    }
}
