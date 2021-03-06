/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.view.siembras;

import ar.com.init.agros.controller.SiembraJpaController;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.validator.InvalidStateException;

/*
 * Clase GUI DialogSiembra
 *
 * @author fbobbio 
 * @version 28-jun-2009
 */
public class DialogSiembra extends javax.swing.JDialog
{
    private EventControl evt;
    private SiembraJpaController siembraJpaController;
    private Siembra siembra;
    private String successMessage = "registr�";

    /** Creates new form DialogProveedor */
    public DialogSiembra(java.awt.Frame parent)
    {
        this(parent, null, true);
    }

    /** Creates new form DialogSiembra */
    public DialogSiembra(java.awt.Frame parent, Siembra siembra, boolean isUpdate)
    {
        super(parent, true);
        GUIUtility.initWindow(this);
        initComponents();
        if (siembra != null) // Caso en el que no ser� utilizada como ventana de alta
        {
            if (!isUpdate) // Caso en el que ser� de consulta
            {
                disableFieldsAndButtons();
            }
            else
            {
                successMessage = "modific�";
            }
            setSiembra(siembra);
        }
        evt = new EventControl();
        oKCancelCleanPanel1.setListenerToButtons(evt);
        oKCancelCleanPanel1.setOwner(this);
        siembraJpaController = new SiembraJpaController();
        panelCostosSiembra1.setFrameNotifier(frameNotifier1);
        panelCampaniaCampoCultivoVariedad1.setFrameNotifier(frameNotifier1);
        panelCampaniaCampoCultivoVariedad1.setTransaccion(PanelCampaniaCampoCultivoVariedad.SIEMBRA);
    }

    private Siembra getSiembra()
    {
        if (siembra == null)
        {
            siembra = new Siembra();
        }
        siembra.setCampania(panelCampaniaCampoCultivoVariedad1.getCampania());
        siembra.setCampo(panelCampaniaCampoCultivoVariedad1.getCampo());
        siembra.setCultivo(panelCampaniaCampoCultivoVariedad1.getCultivo());
        siembra.setVariedadCultivo(panelCampaniaCampoCultivoVariedad1.getVariedad());
        siembra.setSuperficies(panelCampaniaCampoCultivoVariedad1.getSuperficiesSeleccionadas());
        siembra.setCostos(panelCostosSiembra1.getCostosCargados());
        siembra.actualizarRendimientos(); // creamos los rendimientos cada vez que se modifica o registra una siembra
        siembra.setObservaciones(panelObservacion1.getObservacion());
        return siembra;
    }

    private void disableFieldsAndButtons()
    {
        panelCampaniaCampoCultivoVariedad1.disableFields();
        oKCancelCleanPanel1.disableForList();
        panelCostosSiembra1.disableFields();
        panelObservacion1.disableFields();
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
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelCampaniaCampoCultivoVariedad1 = new ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad();
        panelCostosSiembra1 = new ar.com.init.agros.view.components.panels.PanelCostosSiembra();
        panelObservacion1 = new ar.com.init.agros.view.components.panels.PanelObservacion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogSiembra.class);
        jTabbedPane1.addTab(resourceMap.getString("panelCampaniaCampoCultivoVariedad1.TabConstraints.tabTitle"), panelCampaniaCampoCultivoVariedad1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelCostosSiembra1.TabConstraints.tabTitle"), panelCostosSiembra1); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("panelObservacion1.TabConstraints.tabTitle"), panelObservacion1); // NOI18N

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
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setSiembra(Siembra siembra)
    {
        panelCampaniaCampoCultivoVariedad1.setSelectedCampania(siembra.getCampania());
        panelCampaniaCampoCultivoVariedad1.setSelectedCultivo(siembra.getCultivo());
        panelCampaniaCampoCultivoVariedad1.setSelectedCampo(siembra.getCampo());
        panelCampaniaCampoCultivoVariedad1.setSelectedSuperficies(siembra.getSuperficies());
        panelCampaniaCampoCultivoVariedad1.setSelectedVariedad(siembra.getVariedadCultivo());
        panelCostosSiembra1.setCostosCargados(siembra.getCostos());
        panelObservacion1.setObservacion(siembra.getObservaciones());
        this.siembra = siembra;
    }

    public boolean isUnique()
    {
        boolean r = siembraJpaController.exists(siembra);
        if (r)
        {
            frameNotifier1.showErrorMessage("Ya existe una siembra con la combinaci�n seleccionada de a�o, campa�a, establecimiento, lotes y sublotes");
            panelCampaniaCampoCultivoVariedad1.setRedBorders();
            return false;
        }
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        frameNotifier1.showOkMessage();
        return true;
    }

    /** Clase de control de eventos que maneja los eventos de la GUI DialogSiembra y las validaciones de la misma */
    private class EventControl extends AbstractEventControl implements ActionListener
    {

        /** M�todo que maneja los eventos de la GUI DialogSiembra
         *  @param e el evento de acci�n lanzado por alg�n componente de la GUI
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
                closeWindow(DialogSiembra.this);
            }
            if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar())
            {
                if (validateInput(getSiembra()))
                {
                    if (!isUnique())
                    {
                        return;
                    }
                    try
                    {
                        if (GUIUtility.confirmData(DialogSiembra.this))
                        {
                            siembraJpaController.persistOrUpdate(siembra);
                            frameNotifier1.showInformationMessage("Se " + successMessage + " con �xito la siembra para la campa�a " + siembra.getCampania().getNombre());
                            clear();
                        }
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogSiembra.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getLocalizedMessage());
                        Logger.getLogger(DialogSiembra.class.getName()).log(Level.SEVERE, null, ex);
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
        panelCampaniaCampoCultivoVariedad1.clear();
        panelCampaniaCampoCultivoVariedad1.restoreRedBorders();
        panelCostosSiembra1.clear();
        panelObservacion1.clear();
        siembra = null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.panels.PanelCampaniaCampoCultivoVariedad panelCampaniaCampoCultivoVariedad1;
    private ar.com.init.agros.view.components.panels.PanelCostosSiembra panelCostosSiembra1;
    private ar.com.init.agros.view.components.panels.PanelObservacion panelObservacion1;
    // End of variables declaration//GEN-END:variables
}
