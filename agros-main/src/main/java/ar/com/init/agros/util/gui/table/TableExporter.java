package ar.com.init.agros.util.gui.table;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJCrosstab;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.BuilderException;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DJBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.init.agros.reporting.ExportTypeEnum;
import ar.com.init.agros.reporting.dj.AbstractDJReport;
import ar.com.init.agros.reporting.dj.AbstractDJReport.Template;
import ar.com.init.agros.util.gui.Tablizable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author gmatheu
 */
public class TableExporter {

    private String reportTitle;
    private String reportSubtitle;
    private String mainCategoryLabel;
    private String mainCategory;
    private List<Table> tables;
    private AbstractDJReport.Template template;

    public TableExporter() {
        tables = new ArrayList<Table>();
        template = Template.PORTRAIT;
    }

    public TableExporter(Table... tables) {
        this();
        for (Table table : tables) {
            addTable(table);
        }
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportSubtitle() {
        return reportSubtitle;
    }

    public void setReportSubtitle(String reportSubtitle) {
        this.reportSubtitle = reportSubtitle;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getMainCategoryLabel() {
        return mainCategoryLabel;
    }

    public void setMainCategoryLabel(String mainCategoryLabel) {
        this.mainCategoryLabel = mainCategoryLabel;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public final void addTable(Table table) {
        tables.add(table);
    }

    public final void addTable(String title, String[] headers, List<String[]> values) {
        addTable(new Table(title, headers, values));
    }

    public void exportToPDF(final String title, String file) throws JRException, Exception {
        if (!file.endsWith(".pdf")) {
            file += ".pdf";
        }

        export(title, file, ExportTypeEnum.PDF);
    }

    public void exportToExcel(String title, String file) throws JRException, Exception {
        if (!file.endsWith(".xls")) {
            file += ".xls";
        }

        export(title, file, ExportTypeEnum.XLS);
    }

    private List<Map<String, Object>> createTableDatasource(final int rows, final int cols, final Table table) {
        List<Map<String, Object>> coll = new ArrayList<Map<String, Object>>();
        for (int r = 0; r < rows; r++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            for (int c = 0; c < cols; c++) {
                String o = table.values.get(r)[c];
                row.put("c" + c, o != null ? o.toString() : "");
            }
            coll.add(row);
        }
        return coll;
    }

    private void createTableLayout(final int cols, final Table table, DynamicReportBuilder drb) throws ColumnBuilderException {
        for (int c = 0; c < cols; c++) {
            String name = table.headers[c];

            int width = 85;

            if (table.widths != null) {
                width = table.widths[c];
            }

            drb.addColumn(AbstractDJReport.createStringColumn("c" + c, name, width));
        }
    }

    private void export(final String title, final String file, final ExportTypeEnum exportTypeEnum) throws JRException, Exception {


        AbstractDJReport djReport = new AbstractDJReport() {

            @Override
            protected void createLayout() throws BuilderException {
                DynamicReportBuilder drb = getReportBuilder();

                drb.setTitle(reportTitle); //defines the title of the report
                drb.setSubtitle(reportSubtitle);
                drb.setReportName(title);
                drb.setDetailHeight(10);
                drb.setUseFullPageWidth(true);
                drb.setWhenNoDataAllSectionNoDetail();
                drb.setAllowDetailSplit(true);

                ColumnBuilder builder = ColumnBuilder.getNew();
                builder.setColumnProperty("mainTitle", String.class.getName());
                builder.setTitle(mainCategoryLabel + " " + mainCategory);
                builder.setWidth(new Integer(200));
                builder.setStyle(detailStyle);
                builder.setHeaderStyle(headerStyle);

                drb.addColumn(builder.build());

                for (int i = 0; i < tables.size(); i++) {
                    Table table = tables.get(i);
                    JasperReport sr;
                    try {
                        sr = createSubReport(table, getParams());

                        if (sr != null) {
                            drb.addConcatenatedReport(sr, table.title.trim(), DJConstants.DATA_SOURCE_ORIGIN_PARAMETER,
                                    DJConstants.DATA_SOURCE_TYPE_COLLECTION,
                                    table.startOnNewPage);
                        }
                    } catch (Exception ex) {
                        throw new BuilderException(ex);
                    }
                }
            }

            @Override
            protected boolean createDataSource() {
                Table table = tables.get(0);
                final int cols = table.headers.length;
                final int rows = table.values.size();
                List<Map<String, String>> coll = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("mainTitle", "");
                coll.add(map);

                createCollectionDataSource(coll);

                return true;
            }
        };

        djReport.setTemplate(template);
        djReport.exportReport(exportTypeEnum, file);
    }

    private JasperReport createSubReport(final Table table, Map params) throws ColumnBuilderException, JRException, DJBuilderException {
        final int cols = table.headers.length;
        final int rows = table.values.size();

        if (rows == 0) {
            return null;
        }

        DynamicReportBuilder drb = new DynamicReportBuilder();
        drb.setTitle(table.title); //defines the title of the report
        drb.setReportName(table.title);

        Style detailStyle = new Style();
        detailStyle.setVerticalAlign(VerticalAlign.TOP);
        detailStyle.setFont(new Font(10, Font._FONT_VERDANA, true));
        drb.setDefaultStyles(null, null, null, detailStyle);

        createTableLayout(cols, table, drb);

        if (table.footer != null) {
        }

        List<Map<String, Object>> coll = createTableDatasource(rows, cols, table);

        params.put(table.title.trim(), coll);
        JasperReport jr = DynamicJasperHelper.generateJasperReport(drb.build(), new ClassicLayoutManager(), null);
        return jr;
    }

    public static class Table {

        String title;
        String subTitle;
        String[] headers;
        int[] widths;
        List<String[]> values;
        String footer;
        String footerTitle;
        private boolean startOnNewPage;

        public Table() {
            values = new ArrayList<String[]>();
            startOnNewPage = true;
        }

        public Table(String title, String[] headers, List<String[]> values) {
            this();
            this.title = title;
            this.headers = headers;
            this.values = values;
        }

        public Table(String title, List<? extends Tablizable> vals, String[] headers) {
            this();
            this.title = title;
            this.headers = headers;

            for (Tablizable tablizable : vals) {

                List<String> val = new ArrayList<String>();
                for (Object o : tablizable.getTableLine()) {
                    val.add(o.toString());
                }

                values.add(val.toArray(new String[0]));
            }
        }

        public Table(String title, TableModel tm) {
            this();
            this.title = title;
            List<String> h = new ArrayList<String>();

            for (int i = 0; i < tm.getColumnCount(); i++) {
                h.add(tm.getColumnName(i));
            }

            headers = h.toArray(new String[0]);

            for (int i = 0; i < tm.getRowCount(); i++) {
                List<String> c = new ArrayList<String>();
                for (int j = 0; j < tm.getColumnCount(); j++) {
                    c.add(tm.getValueAt(i, j).toString());
                }
                values.add(c.toArray(new String[0]));
            }
        }

        public Table(String title, List<Tablizable> tablizables) {
            this();
            if (!tablizables.isEmpty()) {
                headers = tablizables.get(0).getTableHeaders();
            }

            for (Tablizable t : tablizables) {
                List<String> val = new ArrayList<String>();
                for (Object o : t.getTableLine()) {
                    val.add(o.toString());
                }

                values.add(val.toArray(new String[0]));
            }
        }

        public boolean isStartOnNewPage() {
            return startOnNewPage;
        }

        public void setStartOnNewPage(boolean startOnNewPage) {
            this.startOnNewPage = startOnNewPage;
        }

        public String getFooter() {
            return footer;
        }

        public void setFooter(String footer) {
            this.footer = footer;
        }

        public String getFooterTitle() {
            return footerTitle;
        }

        public void setFooterTitle(String footerTitle) {
            this.footerTitle = footerTitle;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public void setWidths(int[] widths) {
            this.widths = widths;
        }
    }
}
