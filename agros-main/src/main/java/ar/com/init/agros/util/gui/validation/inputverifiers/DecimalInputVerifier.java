package ar.com.init.agros.util.gui.validation.inputverifiers;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.GUIUtility;
import java.text.ParseException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase DecimalInputVerifier para realizar validaciones de ingreso de números decimales
 *
 *
 * @author fbobbio
 * @version 03-jun-2009 
 */
public class DecimalInputVerifier extends InputVerifier
{

    private FrameNotifier frameNotifier;
    public static final int DEFAULT_PRECISION = 3;
    private boolean allowNegatives;
    private int presicion;
    private Double maxValue;
    private Double minValue;

    /** Constructor por defecto de DecimalInputVerifier */
    public DecimalInputVerifier(FrameNotifier fm)
    {
        this(fm, DEFAULT_PRECISION, true);
    }

    public DecimalInputVerifier(FrameNotifier fm, boolean allowNegativeValues)
    {
        this(fm, DEFAULT_PRECISION, allowNegativeValues);
    }

    public DecimalInputVerifier(FrameNotifier fm, int precision, boolean allowNegativeValues)
    {
        this(fm, precision, allowNegativeValues, null);
    }

    public DecimalInputVerifier(FrameNotifier fm, int precision, boolean allowNegativeValues, Double maxValue)
    {
        this(fm, precision, allowNegativeValues, maxValue, null);
    }

    /**
     *
     * @param fm FrameNotifier donde se reportaran los mensajes
     * @param precision cantidad de decimales permitida
     * @param allowNegativeValues si se permiten valores negativos
     * @param maxValue valor maximo permitido
     * @param minValue valor minimo permitido
     */
    public DecimalInputVerifier(FrameNotifier fm, int precision, boolean allowNegativeValues, Double maxValue, Double minValue)
    {
        frameNotifier = fm;
        this.presicion = precision;
        this.allowNegatives = allowNegativeValues;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
    }

    @Override
    public boolean verify(JComponent input)
    {
        final JTextComponent source = (JTextComponent) input;
        String text = source.getText().trim();
        if (text.length() == 0)
        {
            GUIUtility.restoreBorder(input);
            frameNotifier.showOkMessage();
            return true;
        }
        double number = -1;
        try
        {
            if (text.contains("."))
            {
                throw new ParseException(text, presicion);
            }
            number = GUIUtility.NUMBER_FORMAT.parse(text).doubleValue();
        }
        catch (ParseException e)
        {
            frameNotifier.showErrorMessage("Formato de número no válido, ingrese un número decimal");
            GUIUtility.setRedBorder(input);
            return false;
        }

        int dotIdx = text.indexOf(",");
        if (dotIdx > -1 && (text.substring(dotIdx).length() - 1) > presicion)
        {
            frameNotifier.showErrorMessage(
                    "Formato de número no válido, ingrese hasta " + presicion + " decimales");
            GUIUtility.setRedBorder(input);
            return false;
        }

        if (number < 0 && !allowNegatives)
        {
            frameNotifier.showErrorMessage("Formato de número no válido, ingrese solo valores positivos");
            GUIUtility.setRedBorder(input);
            return false;
        }

        if (minValue != null && number < minValue)
        {
            frameNotifier.showErrorMessage("Valor fuera de rango. El valor mínimo es: " + minValue);
            GUIUtility.setRedBorder(input);
            return false;
        }

        if (maxValue != null && number > maxValue)
        {
            frameNotifier.showErrorMessage("Valor fuera de rango. El valor máximo es: " + maxValue);
            GUIUtility.setRedBorder(input);
            return false;
        }

        frameNotifier.showOkMessage();
        GUIUtility.restoreBorder(input);
        source.setText(text);
        return true;
    }

    public Double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(Double maxValue)
    {
        this.maxValue = maxValue;
    }

    public Double getMinValue()
    {
        return minValue;
    }

    public void setMinValue(Double minValue)
    {
        this.minValue = minValue;
    }
}
