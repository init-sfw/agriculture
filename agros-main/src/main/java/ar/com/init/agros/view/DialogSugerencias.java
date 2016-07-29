package ar.com.init.agros.view;


import ar.com.init.agros.controller.SugerenciaJpaController;
import ar.com.init.agros.model.util.Sugerencia;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Listable;
import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.validator.InvalidStateException;

 /*
  * Clase GUI DialogSugerencias
  *
  * @author fbobbio
  * @version 12-mar-2011
  */
public class DialogSugerencias extends javax.swing.JDialog
{
    private EventControl evt;
    private Sugerencia sugerencia;
    private SugerenciaJpaController sugerenciaController;

    /** Creates new form DialogSugerencias */
    public DialogSugerencias(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        GUIUtility.initWindow(this);
        initComponents();
        sugerenciaController = new SugerenciaJpaController();
        evt = new EventControl();
        jButtonEnviar.addActionListener(evt);
        jButtonLimpiar.addActionListener(evt);
        jButtonCancelar.addActionListener(evt);

        jComboBoxTipoMensaje.addItem(TipoSugerencia.SUGERENCIA_GENERAL);
        jComboBoxTipoMensaje.addItem(TipoSugerencia.ERROR);
        jComboBoxTipoMensaje.addItem(TipoSugerencia.RECOMENDACION);
        jComboBoxTipoMensaje.addItem(TipoSugerencia.MEJORA_EXISTENTE);
        jComboBoxTipoMensaje.addItem(TipoSugerencia.NUEVA_FUNCIONALIDAD);
        jComboBoxTipoMensaje.addItem(TipoSugerencia.OTRO);
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
        jLabel1 = new javax.swing.JLabel();
        jComboBoxTipoMensaje = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMensaje = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jButtonEnviar = new javax.swing.JButton();
        jButtonLimpiar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldNombre = new javax.swing.JTextField();
        jTextFieldTelefono = new javax.swing.JTextField();
        jTextFieldMail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogSugerencias.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N
        listableComboBoxRenderer1.setName("listableComboBoxRenderer1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        frameNotifier1.setName("frameNotifier1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jComboBoxTipoMensaje.setName("jComboBoxTipoMensaje"); // NOI18N
        jComboBoxTipoMensaje.setRenderer(listableComboBoxRenderer1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaMensaje.setColumns(20);
        jTextAreaMensaje.setFont(resourceMap.getFont("jTextAreaMensaje.font")); // NOI18N
        jTextAreaMensaje.setForeground(resourceMap.getColor("jTextAreaMensaje.foreground")); // NOI18N
        jTextAreaMensaje.setRows(5);
        jTextAreaMensaje.setText(resourceMap.getString("jTextAreaMensaje.text")); // NOI18N
        jTextAreaMensaje.setName("jTextAreaMensaje"); // NOI18N
        jTextAreaMensaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextAreaMensajeFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(jTextAreaMensaje);

        jPanel1.setName("jPanel1"); // NOI18N

        jButtonEnviar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ar/com/init/agros/view/resources/enviar.png"))); // NOI18N
        jButtonEnviar.setText(resourceMap.getString("jButtonEnviar.text")); // NOI18N
        jButtonEnviar.setName("jButtonEnviar"); // NOI18N
        jPanel1.add(jButtonEnviar);

        jButtonLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ar/com/init/agros/view/resources/limpiar.png"))); // NOI18N
        jButtonLimpiar.setText(resourceMap.getString("jButtonLimpiar.text")); // NOI18N
        jButtonLimpiar.setName("jButtonLimpiar"); // NOI18N
        jPanel1.add(jButtonLimpiar);

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ar/com/init/agros/view/resources/cancel_24.png"))); // NOI18N
        jButtonCancelar.setText(resourceMap.getString("jButtonCancelar.text")); // NOI18N
        jButtonCancelar.setName("jButtonCancelar"); // NOI18N
        jPanel1.add(jButtonCancelar);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jTextFieldNombre.setText(resourceMap.getString("jTextFieldNombre.text")); // NOI18N
        jTextFieldNombre.setName("jTextFieldNombre"); // NOI18N

        jTextFieldTelefono.setText(resourceMap.getString("jTextFieldTelefono.text")); // NOI18N
        jTextFieldTelefono.setName("jTextFieldTelefono"); // NOI18N

        jTextFieldMail.setText(resourceMap.getString("jTextFieldMail.text")); // NOI18N
        jTextFieldMail.setName("jTextFieldMail"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(jComboBoxTipoMensaje, 0, 483, Short.MAX_VALUE)
                    .add(jTextFieldNombre, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(jTextFieldTelefono, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(jTextFieldMail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jComboBoxTipoMensaje, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextFieldNombre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jTextFieldTelefono, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jTextFieldMail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextAreaMensajeFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextAreaMensajeFocusGained
    {//GEN-HEADEREND:event_jTextAreaMensajeFocusGained
        if (jTextAreaMensaje.getText().equalsIgnoreCase("<Ingrese su sugerencia aqu�>"))
        {
            jTextAreaMensaje.setFont(new Font("Verdana", Font.PLAIN, 11));
            jTextAreaMensaje.setForeground(Color.black);
            jTextAreaMensaje.setText("");
        }
    }//GEN-LAST:event_jTextAreaMensajeFocusGained

    private Sugerencia getSugerencia()
    {
        if (sugerencia == null)
        {
            sugerencia = new Sugerencia();
        }
        sugerencia.setFechaEnvio(new Date());
        sugerencia.setNombre(jTextFieldNombre.getText().trim());
        sugerencia.setMail(jTextFieldMail.getText().trim());
        sugerencia.setTelefono(jTextFieldTelefono.getText().trim());
        sugerencia.setTipoSugerencia(getTipoSugerencia());
        sugerencia.setSugerencia(getStringSugerencia());
        return sugerencia;
    }

    private TipoSugerencia getTipoSugerencia()
    {
        if (jComboBoxTipoMensaje.getSelectedItem() instanceof TipoSugerencia)
        {
            return (TipoSugerencia)jComboBoxTipoMensaje.getSelectedItem();
        }
        else
        {
            return null;
        }
    }

    private String getStringSugerencia()
    {
        if (jTextAreaMensaje.getText().equalsIgnoreCase("<Ingrese su sugerencia aqu�>") || jTextAreaMensaje.getText().trim().equals(""))
        {
            return null;
        }
        else
        {
            return jTextAreaMensaje.getText().trim();
        }
    }

    /** Clase de control de eventos que maneja los eventos de la GUI DialogSugerencias y las validaciones de la misma */
    public class EventControl extends AbstractEventControl implements ActionListener
    {
        /** M�todo que maneja los eventos de la GUI DialogSugerencias
         *  @param e el evento de acci�n lanzado por alg�n componente de la GUI
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == jButtonEnviar)
            {
                if (validateInput(getSugerencia()))
                {
                    try
                    {
                        sugerenciaController.persist(sugerencia);
                        frameNotifier1.showInformationMessage("Se ha enviado la sugerencia con �xito");
                        clear();
                    }
                    catch (InvalidStateException ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogSugerencias.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (Exception ex)
                    {
                        frameNotifier1.showErrorMessage(ex.getMessage());
                        Logger.getLogger(DialogSugerencias.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (e.getSource() == jButtonLimpiar)
            {
                clear();
            }
            if (e.getSource() == jButtonCancelar)
            {
                clear();
                DialogSugerencias.this.dispose();
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
        jTextAreaMensaje.setText("<Ingrese su sugerencia aqu�>");

        jComboBoxTipoMensaje.setSelectedIndex(-1);
        jTextFieldNombre.setText("");
        jTextFieldMail.setText("");
        jTextFieldTelefono.setText("");
        sugerencia  = null;
    }

    public enum TipoSugerencia implements Listable
    {
        SUGERENCIA_GENERAL("Sugerencia general"),
        ERROR("Error de sistema"),
        RECOMENDACION("Recomendaci�n"),
        NUEVA_FUNCIONALIDAD("Nueva funcionalidad"),
        MEJORA_EXISTENTE("Mejora de funcionalidad existente"),
        OTRO("Otro");

        private String msj;

        TipoSugerencia (String msj)
        {
            this.msj = msj;
        }

        @Override
        public String getListLine()
        {
            return this.msj;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JButton jButtonLimpiar;
    private javax.swing.JComboBox jComboBoxTipoMensaje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaMensaje;
    private javax.swing.JTextField jTextFieldMail;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldTelefono;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    // End of variables declaration//GEN-END:variables

}
