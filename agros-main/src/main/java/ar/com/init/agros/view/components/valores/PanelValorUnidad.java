/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Clase visual para usar cuando haya que seleccionar un valor de unidad de medida.
 *
 * {@code
 *   panelValorUnidad1.setMagnitud(MagnitudEnum.LONGITUD);
 *  }
 *
 * Created on 05/06/2009, 19:39:37
 */
package ar.com.init.agros.view.components.valores;

import ar.com.init.agros.controller.UnidadMedidaJpaController;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.components.FocusPanelValorUnidadListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JTextField;

/**
 *
 * @author gmatheu
 */
public class PanelValorUnidad extends javax.swing.JPanel
{

    private static final long serialVersionUID = -1L;
    private UnidadMedidaJpaController unidadJpaController;
    private ValorUnidad valorUnidad;
    private FrameNotifier frameNotifier;
    private MagnitudEnum[] magnitudes;
    private List<FocusPanelValorUnidadListener> focusListeners;

    /** Creates new form PanelValorUnidad */
    public PanelValorUnidad()
    {
        initComponents();
        unidadJpaController = new UnidadMedidaJpaController();
    }

    public JTextField getjTextFieldValor()
    {
        return jTextFieldValor;
    }

    public void setjTextFieldValor(JTextField jTextFieldValor)
    {
        this.jTextFieldValor = jTextFieldValor;
    }

    public void clear()
    {
        clear(true, true);
    }

    public void clear(boolean text, boolean combo)
    {
        valorUnidad = null;
        if (text) {
            jTextFieldValor.setText("");
        }
        if (combo && jComboBoxUnidad.getModel().getSize() > 0) {
            jComboBoxUnidad.setSelectedIndex(0);
        }
    }

    public ValorUnidad getValorUnidad()
    {
        if (valorUnidad == null) {
            valorUnidad = new ValorUnidad();
        }

        Object unidad = jComboBoxUnidad.getSelectedItem();

        if (unidad instanceof UnidadMedida) {
            valorUnidad.setUnidad((UnidadMedida) unidad);
        }
        else {
            valorUnidad.setUnidad(null);
        }
        if (jTextFieldValor.getText().length() > 0) {
            valorUnidad.setFormattedValue(jTextFieldValor.getText());
        }
        else {
            valorUnidad.setValor(0D);
        }

        if (!valorUnidad.isValid()) {
            return null;
        }

        return valorUnidad;
    }

    public void selectUnidad(Object unidad)
    {
        if (unidad instanceof UnidadMedida) {
            jComboBoxUnidad.setSelectedItem(unidad);
        }
        else {
            jComboBoxUnidad.setSelectedIndex(0);
        }
    }

    public void setSelectedUnidadMedida(UnidadMedida unidad)
    {
        jComboBoxUnidad.setSelectedItem(unidad);
    }

    public void setValorUnidad(ValorUnidad valorUnidad)
    {
        if (valorUnidad == null || !valorUnidad.isValid()) {
            jComboBoxUnidad.setSelectedIndex(0);
            jTextFieldValor.setText("");
        }
        else {
            jComboBoxUnidad.setSelectedItem(valorUnidad.getUnidad());
            jTextFieldValor.setText(valorUnidad.getFormattedValue());
        }
        this.valorUnidad = valorUnidad;
    }

    public MagnitudEnum getMagnitud()
    {
        return magnitudes[0];
    }

    public void setMagnitudes(MagnitudEnum magnitud)
    {
        GUIUtility.refreshComboBox(unidadJpaController.findByMagnitud(magnitud), jComboBoxUnidad);
        this.magnitudes = new MagnitudEnum[]{
                    magnitud
                };
    }

    public MagnitudEnum[] getMagnitudes()
    {
        return magnitudes;
    }

    public void addMagnitud(MagnitudEnum... magnitudes)
    {
        GUIUtility.refreshComboBox(unidadJpaController.findByMagnitud(magnitudes), jComboBoxUnidad);
        this.magnitudes = magnitudes;
    }

    public void showAllUnidades()
    {
        GUIUtility.refreshComboBox(unidadJpaController.findEntities(), jComboBoxUnidad);
    }

    public void showUnidadesPorMagnitud(MagnitudEnum magnitud)
    {
        GUIUtility.refreshComboBox(unidadJpaController.findByMagnitud(magnitud), jComboBoxUnidad);
    }

    public void addUnidades(UnidadMedida... unidades)
    {
        GUIUtility.refreshComboBox(Arrays.asList(unidades), jComboBoxUnidad);
    }

    public void clearUnidades()
    {
        jComboBoxUnidad.removeAllItems();
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
        jTextFieldValor.setInputVerifier(createInputVerifier(frameNotifier));
    }

    public static InputVerifier createInputVerifier(FrameNotifier frameNotifier)
    {
        return new DecimalInputVerifier(frameNotifier, false);
    }

    @Override
    public InputVerifier getInputVerifier()
    {
        return this.jTextFieldValor.getInputVerifier();
    }

    @Override
    public void setInputVerifier(InputVerifier inputVerifier)
    {
        this.jTextFieldValor.setInputVerifier(inputVerifier);
    }

    public void setEnabled(boolean text, boolean combo)
    {
        jComboBoxUnidad.setEnabled(combo);
        jTextFieldValor.setEditable(text);
    }

    public boolean verify()
    {
        return (getInputVerifier() != null ? getInputVerifier().verify(jTextFieldValor) : true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableComboBoxRenderer = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        jTextFieldValor = new javax.swing.JTextField();
        jComboBoxUnidad = new javax.swing.JComboBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelValorUnidad.class);
        listableComboBoxRenderer.setText(resourceMap.getString("listableComboBoxRenderer.text")); // NOI18N

        jTextFieldValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldValorFocusLost(evt);
            }
        });

        jComboBoxUnidad.setRenderer(listableComboBoxRenderer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTextFieldValor, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxUnidad, 0, 115, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jTextFieldValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jComboBoxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldValorFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldValorFocusLost
    {//GEN-HEADEREND:event_jTextFieldValorFocusLost
        notifyFocusPanelValorUnidadListeners();
    }//GEN-LAST:event_jTextFieldValorFocusLost

    /** Método que se encarga de añadir un listener de cambio de selección de superficie
     *  @param listener el objeto que depende del sujeto
     */
    public void addFocusPanelValorUnidadListener(FocusPanelValorUnidadListener listener)
    {
        if (listener == null) {
            return;
        }
        if (focusListeners == null || focusListeners.size() == 0) {
            focusListeners = new ArrayList<FocusPanelValorUnidadListener>();
        }
        focusListeners.add(listener);
    }

    public boolean removeFocusPanelValorUnidadListener(FocusPanelValorUnidadListener listener)
    {
        if (focusListeners == null || focusListeners.size() == 0) {
            return false;
        }
        else {
            return focusListeners.remove(listener);
        }
    }

    public void notifyFocusPanelValorUnidadListeners()
    {
        for (int i = 0; focusListeners != null && i < focusListeners.size(); i++) {
            if (focusListeners.get(i) != null) {
                focusListeners.get(i).focusLost(getValorUnidad());
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxUnidad;
    private javax.swing.JTextField jTextFieldValor;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer;
    // End of variables declaration//GEN-END:variables
}
