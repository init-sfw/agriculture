package ar.com.init.agros.util.gui.components.personas;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class PhoneTableRenderer
 *
 *
 * @author init() software
 * @version 18/01/2008 
 */
public class PhoneTableRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) 
    {
        Component component = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
        if (value == null)
            ((JLabel) component).setText("");
        if (value != null && value instanceof Object) 
        {
            ((JLabel) component).setText(String.valueOf(value));
            ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            return component;
        }
        if (value != null && value instanceof Integer) 
        {
            ((JLabel) component).setText(String.valueOf(((Integer)value).intValue()));
            ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            return component;
        }
        if (value != null && value instanceof String) 
        {
            ((JLabel) component).setText(String.valueOf(value));
            ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            return component;
        }
        return component;
    }
}