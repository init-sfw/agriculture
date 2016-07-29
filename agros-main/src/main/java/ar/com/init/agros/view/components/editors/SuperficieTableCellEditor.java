package ar.com.init.agros.view.components.editors;

import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.components.panels.PanelSeleccionLotesSublotes;
import ar.com.init.agros.view.components.valores.PanelValorUnidad;
import java.awt.Component;
import java.text.ParseException;
import javax.swing.JTable;

/**
 * Clase SuperficieTableCellEditor
 *
 *
 * @author fbobbio
 * @version 11-ago-2009 
 */
public class SuperficieTableCellEditor extends ValorUnidadTableCellEditor
{

    private static final long serialVersionUID = -1L;
    private PanelSeleccionLotesSublotes panelLotes;

    /** Constructor por defecto de ValorUnidadTableCellEditor */
    public SuperficieTableCellEditor(FrameNotifier frameNotifier, PanelSeleccionLotesSublotes panelLotes)
    {
        this(frameNotifier, (DecimalInputVerifier) PanelValorUnidad.createInputVerifier(frameNotifier), panelLotes);

    }

    public SuperficieTableCellEditor(FrameNotifier frameNotifier, DecimalInputVerifier inputVerifier, PanelSeleccionLotesSublotes panelLotes)
    {
        this(frameNotifier, inputVerifier, -1, panelLotes);
    }

    /**
     *
     * @param frameNotifier
     * @param inputVerifier
     * @param maxValueColumnIndex columna de donde se sacara el valor original de superficie, que será el máximo permitido
     */
    public SuperficieTableCellEditor(FrameNotifier frameNotifier, DecimalInputVerifier inputVerifier, int superficieValueColumnIndex, PanelSeleccionLotesSublotes panelLotes)
    {
        super(frameNotifier, inputVerifier, superficieValueColumnIndex);
        this.panelLotes = panelLotes;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (maxValueColumnIndex > -1)
        {
            if (table.getValueAt(row, maxValueColumnIndex) instanceof Superficie)
            {
                Superficie s = (Superficie) table.getValueAt(row, maxValueColumnIndex);
                inputVerifier.setMaxValue(s.getSuperficie().getValor());
            }
        }
        if (value instanceof ValorUnidad)
        {
            ValorUnidad vu = (ValorUnidad) value;
            textField.setText(GUIUtility.NUMBER_FORMAT.format(vu.getValor()));
        }
        if (value instanceof Double)
        {
            double d = ((Double)value).doubleValue();
            ValorUnidad vu = new ValorUnidad(d, MagnitudEnum.SUPERFICIE.patron());
            textField.setText(GUIUtility.NUMBER_FORMAT.format(vu.getValor()));
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
                return new ValorUnidad(GUIUtility.NUMBER_FORMAT.parse(textField.getText()).doubleValue(),MagnitudEnum.SUPERFICIE.patron());
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
        boolean val = inputVerifier.verify(textField) && super.stopCellEditing();
        if (val)
        {
            panelLotes.calcularSuperficieTotal();
        }
        return val;
    }
}
