package ar.com.init.agros.util.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Clase ListableTableRenderer que renderiza una columna de una tabla para recibir objetos tipo Tablizable
 *
 *
 * @author fbobbio
 * @version 12-jun-2009 
 */
public class ListableTableRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component component = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
        if (value instanceof Listable)
        {
            ((JLabel) component).setText(((Listable)value).getListLine());
            ((JLabel) component).setHorizontalAlignment(JLabel.LEFT);
            return component;
        }
        else
        {
            if (value == null)
                ((JLabel) component).setText("");
            else
                ((JLabel) component).setText(value.toString());
        }
        return component;
    }
}
