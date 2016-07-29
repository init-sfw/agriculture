package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.GUIConstants;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.JCurrencyTextField;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * Class LocaleCurrencyInputVerifier
 *
 *
 * @author init() software
 * @version 03/02/2008 
 */
public class LocaleCurrencyInputVerifier extends InputVerifier
{

    private FrameNotifier frameNotifier;

    public LocaleCurrencyInputVerifier(FrameNotifier fm)
    {
        frameNotifier = fm;
    }

    @Override
    public boolean verify(JComponent input)
    {
        final JCurrencyTextField source = (JCurrencyTextField) input;
        @SuppressWarnings("deprecation")
        String text = source.getText();
        if (text.length() == 0)
        {
            GUIUtility.restoreBorder(input);
            return true;
        }
        Number number = -1;
        try
        {
            number = parseStringToCurrency(text.replaceFirst("\\" + GUIConstants.CURRENCY_HEADING.trim(), ""));
            source.setDoubleAsText(number.doubleValue());
        }
        catch (ParseException e)
        {
            frameNotifier.showErrorMessage("Formato de precio no válido, ingrese un valor de moneda correcto");
            GUIUtility.setRedBorder(input);
            return false;
        }
        if (number == null || (text.length() != 0 && number.doubleValue() < 0))
        {
            frameNotifier.showErrorMessage("Formato de precio no válido, ingrese un valor de moneda correcto");
            GUIUtility.setRedBorder(input);
            return false;
        }
        else
        {
            frameNotifier.showOkMessage();
            GUIUtility.restoreBorder(input);
            return true;
        }
    }

    /** Método que parsea un string a un currency sin separadores de grupos y adecuado al locale
     *  y devuelve una instancia de Number con el número correspondiente
     * @param text
     * @return una instancia de Number con el número correspondiente
     * @throws java.text.ParseException 
     */
    public static Number parseStringToCurrency(String text) throws ParseException
    {
        text = text.trim();
        ParsePosition pp = new ParsePosition(0);
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(GUIConstants.GUI_LOCALE);
        df.setGroupingUsed(false);
        Number number = df.parse(text, pp);
        if (pp.getIndex() != text.length())
        {
            throw new ParseException("Error al parsear", pp.getIndex());
        }
        return number;
    }
}
