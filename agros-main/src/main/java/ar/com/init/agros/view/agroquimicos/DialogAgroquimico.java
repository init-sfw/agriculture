/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DialogAgroquimico.java
 *
 * Created on 22/06/2009, 21:16:47
 */
package ar.com.init.agros.view.agroquimicos;

import ar.com.init.agros.controller.InformacionAgroquimicoJpaController;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.agroquimicos.model.AgroquimicosUsoTableModel;
import ar.com.init.agros.view.agroquimicos.renderers.CasafeLogoTableCellRenderer;
import ar.com.init.agros.view.components.editors.EditableColumnHighlighter;
import ar.com.init.agros.view.components.editors.UnidadMedidaTableCellEditor;
import ar.com.init.agros.view.components.editors.ValorUnidadTableCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.SortOrder;

/**
 *
 * @author gmatheu
 */
public class DialogAgroquimico extends javax.swing.JDialog implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private AgroquimicosUsoTableModel agroquimicosUsoTableModel;
    private static Logger logger = Logger.getLogger(DialogAgroquimico.class.getName());
    private InformacionAgroquimicoJpaController controller;

    /** Creates new form DialogAgroquimico
     *
     * @param parent
     * @param cultivo la instancia de la agroquímico a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     */
    public DialogAgroquimico(java.awt.Frame parent)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();

        JPanel glassPane = (JPanel) getGlassPane();
        glassPane.setBackground(Color.red);


        try {
            UpdatableSubject.addUpdatableListener(this);

            controller = new InformacionAgroquimicoJpaController();

            oKCancelCleanPanel.setOkCleanCancelAbstractEventControl(new OkCleanCancelEventControl());
            oKCancelCleanPanel.setOwner(this);
            oKCancelCleanPanel.setUseBusyPanel(true);
            agroquimicosUsoTableModel = new AgroquimicosUsoTableModel();
            jXTableInformacionesAgroquimico.setModel(agroquimicosUsoTableModel);

            jXTableInformacionesAgroquimico.setDefaultEditor(Double.class, new ValorUnidadTableCellEditor(
                    frameNotifier,
                    new DecimalInputVerifier(frameNotifier, false)));

            List<UnidadMedida> unidades = new ArrayList<UnidadMedida>(2);
            unidades.add(MagnitudEnum.PESO.patron());
            unidades.add(MagnitudEnum.VOLUMEN.patron());
            UnidadMedidaTableCellEditor unidadMedidaTableCellEditor =
                    new UnidadMedidaTableCellEditor(frameNotifier, unidades);
            jXTableInformacionesAgroquimico.setDefaultEditor(UnidadMedida.class, unidadMedidaTableCellEditor);

            //Seteo el renderer para el ícono de CASAFE
            CasafeLogoTableCellRenderer tableRenderer = new CasafeLogoTableCellRenderer();
            TableColumn column = jXTableInformacionesAgroquimico.getColumnModel().getColumn(AgroquimicosUsoTableModel.DETALLE_COLUMN_IDX);
            column.setCellRenderer(tableRenderer);

            refreshUI();
            jXTableInformacionesAgroquimico.packAll();
            jXTableInformacionesAgroquimico.setRowHeight(35);

//            agroquimicosUsoTableModel.addTableModelListener(new TableModelListener()
//            {
//
//                @Override
//                public void tableChanged(TableModelEvent e)
//                {
//                    if (e.getColumn() == AgroquimicosUsoTableModel.SELECCION_COLUMN_IDX) {
//                        if (agroquimicosUsoTableModel.isRowSelected(e.getColumn())) {
//                            jXTableInformacionesAgroquimico.changeSelection(0, AgroquimicosUsoTableModel.UNIDAD_COLUMN_IDX,
//                                    false,
//                                    false);
//                            jXTableInformacionesAgroquimico.requestFocus();
//                            jXTableInformacionesAgroquimico.setColumnSelectionInterval(0,
//                                    AgroquimicosUsoTableModel.UNIDAD_COLUMN_IDX);
//                            jXTableInformacionesAgroquimico.setRowSelectionInterval(0, 0);
//                            jXTableInformacionesAgroquimico.editCellAt(0, AgroquimicosUsoTableModel.UNIDAD_COLUMN_IDX);
//                        }
//                    }
//                }
//            }); 
        }
        catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(DialogAgroquimico.class, e);
        }
        this.setSize(this.getToolkit().getScreenSize().width, this.getToolkit().getScreenSize().height - 20);
        this.setLocation(0, 0);
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
    }

    @Override
    public void refreshUI()
    {
        refreshTable();
    }

    private void refreshTable()
    {
        //Sacar los filtros existentes, para mayor velocidad. Si usa un filtro de expresiones regulares demora mucho en insertar las filas.
        FilterPipeline filters = jXTableInformacionesAgroquimico.getFilters();
        //Sacar los ordenadores existentes, para mayor velocidad.
        TableColumn sortedColumn = jXTableInformacionesAgroquimico.getSortedColumn();
        SortOrder sortOrder = null;
        if (sortedColumn != null) {
            sortOrder = jXTableInformacionesAgroquimico.getSortOrder(sortedColumn.getIdentifier());
        }

        jXTableInformacionesAgroquimico.resetSortOrder();

        jXTableInformacionesAgroquimico.setFilters(null);
        agroquimicosUsoTableModel.setData(controller);
        setHighlighters();
        
        if (sortedColumn != null) {
            jXTableInformacionesAgroquimico.setSortOrder(sortedColumn.getIdentifier(), sortOrder);
        }
        jXTableInformacionesAgroquimico.setFilters(filters);

    }

    private void setHighlighters()
    {
        jXTableInformacionesAgroquimico.setHighlighters(
                new EditableColumnHighlighter(AgroquimicosUsoTableModel.UNIDAD_COLUMN_IDX,
                AgroquimicosUsoTableModel.STOCK_MINIMO_COLUMN_IDX));
        jXTableInformacionesAgroquimico.addHighlighter(
                new EditableColumnHighlighter(false, AgroquimicosUsoTableModel.UNIDAD_COLUMN_IDX,
                AgroquimicosUsoTableModel.STOCK_MINIMO_COLUMN_IDX)
                {

                    @Override
                    public boolean isHighlighted(Component renderer, ComponentAdapter adapter)
                    {
                        int row = jXTableInformacionesAgroquimico.convertRowIndexToModel(adapter.row);
                        return super.isHighlighted(renderer, adapter) && !agroquimicosUsoTableModel.isRowSelected(
                                row);
                    }
                });
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private class OkCleanCancelEventControl extends OKCancelCleanPanel.OkCleanCancelAbstractEventControl
    {

        public OkCleanCancelEventControl()
        {
            super(oKCancelCleanPanel);
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void doOk(ActionEvent e)
        {
            final List<InformacionAgroquimico> modificados =
                    agroquimicosUsoTableModel.getInformacionesAgroquimicoModificadas();

            final List<InformacionAgroquimico> invalidos = new ArrayList<InformacionAgroquimico>();

            for (InformacionAgroquimico ia : modificados) { 
                if (ia.hasAgroquimico() && !validateInput(ia.getAgroquimico(), false)) {
                    if (ia.isSeleccionadoUso()) { 
                        invalidos.add(ia);
                    }
                    else {
                        ia.setAgroquimico(null);
                    }
                }
            }

            if (invalidos.size() == 0) {
                try {
                    controller.persist(modificados);

                    frameNotifier.showInformationMessage(
                            "Se registraron el/los agroquímico/s para ser usado/s");

                }
                catch (InvalidStateException ex) {
                    frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                    Logger.getLogger(DialogAgroquimico.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (ConstraintViolationException ex) {
                    frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                    Logger.getLogger(DialogAgroquimico.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex) {
                    frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                    Logger.getLogger(DialogAgroquimico.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {

                HighlightPredicate predicate = new HighlightPredicate()
                {

                    @Override
                    public boolean isHighlighted(Component renderer, ComponentAdapter adapter)
                    {
                        int row = jXTableInformacionesAgroquimico.convertRowIndexToModel(adapter.row);

                        InformacionAgroquimico ia =
                                agroquimicosUsoTableModel.getInformacionesAgroquimico().get(row);

                        return invalidos.contains(ia);
                    }
                };

                Border border = new LineBorder(Color.RED);
                BorderHighlighter hl = new BorderHighlighter(predicate, border);
                setHighlighters();
                jXTableInformacionesAgroquimico.addHighlighter(hl);

                frameNotifier.showErrorMessage(
                        "Ingrese valores para todas las filas seleccionadas");
            }
        }

        @Override
        public void doCancel(ActionEvent e)
        {
            DialogAgroquimico.this.dispose();
        }

        @Override
        public void doClean(ActionEvent e)
        {
            clear();
            frameNotifier.showOkMessage();
        }
    };

    private void clear()
    {
        refreshTable();
    }

    @Override
    public void dispose()
    {
        controller.close();
        super.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableTableRenderer1 = new ar.com.init.agros.util.gui.ListableTableRenderer();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jXPanelAgroquimico = new org.jdesktop.swingx.JXPanel();
        jLabelValorId = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableInformacionesAgroquimico = new org.jdesktop.swingx.JXTable();
        tableFindAndFilter = new ar.com.init.agros.util.gui.components.TableFindAndFilter(jXTableInformacionesAgroquimico,frameNotifier);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogAgroquimico.class);
        listableTableRenderer1.setText(resourceMap.getString("listableTableRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModal(true);

        jXPanelAgroquimico.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jXPanelAgroquimico.border.title"))); // NOI18N

        jLabelValorId.setText(resourceMap.getString("jLabelValorId.text")); // NOI18N

        jXTableInformacionesAgroquimico.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableInformacionesAgroquimico);

        javax.swing.GroupLayout jXPanelAgroquimicoLayout = new javax.swing.GroupLayout(jXPanelAgroquimico);
        jXPanelAgroquimico.setLayout(jXPanelAgroquimicoLayout);
        jXPanelAgroquimicoLayout.setHorizontalGroup(
            jXPanelAgroquimicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanelAgroquimicoLayout.createSequentialGroup()
                .addContainerGap(795, Short.MAX_VALUE)
                .addComponent(jLabelValorId)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
        );
        jXPanelAgroquimicoLayout.setVerticalGroup(
            jXPanelAgroquimicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanelAgroquimicoLayout.createSequentialGroup()
                .addComponent(jLabelValorId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jXPanelAgroquimico, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableFindAndFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                    .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE))
                .addGap(0, 0, 0))
            .addGroup(layout.createSequentialGroup()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 807, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableFindAndFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jXPanelAgroquimico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JLabel jLabelValorId;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXPanel jXPanelAgroquimico;
    private org.jdesktop.swingx.JXTable jXTableInformacionesAgroquimico;
    private ar.com.init.agros.util.gui.ListableTableRenderer listableTableRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    private ar.com.init.agros.util.gui.components.TableFindAndFilter tableFindAndFilter;
    // End of variables declaration//GEN-END:variables
}
