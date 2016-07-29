package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase RegexInputVerifier realiza validaciones de expresiones regulares.
 *
 *
 * @author gmatheu
 * @version 07/06/2009 
 */
public class RegexInputVerifier extends InputVerifier
{

    /*
     * Various regular expression patterns used to
     * construct convenience objects of this class:
     */
    public static final String TEXT_FIELD = "^(\\S)(.){1,75}(\\S)$";
    public static final String NON_NEGATIVE_INTEGER_FIELD = "(\\d){1,9}";
    public static final String INTEGER_FIELD = "(-)?" + NON_NEGATIVE_INTEGER_FIELD;
    public static final String NON_NEGATIVE_FLOATING_POINT_FIELD =
            "(\\d){1,10}\\.(\\d){1,10}";
    public static final String FLOATING_POINT_FIELD =
            "(-)?" + NON_NEGATIVE_FLOATING_POINT_FIELD;
    public static final String NON_NEGATIVE_MONEY_FIELD = "(\\d){1,15}(\\.(\\d){2})?";
    public static final String MONEY_FIELD = "(-)?" + NON_NEGATIVE_MONEY_FIELD;
    public static final String EMAIL_ADDRESS = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    private FrameNotifier frameNotifier;
    private String message;
    private String regex;

    /** Constructor por defecto de DecimalInputVerifier */
    public RegexInputVerifier(FrameNotifier fm, String regex, String message)
    {
        frameNotifier = fm;
        this.message = message;
        this.regex = regex;
    }

    /** Constructor por defecto de DecimalInputVerifier */
    public RegexInputVerifier(FrameNotifier fm, String regex)
    {
        this(fm, regex, "Formato no valido para el campo");
    }

    @Override
    public boolean verify(JComponent input)
    {
        final JTextComponent source = (JTextComponent) input;
        String text = source.getText().trim();
        if (text.length() == 0) {
            GUIUtility.restoreBorder(input);
            return true;
        }

        if (Pattern.matches(regex, text)) {
            frameNotifier.showOkMessage();
            GUIUtility.restoreBorder(input);
            source.setText(text);
            return true;
        }
        else {
            frameNotifier.showErrorMessage(message);
            GUIUtility.setRedBorder(input);
            return false;
        }
    }
}