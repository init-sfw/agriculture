package ar.com.init.agros.view.agroquimicos.model;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class TextAreaColumnTableRenderer
 *
 *
 * @author init() software
 * @version 18/01/2008 
 */
public class TextAreaColumnTableRenderer extends DefaultTableCellRenderer
{

    private static final long serialVersionUID = -1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);
        if (value == null) {
            ((JLabel) component).setText("");
        }
        else if (value instanceof String) {
            Font aux = component.getFont();
            Font font = new Font(aux.getFontName(), aux.getStyle(), aux.getSize());
            JTextArea txt = new JTextArea(String.valueOf(value));
            txt.setFont(font);
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(txt);
            scroll.createHorizontalScrollBar();
            scroll.createVerticalScrollBar();
            component = scroll;
            return component;
        }
        return component;
    }
}
