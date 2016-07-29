package ar.com.init.agros.view.components.editors;

import java.awt.Color;
import java.awt.Component;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * Clase EditableColumnHighlighter
 * Clase para colorear las columnas que son editables. Esten habilitadas para editarals o no.
 *
 * @author gmatheu
 * @version 12/10/2009 
 */
public class EditableColumnHighlighter extends ColorHighlighter
{

    private Integer[] columnIndexes;
    public static final Color DISABLED_BACKGROUND_COLOR = new Color(208, 225, 213);
    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(215, 255, 230);
    public static final Color FOREGROUND_COLOR = Color.BLACK;

    /** Constructor por defecto de EditableColumnHighlighter */
    public EditableColumnHighlighter(Integer... indexes)
    {
        this(true, indexes);
    }

    public EditableColumnHighlighter(boolean enabled, Integer... indexes)
    {
        this.columnIndexes = indexes;
        HighlightPredicate predicate = new HighlightPredicate()
        {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter)
            {
                return EditableColumnHighlighter.this.isHighlighted(renderer, adapter);
            }
        };

        this.setHighlightPredicate(predicate);
        if (enabled) {
            this.setBackground(DEFAULT_BACKGROUND_COLOR);
        }
        else {
            this.setBackground(DISABLED_BACKGROUND_COLOR);
        }
        this.setForeground(FOREGROUND_COLOR);
    }

    public boolean isHighlighted(Component renderer, ComponentAdapter adapter)
    {
        int column = adapter.column;

        boolean r = false;

        if (columnIndexes != null) {
            for (int i = 0; i < columnIndexes.length; i++) {
                Integer idx = columnIndexes[i];

                if (idx == column) {
                    r = true;
                    break;
                }
            }
        }
        return r;
    }
}
