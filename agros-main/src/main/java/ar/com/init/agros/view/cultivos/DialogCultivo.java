/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DialogCultivo.java
 *
 * Created on 30/06/2009, 20:57:13
 */
package ar.com.init.agros.view.cultivos;

import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.controller.VariedadCultivoJpaController;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.table.TablizableEntityDataModel;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 *
 * @author gmatheu
 */
public class DialogCultivo extends javax.swing.JDialog
{

    private static final long serialVersionUID = -1L;
    private TablizableEntityDataModel<VariedadCultivo> variedadCultivoTableModel;
    private CultivoJpaController cultivoJpaController;
    private Cultivo cultivo;
    private VariedadCultivo variedad;
    private String successMessage = "registró";
    private VariedadCultivoJpaController variedadCultivoController;

    /** Creates new form DialogCultivo */
    public DialogCultivo(java.awt.Frame parent)
    {
        this(parent, null, true);
    }

    /** Creates new form DialogCultivo
     *
     * @param parent
     * @param cultivo la instancia de la cultivo a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     */
    public DialogCultivo(java.awt.Frame parent, Cultivo cultivo, boolean isUpdate)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();
        cultivoJpaController = new CultivoJpaController();
        variedadCultivoController = new VariedadCultivoJpaController();
        variedadCultivoTableModel = new TablizableEntityDataModel<VariedadCultivo>(VariedadCultivo.TABLE_HEADERS);
        jXTableVariedades.setModel(variedadCultivoTableModel);

        if (cultivo != null) // Caso en el que no será utilizada como ventana de alta
        {
            setCultivo(cultivo);
            if (!isUpdate) // Caso en el que será de consulta
            {
                disableFieldsAndButtons();
            }
            else // caso en el que será de modificación
            {
                successMessage = "modificó";
            }
        }

        oKCancelCleanPanel.setListenerToButtons(new OkRemoveCleanEventControl());
        oKCancelCleanPanel.setOwner(this);

        addRemoveUpdatePanel1.setVisible(true, true, true, true);
        addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
        addRemoveUpdatePanel1.addActionListener(new AddRemoveEventControl());

        jXTableVariedades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTableVariedades.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (jXTableVariedades.getSelectedRow() > -1)
                {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(true);
                    if (jXTableVariedades.getSelectedRowCount() == 1)
                    {
                        addRemoveUpdatePanel1.setBtnUpdateEnabled(true);
                        fillVariedadFields(variedadCultivoTableModel.getData().get(jXTableVariedades.convertRowIndexToModel(jXTableVariedades.getSelectedRow())));
                    }
                    else
                    {
                        addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
                    }
                }
                else
                {
                    addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
                    addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
                }
            }
        });
        jXTableVariedades.packAll();
    }

    private void fillVariedadFields(VariedadCultivo var)
    {
        jTextFieldVariedad.setText(var.getNombre());
        jTextAreaDescVariedad.setText(var.getDescripcion());
    }

    public Cultivo getCultivo()
    {
        if (cultivo == null)
        {
            cultivo = new Cultivo();
        }
        cultivo.setNombre(jTextFieldCultivo.getText());
        cultivo.setDescripcion(jTextAreaDescCultivo.getText());
        List<VariedadCultivo> vars = variedadCultivoTableModel.getData();
        for (VariedadCultivo v : vars) {
            v.setCultivo(cultivo);
        }
        cultivo.setVariedades(vars);
        return cultivo;
    }

    public void setCultivo(Cultivo cultivo)
    {
        this.cultivo = cultivo;
        jTextFieldCultivo.setText(cultivo != null ? cultivo.getNombre() : "");
        jTextAreaDescCultivo.setText((cultivo != null ? cultivo.getDescripcion() : ""));

        List<VariedadCultivo> variedades = cultivo.getVariedades();
        Collections.sort(variedades);
        variedadCultivoTableModel.setData(variedades);

        variedad = null;
    }

    private void disableFieldsAndButtons()
    {
        jTextAreaDescCultivo.setEditable(false);
        jTextAreaDescVariedad.setEditable(false);
        jTextFieldCultivo.setEditable(false);
        jTextFieldVariedad.setEditable(false);
        jXTableVariedades.setEnabled(false);
        oKCancelCleanPanel.disableForList();
        addRemoveUpdatePanel1.setEnabled(false);
    }

    private void setVariedad(VariedadCultivo variedad)
    {
        this.variedad = variedad;
        jTextFieldVariedad.setText(variedad != null ? variedad.getNombre() : "");
        jTextAreaDescVariedad.setText((variedad != null ? variedad.getDescripcion() : ""));

        if (variedad == null)
        {
            jXTableVariedades.clearSelection();
        }
    }

    private VariedadCultivo getVariedad()
    {
        if (variedad == null)
        {
            variedad = new VariedadCultivo();
        }
        variedad.setNombre(jTextFieldVariedad.getText());
        variedad.setDescripcion(jTextAreaDescVariedad.getText());
        return variedad;
    }

    private class OkRemoveCleanEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel.getBtnAceptar())
            {
                if (validateInput(getCultivo()))
                {
                    try
                    {
                        cultivoJpaController.persistOrUpdate(cultivo);
                        frameNotifier.showInformationMessage("Se " + successMessage + " el Cultivo " + cultivo.getNombre() + " con éxito");
                        clear();
                    }
                    catch (ConstraintViolationException ex)
                    {
                        frameNotifier.showErrorMessage("Ya existe un cultivo con ese nombre.");
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogCultivo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogCultivo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnCancelar())
            {
                DialogCultivo.this.dispose();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnClean())
            {
                clear();
                frameNotifier.showOkMessage();
            }
        }
    }

    private void clear()
    {
        jTextAreaDescCultivo.setText("");
        jTextFieldCultivo.setText("");
        clearVariedadesFields();
        variedadCultivoTableModel.setData(new ArrayList<VariedadCultivo>());
        cultivo = null;
    }

    private void clearVariedadesFields()
    {
        jTextAreaDescVariedad.setText("");
        jTextFieldVariedad.setText("");
        variedad = null;
    }

    private class AddRemoveEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addRemoveUpdatePanel1.getJButtonAdd())
            {

                if (validateInput(getVariedad()))
                {
                    boolean existe = false;
                    for (VariedadCultivo v : variedadCultivoTableModel.getData())
                    {
                        if (v.getNombre().equalsIgnoreCase(variedad.getNombre()))
                        {
                            existe = true;
                            break;
                        }
                    }

                    if (!existe)
                    {
                        variedadCultivoTableModel.add(variedad);
                        frameNotifier.showInformationMessage("Se agregó la Variedad " + variedad.getNombre());
                        clearVariedadesFields();
                    }
                    else
                    {
                        frameNotifier.showErrorMessage("Ya existe una variedad con el nombre " + variedad.getNombre() + " para el cultivo");
                    }
                }
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonRemove())
            {
                int[] selectedIdx = GUIUtility.getModelSelectedRows(jXTableVariedades);

                for (int idx : selectedIdx) {
                    variedadCultivoTableModel.getData().get(idx).setCultivo(null);
                }

                variedadCultivoTableModel.removeRows(selectedIdx);
                frameNotifier.showInformationMessage("Variedades eliminadas");
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonClean())
            {
                clearVariedadesFields();
            }
            else if (e.getSource() == addRemoveUpdatePanel1.getJButtonUpdate())
            {
                VariedadCultivo aux = variedadCultivoTableModel.getData().get(jXTableVariedades.convertRowIndexToModel(jXTableVariedades.getSelectedRow()));
                if (validateInput(getVariedad()))
                {
                    variedadCultivoTableModel.remove(aux);
                    if (!add())
                    {
                        variedadCultivoTableModel.add(aux);
                    }
                }
            }
        }
    }

    private boolean add()
    {
        boolean existe = false;
        for (VariedadCultivo v : variedadCultivoTableModel.getData())
        {
            if (v.getNombre().equalsIgnoreCase(variedad.getNombre()))
            {
                existe = true;
                frameNotifier.showErrorMessage("Ya existe una variedad con el nombre " + variedad.getNombre() + " para el cultivo");
                break;
            }
        }
        if (!existe && variedadCultivoController.exists(variedad)) // Controlo que no exista en DB
        {
            existe = true;
            frameNotifier.showErrorMessage("Ya existe una variedad con el nombre " + variedad.getNombre() + " en el sistema, el nombre debe ser único");
        }

        if (!existe)
        {
            variedadCultivoTableModel.add(variedad);
            frameNotifier.showInformationMessage("Se " + successMessage + " la variedad " + variedad.getNombre());
            clearVariedadesFields();
            return true;
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jPanelVariedad = new javax.swing.JPanel();
        jLabelVariedad = new javax.swing.JLabel();
        jLabelDescVariedad = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescVariedad = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTableVariedades = new org.jdesktop.swingx.JXTable();
        jTextFieldVariedad = new javax.swing.JTextField();
        addRemoveUpdatePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel();
        jLabelCultivo = new javax.swing.JLabel();
        jLabelDescCultivo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaDescCultivo = new javax.swing.JTextArea();
        jTextFieldCultivo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        frameNotifier.setName("frameNotifier"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogCultivo.class);
        jPanelVariedad.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelVariedad.border.title"))); // NOI18N
        jPanelVariedad.setName("jPanelVariedad"); // NOI18N

        jLabelVariedad.setText(resourceMap.getString("jLabelVariedad.text")); // NOI18N
        jLabelVariedad.setName("jLabelVariedad"); // NOI18N

        jLabelDescVariedad.setText(resourceMap.getString("jLabelDescVariedad.text")); // NOI18N
        jLabelDescVariedad.setName("jLabelDescVariedad"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaDescVariedad.setColumns(20);
        jTextAreaDescVariedad.setRows(5);
        jTextAreaDescVariedad.setName("jTextAreaDescVariedad"); // NOI18N
        jScrollPane1.setViewportView(jTextAreaDescVariedad);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jXTableVariedades.setName("jXTableVariedades"); // NOI18N
        jXTableVariedades.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jXTableVariedades);

        jTextFieldVariedad.setName("jTextFieldVariedad"); // NOI18N
        jTextFieldVariedad.setNextFocusableComponent(jTextAreaDescVariedad);

        addRemoveUpdatePanel1.setName("addRemoveUpdatePanel1"); // NOI18N

        javax.swing.GroupLayout jPanelVariedadLayout = new javax.swing.GroupLayout(jPanelVariedad);
        jPanelVariedad.setLayout(jPanelVariedadLayout);
        jPanelVariedadLayout.setHorizontalGroup(
            jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVariedadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelVariedadLayout.createSequentialGroup()
                        .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelVariedad, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDescVariedad, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(jTextFieldVariedad, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelVariedadLayout.setVerticalGroup(
            jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVariedadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelVariedadLayout.createSequentialGroup()
                        .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelVariedad)
                            .addComponent(jTextFieldVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelVariedadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelVariedadLayout.createSequentialGroup()
                                .addComponent(jLabelDescVariedad)
                                .addGap(106, 106, 106))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveUpdatePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabelCultivo.setText(resourceMap.getString("jLabelCultivo.text")); // NOI18N
        jLabelCultivo.setName("jLabelCultivo"); // NOI18N

        jLabelDescCultivo.setText(resourceMap.getString("jLabelDescCultivo.text")); // NOI18N
        jLabelDescCultivo.setName("jLabelDescCultivo"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextAreaDescCultivo.setColumns(20);
        jTextAreaDescCultivo.setRows(5);
        jTextAreaDescCultivo.setName("jTextAreaDescCultivo"); // NOI18N
        jScrollPane2.setViewportView(jTextAreaDescCultivo);

        jTextFieldCultivo.setName("jTextFieldCultivo"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelCultivo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelDescCultivo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldCultivo, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelVariedad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCultivo)
                    .addComponent(jTextFieldCultivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDescCultivo)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel addRemoveUpdatePanel1;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JLabel jLabelCultivo;
    private javax.swing.JLabel jLabelDescCultivo;
    private javax.swing.JLabel jLabelDescVariedad;
    private javax.swing.JLabel jLabelVariedad;
    private javax.swing.JPanel jPanelVariedad;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextAreaDescCultivo;
    private javax.swing.JTextArea jTextAreaDescVariedad;
    private javax.swing.JTextField jTextFieldCultivo;
    private javax.swing.JTextField jTextFieldVariedad;
    private org.jdesktop.swingx.JXTable jXTableVariedades;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    // End of variables declaration//GEN-END:variables
}
