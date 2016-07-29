package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.GUIUtility;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase NaturalNumberInputVerifier que sirve para validar campos que se desean restringir a ingreso de valores
 * naturales (enteros mayores o iguales que cero)
 *
 *
 * @author fbobbio
 * @version 03-jun-2009 
 */
public class NaturalNumberInputVerifier extends InputVerifier
{
    private FrameNotifier frameNotifier;

    /** Constructor de NaturalNumberInputVerifier 
     * @param fm el FrameNotifier de la ventana que tendrá el input verifier
     */
    public NaturalNumberInputVerifier(FrameNotifier fm)
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
            frameNotifier.showErrorMessage("Formato de número no válido, ingrese un número entero y positivo");
            GUIUtility.setRedBorder(input);
            return false;
        }
        if ((text.length() != 0 && number < 0))
        {
            frameNotifier.showErrorMessage("Formato de número no válido, ingrese un número positivo");
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
