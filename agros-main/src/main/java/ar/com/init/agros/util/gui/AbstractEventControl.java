package ar.com.init.agros.util.gui;

import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Clase AbstractEventControl
 *
 *
 * @author fbobbio
 * @version 04-jun-2009 
 */
public abstract class AbstractEventControl implements ActionListener {

    /** Constructor por defecto de AbstractEventControl */
    public AbstractEventControl() {
    }

    /** Método que cierra la ventana y libera los recursos */
    public void closeWindow(Window frame) {
        getFrameNotifier().showOkMessage();
        frame.dispose();
    }

    public abstract FrameNotifier getFrameNotifier();

    /** Método de validación de campos que se encarga de mostrar los errores en el framenotifier o en diálogos
     *  @param la entidad que se desea validar
     *  @return el valor booleano indicando true: si la ventana no posee errores; false: si posee algún error
     */
    public boolean validateInput(BaseEntity entity) {
        return validateInput(entity, true);
    }

    /** Método de validación de campos que se encarga de mostrar los errores en el framenotifier o en diálogos
     *  @param la entidad que se desea validar
     *  @param showMessage determina si se muestra el mensaje en el FrameNotifier
     *  @return el valor booleano indicando true: si la ventana no posee errores; false: si posee algún error
     */
    @SuppressWarnings("unchecked")
    public boolean validateInput(BaseEntity entity, boolean showMessage) {
        boolean ret = true;
        StringBuilder message = new StringBuilder();

        ClassValidator validator = new ClassValidator(entity.getClass());
        List<InvalidValue> invalidValues = Arrays.asList(validator.getInvalidValues(entity));

        for (InvalidValue invalidValue : invalidValues) {
            String property = invalidValue.getPropertyName();
            property = convertToReadable(property);

            if (entity.getClass().getName().equals("ar.com.init.agros.model.almacenamiento.ValorSemilla") && property.equals("Semilla")) {
                property = "Variedad";
            }

            message.append(property).append(" (").append(invalidValue.getMessage()).append(")\n");
            ret = false;
        }
        if (!ret && showMessage) {
            try {
                getFrameNotifier().showErrorMessage(message.toString());
            } catch (NullPointerException ex) {
                Logger.getLogger(AbstractEventControl.class.getName()).log(Level.SEVERE,
                        "Implementación de FrameNotifier incorrecta", ex);
            }
        }
        return ret;
    }

    /** Método de validación de colecciones de entidades que se encarga de mostrar los errores en el framenotifier o en diálogos.
     *  @param las entidades que se desean validar
     *  @return las entidades con errores de validacion.
     */
    @SuppressWarnings("unchecked")
    public List<BaseEntity> validateInput(List<? extends BaseEntity> entities) {
        List<BaseEntity> r = new ArrayList<BaseEntity>();

        for (BaseEntity baseEntity : entities) {
            if (!validateInput(baseEntity)) {
                r.add(baseEntity);
            }
        }

        return r;
    }

    /** Método que muestra un mensaje de error en un diálogo
     * @param frame el frame sobre el cual se mostrará el diálogo
     * @param msg el mensaje a mostrar
     * @param title el título del diálogo
     */
    public void showDialogErrorMessage(Window frame, String title, String msg) {
        GUIUtility.showErrorMessage(frame, title, msg);
    }

    /** Método que muestra un mensaje de información en un diálogo
     * @param frame el frame sobre el cual se mostrará el diálogo
     * @param msg el mensaje a mostrar
     * @param title el título del diálogo
     */
    public void showDialogInfoMessage(Window frame, String title, String msg) {
        GUIUtility.showInformationMessage(frame, title, msg);
    }

    /** Método que muestra un mensaje de advertencia en un diálogo
     * @param frame el frame sobre el cual se mostrará el diálogo
     * @param msg el mensaje a mostrar
     * @param title el título del diálogo
     */
    public void showDialogWarningMessage(Window frame, String title, String msg) {
        GUIUtility.showWarningMessage(frame, title, msg);
    }

    /** Método que muestra un mensaje de confirmacion
     * @param frame el frame sobre el cual se mostrará el diálogo
     * @param msg el mensaje a mostrar
     * @param title el título del diálogo
     * @return el resultado de la confirmacio
     */
    public boolean showConfirmMessage(Window frame, String title, String msg) {
        int c = GUIUtility.askQuestionMessage(frame, title, msg);

        return (c == JOptionPane.YES_OPTION);
    }

    public void clean(JTextComponent... text) {
        for (int i = 0; i < text.length; i++) {
            GUIUtility.cleanJTextComponent(text[i]);
        }
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString() {
        return super.toString();
    }

    private String convertToReadable(String property) {
        if (property != null && property.length() > 0) {
            property = property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        Matcher wordMatcher = Pattern.compile("([A-Z]|[a-z])[a-z]*").matcher(property);
        String readableProperty = "";
        while (wordMatcher.find()) {
            String word = wordMatcher.group();
            readableProperty += " " + word;
        }
        property = readableProperty.length() > 0 ? readableProperty.trim() : property;
        return property;
    }
}
