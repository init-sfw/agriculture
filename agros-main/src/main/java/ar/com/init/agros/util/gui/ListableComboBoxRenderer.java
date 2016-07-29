package ar.com.init.agros.util.gui;

/**
 * Clase ListableComboBoxRenderer para renderizar los combobox con entidades q implementen la interfaz
 * Listable
 *
 *
 * @author fbobbio
 * @version 11-jun-2009 
 */
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListableComboBoxRenderer extends JLabel implements ListCellRenderer
{
    public ListableComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)
    {
        if (value instanceof Listable)
        {
            Listable l = (Listable)value;
            setText(l.getListLine());
        }
        else
        {
            if (value != null)
                setText(value.toString());
        }
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
