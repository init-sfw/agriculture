package ar.com.init.agros.view.reporting;

import ar.com.init.agros.reporting.components.ReportCriteria;
import ar.com.init.agros.util.gui.Listable;
import ar.com.init.agros.util.gui.components.list.DualList;

/**
 * Clase DualListReportCriteria
 * Clase de base para crear ReportCriteria con listas dobles.
 * Por defecto valida que se seleccione por lo menos un elemento.
 * Por defecto al limpiar deselecciona todos los elementos.
 *
 * @author gmatheu
 * @version 26/07/2009 
 */
public abstract class DualListReportCriteria<T extends Listable> extends DualList<T> implements ReportCriteria {

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DualListReportCriteria */
    public DualListReportCriteria() {
        super();
        fillLists();
    }

    protected void fillLists() {
    }

    @Override
    public void clear() {
        removeAllSelected();
    }

    @Override
    public boolean validateSelection() {
        return getSelected().size() > 0;
    }
}
