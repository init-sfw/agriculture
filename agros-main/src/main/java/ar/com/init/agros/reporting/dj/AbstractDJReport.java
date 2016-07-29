package ar.com.init.agros.reporting.dj;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.BuilderException;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.init.agros.controller.util.HibernateUtil;
import ar.com.init.agros.reporting.AbstractReport;
import java.awt.Color;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;

/**
 * Clase de base para generar reportes con formato consistente.
 * Ayuda sobre como crear un reporte en {@link http://dynamicjasper.sourceforge.net/docs/howto.html}
 *
 * @author gmatheu
 * @version 1.0
 *
 */
public abstract class AbstractDJReport extends AbstractReport {

    private DynamicReportBuilder dynamicReportBuilder;
    private DynamicReport dynamicReport;
    ////ESTILOS PREDEFINIDOS////////////
    protected Integer margin;
    protected static Style detailStyle;
    protected static Style numberDetailStyle;
    protected static Style groupTitleStyle;
    protected static Style headerStyle;
    protected static Style headerVariables;
    protected static Style titleStyle;
    protected static Style oddRowStyle;
    private Template template;

    public enum Template {

        PORTRAIT("PortraitTemplate.jrxml"),
        LANDSCAPE("LandscapeTemplate.jrxml");
        private String fileName;

        private Template(String fileName) {
            this.fileName = fileName;
        }

        public String fileName() {
            return fileName;
        }
    }

    public AbstractDJReport() {
        super();

        detailStyle = new Style();
        detailStyle.setVerticalAlign(VerticalAlign.TOP);
        detailStyle.setFont(new Font(10, Font._FONT_VERDANA, true));

        numberDetailStyle = new Style();
        numberDetailStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        groupTitleStyle = new Style();
        groupTitleStyle.setFont(Font.ARIAL_BIG);

        headerStyle = new Style();
        headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyle.setBorderBottom(Border.PEN_1_POINT);
        headerStyle.setBackgroundColor(Color.gray);
        headerStyle.setTextColor(Color.white);
        headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setTransparency(Transparency.OPAQUE);

        headerVariables = new Style();
        headerVariables.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
        headerVariables.setVerticalAlign(VerticalAlign.MIDDLE);

        titleStyle = new Style();
        titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));

        oddRowStyle = new Style();
        oddRowStyle.setBorder(Border.NO_BORDER);
        oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
        oddRowStyle.setTransparency(Transparency.OPAQUE);

        margin = new Integer(25);

        template = Template.PORTRAIT;
    }

    /**
     * Para crear reportes rapidos de la forma:
     *
     * {@code
     *  getFastReportBuilder().addColumn("State", "state", String.class.getName(),30)
     *      .addColumn("Branch", "branch", String.class.getName(),30)
     *      .addColumn("Product Line", "productLine", String.class.getName(),50)
     *      .addColumn("Item", "item", String.class.getName(),50)
     *      .addColumn("Item Code", "id", Long.class.getName(),30,true)
     *      .addColumn("Quantity", "quantity", Long.class.getName(),60,true)
     *      .addColumn("Amount", "amount", Float.class.getName(),70,true)
     *      .addGroups(2)
     *      .setTitle("November 2006 sales report")
     *      .setSubtitle("This report was generated at " + new Date())
     * }
     *
     * @return FastReportBuilder
     */
    protected FastReportBuilder getFastReportBuilder() {
        FastReportBuilder frb = null;

        if (dynamicReportBuilder == null || dynamicReportBuilder instanceof DynamicReportBuilder) {
            frb = new FastReportBuilder();
            dynamicReportBuilder = frb;
        }
        return frb;
    }

    /**
     * Para crear reportes mas especificos de la forma:
     *
     * Agregar columnas:
     * {@code AbstractColumn columnaItem = ColumnBuilder.getInstance()
     *                 .setColumnProperty("item", String.class.getName())
     *                 .setTitle("Item").setWidth(85)
     *                 .build();
     *
     *        AbstractColumn columnAmount = ColumnBuilder.getInstance()
     *                 .setColumnProperty("amount", Float.class.getName())
     *                 .setTitle("Amount").setWidth(90)
     *                 .setPattern("$ 0.00")           //defines a pattern to apply to the values swhown (uses TextFormat)
     *                 .setStyle(amountStyle)          //special style for this column (align right)
     *                 .build();
     *
     *        getReportBuilder().addColumn(columnaItem);
     *        getReportBuilder().addColumn(columnAmount);
     * }
     *
     * Agrupaciones:
     * 
     * {@code
     *      GroupBuilder gb2 = new GroupBuilder();                                        //Create another group (using another column as criteria)
     *             ColumnsGroup g2 = gb2.setCriteriaColumn((PropertyColumn) columnBranch)        //and we add the same operations for the columnAmount and
     *             .addFooterVariable(columnAmount,ColumnsGroupVariableOperation.SUM)        //columnaQuantity columns
     *             .addFooterVariable(columnaQuantity,ColumnsGroupVariableOperation.SUM)
     *             .build();
     *
     *      getReportBuilder().addGroup(g2);
     * }
     *
     *
     * @return DynamicReportBuilder
     */
    protected DynamicReportBuilder getReportBuilder() {
        if (dynamicReportBuilder == null) {
            dynamicReportBuilder = new DynamicReportBuilder();
        }
        return dynamicReportBuilder;
    }

    private void applyTemplate() {
        URL url = AbstractReport.class.getClassLoader().getResource("ar/com/init/agros/reporting/"
                + TEMPLATE_FOLDER + "/" + DEFAULT_THEME + "/" + template.fileName());

        if (url != null) {
            try {
                Logger.getLogger(AbstractDJReport.class.getName()).log(Level.INFO, "Template file: {0}", url.getFile());
                getReportBuilder().setTemplateFile("ar/com/init/agros/reporting/"
                        + TEMPLATE_FOLDER + "/" + DEFAULT_THEME + "/" + template.fileName());
            } catch (Exception ex) {
                Logger.getLogger(AbstractDJReport.class.getName()).info(
                        "Exception while applying report template");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void applySettings() {
        DynamicReportBuilder drb = getReportBuilder();
        drb.setUseFullPageWidth(true);
        drb.setWhenResourceMissingLeaveEmptySpace();

//        URL url = AbstractReport.class.getClassLoader().getResource("ar/com/init/agros/reporting/" +
//                TEMPLATE_FOLDER + "/" + DEFAULT_THEME);
//        drb.addFirstPageImageBanner(url.getPath() + "/" + FIRST_PAGE_BANNER, new Integer(300), new Integer(80),
//                ImageBanner.ALIGN_LEFT);
//        drb.addImageBanner("ar/com/init/agros/reporting/" +
//                TEMPLATE_FOLDER + "/" + DEFAULT_THEME + "/" + PAGE_BANNER, new Integer(38), new Integer(33),
//                ImageBanner.ALIGN_RIGHT);

        //Autotext
//        drb.addAutoText(AutoText.AUTOTEXT_CREATED_ON, AutoText.POSITION_FOOTER,
//                AutoText.ALIGMENT_LEFT, AutoText.PATTERN_DATE_DATE_TIME);
//        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y, AutoText.POSITION_FOOTER,
//                AutoText.ALIGMENT_RIGHT);

        //Styles
        drb.setTitleStyle(titleStyle);
        drb.setDetailHeight(new Integer(15));
        drb.setLeftMargin(margin);
        drb.setRightMargin(margin);
        drb.setTopMargin(margin);
        drb.setBottomMargin(margin);
        drb.setPrintBackgroundOnOddRows(false);
        drb.setGrandTotalLegendStyle(headerVariables);
        drb.setDefaultStyles(titleStyle, null, headerStyle, detailStyle);
        drb.setPrintColumnNames(true);
        drb.setOddRowBackgroundStyle(oddRowStyle);

        //Parameters
//        params.put("leftHeader", "Camportunidades.com");

    }

    private LayoutManager getLayoutManager() {
        return new ClassicLayoutManager();
    }

    @Override
    protected void buildReport() throws JRException, Exception {
        createLayout();
        applyTemplate();
        applySettings();
        createDataSource();

        try {
            dynamicReport = getReportBuilder().build();

            if (jasperReport == null) {
                jasperReport = DynamicJasperHelper.generateJasperReport(dynamicReport, getLayoutManager(),
                        params);
            }

            fillReport();

        } catch (JRException ex) {
            Logger.getLogger(AbstractDJReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void createHQLDataSource(String hql) {
        getReportBuilder().setQuery(hql, DJConstants.QUERY_LANGUAGE_HQL);
        params.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION,
                HibernateUtil.getHibernateSession());
        dataSource = null;
    }

    /**
     * Define la estructura del reporte (columnas, cabeceras, formatos condicionales, grupos,
     * sumarizaciones, etc.).
     *
     * Mas informacion para crear reportes en {@link @link http://dynamicjasper.sourceforge.net/docs/howto.html}
     *
     * @see AbstractDJReport#getFastReportBuilder()
     * @see AbstractDJReport#getReportBuilder()
     */
    protected abstract void createLayout() throws BuilderException;

    /**
     * Crea la columna por defecto para valores alfanumericos.
     * @param property
     * @param header
     * @param width
     * @return
     * @throws ColumnBuilderException
     */
    public static AbstractColumn createStringColumn(String property, String header, int width) throws ColumnBuilderException {
        ColumnBuilder builder = ColumnBuilder.getNew();
        builder.setColumnProperty(property, String.class.getName());
        builder.setTitle(header);
        builder.setWidth(new Integer(width));
        builder.setStyle(detailStyle);
        builder.setHeaderStyle(headerStyle);

        return builder.build();
    }

    /**
     * Crea la columna por defecto para valores decimales.
     * @param property
     * @param header
     * @param width
     * @return
     * @throws ColumnBuilderException
     */
    protected AbstractColumn createDoubleColumn(String property, String header, int width) throws ColumnBuilderException {
        ColumnBuilder builder = ColumnBuilder.getInstance();
        builder.setColumnProperty(property, Double.class.getName());
        builder.setTitle(header);
        builder.setWidth(new Integer(width));
        builder.setPattern("0.00");
        builder.setStyle(numberDetailStyle);
        builder.setHeaderStyle(headerStyle);

        return builder.build();
    }

    /**
     * Crea la columna por defecto para propiedades que agrupan.
     * @param property
     * @param header
     * @param width
     * @return
     * @throws ColumnBuilderException
     */
    protected AbstractColumn createGroupColumn(String property, String header, int width) throws ColumnBuilderException {
        ColumnBuilder builder = ColumnBuilder.getInstance();
        builder.setColumnProperty(property, String.class.getName());
        builder.setTitle(header);
        builder.setWidth(new Integer(width));
        builder.setStyle(titleStyle);
        builder.setHeaderStyle(groupTitleStyle);

        return builder.build();
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
