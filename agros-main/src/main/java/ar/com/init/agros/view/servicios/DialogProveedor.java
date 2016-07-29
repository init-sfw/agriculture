/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.view.servicios;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI DialogProveedor para hacer altas, modificaciones y consultas de los proveedores
 *
 * @author fbobbio
 * @version 08-jun-2009
 */
public class DialogProveedor extends javax.swing.JDialog
{
    private EventControl evt;
    private BaseEntityJpaController<Servicio> proveedorJpaController;
    private Servicio proveedor;
    private String successMessage = "registró";

    /** Creates new form DialogProveedor */
    public DialogProveedor(java.awt.Frame parent)
    {
        this(parent, null, true);
    }

    /** Creates new form DialogProveedor
     *
     * @param parent
     * @param proveedor la instancia del proveedor a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     */
    public DialogProveedor(java.awt.Frame parent, Servicio proveedor, boolean isUpdate)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();
        if (proveedor != null) // Caso en el que no será utilizada como ventana de alta
        {
            setProveedor(proveedor);
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
//        oKCancelCleanPanel1.setOwner(this);
        proveedorJpaController = new BaseEntityJpaController<Servicio>(Servicio.class);
    }

    public Servicio getProveedor()
    {
        if (proveedor == null)
        {
            proveedor = new Servicio();
        }
        proveedor.setDomicilio(txtDomicilio.getText().trim());
        proveedor.setRazonSocial(txtRazonSocial.getText().trim());
        proveedor.setTelefonos(phonePanel1.getTelefonos());
        proveedor.setTipo(getTiposervicioSeleccionado());
        return proveedor;
    }

    public void setProveedor(Servicio proveedor)
    {
        txtDomicilio.setText(proveedor.getDomicilio());
        txtRazonSocial.setText(proveedor.getRazonSocial());
        phonePanel1.initPhones(proveedor.getTelefonos());
        setTipoServicioSeleccionado(proveedor.getTipo());
        this.proveedor = proveedor;
    }

    private void disableFieldsAndButtons()
    {
        txtDomicilio.setEditable(false);
        txtRazonSocial.setEditable(false);
        phonePanel1.disableFields();
        oKCancelCleanPanel1.disableForList();
        jRadioButtonComprador.setEnabled(false);
        jRadioButtonContratista.setEnabled(false);
        jRadioButtonOtros.setEnabled(false);
        jRadioButtonProveedorInsumos.setEnabled(false);
        jRadioButtonProveedorPostCosecha.setEnabled(false);
    }

    private TipoServicio getTiposervicioSeleccionado()
    {
        if (jRadioButtonComprador.isSelected())
        {
            return TipoServicio.COMPRADOR;
        }
        if (jRadioButtonContratista.isSelected())
        {
            return TipoServicio.CONTRATISTA;
        }
        if (jRadioButtonOtros.isSelected())
        {
            return TipoServicio.OTROS;
        }
        if (jRadioButtonProveedorInsumos.isSelected())
        {
            return TipoServicio.PROVEEDOR_INSUMOS;
        }
        if (jRadioButtonProveedorPostCosecha.isSelected())
        {
            return TipoServicio.PROVEEDOR_POST_COSECHA;
        }
        return null;
    }

    private void setTipoServicioSeleccionado(TipoServicio tipo)
    {
        if (tipo.equals(TipoServicio.COMPRADOR))
        {
            jRadioButtonComprador.setSelected(true);
        }
        if (tipo.equals(TipoServicio.CONTRATISTA))
        {
            jRadioButtonContratista.setSelected(true);
        }
        if (tipo.equals(TipoServicio.OTROS))
        {
            jRadioButtonOtros.setSelected(true);
        }
        if (tipo.equals(TipoServicio.PROVEEDOR_INSUMOS))
        {
            jRadioButtonProveedorInsumos.setSelected(true);
        }
        if (tipo.equals(TipoServicio.PROVEEDOR_POST_COSECHA))
        {
            jRadioButtonProveedorPostCosecha.setSelected(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jPanel1 = new javax.swing.JPanel();
        phonePanel1 = new ar.com.init.agros.util.gui.components.personas.PhonePanel();
        jLabel7 = new javax.swing.JLabel();
        txtDomicilio = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRazonSocial = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonProveedorInsumos = new javax.swing.JRadioButton();
        jRadioButtonComprador = new javax.swing.JRadioButton();
        jRadioButtonContratista = new javax.swing.JRadioButton();
        jRadioButtonProveedorPostCosecha = new javax.swing.JRadioButton();
        jRadioButtonOtros = new javax.swing.JRadioButton();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogProveedor.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N

        txtDomicilio.setNextFocusableComponent(phonePanel1.getBtnNewPhone());

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        txtRazonSocial.setText(resourceMap.getString("txtRazonSocial.text")); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButtonProveedorInsumos);
        jRadioButtonProveedorInsumos.setText(resourceMap.getString("jRadioButtonProveedorInsumos.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jRadioButtonProveedorInsumos, gridBagConstraints);

        buttonGroup1.add(jRadioButtonComprador);
        jRadioButtonComprador.setText(resourceMap.getString("jRadioButtonComprador.text")); // NOI18N
        jPanel2.add(jRadioButtonComprador, new java.awt.GridBagConstraints());

        buttonGroup1.add(jRadioButtonContratista);
        jRadioButtonContratista.setText(resourceMap.getString("jRadioButtonContratista.text")); // NOI18N
        jPanel2.add(jRadioButtonContratista, new java.awt.GridBagConstraints());

        buttonGroup1.add(jRadioButtonProveedorPostCosecha);
        jRadioButtonProveedorPostCosecha.setText(resourceMap.getString("jRadioButtonProveedorPostCosecha.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel2.add(jRadioButtonProveedorPostCosecha, gridBagConstraints);

        buttonGroup1.add(jRadioButtonOtros);
        jRadioButtonOtros.setText(resourceMap.getString("jRadioButtonOtros.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jRadioButtonOtros, gridBagConstraints);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRazonSocial, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(txtDomicilio, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(phonePanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(phonePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel5.getAccessibleContext().setAccessibleName("Razon Social:"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oKCancelCleanPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Clase de control de eventos que maneja los eventos de la GUI DialogProveedor y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** Método que maneja los eventos de la GUI FrameProveedor
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
                closeWindow(DialogProveedor.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar())
            {
                if (validateInput(getProveedor()))
                {
                    try
                    {
                        proveedorJpaController.persistOrUpdate(proveedor);
                        frameNotifier1.showInformationMessage("Se " + successMessage + " con éxito el servicio " + proveedor.getRazonSocial());
                        clear();
                    }
                    catch (ConstraintViolationException ex)
                    {
                        frameNotifier1.showErrorMessage("Ya existe un servicio con la Razón Social " + proveedor.getRazonSocial());
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogProveedor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogProveedor.class.getName()).log(Level.SEVERE, null, ex);
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
        evt.clean(txtDomicilio, txtRazonSocial);
        phonePanel1.cleanPhones();
        buttonGroup1.clearSelection();
        proveedor = null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonComprador;
    private javax.swing.JRadioButton jRadioButtonContratista;
    private javax.swing.JRadioButton jRadioButtonOtros;
    private javax.swing.JRadioButton jRadioButtonProveedorInsumos;
    private javax.swing.JRadioButton jRadioButtonProveedorPostCosecha;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.util.gui.components.personas.PhonePanel phonePanel1;
    private javax.swing.JTextField txtDomicilio;
    private javax.swing.JTextField txtRazonSocial;
    // End of variables declaration//GEN-END:variables
}
