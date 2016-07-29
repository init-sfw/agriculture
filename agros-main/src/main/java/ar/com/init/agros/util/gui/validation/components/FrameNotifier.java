package ar.com.init.agros.util.gui.validation.components;

import ar.com.init.agros.util.gui.*;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * FrameNotifier.java
 *
 * Panel notificador de errores para ventanas
 *
 * @author fbobbio
 * @version 24 de enero de 2008
 */
public class FrameNotifier extends javax.swing.JPanel
{

    /** Constante que refiere al tipo de mensaje de Ok */
    public static final int OK_MESSAGE = JOptionPane.PLAIN_MESSAGE;
    /** Constante que refiere al tipo de mensaje de error */
    public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
    /** Constante que refiere al tipo de mensaje de información */
    public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
    /** Constante que refiere al tipo de mensaje de pregunta o ayuda */
    public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
    /** Constante que refiere al tipo de mensaje de actualización */
    public static final int UPDATING_MESSAGE = -2;
    /** Constante que refiere al tipo de mensaje de advertencia */
    public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
    /** El estado actual del bean */
    private int currentState;

    /** Creates new form FrameNotifier */
    public FrameNotifier()
    {
        try
        {
            initComponents();
            showOkMessage();
        }
        catch (Exception e) // catcheo que evita problemas con el editor de netbeans
        {
            GUIUtility.logError(FrameNotifier.class, e);
        }
    }

    /** Devuelve el estado actual del FrameNotifier
     * @return el estado actual del FrameNotifier
     */
    public int getCurrentState()
    {
        return currentState;
    }

    /** Setea el estado actual del FrameNotifier
     * @param actualState el estado actual del FrameNotifier
     */
    public void setCurrentState(int actualState)
    {
        this.currentState = actualState;
    }

    private String getStringFromType(int type)
    {
        switch (type)
        {
            case ERROR_MESSAGE:
                return "Error:";
            case INFORMATION_MESSAGE:
                return "Info:";
            case WARNING_MESSAGE:
                return "Advertencia:";
            case QUESTION_MESSAGE:
                return "Pregunta:";
            case UPDATING_MESSAGE:
                return "Actualización:";
            default:
                return "";
        }
    }

    /** Método que se encarga de mostrar el mensaje en el panel o en un dialog dependiendo de la validación de ancho
     * @param title el título del dialog
     * @param msg el mensaje a mostrar
     * @param icon el ícono a mostrar
     * @param type el tipo de mensaje
     */
    private void showMessage(String title, String msg, ImageIcon icon, int type)
    {
        if (isStringLargerThanContainer(msg))
        {
            if (type != OK_MESSAGE && type != UPDATING_MESSAGE)
            {
                GUIUtility.showMessage(GUIUtility.getParentFrame(this), title, msg, type);
                setLabelsColor(Color.black);
                setMessage("", new ImageIcon(getClass().getResource(GUIConstants.OK_ICON)), OK_MESSAGE);
            }
        }
        else
        {
            setMessage(msg, icon, type);
        }
        GUIUtility.getParentFrame(this).paintComponents(GUIUtility.getParentFrame(this).getGraphics());
    }

    private void setMessage(String msg, ImageIcon icon, int type)
    {
        lblMessage.setText(GUIUtility.convertPlainStringToHTML(msg));
        lblImage.setText(getStringFromType(type));
        this.setToolTipText(msg);
        lblImage.setIcon(icon);
        setCurrentState(type);
        setLabelsColor(Color.black);
        showNotifier();
    }

    public void showNotifier()
    {
        this.setSize(GUIUtility.getParentFrame(this).getWidth(), 300);
        this.setVisible(true);
    }

    public void hideNotifier()
    {
        this.setVisible(false);
    }

    /** Método que verifica que el mensaje a mostrar no sea considerablemente más grande que el ancho de la ventana
     * teniendo también en cuenta los posibles saltos de línea que tendrá el mensaje
     * @param msg el mensaje a mostrar
     * @return true: si el mensaje supera el ancho de la ventana - false: si el mensaje no supera el ancho de la ventana
     */
    private boolean isStringLargerThanContainer(String msg)
    {
        /* Creo una lista con todas las líneas del mensaje */
        String aux = msg;
        List<String> lines = new ArrayList<String>();
        while (aux.contains("\n"))
        {
            int lastIndex = aux.indexOf("\n") + 1;
            lines.add(aux.substring(0, lastIndex));
            aux = aux.replace(lines.get(lines.size() - 1), "");
        }
        lines.add(aux);

        for (String line : lines)
        {
            FontMetrics metrics = new FontMetrics(lblMessage.getFont())
            {
            };
            Rectangle2D bounds = metrics.getStringBounds(line, null);
            int stringPixels = (int) bounds.getWidth();
            int parentWidth = 0;
            try
            {
                parentWidth = GUIUtility.getParentFrame(this).getWidth();
            }
            catch (NullPointerException ex)// Caso que no se encuentre el parent frame
            {
                Logger.getLogger(FrameNotifier.class.getName()).log(Level.SEVERE, "Falta implementación correcta del componente FrameNotifier", ex);
            }
            if (stringPixels > parentWidth)
            {
                return true;
            }
        }
        return false;
    }

    private void setLabelsColor(Color c)
    {
        lblMessage.setForeground(c);
        lblImage.setForeground(c);
    }

    /** Mostrar un mensaje de operación o estado correcto */
    public void showOkMessage()
    {
        if (this.getCurrentState() != OK_MESSAGE) //si no está en ok message lo cambio
        {
            showMessage("", "", new ImageIcon(getClass().getResource(GUIConstants.OK_ICON)), OK_MESSAGE);
            setLabelsColor(Color.black);
        }
    }

    /** Mostrar un mensaje de error
     * @param msg el mensaje a mostrar
     */
    public void showErrorMessage(String msg)
    {
        showMessage("Mensaje de error", msg, new ImageIcon(getClass().getResource(GUIConstants.ERROR_ICON)), ERROR_MESSAGE);
        setLabelsColor(Color.red);
        Toolkit.getDefaultToolkit().beep();
    }

    /** Mostrar un mensaje de información
     * @param msg el mensaje a mostrar
     */
    public void showInformationMessage(String msg)
    {
        showMessage("Mensaje informativo", msg, new ImageIcon(getClass().getResource(GUIConstants.INFO_ICON)), INFORMATION_MESSAGE);
        setLabelsColor(new Color(0, 183, 0));
    }

    /** Mostrar un mensaje de pregunta
     * @param msg el mensaje a mostrar
     */
    public void showQuestionMessage(String msg)
    {
        showMessage("Mensaje de pregunta", msg, new ImageIcon(getClass().getResource(GUIConstants.QUESTION_ICON)), QUESTION_MESSAGE);
    }

    /** Mostrar un mensaje de actualización
     * @param msg el mensaje a mostrar
     */
    public void showUpdatingMessage(String msg)
    {
        showMessage("Mensaje de actualización", msg, new ImageIcon(getClass().getResource(GUIConstants.UPDATE_ICON)), UPDATING_MESSAGE);
    }

    /** Mostrar un mensaje de advertencia
     * @param msg el mensaje a mostrar
     */
    public void showWarningMessage(String msg)
    {
        showMessage("Mensaje de advertencia", msg, new ImageIcon(getClass().getResource(GUIConstants.WARNING_ICON)), WARNING_MESSAGE);
        setLabelsColor(Color.blue);
    }
    
    public void showRedWarningMessage(String msg)
    {
        showMessage("Mensaje de advertencia", msg, new ImageIcon(getClass().getResource(GUIConstants.WARNING_ICON)), WARNING_MESSAGE);
        setLabelsColor(Color.red);
        Toolkit.getDefaultToolkit().beep();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panelMessage = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lblMessage, org.jdesktop.beansbinding.ELProperty.create("${text}"), this, org.jdesktop.beansbinding.BeanProperty.create("toolTipText"));
        bindingGroup.addBinding(binding);

        panelMessage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ar/com/init/agros/util/gui/resources/check.png"))); // NOI18N
        panelMessage.add(lblImage);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(FrameNotifier.class);
        lblMessage.setText(resourceMap.getString("lblMessage.text")); // NOI18N
        panelMessage.add(lblMessage);

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N

        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
            .addComponent(panelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JPanel panelMessage;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
