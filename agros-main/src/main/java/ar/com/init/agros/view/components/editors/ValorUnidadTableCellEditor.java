package ar.com.init.agros.view.components.editors;

import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.components.valores.PanelValorUnidad;
import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Clase ValorUnidadTableCellEditor
 *
 *
 * @author gmatheu
 * @version 28/06/2009 
 */
public class ValorUnidadTableCellEditor extends DefaultCellEditor
{

    private static final long serialVersionUID = -1L;
    protected FrameNotifier frameNotifier;
    protected DecimalInputVerifier inputVerifier;
    protected JTextField textField;
    protected int maxValueColumnIndex;

    /** Constructor por defecto de ValorUnidadTableCellEditor */
    public ValorUnidadTableCellEditor(FrameNotifier frameNotifier)
    {
        this(frameNotifier, (DecimalInputVerifier) PanelValorUnidad.createInputVerifier(frameNotifier));

    }

    public ValorUnidadTableCellEditor(FrameNotifier frameNotifier, DecimalInputVerifier inputVerifier)
    {
        this(frameNotifier, inputVerifier, -1);
    }

    /**
     *
     * @param frameNotifier
     * @param inputVerifier
     * @param maxValueColumnIndex columna de donde se sacara un ValorUnidad cuya cantidad sera el valor maximo permitido.
     */
    public ValorUnidadTableCellEditor(FrameNotifier frameNotifier, DecimalInputVerifier inputVerifier, int maxValueColumnIndex)
    {
        super(new JTextField());
        textField = (JTextField) getComponent();
        textField.setInputVerifier(inputVerifier);
        textField.setHorizontalAlignment(JTextField.CENTER);
        this.frameNotifier = frameNotifier;
        this.inputVerifier = inputVerifier;
        this.maxValueColumnIndex = maxValueColumnIndex;
        textField.setBackground(new Color(170, 255, 190));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (maxValueColumnIndex > -1)
        {
            if (table.getValueAt(row, maxValueColumnIndex) instanceof ValorUnidad)
            {
                ValorUnidad v = (ValorUnidad) table.getValueAt(row, maxValueColumnIndex);
                inputVerifier.setMaxValue(v.getValor());
            }
        }
        if (value instanceof Double)
        {
            double val = (Double) value;
            textField.setText(GUIUtility.NUMBER_FORMAT.format(val));
        }
        if (value instanceof ValorUnidad)
        {
            ValorUnidad v = (ValorUnidad) value;
            textField.setText(GUIUtility.NUMBER_FORMAT.format(v.getValor()));
        }

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
        return super.stopCellEditing() && inputVerifier.verify(textField);
    }
}
