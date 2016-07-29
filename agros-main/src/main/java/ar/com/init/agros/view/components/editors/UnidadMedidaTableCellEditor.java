package ar.com.init.agros.view.components.editors;

import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Clase UnidadMedidaTableCellEditor
 *
 *
 * @author gmatheu
 * @version 28/07/2009 
 */
public class UnidadMedidaTableCellEditor extends DefaultCellEditor
{

    private static final long serialVersionUID = -1L;
    private FrameNotifier frameNotifier;
    private JComboBox comboBox;
    private List<UnidadMedida> unidades;

    /**
     *
     * @param frameNotifier
     * @param inputVerifier
     * @param maxValueColumnIndex columna de donde se sacara un ValorUnidad cuya cantidad sera el valor maximo permitido.
     */
    public UnidadMedidaTableCellEditor(FrameNotifier frameNotifier, List<UnidadMedida> unidades)
    {
        super(new JComboBox());
        comboBox = (JComboBox) getComponent();
        this.frameNotifier = frameNotifier;
        this.unidades = unidades;

        GUIUtility.refreshComboBox(unidades, comboBox);
        comboBox.setSelectedIndex(0);

        comboBox.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                fireEditingStopped();
            }
        });

        comboBox.addFocusListener(new FocusListener()
        {

            @Override
            public void focusGained(FocusEvent e)
            {
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                fireEditingCanceled();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (value != null && value instanceof UnidadMedida) {
            comboBox.setSelectedItem(value);
        }
        else {
            comboBox.setSelectedIndex(0);
        }
        return component;
    }

    @Override
    public Object getCellEditorValue()
    {
        if (comboBox.getSelectedItem() instanceof UnidadMedida) {
            return comboBox.getSelectedItem();
        }
        return null;
    }

    @Override
    public boolean stopCellEditing()
    {
        boolean r = !(comboBox.getSelectedItem() != null && comboBox.getSelectedItem().equals(
                GUIUtility.DEFAULT_COMBO_VALUE));

        if (r) {
//            frameNotifier.showOkMessage();
        }
        else {
//            frameNotifier.showErrorMessage("Debe seleccionar una unidad");
        }

        return r;
    }
}
