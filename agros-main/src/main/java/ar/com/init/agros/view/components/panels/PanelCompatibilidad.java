/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelCompatibilidad.java
 *
 * Created on 12/06/2009, 00:41:41
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 *
 * @author gmatheu
 */
public class PanelCompatibilidad extends javax.swing.JPanel implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private AgroquimicoJpaController agroquimicoController;
    private FrameNotifier frameNotifier;

    /** Creates new form PanelCompatibilidad */
    public PanelCompatibilidad()
    {
        initComponents();
        dualListCompatibles.setFrameNotifier(frameNotifier);

        agroquimicoController = new AgroquimicoJpaController();
        try
        {
            refreshUI();
        }
        catch (PersistenceException e)
        {
            if (frameNotifier != null)
            {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelCompatibilidad.class, e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Agroquimico> getAgroquimicosCompatibles()
    {
        return (List<Agroquimico>) dualListCompatibles.getSelected();
    }

    public void setAgroquimicosCompatibles(Agroquimico agroquimico)
    {
        dualListCompatibles.clearSelected();
//        dualListCompatibles.addSelected(agroquimico.getCompatibles());
    }

    private void fillDisponibles()
    {
        dualListCompatibles.addAvailable(agroquimicoController.findEntities());
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dualListCompatibles = new ar.com.init.agros.util.gui.components.list.DualList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dualListCompatibles, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dualListCompatibles, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.list.DualList dualListCompatibles;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshUI()
    {
        fillDisponibles();
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
