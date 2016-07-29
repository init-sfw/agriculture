/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.com.init.agros.view.agroquimicos;


import ar.com.init.agros.controller.InformacionAgroquimicoJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.view.agroquimicos.model.AccionesTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

 /*
  * Clase GUI DialogAgroquimicoManual
  *
  * @author fbobbio
  * @version 18-jul-2010
  */
public class DialogAgroquimicoManual extends javax.swing.JDialog
{
    private EventControl evt;
    private AccionesTableModel accionesTableModel;
    //private Agroquimico agroquimico;
    private InformacionAgroquimico infoAgroquimico;
    private InformacionAgroquimicoJpaController infoAgroquimicoController;
    private String successMessage = "registró";

    public DialogAgroquimicoManual(java.awt.Frame parent)
    {
        this (parent,null,true);
    }
    
    public DialogAgroquimicoManual(java.awt.Frame parent, InformacionAgroquimico agroquimico, boolean isUpdate)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();
        accionesTableModel = new AccionesTableModel();
        infoAgroquimicoController = new InformacionAgroquimicoJpaController();
        jXTableAcciones.setModel(accionesTableModel);
        accionesTableModel.setAcciones(infoAgroquimicoController.findAllAcciones());

        UnidadMedida[] unidades = new UnidadMedida[2];
        unidades[0] = MagnitudEnum.PESO.patron();
        unidades[1] = MagnitudEnum.VOLUMEN.patron();
        panelValorUnidadStockMinimo.addUnidades(unidades);
        panelValorUnidadStockMinimo.setEnabled(true, false);
        List<UnidadMedida> unids = new ArrayList<UnidadMedida>(2);
        unids.add(unidades[0]);
        unids.add(unidades[1]);
        GUIUtility.refreshComboBox(unids,jComboBoxUnidadMedida);

        if (agroquimico != null) // Caso en el que no será utilizada como ventana de alta
        {
            setAgroquimico(agroquimico);
            if (!isUpdate) // Caso en el que será de consulta
            {
                disableFieldsAndButtons();
            }
            else
            {
                successMessage = "modificó";
            }
        }
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        panelValorUnidadStockMinimo.setEnabled(false,false);
//        oKCancelCleanPanel1.setOwner(this);
    }

    public InformacionAgroquimico getAgroquimico()
    {
        if (infoAgroquimico == null)
        {
            infoAgroquimico = new InformacionAgroquimico(true);
        }
        infoAgroquimico.setNombreComercial(jTextFieldNombreComercial.getText().trim());
        infoAgroquimico.setPrincipioActivo(jTextFieldPrincipioActivo.getText().trim());
        infoAgroquimico.setConcentracion(jTextFieldConcentracion.getText().trim());
        infoAgroquimico.setCompatibilidad(jTextAreaCompatibilidad.getText().trim());
        infoAgroquimico.setRestriccionesDeUso(jTextAreaRestricciones.getText().trim());
        infoAgroquimico.setAcciones(accionesTableModel.getCheckedAcciones());
        if (jCheckBoxSelParaUso.isSelected())
        {
            infoAgroquimico.setSeleccionadoUso(true);
            Agroquimico agroquimico = new Agroquimico();
            agroquimico.setInformacion(infoAgroquimico);
            agroquimico.setUnidad(getSelectedUnidadMedida());
            agroquimico.setStockMinimo(panelValorUnidadStockMinimo.getValorUnidad());
            infoAgroquimico.setAgroquimico(agroquimico);
        }
        else
        {
            infoAgroquimico.setSeleccionadoUso(false);
            infoAgroquimico.setAgroquimico(null);
        }
        return infoAgroquimico;
    }

    public void setAgroquimico(InformacionAgroquimico infoAgroquimico)
    {
        jTextFieldNombreComercial.setText(infoAgroquimico.getNombreComercial());
        jTextFieldPrincipioActivo.setText(infoAgroquimico.getPrincipioActivo());
        jTextFieldConcentracion.setText(infoAgroquimico.getConcentracion());
        jTextAreaCompatibilidad.setText(infoAgroquimico.getCompatibilidad());
        jTextAreaRestricciones.setText(infoAgroquimico.getRestriccionesDeUso());
        if (infoAgroquimico.getAgroquimico() != null)
        {
            panelValorUnidadStockMinimo.setValorUnidad(infoAgroquimico.getAgroquimico().getStockMinimo());
            jComboBoxUnidadMedida.setSelectedItem(infoAgroquimico.getAgroquimico().getUnidad());
        }
        accionesTableModel.setAcciones(infoAgroquimicoController.findAllAcciones(), infoAgroquimico.getAcciones());
        this.infoAgroquimico = infoAgroquimico;
    }

    private void disableFieldsAndButtons()
    {
        jTextFieldNombreComercial.setEditable(false);
        jTextFieldPrincipioActivo.setEditable(false);
        jTextFieldConcentracion.setEditable(false);
        jTextAreaCompatibilidad.setEditable(false);
        jTextAreaRestricciones.setEditable(false);
        jComboBoxUnidadMedida.setEnabled(false);
        panelValorUnidadStockMinimo.setEnabled(false, false);
        jXTableAcciones.setEditable(false);
        oKCancelCleanPanel1.disableForList();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableComboBoxRenderer1 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldNombreComercial = new javax.swing.JTextField();
        jTextFieldPrincipioActivo = new javax.swing.JTextField();
        jTextFieldConcentracion = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableAcciones = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaRestricciones = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaCompatibilidad = new javax.swing.JTextArea();
        jCheckBoxSelParaUso = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxUnidadMedida = new javax.swing.JComboBox();
        panelValorUnidadStockMinimo = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jLabel5 = new javax.swing.JLabel();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogAgroquimicoManual.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N

        jTextFieldNombreComercial.setNextFocusableComponent(jTextFieldPrincipioActivo);

        jXTableAcciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jXTableAcciones);

        jTextAreaRestricciones.setColumns(20);
        jTextAreaRestricciones.setRows(5);
        jScrollPane2.setViewportView(jTextAreaRestricciones);

        jTextAreaCompatibilidad.setColumns(20);
        jTextAreaCompatibilidad.setRows(5);
        jScrollPane3.setViewportView(jTextAreaCompatibilidad);

        jCheckBoxSelParaUso.setText(resourceMap.getString("jCheckBoxSelParaUso.text")); // NOI18N
        jCheckBoxSelParaUso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSelParaUsoActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        jComboBoxUnidadMedida.setEnabled(false);
        jComboBoxUnidadMedida.setRenderer(listableComboBoxRenderer1);
        jComboBoxUnidadMedida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUnidadMedidaActionPerformed(evt);
            }
        });

        panelValorUnidadStockMinimo.setEnabled(false);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel4)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jComboBoxUnidadMedida, 0, 321, Short.MAX_VALUE)
                    .add(panelValorUnidadStockMinimo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jComboBoxUnidadMedida, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel5)
                        .add(6, 6, 6))
                    .add(panelValorUnidadStockMinimo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel3)
                                    .add(jLabel2)
                                    .add(jLabel1)
                                    .add(jCheckBoxSelParaUso))
                                .add(5, 5, 5)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTextFieldPrincipioActivo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                                    .add(jTextFieldConcentracion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                                    .add(jTextFieldNombreComercial, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)))
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(jTextFieldNombreComercial, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jTextFieldPrincipioActivo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jTextFieldConcentracion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jCheckBoxSelParaUso)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                .add(14, 14, 14)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
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
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxUnidadMedidaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxUnidadMedidaActionPerformed
    {//GEN-HEADEREND:event_jComboBoxUnidadMedidaActionPerformed
        if (getSelectedUnidadMedida() != null)
        {
            panelValorUnidadStockMinimo.setSelectedUnidadMedida(getSelectedUnidadMedida());
        }
        else
        {
            panelValorUnidadStockMinimo.clear();
        }
    }//GEN-LAST:event_jComboBoxUnidadMedidaActionPerformed

    private void jCheckBoxSelParaUsoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxSelParaUsoActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxSelParaUsoActionPerformed
        if (jCheckBoxSelParaUso.isSelected())
        {
            jComboBoxUnidadMedida.setEnabled(true);
            panelValorUnidadStockMinimo.setEnabled(true, false);
        }
        else
        {
            jComboBoxUnidadMedida.setEnabled(false);
            jComboBoxUnidadMedida.setSelectedIndex(0);
            panelValorUnidadStockMinimo.setEnabled(false, false);
            panelValorUnidadStockMinimo.clear();
        }
    }//GEN-LAST:event_jCheckBoxSelParaUsoActionPerformed


    private UnidadMedida getSelectedUnidadMedida()
    {
        Object aux = jComboBoxUnidadMedida.getSelectedItem();
        if (aux instanceof UnidadMedida)
        {
            return (UnidadMedida) aux;
        }
        else
        {
            return null;
        }
    }
    /** Clase de control de eventos que maneja los eventos de la GUI DialogAgroquimicoManual y las validaciones de la misma */
    public class EventControl extends AbstractEventControl implements ActionListener
    {
        /** Método que maneja los eventos de la GUI DialogAgroquimicoManual
         *  @param e el evento de acción lanzado por algún componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {

            if (e.getSource() == oKCancelCleanPanel1.getBtnClean())
            {
                clear();
                frameNotifier1.showOkMessage();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar())
            {
                closeWindow(DialogAgroquimicoManual.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar())
            {
                if (validateInput(getAgroquimico()))
                {
                    if (infoAgroquimico.getAgroquimico() == null || validateInput(infoAgroquimico.getAgroquimico()))
                    {
                        try
                        {
                            infoAgroquimicoController.persistOrUpdate(infoAgroquimico);
                            frameNotifier1.showInformationMessage("Se " + successMessage + " con éxito el agroquímico " + infoAgroquimico.getNombreComercial());
                            clear();
                        }
                        catch (ConstraintViolationException ex)
                        {
                            frameNotifier1.showErrorMessage("Ya existe un agroquímico con la combinación nombre comercial " + infoAgroquimico.getNombreComercial() + ", principio activo " + infoAgroquimico.getPrincipioActivo() + " y concentración " + infoAgroquimico.getConcentracion());
                        }
                        catch (InvalidStateException ex)
                        {
                            frameNotifier1.showErrorMessage(ex.getMessage());
                            Logger.getLogger(DialogAgroquimicoManual.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        catch (Exception ex)
                        {
                            frameNotifier1.showErrorMessage(ex.getMessage());
                            Logger.getLogger(DialogAgroquimicoManual.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier1;
        }
    }

    public void clear()
    {
        jTextAreaCompatibilidad.setText("");
        jTextAreaRestricciones.setText("");
        jTextFieldConcentracion.setText("");
        jTextFieldNombreComercial.setText("");
        jTextFieldPrincipioActivo.setText("");
        panelValorUnidadStockMinimo.clear();
        jComboBoxUnidadMedida.setSelectedIndex(0);
        accionesTableModel.changeAll(false);
        infoAgroquimico = null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JCheckBox jCheckBoxSelParaUso;
    private javax.swing.JComboBox jComboBoxUnidadMedida;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextAreaCompatibilidad;
    private javax.swing.JTextArea jTextAreaRestricciones;
    private javax.swing.JTextField jTextFieldConcentracion;
    private javax.swing.JTextField jTextFieldNombreComercial;
    private javax.swing.JTextField jTextFieldPrincipioActivo;
    private org.jdesktop.swingx.JXTable jXTableAcciones;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadStockMinimo;
    // End of variables declaration//GEN-END:variables

}
