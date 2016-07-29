package ar.com.init.agros.reporting;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.JFreeChart;

/**
 * Clase para exportar chart JFreeChart a diferentes formatos de archivos.
 *
 * @author gmatheu
 * @version 02/09/2009 
 */
public class JFreeChartExporter
{

    /** Constructor por defecto de JFreeChartExporter */
    public JFreeChartExporter()
    {
    }

    /**
     * Saves a chart to a PDF file.
     *
     * @param file  the file.
     * @param chart  the chart.
     * @param width  the chart width.
     * @param height  the chart height.
     * @param mapper  the font mapper.
     *
     * @throws IOException if there is an I/O problem.
     */
    public static void saveChartAsPDF(File file,
            JFreeChart chart,
            int width,
            int height,
            FontMapper mapper) throws IOException
    {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsPDF(out, chart, width, height, mapper);
        out.close();

        Logger.getLogger(JFreeChartExporter.class.getName()).log(Level.INFO, "Exporting chart to: " + file);
    }

    /**
     * Writes a chart to an output stream in PDF format.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the chart width.
     * @param height  the chart height.
     * @param mapper  the font mapper.
     *
     * @throws IOException if there is an I/O problem.
     */
    private static void writeChartAsPDF(OutputStream out,
            JFreeChart chart,
            int width,
            int height,
            FontMapper mapper) throws IOException
    {
        
        Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.addAuthor("Osiris");
            document.addSubject("Osiris");
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            Graphics2D g2 = tp.createGraphics(width, height, mapper);
            Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
            chart.draw(g2, r2D);
            g2.dispose();
            cb.addTemplate(tp, 50, 50);
        }
        catch (DocumentException de) {
            System.err.println(de.getMessage());
        }
        document.close();
    }
}
