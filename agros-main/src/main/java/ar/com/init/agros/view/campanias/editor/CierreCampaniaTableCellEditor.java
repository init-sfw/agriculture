package ar.com.init.agros.view.campanias.editor;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.campanias.model.SeleccionSuperficiesTableModel;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import ar.com.init.agros.view.components.valores.PanelValorUnidad;

/**
 * Clase CierreCampaniaTableCellEditor
 *
 *
 * @author fbobbio
 * @version 03-ago-2009 
 */
public class CierreCampaniaTableCellEditor extends ValorUnidadTableCellEditor
{

    private static final long serialVersionUID = -1L;
    private SeleccionSuperficiesTableModel tableModel;

    /** Constructor por defecto de ValorUnidadTableCellEditor */
    public CierreCampaniaTableCellEditor(FrameNotifier frameNotifier, SeleccionSuperficiesTableModel model)
    {
        this(frameNotifier, model, (DecimalInputVerifier) PanelValorUnidad.createInputVerifier(frameNotifier));

    }

    public CierreCampaniaTableCellEditor(FrameNotifier frameNotifier, SeleccionSuperficiesTableModel model, DecimalInputVerifier inputVerifier)
    {
        this(frameNotifier, model, inputVerifier, -1);
    }

    /**
     *
     * @param frameNotifier
     * @param inputVerifier
     * @param maxValueColumnIndex columna de donde se sacara un ValorUnidad cuya cantidad sera el valor maximo permitido.
     */
    public CierreCampaniaTableCellEditor(FrameNotifier frameNotifier, SeleccionSuperficiesTableModel model, DecimalInputVerifier inputVerifier, int maxValueColumnIndex)
    {
        super(frameNotifier, inputVerifier, maxValueColumnIndex);
        this.tableModel = model;
    }

    @Override
    public boolean stopCellEditing()
    {
        boolean val = inputVerifier.verify(textField) && super.stopCellEditing();
        tableModel.updateRendimientosValues();
        return val;
    }
}
