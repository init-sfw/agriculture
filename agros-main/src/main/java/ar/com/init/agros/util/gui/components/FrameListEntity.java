package ar.com.init.agros.util.gui.components;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.exceptions.BaseException;
import ar.com.init.agros.controller.exceptions.NonExistentEntityException;
import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.SortOrder;

/*
 * Clase GUI FrameListEntity que será utilizada como template para todas las ventanas de consulta
 * de las entidades que extiendan de TablizableEntity
 *
 * @author fbobbio
 * @version 09-jun-2009
 */
public abstract class FrameListEntity<T extends TablizableEntity> extends javax.swing.JFrame implements
        UpdatableListener
{

    protected BaseEntityJpaController<T> jpaController;
    protected TablizableEntityDataModel<T> tableModel;
    protected Class<T> clazz;
    protected ResourceMap resourceMap;
    protected ListSelectionListener listSelectionListener;

    /** Arreglo con las entidades seleccionadas */
//    private List<T> selectedEntities;
    /** Array de entidades que implementen la interfaz Tablizable*/
//    private List<T> entities;
    private EventControl evt;
    /** Constante para indicar que no hay selección en la tabla */
    public static final int NO_SELECTION = 0;
    /** Constante para indicar que hay selección múltiple en la tabla */
    public static final int MULTIPLE_SELECTION = 2;
    /** Constante para indicar que hay selección simple en la tabla */
    public static final int SINGLE_SELECTION = 1;
    private boolean useBusyPanel;

    /** Crea una nueva GUI tipo FrameListEntity
     * @param clazz la clase de entidad que se listará
     */
    public FrameListEntity(Class<T> clazz, String[] headers)
    {
        this(clazz, null, headers);
    }

    /** Crea una nueva GUI tipo FrameListEntity
     * @param clazz la clase de entidad que se listará
     * @param jpaController el controlador específico
     */
    public FrameListEntity(Class<T> clazz, BaseEntityJpaController<T> jpaController, String[] headers)
    {
        super();
        UpdatableSubject.addUpdatableListener(this);

        this.getRootPane().registerKeyboardAction(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                closeWindow();
            }
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.clazz = clazz;
        if (jpaController == null) {
            this.jpaController = new BaseEntityJpaController<T>(clazz, true);
        }
        else {
            this.jpaController = jpaController;
            jpaController.setKeepEntityManager(true);
        }
        GUIUtility.initWindow(this);
        initComponents();
        evt = new EventControl();
        try {
            tableModel = new TablizableEntityDataModel<T>(new ArrayList<T>(), headers);
            jXTable1.setModel(tableModel);
        }
        catch (Exception ex) {
            showErrorMessage(ex.getLocalizedMessage());
        }

        jXBusyLabel.setVisible(false);
        useBusyPanel = true;

        refreshTable();
        jXTable1.packAll();

        btnConsultar.addActionListener(evt);
        btnEliminar.addActionListener(evt);
        btnModificar.addActionListener(evt);
        btnNuevo.addActionListener(evt);

        ListSelectionModel rowsModel = jXTable1.getSelectionModel();
        listSelectionListener = new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting()) {
                    return;
                } // if you don't want to handle intermediate selections
                if (frameNotifier1.getCurrentState() != FrameNotifier.OK_MESSAGE) {
                    frameNotifier1.showOkMessage();
                }
                if (jXTable1.getSelectedRows().length > 1) {
                    selection(MULTIPLE_SELECTION);
                }
                else {
                    if (jXTable1.getSelectedRows().length == 1) {
                        selection(SINGLE_SELECTION);
                    }
                    else {
                        selection(NO_SELECTION);
                    }
                }
            }
        };
        rowsModel.addListSelectionListener(listSelectionListener);

        jXTable1.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2) {
                    doDoubleClick();
                }
            }
        });
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setUseBusyPanel(boolean useBusyPanel)
    {
        this.useBusyPanel = useBusyPanel;
    }

    /**
     * Este metodo define la accion que se realizará al hacer doble click en una fila.
     */
    public void doDoubleClick()
    {
        doList();
    }

    public Frame getCurrentFrame()
    {
        return this;
    }

    /** Método que devuelve las entidades seleccionadas desde la tabla
     *
     * @return la lista de entidades seleccionadas desde la tabla
     */
    public List<T> getSelectedEntities()
    {
        ArrayList<T> r = new ArrayList<T>();
        int[] selectedRows = jXTable1.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            r.add(tableModel.getData().get(GUIUtility.getModelRowIndex(jXTable1, selectedRows[i])));
        }

        return r;
    }

    //TODO Marcar como seleccionadas las filas teniendo en cuenta los filtros.
    public void setSelectedEntities(List<T> selectedEntities)
    {
    }

    private void selection(int selection)
    {
        switch (selection) {
            case NO_SELECTION:
                btnConsultar.setEnabled(false);
                btnEliminar.setEnabled(false);
                btnModificar.setEnabled(false);
                break;
            case MULTIPLE_SELECTION:
                btnConsultar.setEnabled(false);
                btnEliminar.setEnabled(true);
                btnModificar.setEnabled(false);
                break;
            case SINGLE_SELECTION:
                btnConsultar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnModificar.setEnabled(true);
                break;
        }
    }

    public List<T> getEntities()
    {
        return tableModel.getData();
    }

    public void setEntities(List<T> entities)
    {
        tableModel.setData(entities);
        tableModel.fireTableDataChanged();
    }

    public void refreshTable()
    {
        //Sacar los filtros existentes, para mayor velocidad. Si usa un filtro de expresiones regulares demora mucho en insertar las filas.
        FilterPipeline filters = jXTable1.getFilters();
        //Sacar los ordenadores existentes, para mayor velocidad.
        TableColumn sortedColumn = jXTable1.getSortedColumn();
        SortOrder sortOrder = null;
        if (sortedColumn != null) {
            sortOrder = jXTable1.getSortOrder(sortedColumn.getIdentifier());
        }

        jXTable1.resetSortOrder();

        jXTable1.setFilters(null);

        jpaController.clear();
        List<T> l = jpaController.findEntities();

        setEntities(l);

        if (sortedColumn != null) {
            jXTable1.setSortOrder(sortedColumn.getIdentifier(), sortOrder);
        }
        jXTable1.setFilters(filters);
        tableFindAndFilter1.filter();
    }

    public ResourceMap getResourceMap()
    {
        return resourceMap;
    }

    public void setResourceMap(ResourceMap resourceMap)
    {
        this.resourceMap = resourceMap;
    }

    public void clean()
    {
        this.tableFindAndFilter1.clean();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jPanel1 = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jXBusyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCerrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        tableFindAndFilter1 = new ar.com.init.agros.util.gui.components.TableFindAndFilter(jXTable1, frameNotifier1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameListEntity.class);
        btnNuevo.setIcon(resourceMap.getIcon("btnNuevo.icon")); // NOI18N
        btnNuevo.setText(resourceMap.getString("btnNuevo.text")); // NOI18N

        btnModificar.setIcon(resourceMap.getIcon("btnModificar.icon")); // NOI18N
        btnModificar.setText(resourceMap.getString("btnModificar.text")); // NOI18N
        btnModificar.setEnabled(false);

        btnConsultar.setIcon(resourceMap.getIcon("btnConsultar.icon")); // NOI18N
        btnConsultar.setText(resourceMap.getString("btnConsultar.text")); // NOI18N
        btnConsultar.setEnabled(false);

        btnEliminar.setIcon(resourceMap.getIcon("btnEliminar.icon")); // NOI18N
        btnEliminar.setText(resourceMap.getString("btnEliminar.text")); // NOI18N
        btnEliminar.setEnabled(false);

        jXBusyLabel.setText(resourceMap.getString("jXBusyLabel.text")); // NOI18N
        jXBusyLabel.setBusy(true);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnNuevo)
                    .add(btnConsultar)
                    .add(btnModificar)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnEliminar)
                    .add(jXBusyLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {btnConsultar, btnEliminar, btnModificar, btnNuevo}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(btnNuevo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnConsultar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnModificar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnEliminar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jXBusyLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(60, 60, 60))
        );

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(FrameListEntity.class, this);
        btnCerrar.setAction(actionMap.get("doCerrar")); // NOI18N
        btnCerrar.setIcon(resourceMap.getIcon("btnCerrar.icon")); // NOI18N
        btnCerrar.setText(resourceMap.getString("btnCerrar.text")); // NOI18N
        jPanel2.add(btnCerrar);

        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTable1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .add(26, 26, 26)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
            .add(tableFindAndFilter1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(tableFindAndFilter1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, 0, 0, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Clase de control de eventos que maneja los eventos de la GUI FrameListEntity y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** Método que maneja los eventos de la GUI FrameListEntity
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == btnConsultar) {
                doList();
            }
            if (e.getSource() == btnEliminar) {
                if (askRemove()) {
                    remove();
                }
            }
            if (e.getSource() == btnModificar) {
                modificar();
            }
            if (e.getSource() == btnNuevo) {
                newEntity();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }
    }

    private void consultar()
    {
        list(getSelectedEntities().get(0));
    }

    private void modificar()
    {
        T entity = getSelectedEntities().get(0);
        update(entity);

        jpaController.refreshEntity(entity);
    }

    private boolean askRemove()
    {
        int ret =
                GUIUtility.askQuestionMessage(this, "Borrando registros",
                "¿Está seguro que desea borrar el/los " + getSelectedEntities().size() + " registro/s seleccionado/s?");
        if (ret == JOptionPane.YES_OPTION) {
            return true;
        }
        else {
            return false;
        }
    }

    private void remove()
    {
        for (T en : getSelectedEntities()) {
            try {
                jpaController.delete(en);
            }
            catch (ConstraintViolationException ex) {
                showErrorMessage(
                        "Imposible borrar " + en.entityName() + " por tener información dependiente que no debe perderse");
            }
            catch (BaseException ex) {
                showErrorMessage(ex.getLocalizedMessage());
            }
            catch (NonExistentEntityException ex) {
                showErrorMessage("No se encontró la entidad con ID: " + en.getId() + " en la base de datos");
                Logger.getLogger(FrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InvalidStateException ex) {
                frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(FrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex) {
                frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(FrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /** Método abstracto que deberá redefinirse para realizar el llamado a la ventana de consulta
     * correspondiente
     * @param entity la entidad a ser consultada
     */
    public abstract void list(T entity);

    /**
     * Metodo usado para hacer la consulta de una entidad
     */
    public void doList()
    {
        list(getSelectedEntities().get(0));
    }

    /** Método abstracto que deberá redefinirse para realizar el llamado a la ventana de alta
     *  correspondiente
     */
    public abstract void newEntity();

    /** Método abstracto que deberá redefinirse para realizar el llamado a la ventana de modificación
     *  correspondiente
     * @param entity la entidad a ser modificada
     */
    public abstract void update(T entity);

    public void showErrorMessage(String msg)
    {
        frameNotifier1.showErrorMessage(msg);
    }

    public void showInformationMessage(String msg)
    {
        frameNotifier1.showInformationMessage(msg);
    }

    public JButton getBtnModificar()
    {
        return btnModificar;
    }

    public void setBtnModificar(JButton btnModificar)
    {
        this.btnModificar = btnModificar;
    }

    /** Método que cierra la ventana y libera los recursos */
    public void closeWindow()
    {
        this.dispose();
    }

    @Override
    public void refreshUI()
    {
        refreshTable();
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        if (b) {
            refreshUI();
        }
    }

    /**
     * Hace visibles o no visibles a los botones de acuerdo a los parametros.
     *
     * @param nuevo
     * @param consultar
     * @param eliminar
     * @param exportar
     * @param modificar
     */
    public void setVisibleButtons(boolean nuevo, boolean eliminar, boolean modificar, boolean consultar, boolean exportar)
    {
        btnNuevo.setVisible(nuevo);
        btnConsultar.setVisible(consultar);
        btnEliminar.setVisible(eliminar);
        btnModificar.setVisible(modificar);
    }

    @Action
    public void doCerrar()
    {
        closeWindow();
    }
    private boolean nvoVisible;
    private boolean modVisible;
    private boolean elimVisible;
    private boolean consVisible;
    private JPanel busyPanel;
    private static final Color backgroundColor = new Color(0.8f, 0.8f, 0.8f, 0.2f);

    protected void setBusy(boolean b)
    {
        if (useBusyPanel) {
            if (b) {
                nvoVisible = btnNuevo.isVisible();
                modVisible = btnModificar.isVisible();
                elimVisible = btnEliminar.isVisible();
                consVisible = btnConsultar.isVisible();
            }

            if (busyPanel == null) {
                busyPanel = new JPanel()
                {

                    private static final long serialVersionUID = -1L;

                    @Override
                    public void paintComponent(Graphics g)
                    {
                        //Set the color to with red with a 50% alpha
                        g.setColor(backgroundColor);

                        //Fill a rectangle with the 50% red color
                        g.fillRect(0, 0,
                                this.getWidth(),
                                this.getHeight());

                    }
                };
                busyPanel.setOpaque(false);
                this.setGlassPane(busyPanel);
            }

            busyPanel.setVisible(b);

            btnCerrar.setEnabled(!b);
            btnNuevo.setVisible(!b && nvoVisible);
            btnModificar.setVisible(!b && modVisible);
            btnConsultar.setVisible(!b && consVisible);
            btnEliminar.setVisible(!b && elimVisible);

            jXBusyLabel.setVisible(b);
            jXBusyLabel.setBusy(b);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    protected javax.swing.JButton btnConsultar;
    protected javax.swing.JButton btnEliminar;
    protected javax.swing.JButton btnModificar;
    protected javax.swing.JButton btnNuevo;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXBusyLabel jXBusyLabel;
    protected org.jdesktop.swingx.JXTable jXTable1;
    private ar.com.init.agros.util.gui.components.TableFindAndFilter tableFindAndFilter1;
    // End of variables declaration//GEN-END:variables
}
