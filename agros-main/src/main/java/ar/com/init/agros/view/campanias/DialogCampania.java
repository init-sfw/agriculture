package ar.com.init.agros.view.campanias;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI DialogCampania
 *
 * @author fbobbio
 * @version 14-jun-2009
 */
public class DialogCampania extends javax.swing.JDialog
{

    private EventControl evt;
    private BaseEntityJpaController<Campania> jpaController;
    private Campania campania;

    /** Creates new form DialogCampania */
    public DialogCampania(java.awt.Frame parent)
    {
        this(parent, null, true);
    }

    /** Creates new form DialogCampania
     *
     * @param parent
     * @param campania la instancia de la campania a modificar/consultar
     * @param isUpdate true si se quiere utilizar para modificar, false para consultar
     */
    public DialogCampania(java.awt.Frame parent, Campania campania, boolean isUpdate)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();

        if (campania != null) // Caso en el que no ser� utilizada como ventana de alta
        {
            setCampania(campania);
            if (!isUpdate) // Caso en el que ser� de consulta
            {
                disableFieldsAndButtons();
            }
        }
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
        jpaController = new BaseEntityJpaController<Campania>(Campania.class);
    }

    public Campania getCampania()
    {
        if (campania == null)
        {
            campania = new Campania();
        }

        campania.setNombre(txtNombre.getText());
        if (campania.getFechaCreacion() == null)
        {
            campania.setFechaCreacion(new Date());
        }
        return campania;
    }

    public void setCampania(Campania campania)
    {
        txtNombre.setText(campania.getNombre());
        this.campania = campania;
    }

    private void disableFieldsAndButtons()
    {
        txtNombre.setEditable(false);
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

        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogCampania.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtNombre, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtNombre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Clase de control de eventos que maneja los eventos de la GUI DialogCampania y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** M�todo que maneja los eventos de la GUI DialogCampania
         *  @param e el evento de acci�n lanzado por alg�n componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar())
            {
                closeWindow(DialogCampania.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnClean())
            {
                clear();
                frameNotifier1.showOkMessage();
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar())
            {
                if (validateInput(getCampania()))
                {
                    try
                    {
                        jpaController.persistOrUpdate(campania);
                        frameNotifier1.showInformationMessage(
                                "Se registr� con �xito la campa�a " + campania.getNombre());
                        clear();
                        pack();
                    }
                    catch (ConstraintViolationException ex)
                    {
                        frameNotifier1.showErrorMessage("Ya existe una campa�a con el nombre " + campania.getNombre());
                        jpaController.refreshEntity(campania);
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogCampania.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogCampania.class.getName()).log(Level.SEVERE, null, ex);
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

    private void clear()
    {
        txtNombre.setText("");
        campania = null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JLabel jLabel1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
