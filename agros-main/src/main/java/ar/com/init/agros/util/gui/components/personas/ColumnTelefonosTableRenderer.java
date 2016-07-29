package ar.com.init.agros.util.gui.components.personas;

import ar.com.init.agros.model.Telefono;
import ar.com.init.agros.util.gui.GUIConstants;
import java.awt.Component;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class ColumnTelefonosTableRenderer
 *
 *
 * @author init() software
 * @version 21/01/2008 
 */
public class ColumnTelefonosTableRenderer extends DefaultTableCellRenderer 
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) 
    {
        Component component = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
        ((JLabel) component).setIcon(new ImageIcon(getClass().getResource(GUIConstants.PHONES_ICON)));
        ((JLabel) component).setHorizontalAlignment(JLabel.LEFT);
        if (value != null && value instanceof Vector)
        {
            @SuppressWarnings("unchecked")
            Vector<Telefono> aux = (Vector<Telefono>) value;
            if (aux.size() == 1)
                ((JLabel) component).setText(aux.get(0).toString());
            else
                ((JLabel) component).setText(aux.size() + " teléfonos registrados");
            return component;
        }
        if (value == null)
        {
            ((JLabel) component).setText(0 + " teléfonos registrados");
            return component;
        }
        return component;
    }
}
