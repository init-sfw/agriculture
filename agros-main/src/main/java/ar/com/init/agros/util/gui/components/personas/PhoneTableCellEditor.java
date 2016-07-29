package ar.com.init.agros.util.gui.components.personas;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * Class PhoneTableCellEditor
 *
 *
 * @author init() software
 * @version 24/02/2008 
 */
public class PhoneTableCellEditor extends DefaultCellEditor
{
    Class[] argTypes = new Class[]{String.class};
    java.lang.reflect.Constructor constructor;
    Object value;

    public PhoneTableCellEditor()
    {
        super(new JTextField());
        getComponent().setName("Table.editor");
    }

    public boolean stopCellEditing()
    {
        String s = (String) super.getCellEditorValue();
        if ("".equals(s))
        {
            if (constructor.getDeclaringClass() == String.class)
            {
                value = s;
            }
            super.stopCellEditing();
        }

        try
        {
            value = constructor.newInstance(new Object[]{s});
            long val = Long.parseLong(value.toString());
            if (val < 0)
                throw new Exception("Imposible ingresar un teléfono negativo");
        }
        catch (Exception e)
        {
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        return super.stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column)
    {
        this.value = null;
        ((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
        try
        {
            Class type = table.getColumnClass(column);
            if (type == Object.class)
            {
                type = String.class;
            }
            constructor = type.getConstructor(argTypes);
        }
        catch (Exception e)
        {
            return null;
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public Object getCellEditorValue()
    {
        return value;
    }
}
