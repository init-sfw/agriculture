/*
 * FrameNamedEntity.java
 *
 * Created on 07/06/2009, 20:00:56
 */
package ar.com.init.agros.util.gui.components.namedentities;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.base.NamedEntity;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 *
 * Clase que sirve para crear dialogos para insertar/modificar entidades que implementan NamedEntity
 *
 * {@code
 *  FrameNamedEntity<FormaFumigacion> f = new FrameNamedEntity<FormaFumigacion>(this.getFrame(), true, FormaFumigacion.class);
 *  f.setTitle(getResourceMap().getString("formaFumigacion.title"));
 *  f.setVisible(true);
 * }
 * @author gmatheu
 */
public class FrameNamedEntity<T extends NamedEntity> extends javax.swing.JDialog
{

    private static final long serialVersionUID = -1L;
    private NamedEntityJpaController<T> namedEntityJpaController;
    private Class<T> clazz;
    private NamedEntity namedEntity;

    /** Creates new form FrameNamedEntity */
    public FrameNamedEntity(java.awt.Frame parent, boolean modal, Class<T> clazz)
    {
        super(parent, modal);
        GUIUtility.initWindow(this);
        initComponents();

        EventControl evt = new EventControl();
        oKCancelCleanPanel.setListenerToButtons(evt);
        oKCancelCleanPanel.setOwner(this);

        this.clazz = clazz;
        namedEntityJpaController = new NamedEntityJpaController<T>(clazz);
    }

    public FrameNamedEntity(java.awt.Frame parent, boolean modal, Class<T> clazz, T entity, boolean enabled)
    {
        this(parent, modal, clazz);
        setNamedEntity(entity);
        setEnabled(enabled);
    }

    public NamedEntity getNamedEntity()
    {
        if (namedEntity == null) {
            try {
                namedEntity = (NamedEntity) Class.forName(clazz.getName()).newInstance();
            }
            catch (ClassNotFoundException ex) {
                Logger.getLogger(FrameNamedEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InstantiationException ex) {
                Logger.getLogger(FrameNamedEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalAccessException ex) {
                Logger.getLogger(FrameNamedEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        namedEntity.setDescripcion(jTextAreaDescripcion.getText());
        namedEntity.setNombre(jTextFieldNombre.getText());
        return namedEntity;
    }

    public void setNamedEntity(NamedEntity namedEntity)
    {
        jTextAreaDescripcion.setText(namedEntity.getDescripcion());
        jTextFieldNombre.setText(namedEntity.getNombre());
        this.namedEntity = namedEntity;
    }

    @Override
    public void setEnabled(boolean b)
    {
        jTextAreaDescripcion.setEditable(b);
        jTextFieldNombre.setEditable(b);

        if (!b) {
            oKCancelCleanPanel.disableForList();
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

        frameNotifier = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNombre = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescripcion = new javax.swing.JTextArea();
        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);

        frameNotifier.setName("frameNotifier"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameNamedEntity.class);
        jLabel1.setText(resourceMap.getString("jLabelNombre.text")); // NOI18N
        jLabel1.setName("jLabelNombre"); // NOI18N

        jTextFieldNombre.setName("jTextFieldNombre"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaDescripcion.setColumns(20);
        jTextAreaDescripcion.setRows(5);
        jTextAreaDescripcion.setName("jTextAreaDescripcion"); // NOI18N
        jScrollPane1.setViewportView(jTextAreaDescripcion);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addContainerGap())
        );

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameNotifier, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addGap(11, 11, 11))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameNotifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** M�todo que maneja los eventos de la GUI FrameProveedor
         *  @param e el evento de acci�n lanzado por alg�n componente de la GUI
         */
        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {

            if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
                FrameNamedEntity.this.clean();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                FrameNamedEntity.this.closeWindow();
            }
            else if (e.getSource() == oKCancelCleanPanel.getBtnAceptar()) {
                if (validateInput(getNamedEntity())) {
                    try {
                        namedEntityJpaController.persistOrUpdate((T) namedEntity);
                        frameNotifier.showInformationMessage(
                                "Se registr� con �xito el/la " + namedEntity.entityName());
                        FrameNamedEntity.this.clean();
                    }
                    catch (ConstraintViolationException ex) {
                        frameNotifier.showErrorMessage(
                                "Ya existe un/a " + namedEntity.entityName() + " con el mismo nombre");
//                         namedEntityJpaController.merge((T) namedEntity);
//                        namedEntityJpaController.refreshEntity((T) namedEntity);
                    }
                    catch (InvalidStateException ex) {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameNamedEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex) {
                        frameNotifier.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(FrameNamedEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return frameNotifier;
        }
    }

    /** M�todo que cierra la ventana y libera los recursos */
    public void closeWindow()
    {
        this.dispose();
    }

    /** M�todo donde se limpian todos los campos de la ventana */
    public void clean()
    {
        namedEntity = null;
        jTextAreaDescripcion.setText("");
        jTextFieldNombre.setText("");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaDescripcion;
    private javax.swing.JTextField jTextFieldNombre;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    // End of variables declaration//GEN-END:variables
}