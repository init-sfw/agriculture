package ar.com.init.agros.view.agroquimicos.renderers;

import ar.com.init.agros.util.gui.GUIConstants;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Clase CasafeLogoTableCellRenderer
 *
 *
 * @author fbobbio
 * @version 13-jul-2010 
 */
public class CasafeLogoTableCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component component = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
        if (value != null && value instanceof String)
        {
            boolean bool = Boolean.parseBoolean((String)value);

            JLabel label = ((JLabel) component);

            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setText("");
            if (bool) // agroquímico ingresado manualmente
            {
                label.setIcon(new ImageIcon(getClass().getResource(GUIConstants.CASAFE_DESHAB_ICON)));
            }
            else // agroquímico ingresado por la guía de casafe
            {
                label.setIcon(new ImageIcon(getClass().getResource(GUIConstants.CASAFE_HAB_ICON)));
            }
            return component;
        }
        return null;
    }
}
