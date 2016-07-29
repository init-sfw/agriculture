package ar.com.init.agros.util.gui;

import ar.com.init.agros.model.Divisa;
import ar.com.init.agros.view.ApplicationView;
import java.awt.Color;
import java.util.Locale;

/**
 * Interface GUIConstants que contiene definidas las constantes del framework GUI
 *
 *
 * @author init() software
 * @version 11/01/2008 
 */

public interface GUIConstants
{
    /** Constante que encabeza a todas las cifras monetarias del sistema*/
    public static final String CURRENCY_HEADING = Divisa.getPatron().getAbreviatura() + " ";

    /** Constante que indica el locale a usar en la aplicación */
    public static final Locale GUI_LOCALE = Locale.getDefault();

    /** Color utilizado para llamar la atención al usuario de algún error o dato negativo de relevancia */
    public static final Color FOREGROUND_ERROR_COLOR = Color.RED;

    /** Color utilizado para llamar la atención al usuario de algún dato que necesite advertencia */
    public static final Color FOREGROUND_WARNING_COLOR = Color.BLUE;

    /** Color utilizado por defecto para todos los datos sin necesidad de ser resaltados */
    public static final Color FOREGROUND_OK_COLOR = Color.BLACK;

    /** Color utilizado para remarcar algún dato correctamente ingresado o positivo al usuario */
    public static final Color FOREGROUND_FINE_COLOR = new Color(0,204,0);

    /** Constante que posee la url del ícono para las ventanas del sistema */
    public static final String FRAME_ICON_URL = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(ApplicationView.class).getString("frame.icon");

    /** Ruta del icono de Ok */
    public static final String OK_ICON = "/ar/com/init/agros/util/gui/resources/check.png";

    /** Ruta del icono de error */
    public static final String ERROR_ICON = "/ar/com/init/agros/util/gui/resources/error.png";

    /** Ruta del icono de información */
    public static final String INFO_ICON = "/ar/com/init/agros/util/gui/resources/info.png";

    /** Ruta del icono de actualización */
    public static final String UPDATE_ICON = "/ar/com/init/agros/util/gui/resources/update.png";

    /** Ruta del icono de pregunta o ayuda */
    public static final String QUESTION_ICON = "/ar/com/init/agros/util/gui/resources/question.png";

    /** Ruta del icono de advertencia */
    public static final String WARNING_ICON = "/ar/com/init/agros/util/gui/resources/warning.png";

    /** Ruta del icono de teléfonos */
    public static final String PERSON_ICON = "/ar/com/init/agros/util/gui/components/personas/resources/man.png";

    /** Ruta del icono de teléfonos */
    public static final String PHONES_ICON = "/ar/com/init/agros/util/gui/components/personas/resources/phones.png";

    /** Ruta del icono de teléfonos */
    public static final String ADDRESSES_ICON = "/ar/com/init/agros/util/gui/components/personas/resources/addresses.png";

    /** Ruta del icono de teléfonos */
    public static final String CASAFE_HAB_ICON = "/ar/com/init/agros/view/resources/casafe mini-hab.PNG";

    /** Ruta del icono de teléfonos */
    public static final String CASAFE_DESHAB_ICON = "/ar/com/init/agros/view/resources/casafe mini-deshab.PNG";

    /** Constante que abre un bloque HTML */
    public static final String HTML_HEADING = "<HTML><DIV font: Tahoma plain 11pt;>";

    /** Constante que cierra un bloque HTML */
    public static final String HTML_CLOSING = "</DIV></HTML>";
    
    /** Constante del alto de las filas que utilicen un renderer de text area */
    public static final int TEXTAREA_ROW_HEIGHT = 40;

    public static final String SUPERFICIE_DE_TRABAJO_TEXT = "El usuario puede modificar el valor de la superficie para indicar la superficie real fumigada";
}
