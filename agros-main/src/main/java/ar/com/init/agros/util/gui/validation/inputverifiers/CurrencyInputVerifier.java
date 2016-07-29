package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.text.ParseException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase CurrencyInputVerifier
 *
 *
 * @author fbobbio
 * @version 27-jun-2009 
 */
public class CurrencyInputVerifier extends InputVerifier
{
    private FrameNotifier frameNotifier;
    private String fieldName;

    /** Constructor por defecto de DecimalInputVerifier
     *
     * @param fm referencia al frame notifier
     * @param fieldName el nombre del campo sobre el que se aplicará el input verifier
     */
    public CurrencyInputVerifier(FrameNotifier fm, String fieldName)
    {
        frameNotifier = fm;
        this.fieldName = fieldName;
    }

    @Override
    public boolean verify(JComponent input)
    {
        final JTextComponent source = (JTextComponent) input;
        String text = source.getText().trim();
        if (text.length() == 0)
        {
            GUIUtility.restoreBorder(input);
            return true;
        }
        double number = -1;
        try
        {
            number = GUIUtility.NUMBER_FORMAT.parse(text).doubleValue();
        }
        catch (ParseException e)
        {
            frameNotifier.showErrorMessage("Formato de moneda no válido en " + fieldName);
            GUIUtility.setRedBorder(input);
            return false;
        }
        if ((text.length() != 0 && number < 0))
        {
            frameNotifier.showErrorMessage("Formato de moneda no válido en " + fieldName);
            GUIUtility.setRedBorder(input);
            return false;
        }
        else
        {
            frameNotifier.showOkMessage();
            GUIUtility.restoreBorder(input);
            source.setText(text);
            return true;
        }
    }
}
