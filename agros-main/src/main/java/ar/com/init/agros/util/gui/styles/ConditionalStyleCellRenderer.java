package ar.com.init.agros.util.gui.styles;

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Clase ConditionalStyleCellRenderer
 * Aplica los estilos configurados mediante un ConditionalSyle
 *
 * @author gmatheu
 * @version 28/06/2009 
 */
public class ConditionalStyleCellRenderer extends DefaultTableCellRenderer
{

    private static Logger log = Logger.getLogger(ConditionalStyleCellRenderer.class.getName());
    private static final long serialVersionUID = -1L;

    public ConditionalStyleCellRenderer()
    {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof ConditionalStyle && component instanceof JLabel) {
            ConditionalStyle condStyle = (ConditionalStyle) value;
            JLabel label = new JLabel(((JLabel) component).getText());

            setStyles(label, condStyle);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return label;
        }

        return component;
    }

    @SuppressWarnings("unchecked")
    private void setStyles(JLabel label, ConditionalStyle condStyle)
    {
        String value = condStyle.toString();

        for (Object o : condStyle.getConditions()) {
            StyleCondition sc = (StyleCondition) o;
            try {
                if (sc.isTrue(condStyle.getValue())) {
                    for (Object oStyle : sc.getStyles()) {
                        Style s = (Style) oStyle;

                        if (s.getValue() != null) {
                            switch (s.getStyleParam()) {
                                case TEXT_COLOR:
                                    label.setForeground((Color) s.getValue());
                                    break;
                                case BACKGROUND_COLOR:
                                    label.setBackground((Color) s.getValue());
                                    break;
                                case HORIZONTAL_ALIGNMENT:
                                    label.setHorizontalAlignment((Integer) s.getValue());
                                    break;
                                case PREFIX:
                                    value = s.getValue() + value;
                                    break;
                                case SUFFIX:
                                    value = value + s.getValue();
                                    break;
                                default:
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                log.info("Exception while setting style");
                if (log.isLoggable(Level.INFO)) {
                    ex.printStackTrace();
                }
            }
        }

        label.setText(value);
    }
}
