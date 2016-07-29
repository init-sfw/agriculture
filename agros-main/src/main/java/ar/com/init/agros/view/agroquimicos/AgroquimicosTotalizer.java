package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.util.gui.components.DialogTotalizer;
import java.awt.Frame;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hibernate.ejb.QueryImpl;

/**
 * Clase AgroquimicosTotalizer
 *
 *
 * @author fbobbio
 * @version 19-nov-2009 
 */
public class AgroquimicosTotalizer extends DialogTotalizer
{

    public static final long serialVersionUID = -1L;    
    private static final int AGROQUIMICO_COLUMN = 0;
    private static final int VALOR_COLUMN = 1;

    /** Constructor por defecto de AgroquimicosTotalizer */
    public AgroquimicosTotalizer(Frame parent)
    {
        super(parent, "Stock Total de Agroquímicos");
        title = "Stock Total de Agroquímicos";        
    }

    private class AgroquimicoTotalizerTableModel extends DefaultTableModel
    {

        public static final long serialVersionUID = -1L;

        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    }

    @Override
    protected TableModel createTableModel()
    {
        String[] headers = {"Agroquímico", "Stock Total"};
        AgroquimicoTotalizerTableModel agroqModel = new AgroquimicoTotalizerTableModel();
        agroqModel.setColumnIdentifiers(headers);        
        return agroqModel;
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    @Override
    protected void findData()
    {
        EntityManager em = EntityManagerUtil.createEntityManager();

        String query = "SELECT v.agroquimico, SUM(v.stockActual.valor)" +
                " FROM " + ValorAgroquimico.class.getName() + " AS v " +
                " GROUP BY v.agroquimico" +
                " ORDER BY v.agroquimico";

        QueryImpl hibernateQuery = (QueryImpl) em.createQuery(query);

        List<Object[]> result = hibernateQuery.getResultList();
        AgroquimicoTotalizerTableModel agroqModel = (AgroquimicoTotalizerTableModel) model;    
        agroqModel.setRowCount(result.size());
        for (int i = 0; i < result.size(); i++) {
            Object[] o = result.get(i);
            agroqModel.setValueAt(((Agroquimico) o[0]).toString(), i, AGROQUIMICO_COLUMN);
            agroqModel.setValueAt(new ValorUnidad((Double) o[1], ((Agroquimico) o[0]).getUnidad()), i, VALOR_COLUMN);
        }

        em.close();
    }
}
