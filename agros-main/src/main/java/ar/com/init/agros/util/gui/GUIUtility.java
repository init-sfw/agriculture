package ar.com.init.agros.util.gui;

import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.util.gui.validation.DocumentSizeFilter;
import ar.com.init.agros.view.Application;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXTable;

/**
 * Clase GUIUtility que ofrece servicios a las interfaces gráficas de usuario
 *
 * @author Federico Sebastián Bobbio - init() software
 * @version 22 de agosto de 2007
 */
public class GUIUtility {

    public static final String DEFAULT_COMBO_VALUE = "Seleccionar...";
    public static final Locale SYSTEM_LOCALE = new Locale(System.getProperty("user.language"));

    //FIX: activar esto al momento de implementar y cambiarlo de clase
    /** Método que se encarga de loguear el error cuando se carga un componente visual que utiliza funciones de la
     * base de datos, deberá estar desactivado el logueo mientras estemos en un entorno de desarrollo y se deberá
     * activar en ela implementación
     * @param aClass
     * @param e
     */
    public static void logPersistenceError(Class aClass, PersistenceException e) {
//        Logger.getLogger(aClass.getName()).log(Level.SEVERE, "Error de base de datos", e);
//        try
//        {
//            // Create file
//            FileWriter fstream = new FileWriter("C:/out.txt");
//            BufferedWriter out = new BufferedWriter(fstream);
//            out.write(e.getLocalizedMessage());
//            for (StackTraceElement el : e.getStackTrace())
//            {
//                out.write(el.toString());
//            }
//            //Close the output stream
//            out.close();
//        }
//        catch (Exception ex)
//        {//Catch exception if any
//            System.err.println("Error: " + ex.getMessage());
//        }
    }

    public static void logError(Class aClass, Exception e) {
        Logger.getLogger(aClass.getName()).log(Level.SEVERE, "Error de base de datos", e);
        try {
            // Create file
            FileWriter fstream = new FileWriter("." + File.pathSeparator + "out.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(e.getLocalizedMessage());
            for (StackTraceElement el : e.getStackTrace()) {
                out.write(el.toString());
            }
            //Close the output stream
            out.close();
        } catch (Exception ex) {//Catch exception if any
            System.err.println("Error: " + ex.getMessage());
        }
    }

    public static void showToolTipText(JComponent component, String toolTipText) {
        component.setToolTipText(toolTipText);
        component.dispatchEvent(new KeyEvent(
                component,
                KeyEvent.KEY_PRESSED,
                0,
                KeyEvent.CTRL_MASK,
                KeyEvent.VK_F1,
                KeyEvent.CHAR_UNDEFINED));
    }
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(3);
        NUMBER_FORMAT.setGroupingUsed(false);
    }
    private static final Border redBorder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0), 2);

    /** Método que pone el borde rojo a un componente
     * @param input el componente al cual setearle el borde
     */
    public static void setRedBorder(JComponent input) {
        input.setBorder(redBorder);
    }

    /** Método que devuelve, al componente, el borde que poseía antes de ser modificado
     * @param input el componente al cual setearle el borde
     */
    public static void restoreBorder(JComponent input) {
        if (input != null && input.getBorder() != null && input.getBorder().equals(redBorder)) // si está puesto el borde rojo lo reseteo, si no no
        {
            String nameUI = input.getUIClassID();
            String key = nameUI.substring(0, nameUI.indexOf("UI")) + ".border";
            Border b = (Border) UIManager.getLookAndFeelDefaults().get(key);
            input.setBorder(b);
        }
    }

    /** Método que setea el máximo de caracteres que se permiten ingresar en un componente
     *  de texto JTextComponent, utilizando un filtro de tamaño.
     *  @param component la referencia al JTextComponent que se desea limitar en ingreso de caracteres
     *  @param maxChars el máximo de caracteres que el JTextComponent permitirá
     */
    public static void setMaxAllowedChars(JTextComponent component, int maxChars) {
        DocumentSizeFilter sizeFilter = new DocumentSizeFilter(maxChars);
        AbstractDocument doc;
        Document doci = component.getDocument();
        if (doci instanceof AbstractDocument) {
            doc = (AbstractDocument) doci;
            doc.setDocumentFilter(sizeFilter);
        }
    }

    /** Método que realiza la actualización sobre una lista particular dependiendo de los datos que reciba por parámetro,
     *  a partir de una colección que posee los datos que se desean mostrar.
     *  @param list la referencia al objeto JList que se desea actualizar
     *  @param data la referencia a la colección que contiene los objetos que a su vez contienen datos a agregar en la lista
     */
    public static void refreshList(JList list, List data) {
        if (data.size() > 0) {
            clearList(list);
            list.setListData(data.toArray());
        }
    }

    /** Método que actualiza un comboBox
     *
     * @param list con la cual llenar el combobox
     * @param cbx el combobox
     */
    public static void refreshComboBox(List<? extends Listable> list, JComboBox cbx) {
        Object selected = cbx.getSelectedItem();
        Vector<Object> aux = new Vector<Object>();
        aux.add(DEFAULT_COMBO_VALUE);
        aux.addAll(list);
        cbx.setModel(new DefaultComboBoxModel(aux));
        if (selected instanceof Listable && list != null && list.contains(selected)) {
            cbx.setSelectedItem(selected);
        }
    }

    /** Método que limpa el model de un comboBox
     *
     * @param cbx el combobox
     */
    public static void restoreComboBox(JComboBox cbx) {
        Vector<Object> aux = new Vector<Object>();
        aux.add(DEFAULT_COMBO_VALUE);
        cbx.setModel(new DefaultComboBoxModel(aux));
    }

    /** Método que se encarga de limpiar las filas de una lista
     * @param list la referencia a la lista que debe ser limpiada
     */
    public static void clearList(JList list) {
        list.setListData((Object[]) null);
    }

    /** Método que realiza la actualización de un objeto JTable recibiendo los datos a cargar
     *  de la tabla. Crea un modelo de tabla y se lo asigna a la misma.
     *  @param table la referencia a la tabla que se desea actualizar
     *  @param data la referencia al objeto Vector que posee los datos a cargar en cada fila del JTable
     */
    public static void refreshTable(JXTable table, List<? extends TablizableEntity> data) {
        DefaultTableModel def = (DefaultTableModel) table.getModel();
        clearTable(table);
        setTableRowCount(table, data.size());
        for (int i = 0; i < data.size(); i++) {
            List<Object> aux = data.get(i).getTableLine();
            for (int j = 0; j < aux.size(); j++) {
                def.setValueAt(aux.get(j), i, j);
            }
        }
        table.packAll();
    }

    /** Método que realiza la actualización de un objeto JTable que poseerá X columnas únicamente y
     *  la primera será SIEMPRE una columna para selección con CheckBox's
     *  @param table la referencia a la tabla que se desea actualizar
     *  @param data la referencia a la colección que posee los datos a cargar en la fila del JTable
     *  @param selecteds referencia a la colección que posee los datos a marcar como seleccionados
     */
    public static void refreshSelectionTable(JXTable table, List<? extends Listable> data, List<? extends Listable> selecteds) {
        DefaultTableModel def = (DefaultTableModel) table.getModel();
        clearTable(table);
        setTableRowCount(table, data.size());
        for (int i = 0; i < data.size(); i++) {
            Listable aux = data.get(i);
            def.setValueAt(new Boolean(selecteds.contains(aux)), i, 0);
            def.setValueAt(aux, i, 1);
        }
        table.packAll();
    }

    /** Método que realiza devuelve los valores seleccionados de una tabla de 2 columnas con la primera como checkbox
     *  @param table la referencia a la tabla que se desea actualizar
     */
    public static List<? extends Listable> getSelectionTableData(JXTable table) {
        int rows = table.getModel().getRowCount();
        List<Listable> ret = new ArrayList<Listable>();
        DefaultTableModel def = (DefaultTableModel) table.getModel();
        for (int i = 0; i < rows; i++) {
            boolean val = (Boolean) def.getValueAt(i, 0);
            if (val) {
                ret.add((Listable) def.getValueAt(i, 1));
            }
        }
        return ret;
    }

    /** Método que marca toda la columna de checkbox como seleccionados
     *
     * @param table
     */
    public static void selectAllSelectionTable(JXTable table) {
        DefaultTableModel def = (DefaultTableModel) table.getModel();
        for (int i = 0; i < def.getRowCount(); i++) {
            def.setValueAt(new Boolean(true), i, 1);
        }
        table.packAll();
    }

    /** Método que desmarca la columna de checkboxs
     *
     * @param table
     */
    public static void deselectAllSelectionTable(JXTable table) {
        DefaultTableModel def = (DefaultTableModel) table.getModel();
        for (int i = 0; i < def.getRowCount(); i++) {
            def.setValueAt(new Boolean(false), i, 1);
        }
        table.packAll();
    }

    /** Método que devuelve un objeto determinado del model de una JXTable (SwingX) recibiendo la tabla,
     *  el número de fila de la vista y el identificador de la columna que se requiere.
     *  Como resultado el método convierte el índice de la vista al correspondiente en el model y devuelve
     *  el objeto que se encuentra en la columna especificada por el identificador, esté este visible o
     *  no en la vista de la tabla.
     * @param table la tabla de SwingX de la cual se desea extraer información
     * @param viewRow la fila correspondiente de la vista de la tabla de la cual se quiere conocer algún dato
     * @param identifier el nombre de la columna del dato que se quiere recuperar, esté o no visible
     * @return 
     */
    public static Object getValueFromModel(JXTable table, int viewRow, String identifier) {
        return table.getModel().getValueAt(table.convertRowIndexToModel(viewRow), table.getColumnExt(
                identifier).getModelIndex());
    }

    /** Método inserta un objeto determinado en el model de una JXTable (SwingX) recibiendo la tabla,
     *  el objeto a insertar, el número de fila de la vista y el identificador de la columna que se requiere.
     *  Como resultado el método convierte el índice de la vista al correspondiente en el model e inserta
     *  el objeto en la fila y en la columna especificada por el identificador, esté este visible o
     *  no en la vista de la tabla.
     * @param table la tabla de SwingX a la cual se desea insertar información
     * @param value el objeto que se desea insertar en el model
     * @param viewRow la fila correspondiente de la vista de la tabla en la cual se desea insertar algún dato
     * @param identifier el nombre de la columna del dato que se quiere insertar, esté o no visible
     */
    public static void setValueToModel(JXTable table, Object value, int viewRow, String identifier) {
        table.getModel().setValueAt(value, table.convertRowIndexToModel(viewRow), table.getColumnExt(
                identifier).getModelIndex());
    }

    public static int[] getModelSelectedRows(JXTable table) {
        int[] ret = new int[table.getSelectedRows().length];
        for (int i = 0; i < table.getSelectedRows().length; i++) {
            ret[i] = table.convertRowIndexToModel(table.getSelectedRows()[i]);
        }
        return ret;
    }

    /** Método que devuelve el índice correspondiente en el modelo de datos de la JXTable (SwingX) recibiendo
     *  la tabla y el identificador de la columna buscado.
     *  Se devuelve el índice correspondiente de esa columna en el modelo de datos de la tabla.
     * @param table la tabla
     * @param identifier el identificador de la columna
     * @return el índice correspondiente de esa columna en el modelo de datos de la tabla
     */
    public static int getModelColumnIndex(JXTable table, String identifier) {
        return table.getColumnExt(identifier).getModelIndex();
    }

    /** Método que devuelve el índice correspondiente en el modelo de datos de la JXTable (SwingX) recibiendo
     *  la tabla y el índice de la fila en el modelo de vista.
     *  Se devuelve el índice correspondiente de esa fila en el modelo de datos de la tabla.
     * @param table la tabla
     * @param viewIndex el índice de la fila en el modelo de vista
     * @return el índice de la fila en el modelo de datos
     */
    public static int getModelRowIndex(JXTable table, int viewIndex) {
        return table.convertRowIndexToModel(viewIndex);
    }

    /** Método que devuelve el índice correspondiente a la vista en el modelo de datos de la JXTable (SwingX) recibiendo
     *  la tabla.
     *  Se devuelve el índice correspondiente de la fila seleccionada en el modelo de datos de la tabla.
     * @param table la tabla
     * @return el índice de la fila seleccionada en el modelo de datos
     */
    public static int getModelViewRowIndex(JXTable table) {
        return table.convertRowIndexToModel(table.getSelectedRow());
    }

    /** Método que setea la cantidad de filas de una tabla 
     *  @param table la referencia a la tabla
     *  @param rowCount la cantidad de filas que tendrá la tabla
     */
    public static void setTableRowCount(JXTable table, int rowCount) {
        ((DefaultTableModel) table.getModel()).setRowCount(rowCount);
    }

    /** Método que se encarga de limpiar las filas de una tabla
     * @param table la referencia a la tabla cuyas filas quieren ser limpiadas
     */
    public static void clearTable(JXTable table) {
        setTableRowCount(table, 0);
    }

    /** Método que se encarga de eliminar una fila determinada de una tabla
     * @param table la referencia a la tabla de la cual se quiere eliminar una fila
     * @param viewRow el indice de la fila a eliminar en la vista
     */
    public static void removeRowFromTable(JXTable table, int viewRow) {
        ((DefaultTableModel) table.getModel()).removeRow(getModelRowIndex(table, viewRow));
    }

    /** Método que agrega una nueva fila en blanco a la tabla 
     * @param table la referencia a la tabla a la cual se le quiere añadir una fila
     */
    public static void addRowToTable(JTable table) {
        ((DefaultTableModel) table.getModel()).addRow(new Object[table.getColumnCount()]);
    }

    /** Método que recibe un objeto JTable y devuelve un objeto Vector conteniendo los nombres de cada una de las columnas
     *  en cada elemento del vector.
     *  @param table el objeto JTable del que se desea extraer el nombre de todas las columnas
     * @return 
     */
    public static Vector<String> getTableColumnsName(JTable table) {
        Vector<String> columnNames = new Vector<String>();
        for (int i = 0; i < table.getColumnCount(); i++) // buscamos los nombres de las columnas de la tabla
        {
            columnNames.add(table.getColumnName(i));
        }
        return columnNames;
    }

    /** Método que recibe por parámetro un objeto JTextComponent y ejecuta el método de "limpieza" de texto
     *  @param txt la referencia al objeto JTextComponent que desea ser limpiado
     */
    public static void cleanJTextComponent(JTextComponent txt) {
        txt.setText("");
    }

    /** Método que recibe por parámetro un vector JTextComponent y ejecuta el método de "limpieza" de todos 
     *  los campos de texto que posee el vector
     *  @param arrayTxt el vector de referencias a objetos JTextComponent que desean ser limpiados
     */
    public static void cleanJTextComponent(JTextComponent[] arrayTxt) {
        for (int i = 0; i < arrayTxt.length; i++) {
            if (arrayTxt[i] != null) {
                cleanJTextComponent(arrayTxt[i]); // llamado al método que se encarga de limpiar un JTextField
            }
        }
    }

    /** Método que recibe por parámetro un objeto Vector que contiene objetos JTextComponent y ejecuta el método de 
     *  "limpieza" de todos los campos de texto que posee el vector.
     *  @param vectorTxt la referencia al objeto Vector que posee los objetos JTextComponent que desean ser limpiados
     */
    public static void cleanJTextComponent(Vector<JTextComponent> vectorTxt) {
        for (int i = 0; i < vectorTxt.size(); i++) {
            cleanJTextComponent(vectorTxt.get(i)); // llamado al método que se encarga de limpiar un JTextComponent
        }
    }

    /** Método que recibe por parámetro un objeto JComboBox y ejecuta el método de "limpieza" de selección
     *  @param cbx la referencia al objeto JComboBox que desea ser limpiado
     */
    public static void cleanJComboBox(JComboBox cbx) {
        cbx.setSelectedIndex(0);
    }

    /** Method thath notifies an error to the user
     *  @param frame the frame where the message will be shown
     *  @param title the title of the error
     *  @param description the detailed error message
     */
    public static void showErrorMessage(Window frame, String title, String description) {
        showMessage(frame, title, description, JOptionPane.ERROR_MESSAGE);
    }

    /** Method thath notifies information to the user
     *  @param frame the frame where the message will be shown
     *  @param title the title of the info
     *  @param description the detailed information message
     */
    public static void showInformationMessage(Window frame, String title, String description) {
        showMessage(frame, title, description, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Method thath notifies a warning to the user
     *  @param frame the frame where the message will be shown
     *  @param title the title of the warning
     *  @param description the detailed warning message
     */
    public static void showWarningMessage(Window frame, String title, String description) {
        showMessage(frame, title, description, JOptionPane.WARNING_MESSAGE);
    }

    /** Method thath notifies a question to the user
     *  @param frame the frame where the message will be shown
     *  @param title the title of the question
     *  @param description the detailed question message
     */
    public static void showQuestionMessage(Window frame, String title, String description) {
        showMessage(frame, title, description, JOptionPane.QUESTION_MESSAGE);
    }

    public static int askQuestionMessage(Window frame, String title, String question) {
        return JOptionPane.showConfirmDialog(frame, question, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }

    /** Method thath notifies shows a message to the user
     *  @param frame the frame where the message will be shown
     *  @param title the title of the error
     *  @param description the detailed error message
     *  @param messageType
     */
    public static void showMessage(Window frame, String title, String description, int messageType) {
        JOptionPane.showMessageDialog(frame, description, title, messageType);
    }

    public static boolean confirmWarningData(Window frame, String text, String title) {
        int ret = JOptionPane.showConfirmDialog(frame, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ret == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean confirmData(Window frame) {
        int ret = JOptionPane.showConfirmDialog(frame, "¿Está seguro que los datos ingresados son correctos?",
                "Confirmar datos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ret == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    /** Método que maneja algunas configuraciones relacionadas al sistema operativo
     * @param frame la ventana que se desea inicializar
     */
    public static void initWindow(Window frame) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 10;
        int y = (screenSize.height - frame.getHeight()) / 12;
        frame.setLocation(x, y);
        Image logo = kit.getImage(GUIConstants.FRAME_ICON_URL);
        frame.setIconImage(logo);
    }

    /** Método que recibe una cadena que tendrá valores HTML y la devuelve con los tags correspondientes
     * @param text la cadena a convertir
     * @return la cadena convertida
     */
    public static String convertPlainStringToHTML(String text) {
        text = text.replaceAll("\n", "<P>");
        return GUIConstants.HTML_HEADING + text + GUIConstants.HTML_CLOSING;
    }

    /** Método que formatea un objeto Date y devuelve la fecha con formato aaaa/mm/dd
     *  @param date la fecha que se desea formatear
     *  @return la cadena con formato aaaa/mm/dd
     */
    public static String toShortDBDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(date);
    }

    /** Método que formatea un objeto Date y devuelve la fecha con formato dd/mm/aaaa
     *  @param date la fecha que se desea formatear
     *  @return la cadena con formato dd/mm/aaaa
     */
    public static String toMediumDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return f.format(date);
    }

    /** Método que formatea un objeto Date y devuelve la fecha y hora con formato dd/mm/aaaa HH:mm:ss
     *  @param date la fecha que se desea formatear
     *  @return la cadena con formato dd/mm/aaaa
     */
    public static String toFullDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        return f.format(date);
    }

    public static Window getApplicationFrame() {
        return Application.getApplication().getMainFrame();
    }

    /** Método que devuelve el componente Window que contenga al componente que le enviemos por parámetro */
    public static Window getParentFrame(Component c) {
        Window w = (Window) SwingUtilities.getRoot(c);
        if (w == null) {
            w = Application.getApplication().getMainFrame();
        }
        return w;
    }

    public static double redondearYLimitarDecimales(double val) {
        DecimalFormat format = new DecimalFormat("#########0.00");
        Number num = null;
        try {
            num = format.parse(String.valueOf(val));
        } catch (ParseException ex) {
            Logger.getLogger(GUIUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (num != null) {
            return ((Double) num).doubleValue();
        } else {
            return 0;
        }
    }

    /** Redondea un double a 4 decimales */
    public static double redondearDecimales(double val)
    {
        //Redondeo en 4 decimales para evitar errores mágicos
        int red = (int) (val * 10000);
        val = (double)red / 10000;
        return val;
    }

    public static String convertMonth(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "";
        }
    }

    /** Método toString que devuelve la cadena que representa al objeto GUIUtility
     *  @return la cadena que representa al objeto GUIUtility
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
