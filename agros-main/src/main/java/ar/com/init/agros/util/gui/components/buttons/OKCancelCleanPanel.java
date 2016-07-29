/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OKCancelCleanPanel.java
 *
 * Created on 07-jun-2009, 18:23:38
 */
package ar.com.init.agros.util.gui.components.buttons;

import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.view.Application;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author fbobbio
 */
public class OKCancelCleanPanel extends javax.swing.JPanel
{

    private static final long serialVersionUID = -1L;
    private OkCleanCancelAbstractEventControl okCleanCancelAbstractEventControl;
    private RootPaneContainer owner;
    private boolean useBusyPanel;

    /** Creates new form OKCancelCleanPanel */
    public OKCancelCleanPanel()
    {
        initComponents();
        jXBusyLabel.setVisible(false);
        useBusyPanel = false;
    }

    public void disableForList()
    {
        btnAceptar.setVisible(false);
        btnClean.setVisible(false);
    }

    public void setUseBusyPanel(boolean useBusyPanel)
    {
        this.useBusyPanel = useBusyPanel;
    }

    public void setOkCleanCancelAbstractEventControl(OkCleanCancelAbstractEventControl okCleanCancelAbstractEventControl)
    {
        this.okCleanCancelAbstractEventControl = okCleanCancelAbstractEventControl;
    }

    public void setListenerToButtons(AbstractEventControl evt)
    {
        btnAceptar.addActionListener(evt);
        btnCancelar.addActionListener(evt);
        btnClean.addActionListener(evt);
    }

    public void setListenerToButtons(ActionListener evt)
    {
        btnAceptar.addActionListener(evt);
        btnCancelar.addActionListener(evt);
        btnClean.addActionListener(evt);
    }

    public ActionListener createEscapeAction()
    {
        return new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (btnCancelar.isEnabled() && btnCancelar.isVisible()) {
                    btnCancelar.doClick();
                }
            }
        };
    }

    public ActionListener createEnterAction()
    {
        return new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (btnAceptar.isEnabled() && btnAceptar.isVisible()) {
                    btnAceptar.doClick();
                }
            }
        };
    }

    public JButton getBtnAceptar()
    {
        return btnAceptar;
    }

    public void setBtnAceptar(JButton btnAceptar)
    {
        this.btnAceptar = btnAceptar;
    }

    public JButton getBtnCancelar()
    {
        return btnCancelar;
    }

    public void setBtnCancelar(JButton btnCancelar)
    {
        this.btnCancelar = btnCancelar;
    }

    public JButton getBtnClean()
    {
        return btnClean;
    }

    public void setBtnClean(JButton btnClean)
    {
        this.btnClean = btnClean;
    }

    public void setVisible(boolean ok, boolean cancel, boolean clean)
    {
        btnAceptar.setVisible(ok);
        btnCancelar.setVisible(cancel);
        btnClean.setVisible(clean);
    }

    public void setEnabled(boolean ok, boolean cancel, boolean clean)
    {
        btnAceptar.setEnabled(ok);
        btnCancelar.setEnabled(cancel);
        btnClean.setEnabled(clean);
    }

    public void setOwner(RootPaneContainer owner)
    {
        this.owner = owner;
        owner.getRootPane().registerKeyboardAction(this.createEnterAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        owner.getRootPane().registerKeyboardAction(this.createEscapeAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Action
    public Task doOk(final ActionEvent e)
    {
        if (okCleanCancelAbstractEventControl != null) {
            setBusy(true);
            return new Task(Application.getApplication())
            {

                @Override
                protected Object doInBackground() throws Exception
                {
                    okCleanCancelAbstractEventControl.doOk(e);
                    return "";
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void succeeded(Object result)
                {
                    super.succeeded(result);
                    setBusy(false);
                }
            };
        }
        return null;
    }

    @Action
    public void doClean(ActionEvent e)
    {
        if (okCleanCancelAbstractEventControl != null) {
            okCleanCancelAbstractEventControl.doClean(e);
        }
    }

    @Action
    public void doCancel(ActionEvent e)
    {
        if (okCleanCancelAbstractEventControl != null) {
            okCleanCancelAbstractEventControl.doCancel(e);
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

        jXBusyLabel = new org.jdesktop.swingx.JXBusyLabel();
        btnAceptar = new javax.swing.JButton();
        btnClean = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(OKCancelCleanPanel.class);
        jXBusyLabel.setText(resourceMap.getString("jXBusyLabel.text")); // NOI18N
        add(jXBusyLabel);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(OKCancelCleanPanel.class, this);
        btnAceptar.setAction(actionMap.get("doOk")); // NOI18N
        btnAceptar.setIcon(resourceMap.getIcon("btnAceptar.icon")); // NOI18N
        btnAceptar.setText(resourceMap.getString("btnAceptar.text")); // NOI18N
        add(btnAceptar);

        btnClean.setAction(actionMap.get("doClean")); // NOI18N
        btnClean.setIcon(resourceMap.getIcon("btnClean.icon")); // NOI18N
        btnClean.setText(resourceMap.getString("btnClean.text")); // NOI18N
        add(btnClean);

        btnCancelar.setAction(actionMap.get("doCancel")); // NOI18N
        btnCancelar.setIcon(resourceMap.getIcon("btnCancelar.icon")); // NOI18N
        btnCancelar.setText(resourceMap.getString("btnCancelar.text")); // NOI18N
        add(btnCancelar);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnClean;
    private org.jdesktop.swingx.JXBusyLabel jXBusyLabel;
    // End of variables declaration//GEN-END:variables
    private boolean aceptarVisible;
    private boolean limpiarVisible;
    private boolean cancelarVisible;
    private JPanel busyPanel;
    private static final Color backgroundColor = new Color(0.8f, 0.8f, 0.8f, 0.2f);

    private void setBusy(boolean b)
    {
        if (useBusyPanel) {
            if (b) {
                aceptarVisible = btnAceptar.isVisible();
                cancelarVisible = btnCancelar.isVisible();
                limpiarVisible = btnClean.isVisible();
            }

            if (owner != null) {
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
                    owner.setGlassPane(busyPanel);

                    busyPanel.setBackground(Color.red);
                }

                busyPanel.setVisible(b);
            }

            btnAceptar.setVisible(!b && aceptarVisible);
            btnCancelar.setVisible(!b && cancelarVisible);
            btnClean.setVisible(!b && limpiarVisible);

            jXBusyLabel.setVisible(b);
            jXBusyLabel.setBusy(b);
        }
    }

    public static abstract class OkCleanCancelAbstractEventControl extends AbstractEventControl
    {

        private OKCancelCleanPanel oKCancelCleanPanel;

        public OkCleanCancelAbstractEventControl(OKCancelCleanPanel oKCancelCleanPanel)
        {
            this.oKCancelCleanPanel = oKCancelCleanPanel;
        }

        @Override
        public final void actionPerformed(ActionEvent e)
        {
//            if (e.getSource() == oKCancelCleanPanel.getBtnAceptar()) {
//                doOk(e);
//            }
//            else if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
//                doCancel(e);
//            }
//            else if (e.getSource() == oKCancelCleanPanel.getBtnClean()) {
//                doClean(e);
//            }
        }

        public void doOk(ActionEvent e)
        {
        }

        public void doCancel(ActionEvent e)
        {
        }

        public void doClean(ActionEvent e)
        {
        }
    }
}
