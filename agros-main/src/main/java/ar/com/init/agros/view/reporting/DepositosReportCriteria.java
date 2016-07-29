package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.util.gui.GUIUtility;
import javax.persistence.PersistenceException;

/**
 * Clase DepositosReportCriteria
 *
 *
 * @author gmatheu
 * @version 26/07/2009 
 */
public class DepositosReportCriteria extends DualListReportCriteria<Deposito>
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DepositosReportCriteria */
    public DepositosReportCriteria()
    {
        super();       
    }

    protected void fillLists() {
        try {
            DepositoJpaController depositoJpaController = new DepositoJpaController();
            this.addAvailable(depositoJpaController.findEntities());
        } catch (PersistenceException e) {
            GUIUtility.logPersistenceError(DepositosReportCriteria.class, e);
        }
    }

    @Override
    public String getTabTitle()
    {
        return "Depositos";
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar por lo menos un depósito";
    }
}
