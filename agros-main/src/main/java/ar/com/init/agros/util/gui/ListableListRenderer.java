package ar.com.init.agros.util.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Clase ListableListRenderer que renderiza una línea de una lista para recibir objetos tipo Tablizable
 *
 *
 * @author fbobbio
 * @version 13-jun-2009 
 */
public class ListableListRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        Component component = super.getListCellRendererComponent(list, value,index,isSelected,cellHasFocus);
        if (value instanceof Listable)
        {
            Listable lis = (Listable) value;

            ((JLabel) component).setText(lis.getListLine());
            ((JLabel) component).setHorizontalAlignment(JLabel.LEFT);
            return component;
        }
        return null;
    }
}
