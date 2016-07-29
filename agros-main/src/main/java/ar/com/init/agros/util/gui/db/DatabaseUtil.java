package ar.com.init.agros.util.gui.db;

import ar.com.init.agros.db.h2.BackupTool;
import ar.com.init.agros.util.db.DatabaseImporter;
import ar.com.init.agros.util.db.VersionChecker;
import ar.com.init.agros.util.db.Versions;
import ar.com.init.agros.util.gui.components.ExportFileChooser;
import ar.com.init.agros.view.Application;
import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.xito.dialog.DialogManager;

/**
 * 
 * @author gmatheu
 */
public class DatabaseUtil {

    public static final String MENSAJE_PERDIDA_DATOS = "Se perder\u00e1n TODOS los datos actuales de la aplicaci\u00f3n. \u00bfDesea continuar?.";
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
    public static Versions CURRENT_VERSION = Versions.V1_2;

    public static void realizarCopiaBD(Frame owner) {
        try {

            String path = ExportFileChooser.showZipFileChooser(owner, JFileChooser.SAVE_DIALOG);

            if (path != null) {

                BackupTool.backup(path);
                VersionChecker.addVersionEntry(path, CURRENT_VERSION);
                DialogManager.showInfoMessage(owner, "Copia de Seguridad de Base Datos",
                        "Copia de Seguridad Realizada Correctamente");
            }
        } catch (Exception ex) {
            DialogManager.showError(owner, "Copia de Seguridad de Base Datos",
                    "Hubo un error mientras se realizaba la copia de seguridad.",
                    ex);
            logger.log(Level.SEVERE, "Exception while backing up DB", ex);
        }
    }

    private static boolean showMensajeConfirmacion(String msg, String title) {
        int r = JOptionPane.showConfirmDialog(Application.getApplication().getMainFrame(), msg, "Advertencia - " + title,
                JOptionPane.YES_NO_OPTION);

        return r == JOptionPane.YES_OPTION;
    }

    public static void restaurarBD(Frame owner) {
        String title = "Restaurar Base Datos";
        try {

            if (showMensajeConfirmacion(MENSAJE_PERDIDA_DATOS, title)) {
                String path = ExportFileChooser.showZipFileChooser(owner, JFileChooser.OPEN_DIALOG);

                if (path != null) {
                    try {
                        String version = VersionChecker.checkVersion(path);

                        if (version.equals(CURRENT_VERSION.id())) {
                            if (showMensajeConfirmacion(MENSAJE_PERDIDA_DATOS, title)) {
                                BackupTool.restore(path);
                                DialogManager.showInfoMessage(owner, title,
                                        "Base de Datos restaurada correctamente.");
                                DialogManager.showInfoMessage(owner, title,
                                        "Se debe reiniciar la aplicación para aplicar los cambios.");
                            }
                        } else {
                            DialogManager.showInfoMessage(owner, title,
                                    String.format("Base de datos de versión incorrecta (%s). Sólo se puede restaturar de versión %s.", version, CURRENT_VERSION.id()));
                        }
                    } catch (IllegalArgumentException iae) {
                        DialogManager.showInfoMessage(owner, title,
                                "Base de datos desconocida. No se puede importar.");
                    }
                }
            }
        } catch (Exception ex) {
            DialogManager.showError(owner, "Restaurar Base Datos",
                    "Hubo un error mientras se restauraba la base de datos.",
                    ex);
            logger.log(Level.SEVERE, "Exception while restoring DB", ex);
        }
    }

    public static void importarBD(Frame owner) {
        String title = "Importar Base Datos";

        try {

            String path = ExportFileChooser.showZipFileChooser(owner, JFileChooser.OPEN_DIALOG);

            if (path != null) {

                DatabaseImporter importer = new DatabaseImporter(path);

                try {
                    String version = VersionChecker.checkVersion(path);

                    if (version.equals(Versions.V1_0.id())) {

                        if (showMensajeConfirmacion(String.format(
                                "Se ha detectado una Base de Datos de Osiris v%s.\r\n" + "¿Continuar?", version), title)) {

                            if (showMensajeConfirmacion(String.format(
                                    MENSAJE_PERDIDA_DATOS, version), title)) {
                                importer.importData();

                                DialogManager.showInfoMessage(owner, title,
                                        "Datos importados correctamente.");
                                DialogManager.showInfoMessage(owner, title,
                                        "Se debe reiniciar la aplicación para aplicar los cambios.");
                            }
                        }
                    } else {
                        DialogManager.showInfoMessage(owner, title,
                                String.format("Base de datos de versión incorrecta (%s). Sólo se puede importar de versión %s.", version, Versions.V1_0.id()));
                    }

                } catch (IllegalArgumentException iae) {
                    DialogManager.showInfoMessage(owner, title,
                            "Base de datos desconocida. No se puede importar.");
                }


            }
        } catch (Exception ex) {
            DialogManager.showError(owner, title,
                    "Hubo un error mientras se importaba la base de datos.",
                    ex);
            logger.log(Level.SEVERE, "Exception while restoring DB", ex);

        }
    }
}
