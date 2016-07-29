package ar.com.init.agros.util.gui.table;

import ar.com.fdvs.dj.domain.builders.BuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.init.agros.reporting.ExportTypeEnum;
import ar.com.init.agros.reporting.dj.AbstractDJReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase para exportar a pdf y excel clases de TableModel correspondientes a JTables.
 *
 *
 * @author gmatheu
 * @version 21/11/2009 
 */
public class TableModelExporter {

    /** Constructor por defecto de TableModelExporter */
    private TableModelExporter() {
    }

    public static void exportToPDF(final TableModel tm, final String title, String file) throws JRException, Exception {
        if (!file.endsWith(".pdf")) {
            file += ".pdf";
        }

        export(tm, title, file, ExportTypeEnum.PDF);
    }

    public static void exportToExcel(final TableModel tm, String title, String file) throws JRException, Exception {
        if (!file.endsWith(".xls")) {
            file += ".xls";
        }

        export(tm, title, file, ExportTypeEnum.XLS);
    }

    private static void export(final TableModel tm, final String title, final String file, final ExportTypeEnum exportTypeEnum) throws JRException, Exception {
        final int cols = tm.getColumnCount();
        final int rows = tm.getRowCount();

        if (rows == 0) {
            return;
        }

        AbstractDJReport djReport = new AbstractDJReport() {

            @Override
            protected void createLayout() throws BuilderException {
                DynamicReportBuilder drb = getReportBuilder();
                drb.setTitle(title); //defines the title of the report
                drb.setReportName(title);

                for (int c = 0; c < cols; c++) {
                    String name = tm.getColumnName(c);
                    drb.addColumn(createStringColumn("c" + c, name, 85));
                }
            }

            @Override
            protected boolean createDataSource() {
                List<HashMap<String, Object>> coll = new ArrayList<HashMap<String, Object>>();

                for (int r = 0; r < rows; r++) {

                    HashMap<String, Object> row = new HashMap<String, Object>();
                    for (int c = 0; c < cols; c++) {
                        Object o = tm.getValueAt(r, c);

                        row.put("c" + c, o.toString());
                    }

                    coll.add(row);
                }

                createCollectionDataSource(coll);

                return (coll.size() > 0);
            }
        };

        djReport.exportReport(exportTypeEnum, file);
    }
}
