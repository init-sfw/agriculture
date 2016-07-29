package ar.com.init.agros.view.campo.inputverifier;

import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.components.panels.PanelSeleccionLotesSublotes;
import java.text.ParseException;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Clase SuperficieInputVerifier
 *
 *
 * @author fbobbio
 * @version 12-ago-2009 
 */
public class SuperficieInputVerifier extends DecimalInputVerifier
{

    private PanelSeleccionLotesSublotes panelSeleccionLotes;

    public SuperficieInputVerifier(FrameNotifier fm, int precision, boolean allowNegativeValues, Double maxValue, Double minValue, PanelSeleccionLotesSublotes panelSeleccionLotes)
    {
        super(fm, precision, allowNegativeValues, maxValue, minValue);
        this.panelSeleccionLotes = panelSeleccionLotes;
    }

    public SuperficieInputVerifier(FrameNotifier fm, int precision, boolean allowNegativeValues, Double maxValue, PanelSeleccionLotesSublotes panelSeleccionLotes)
    {
        this(fm, precision, allowNegativeValues, maxValue, null, panelSeleccionLotes);
    }

    @Override
    public boolean verify(JComponent input)
    {
        boolean val = super.verify(input);
        if (val)
        {
            JTextComponent source = (JTextComponent) input;
            double sup = 0;
            try
            {
                sup = GUIUtility.NUMBER_FORMAT.parse(source.getText().trim()).doubleValue();
            }
            catch (ParseException e)
            {
                sup = 0;
            }
            panelSeleccionLotes.setSuperficieTotal(sup);
            return val;
        }
        else
        {
            return false;
        }
    }
}
