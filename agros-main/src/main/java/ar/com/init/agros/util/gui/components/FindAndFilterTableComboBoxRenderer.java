package ar.com.init.agros.util.gui.components;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableColumn;

public class FindAndFilterTableComboBoxRenderer extends JLabel implements ListCellRenderer
{
    /**
     * 
     */
    public FindAndFilterTableComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }
    
    @Override
    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) 
    {
        if (value instanceof TableColumn)
        {
            TableColumn t = (TableColumn)value;
            setText(t.getIdentifier().toString());
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
