package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.GUIUtility;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase IntegerInputVerifier para realizar validaciones de ingreso de números enteros
 *
 *
 * @author fbobbio
 * @version 03-jun-2009 
 */
public class IntegerInputVerifier extends InputVerifier
{

    private FrameNotifier frameNotifier;

    /** Constructor por defecto de IntegerInputVerifier */
    public IntegerInputVerifier(FrameNotifier fm)
    {
        frameNotifier = fm;
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
        int number = -1;
        try
        {
            number = Integer.parseInt(text);
        }
        catch (NumberFormatException e)
        {
            frameNotifier.showErrorMessage("Formato de número no válido, ingrese un número entero");
            GUIUtility.setRedBorder(input);
            return false;
        }
        frameNotifier.showOkMessage();
        GUIUtility.restoreBorder(input);
        source.setText(text);
        return true;
    }
}
