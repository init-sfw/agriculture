/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.view.campo;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.view.campo.model.SubLoteTableModel;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
 * Clase GUI DialogLote
 *
 * @author fbobbio
 * @version 04-jul-2009
 */
public class DialogLote extends javax.swing.JDialog
{

    private static final long serialVersionUID = -1L;
    private Campo campo;
    private CampoJpaController jpaController;
    private Lote lote;
    private SubLote subLote;
    private String successMessage = "registró";
    private SubLoteTableModel subLoteTableModel1;

    /** Creates new form DialogLote */
    public DialogLote(java.awt.Frame parent, Campo c)
    {
        this(parent, null, true, c);
    }

    /** Creates new form DialogLote
     *
     * @param parent
     * @param cultivo la instancia del lote a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     * @param c el campo padre de los lotes
     */
    public DialogLote(java.awt.Frame parent, Lote lote, boolean isUpdate, Campo c)
    {
        super(parent, true);
        subLoteTableModel1 = new SubLoteTableModel();
        campo = c;
        GUIUtility.initWindow(this);
        initComponents();
        jXTableSubLote.setModel(subLoteTableModel1);
        panelValorUnidadSuperficieLote.addUnidades(MagnitudEnum.SUPERFICIE.patron());
        panelValorUnidadSuperficieLote.selectUnidad(MagnitudEnum.SUPERFICIE.patron());
        panelValorUnidadSuperficieLote.setEnabled(true, false);
        panelValorUnidadSuperficieLote.setFrameNotifier(frameNotifier1);
        panelValorUnidadSuperficieSubLote.addUnidades(MagnitudEnum.SUPERFICIE.patron());
        panelValorUnidadSuperficieSubLote.selectUnidad(MagnitudEnum.SUPERFICIE.patron());
        panelValorUnidadSuperficieSubLote.setEnabled(true, false);
        panelValorUnidadSuperficieSubLote.setFrameNotifier(frameNotifier1);

        if (lote != null) // Caso en el que no será utilizada como ventana de alta
        {
            setLote(lote);
            if (!isUpdate) // Caso en el que será de consulta
            {
                disableFieldsAndButtons();
            }
            else {
                successMessage = "modificó";
            }
        }

        oKCancelCleanPanel1.setListenerToButtons(new EventControl());
        oKCancelCleanPanel1.setOwner(this);

        addRemoveUpdatePanel1.setVisible(true, true, true, true);
        addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
        addRemoveUpdatePanel1.addActionListener(new AddRemoveEventControl());

        jXTableSubLote.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTableSubLote.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                frameNotifier1.showOkMessage();

                if (jXTableSubLote.getSelectedRow() > -1) {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(true);
                    addRemoveUpdatePanel1.setBtnAddEnabled(false);

                    if (jXTableSubLote.getSelectedRowCount() == 1) {
                        addRemoveUpdatePanel1.setBtnUpdateEnabled(true);
                        setSublote(subLoteTableModel1.getData().get(jXTableSubLote.convertRowIndexToModel(
                                jXTableSubLote.getSelectedRow())));
                    }
                    else {
                        addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
                        clearSubLoteFields();
                    }
                }
                else {
                    addRemoveUpdatePanel1.setBtnAddEnabled(true);
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
                    addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
                    clearSubLoteFields();
                }
            }
        });
        jXTableSubLote.packAll();
    }

    public Lote getLoteCargado()
    {
        return lote;
    }

    public void setLoteCargado(Lote lote)
    {
        this.lote = lote;
    }

    private Lote getLote()
    {
        if (lote == null) {
            lote = new Lote(campo);
        }
        lote.setNombre(jTextFieldNombreLote.getText().trim());
        lote.setSuperficie(panelValorUnidadSuperficieLote.getValorUnidad());
        lote.setSubLotes(subLoteTableModel1.getData());
        return lote;
    }

    public void setLote(Lote lote)
    {
        this.lote = lote;
        jTextFieldNombreLote.setText(lote.getNombre());
        panelValorUnidadSuperficieLote.setValorUnidad(new ValorUnidad(lote.getSuperficie().getValor(),
                lote.getSuperficie().getUnidad()));
        subLoteTableModel1.setData(lote.getSubLotes());

        subLote = null;
    }

    private void disableFieldsAndButtons()
    {
        jTextFieldNombreLote.setEditable(false);
        jTextFieldNombreSubLote.setEditable(false);
        panelValorUnidadSuperficieLote.setEnabled(false);
        panelValorUnidadSuperficieSubLote.setEnabled(false);
        jXTableSubLote.setEditable(false);
        oKCancelCleanPanel1.disableForList();
        addRemoveUpdatePanel1.setEnabled(false);
    }

    private void setSublote(SubLote subLote)
    {
        this.subLote = subLote;
        jTextFieldNombreSubLote.setText(subLote != null ? subLote.getNombre() : "");
        if (subLote != null) {
            panelValorUnidadSuperficieSubLote.setValorUnidad(subLote.getSuperficie());
        }

        if (subLote == null) {
            jXTableSubLote.clearSelection();
        }
    }

    private SubLote getSubLote()
    {
        if (subLote == null) {
            subLote = new SubLote(getLote());
        }
        subLote.setNombre(jTextFieldNombreSubLote.getText().trim());
        subLote.setSuperficie(panelValorUnidadSuperficieSubLote.getValorUnidad());
        return subLote;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panelValorUnidadSuperficieLote = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jTextFieldNombreLote = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        panelValorUnidadSuperficieSubLote = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jTextFieldNombreSubLote = new javax.swing.JTextField();
        addRemoveUpdatePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableSubLote = new org.jdesktop.swingx.JXTable();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogLote.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N

        jTextFieldNombreSubLote.setNextFocusableComponent(panelValorUnidadSuperficieSubLote.getjTextFieldValor());

        jXTableSubLote.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableSubLote);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(panelValorUnidadSuperficieSubLote, 0, 0, Short.MAX_VALUE)
                            .add(jTextFieldNombreSubLote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(addRemoveUpdatePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(221, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jTextFieldNombreSubLote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(panelValorUnidadSuperficieSubLote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addRemoveUpdatePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldNombreLote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .add(panelValorUnidadSuperficieLote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                .addContainerGap())
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldNombreLote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(panelValorUnidadSuperficieLote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        lote = null;
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    /** Clase de control de eventos que maneja los eventos de la GUI DialogLote y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** Método que maneja los eventos de la GUI DialogLote
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {
                if (validateInput(getLote()) && checkSuperficie()) {
                    clear();
                    frameNotifier1.showOkMessage();
                    DialogLote.this.dispose();
                }
            }
            else if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
                if (lote != null) {
                    for (SubLote subLote : lote.getSubLotes()) {
                        jpaController.refreshEntity(subLote);
                    }
                }

                lote = null;
                DialogLote.this.dispose();
            }
            else if (e.getSource() == oKCancelCleanPanel1.getBtnClean()) {
                clear();
                lote = null;
                frameNotifier1.showOkMessage();
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }
    }

    private boolean checkSuperficie()
    {
        if (!lote.isSuperficieConsistente()) {
            frameNotifier1.showErrorMessage(
                    "La superficie del lote debe ser IGUAL a la suma de los sublotes\nSuperficie del lote: " + lote.getSuperficie().getFormattedValue() + "\nSuperficie total de sublotes: " + lote.calcularSuperficie().getFormattedValue());
            return false;
        }
        return true;
    }

    private void clear()
    {
        jTextFieldNombreLote.setText("");
        panelValorUnidadSuperficieLote.clear(true, false);
        clearSubLoteFields();
        subLoteTableModel1.setData(new ArrayList<SubLote>());
    }

    private void clearSubLoteFields()
    {
        jTextFieldNombreSubLote.setText("");
        panelValorUnidadSuperficieSubLote.clear(true, false);
        jXTableSubLote.clearSelection();
    }

    private class AddRemoveEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonAdd()) {
                if (validateInput(getSubLote())) {
                    add();
                }
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonRemove()) {
                subLoteTableModel1.removeRows(GUIUtility.getModelSelectedRows(jXTableSubLote));
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonClean()) {
                clearSubLoteFields();
                subLote = null;
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonUpdate()) {

//                int idx = jXTableSubLote.convertRowIndexToModel(jXTableSubLote.getSelectedRow());
//                SubLote aux = subLoteTableModel1.getData().get(idx);
//                try {
//                    aux = (SubLote) aux.clone();
//                }
//                catch (CloneNotSupportedException ex) {
//                    Logger.getLogger(DialogLote.class.getName()).log(Level.SEVERE, null, ex);
//                }
                SubLote sl = getSubLote();
                if (validateInput(sl)) {
                    subLoteTableModel1.remove(sl);
                    if (!add()) {
                        subLoteTableModel1.add(sl);
                        jpaController.refreshEntity(sl);
                    }
                }
            }
        }

        private boolean add()
        {
            boolean existe = false;
            for (SubLote l : subLoteTableModel1.getData()) {
                if (l.getNombre().equalsIgnoreCase(subLote.getNombre())) {
                    existe = true;
                    frameNotifier1.showErrorMessage(
                            "Ya existe un sublote con el nombre " + subLote.getNombre() + " para el lote");
                    break;
                }
            }
            if (!existe && isSuperficieTotalSuperada(subLote)) {
                existe = true;
                frameNotifier1.showErrorMessage(
                        "La sumatoria de superficies de los sublotes no puede superar la superficie del lote");
            }

            if (!existe) {
                subLoteTableModel1.add(subLote);
                frameNotifier1.showInformationMessage(
                        "Se " + successMessage + " el Sublote " + subLote.toString());
                subLote = null;
                clearSubLoteFields();
                return true;
            }
            return false;
        }

        /** Método que verifica si la sumatoria de superficies de los sublotes supera la del lote */
        private boolean isSuperficieTotalSuperada(SubLote toAdd)
        {
            Lote l = getLote();
            double acum = 0;
            for (SubLote s : subLoteTableModel1.getData()) {
                acum += s.getSuperficie().getValor();
            }
            acum += toAdd.getSuperficie().getValor();
            acum = GUIUtility.redondearDecimales(acum);
            if (l == null || l.getSuperficie() == null || !l.getSuperficie().isValid() || acum > l.getSuperficie().getValor()) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public void setController(CampoJpaController jpaController)
    {
        this.jpaController = jpaController;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel addRemoveUpdatePanel1;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldNombreLote;
    private javax.swing.JTextField jTextFieldNombreSubLote;
    private org.jdesktop.swingx.JXTable jXTableSubLote;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadSuperficieLote;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadSuperficieSubLote;
    // End of variables declaration//GEN-END:variables
}
