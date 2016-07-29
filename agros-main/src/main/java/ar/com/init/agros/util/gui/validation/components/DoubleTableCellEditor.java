package ar.com.init.agros.util.gui.validation.components;

import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import java.awt.Component;
import java.text.ParseException;
import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Clase DoubleTableCellEditor
 *
 *
 * @author fbobbio
 * @version 25-ago-2009 
 */
public class DoubleTableCellEditor extends DefaultCellEditor
{

    private static final long serialVersionUID = -1L;
    protected FrameNotifier frameNotifier;
    protected DecimalInputVerifier inputVerifier;
    protected JTextField textField;

    /**
     *
     * @param frameNotifier
     * @param maxValueColumnIndex columna de donde se sacara un ValorUnidad cuya cantidad sera el valor maximo permitido.
     */
    public DoubleTableCellEditor(FrameNotifier frameNotifier)
    {
        super(new JTextField());
        this.frameNotifier = frameNotifier;
        textField = (JTextField) getComponent();
        inputVerifier = new DecimalInputVerifier(frameNotifier);
        textField.setInputVerifier(inputVerifier);
        textField.setHorizontalAlignment(JTextField.CENTER);
    }

    public void changeInputVerifier(DecimalInputVerifier verifier)
    {
        this.inputVerifier = verifier;
        textField.setInputVerifier(inputVerifier);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        try
        {
            String s = textField.getText();
            textField.setText(GUIUtility.NUMBER_FORMAT.format(Double.parseDouble(s)));
        }
        catch (NumberFormatException e) {}

        return component;
    }

    @Override
    public Object getCellEditorValue()
    {
        if (textField.getText().length() > 0)
        {
            try
            {
                return GUIUtility.NUMBER_FORMAT.parse(textField.getText()).doubleValue();
            }
            catch (ParseException ex)
            {
            }
        }
        return "";

    }

    @Override
    public boolean stopCellEditing()
    {
        return inputVerifier.verify(textField) && super.stopCellEditing();
    }
}
