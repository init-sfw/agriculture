package ar.com.init.agros.util.gui.validation.components;

import ar.com.init.agros.util.gui.*;
import ar.com.init.agros.util.gui.validation.inputverifiers.LocaleCurrencyInputVerifier;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JTextField;

/**
 * Class JCurrencyTextField que nos brinda un componente de ingreso de texto para valores monetarios manejando
 * los ingresos.
 *
 * @author init() software
 * @version 22/02/2008 
 */
public class JCurrencyTextField extends JTextField
{

    /** Constructor of JCurrencyTextField que hace q el textField funcione sin cambios */
    public JCurrencyTextField()
    {
        super();
    }

    /** Constructor de JCurrencyTextField que habilita todas las funcionalidades particulares 
     * @param fm 
     */
    public JCurrencyTextField(FrameNotifier fm)
    {
        this(null,fm);
    }

    /** Constructor de JCurrencyTextField que habilita todas las funcionalidades particulares 
     * @param text
     * @param fm 
     */
    public JCurrencyTextField(String text, FrameNotifier fm)
    {
        super(text);
        setInputVerifier(new LocaleCurrencyInputVerifier(fm));
    }

    /**
     * Sets the text of this <code>TextComponent</code>
     * to the specified text.  If the text is <code>null</code>
     * or empty, has the effect of simply deleting the old text.
     * When text has been inserted, the resulting caret location
     * is determined by the implementation of the caret class.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
     * to Use Threads</A> for more information.     
     *
     * Note that text is not a bound property, so no <code>PropertyChangeEvent
     * </code> is fired when it changes. To listen for changes to the text,
     * use <code>DocumentListener</code>.
     *
     * @param t the new text to be set
     * @see #getText
     * @see DefaultCaret
     * @beaninfo
     * description: the text of this component
     * @deprecated por la razón de ser un text field para currency, ver setText(double) o setText(long)
     */
    @Override
    @Deprecated
    public void setText(String t)
    {
        super.setText(t);
    }

    /**
     * Returns the text contained in this <code>TextComponent</code>.
     * If the underlying document is <code>null</code>,
     * will give a <code>NullPointerException</code>.
     *
     * Note that text is not a bound property, so no <code>PropertyChangeEvent
     * </code> is fired when it changes. To listen for changes to the text,
     * use <code>DocumentListener</code>.
     *
     * @return the text
     * @exception NullPointerException if the document is <code>null</code>
     * @see #setText
     * @deprecated usar en cambio getDoubleAsText()
     */
    @Override
    @Deprecated
    public String getText()
    {
        return super.getText();
    }

    public double getValue() throws ParseException
    {
        return convertAnyTextToDoubleAsText(super.getText());
    }

    /** Método que inicializa al text field con el valor correspondiente de la moneda
     * @param t el valor que se desea inicializar
     */
    public void setDoubleAsText(double t)
    {
        super.setText(GUIConstants.CURRENCY_HEADING + convertDoubleToCurrency(t));
    }

    /** Método que devuelve el valor formateado como un double pero de tipo String
     *  @return el valor formateado como un double pero de tipo String
     * @throws java.text.ParseException 
     */
    public String getDoubleAsText() throws ParseException
    {
        return String.valueOf(convertAnyTextToDoubleAsText(super.getText()));
    }
    
    /** Método que devuelve el double correspondiente de cualquier
     *  cadena de texto tipo currency
     * @param text el string con el signo monetario
     * @return el valor double del string
     * @throws java.text.ParseException 
     */
    public static double convertAnyTextToDoubleAsText(String text) throws ParseException
    {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(GUIConstants.GUI_LOCALE);
        df.setGroupingUsed(false);
        Number number = null;
        number = df.parse(text.replaceFirst("\\" + GUIConstants.CURRENCY_HEADING, "").trim());
        return number.doubleValue();
    }

    /** Método que convierte un valor double en una cadena que corresponda con la moneda en uso
     * @param d el valor a convertir a texto monetario
     * @return 
     */
    public static String convertDoubleToCurrency(double d)
    {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(GUIConstants.GUI_LOCALE);
        df.setGroupingUsed(false);
        return df.format(d);
    }
}
