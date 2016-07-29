package ar.com.init.agros.conf;

import java.io.File;

/**
 * Clase GuiaFitosanitariaMgr
 *
 *
 * @author gmatheu
 * @version 28/08/2009 
 */
public class GuiaFitosanitariaMgr
{

    public static final String GUIA_FITOSANITARIA_FOLDER_KEY = "GUIA_FITOSANITARIA_FOLDER";
    public static final String GUIA_FITOSANITARIA_AUTOMATIC_SHOW_DISABLED_KEY = "GUIA_FITOSANITARIA_AUTOMATIC_SHOW_DISABLED";
    public static String INSTALL_PATH;
    public static final String PDF_PREFIX = "Biblioteca" + File.separator + "pdf";
    public static final String PDF_SUFFIX = ".pdf";
    public static final String DEFAULT_INSTALL_PATH =
            "C:" +
            File.separator + "Archivos de Programa" + File.separator + "CASAFE" + File.separator + "Guía de Productos Fitosanitarios 2009";

    public static void loadConfiguration(ConfMgr manager)
    {
        String value = manager.getController().findValueByKey(GUIA_FITOSANITARIA_FOLDER_KEY);

        if (value == null) {
            INSTALL_PATH = DEFAULT_INSTALL_PATH;
        }
        else {
            INSTALL_PATH = value;
        }
    }

    public static boolean checkCarpetaGuia()
    {
        if (INSTALL_PATH.equals(DEFAULT_INSTALL_PATH)) {
            return checkCarpetaGuia(INSTALL_PATH);
        }
        else {
            return true;
        }
    }

    public static boolean checkCarpetaGuia(String path)
    {
        File exe = new File(path + File.separator + "guia.exe");
        File mdb = new File(path + File.separator + "GuiaFitosanitaria.mdb");

        return exe.exists() && mdb.exists();
    }

    public static String translateToPdfPath(String fileName)
    {
        return INSTALL_PATH + File.separator + PDF_PREFIX + File.separator + fileName + PDF_SUFFIX;
    }
}
