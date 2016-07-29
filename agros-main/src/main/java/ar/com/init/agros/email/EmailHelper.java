package ar.com.init.agros.email;

import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Tablizable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Clase EmailHelper
 *
 *
 * @author gmatheu
 * @version 17/07/2009 
 */
public class EmailHelper {

    public enum TextSize {

        XS("x-small"),
        S("small"),
        M("medium"),
        L("large"),
        XL("x-large");

        private TextSize(String size) {
            this.size = size;
        }
        private String size;

        public String size() {
            return size;
        }
    }
    private StringBuilder builder;

    public EmailHelper() {
        builder = new StringBuilder();
    }

    /**
     * Crea la cadena con las instrucciones pasadas previamente.
     * @return
     */
    public String buildContent() {
        builder.insert(0, "<html><body>");
        builder.append("</body></html>");

        return builder.toString();
    }

    public void writeLogo() {
    }

    /**
     * Crea una tabla HTML con los elementos pasados por parametro.
     * Si la lista no es homogenea, puede que los resultados no sean correctos.
     * Las cabeceras tienen que tener por lo menos igual cantidad que los detalles de los elementos devueltos de la lista.
     * @param tableHeaders cabeceras a usar en la tabla
     * @param list
     */
    public void writeTable(String[] tableHeaders, Iterator<? extends Tablizable> iterator) {
        StringBuilder b = new StringBuilder();

        b.append("<TABLE border=\"1\">");
        b.append("<TR>");
        for (int i = 0; i < tableHeaders.length; i++) {
            b.append("<TH>");
            b.append(tableHeaders[i]);
            b.append("</TH>");
        }
        b.append("</TR>");


        while (iterator.hasNext()) {
            Tablizable tablizable = iterator.next();
            List line = tablizable.getTableLine();

            b.append("<TR>");
            for (int i = 0; i < tableHeaders.length; i++) {
                b.append("<TD>");
                if (line.size() >= i) {
                    Object c = line.get(i);

                    String cell = "";

                    if (c != null) {
                        if (c instanceof Date) {
                            cell = formatDate((Date) c);
                        } else {
                            cell = c.toString();
                        }
                    }

                    b.append(cell);
                }
                b.append("</TD>");
            }
            b.append("</TR>");
        }

        b.append("</TABLE>");
        builder.append(b);
    }

    public void writeTable(String[] tableHeaders, List<? extends Tablizable> list) {
        writeTable(tableHeaders, list.iterator());
    }

    /**
     * Crea una linea con un retorno de carro al final
     * @param line   
     */
    public void writeLine(String line) {
        writeLine((line != null ? line : ""), TextSize.M);
    }

    /**
     * Crea una linea con un retorno de carro al final
     * @param line texto a escribir
     * @param size tamaño de la linea
     */
    public void writeLine(String line, TextSize size) {
        String l = "<SPAN style=\"font-size:" + size.size() + ";\"> " + line + "</SPAN>";

        builder.append(l + "<BR/>");
    }

    public void writeTitle(String title) {

        String l = "<H1>" + title;

        for (int i = 0; i < 60; i++) {
            l += "&nbsp;";
        }
        l += "<img src=\"cid:logo\"/></H1>";

        builder.append(l + "<BR/>");
    }

    public void writeBlankLine() {
        writeLine("");
    }

    /**
     * Crea una linea con el elemento pasado por parametro
     * @param detail
     */
    public void writeDetailLine(Tablizable line) {
        writeDetailLine(line.getTableLine());
    }

    /**
     * Crea una linea con el elemento pasado por parametro
     * @param detail     
     */
    public void writeDetailLine(Object... line) {
        StringBuilder r = new StringBuilder();

        for (int i = 0; i < line.length; i++) {
            Object object = line[i];
            r.append(object.toString() + " ");
        }

        r.append("<BR/>");
        builder.append(r.toString());
    }
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatFullDate(Date date) {
        return FULL_DATE_FORMAT.format(date);
    }
}
