package ar.com.init.agros.util.gui.components;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Clase ExportFileChooser
 *
 *
 * @author gmatheu
 * @version 22/11/2009 
 */
public class ExportFileChooser {

    private static JFileChooser fileChooser = new JFileChooser();

    /** Constructor por defecto de ExportFileChooser */
    public ExportFileChooser() {
    }

    private static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle("Seleccionar Archivo...");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        return fileChooser;
    }

    public static String showFileChooser(Component parent, FileFilter fileFilter, int dialogType) {
        JFileChooser fc = getFileChooser();

        fc.setSelectedFile(new File(""));
        fc.resetChoosableFileFilters();
        fc.setDialogType(dialogType);
        fc.setFileFilter(fileFilter);
        int r = fc.showSaveDialog(parent);

        if (r == JFileChooser.CANCEL_OPTION || fc.getSelectedFile() == null) {
            return "";
        }

        return fc.getSelectedFile().getAbsolutePath();
    }

    public static String showPDFFileChooser(Component parent) {
        return showFileChooser(parent, new FileNameExtensionFilter("Archivos PDF", "pdf"), JFileChooser.SAVE_DIALOG);
    }

    public static String showExcelFileChooser(Component parent) {
        return showFileChooser(parent, new FileNameExtensionFilter("Archivos de Excel (*.xls)", "xls"), JFileChooser.SAVE_DIALOG);
    }

    public static String showZipFileChooser(Component parent, int dialogType) {
        String r = showFileChooser(parent, new FileNameExtensionFilter("Archivos Comprimidos (*.zip)", "zip"), dialogType);

        if (r != null && !r.isEmpty()) {
            if (!r.endsWith(".zip")) {
                r += ".zip";
            }
        } else {
            r = null;
        }
        return r;
    }
}
