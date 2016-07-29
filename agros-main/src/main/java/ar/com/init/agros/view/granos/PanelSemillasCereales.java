/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelSemillasCereales.java
 *
 * Created on 09-dic-2010, 14:59:42
 */
package ar.com.init.agros.view.granos;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.controller.almacenamiento.AlmacenamientoJpaController;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.cultivos.DialogCultivo;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.persistence.PersistenceException;
import javax.swing.JTextField;
import org.jdesktop.application.Action;

/**
 *
 * @author fbobbio
 */
public class PanelSemillasCereales extends javax.swing.JPanel implements UpdatableListener {

    private Observable observable;
    private FrameNotifier frameNotifier;
    private CampoJpaController establecimientoController;
    private CultivoJpaController cultivoController;
    private AlmacenamientoJpaController almacenamientoController;
    private TipoGrano tipoIngreso;
    private UnidadGrano unidadIngreso;

    /** Creates new form PanelSemillasCereales */
    public PanelSemillasCereales() {
        establecimientoController = new CampoJpaController();
        cultivoController = new CultivoJpaController();
        tipoIngreso = TipoGrano.SIN_SELECCION;
        unidadIngreso = UnidadGrano.SIN_SELECCION;
        initComponents();
        UpdatableSubject.addUpdatableListener(this);
        try {
            GUIUtility.refreshComboBox(new ArrayList<VariedadCultivo>(), jComboBoxVariedad);
            refreshUI();
        } catch (PersistenceException e) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelSemillasCereales.class, e);
        }

        observable = new Observable();
        setAgregarVariedadVisible(false);
        jButtonAgregarVariedad.setEnabled(false);
    }

    public TipoGrano getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(TipoGrano tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public UnidadGrano getUnidadIngreso() {
        return unidadIngreso;
    }

    public void setUnidadIngreso(UnidadGrano unidadIngreso) {
        this.unidadIngreso = unidadIngreso;
    }

    public FrameNotifier getFrameNotifier() {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier) {
        this.frameNotifier = frameNotifier;
        jTextFieldCantidad.setInputVerifier(new DecimalInputVerifier(frameNotifier, false));
        jTextFieldHumedad.setInputVerifier(new DecimalInputVerifier(frameNotifier, 2, false, 100.0, 0.0));
        jTextFieldKgPorBolsa.setInputVerifier(new DecimalInputVerifier(frameNotifier, false));
    }

    public Date getFecha() {
        return singleDateChooserFecha.getDate();
    }

    public void setFecha(Date fecha) {
        singleDateChooserFecha.setDate(fecha);
    }

    public Cultivo getSemillaCereal() {
        if (jComboBoxSemillaCereal.getSelectedItem() instanceof Cultivo) {
            return (Cultivo) jComboBoxSemillaCereal.getSelectedItem();
        } else {
            return null;
        }
    }

    public void setSemillaCereal(Cultivo c) {
        jComboBoxSemillaCereal.setSelectedItem(c);
    }

    public VariedadCultivo getVariedad() {
        if (jComboBoxVariedad.getSelectedItem() instanceof VariedadCultivo) {
            return (VariedadCultivo) jComboBoxVariedad.getSelectedItem();
        } else {
            return null;
        }
    }

    public void setVariedad(VariedadCultivo c) {
        jComboBoxVariedad.setSelectedItem(c);
    }

    public Campo getEstablecimiento() {
        if (jComboBoxEstablecimiento.getSelectedItem() instanceof Campo) {
            return (Campo) jComboBoxEstablecimiento.getSelectedItem();
        } else {
            return null;
        }
    }

    public void setEstablecimiento(Campo e) {
        jComboBoxEstablecimiento.setSelectedItem(e);
    }

    public Almacenamiento getAlmacenamiento() {
        if (jComboBoxDepositoSilo.getSelectedItem() instanceof Almacenamiento) {
            return (Almacenamiento) jComboBoxDepositoSilo.getSelectedItem();
        } else {
            return null;
        }
    }

    public void setSemillaCereal(Almacenamiento a) {
        jComboBoxDepositoSilo.setSelectedItem(a);
    }

    public Double getHumedad() {
        return getValorTextField(jTextFieldHumedad);
    }

    public void setHumedad(double humedad) {
        setValorTextField(humedad, jTextFieldHumedad);
    }

    public Double getCantidad() {
        return getValorTextField(jTextFieldCantidad);
    }

    public void setCantidad(double cantidad) {
        setValorTextField(cantidad, jTextFieldCantidad);
    }

    public Double getKgPorBolsa() {
        return getValorTextField(jTextFieldKgPorBolsa);
    }

    public void setKgPorBolsa(double kgPorBolsa) {
        setValorTextField(kgPorBolsa, jTextFieldKgPorBolsa);
    }

    private Double getValorTextField(JTextField txt) {
        try {
            return Double.parseDouble(txt.getText().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void setValorTextField(double valor, JTextField txt) {
        txt.setText(GUIUtility.NUMBER_FORMAT.format(valor));
    }

    public void setAgregarVariedadVisible(boolean b) {
        jButtonAgregarVariedad.setVisible(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        listableComboBoxRenderer1 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        singleDateChooserFecha = new ar.com.init.agros.util.gui.components.date.SingleDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonSemilla = new javax.swing.JRadioButton();
        jRadioButtonCereal = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonKilogramos = new javax.swing.JRadioButton();
        jRadioButtonBolsas = new javax.swing.JRadioButton();
        jLabelKgOBolsa = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxSemillaCereal = new javax.swing.JComboBox();
        jComboBoxVariedad = new javax.swing.JComboBox();
        jComboBoxEstablecimiento = new javax.swing.JComboBox();
        jComboBoxDepositoSilo = new javax.swing.JComboBox();
        jTextFieldHumedad = new javax.swing.JTextField();
        jTextFieldCantidad = new javax.swing.JTextField();
        jTextFieldKgPorBolsa = new javax.swing.JTextField();
        jButtonAgregarVariedad = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelSemillasCereales.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        buttonGroup1.add(jRadioButtonSemilla);
        jRadioButtonSemilla.setText(resourceMap.getString("jRadioButtonSemilla.text")); // NOI18N
        jRadioButtonSemilla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSemillaActionPerformed(evt);
            }
        });
        jPanel1.add(jRadioButtonSemilla);

        buttonGroup1.add(jRadioButtonCereal);
        jRadioButtonCereal.setText(resourceMap.getString("jRadioButtonCereal.text")); // NOI18N
        jRadioButtonCereal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCerealActionPerformed(evt);
            }
        });
        jPanel1.add(jRadioButtonCereal);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N

        buttonGroup2.add(jRadioButtonKilogramos);
        jRadioButtonKilogramos.setText(resourceMap.getString("jRadioButtonKilogramos.text")); // NOI18N
        jRadioButtonKilogramos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonKilogramosActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonKilogramos);

        buttonGroup2.add(jRadioButtonBolsas);
        jRadioButtonBolsas.setText(resourceMap.getString("jRadioButtonBolsas.text")); // NOI18N
        jRadioButtonBolsas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonBolsasActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonBolsas);

        jLabelKgOBolsa.setText(resourceMap.getString("jLabelKgOBolsa.text")); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N

        jComboBoxSemillaCereal.setEnabled(false);
        jComboBoxSemillaCereal.setRenderer(listableComboBoxRenderer1);
        jComboBoxSemillaCereal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSemillaCerealActionPerformed(evt);
            }
        });

        jComboBoxVariedad.setEnabled(false);
        jComboBoxVariedad.setRenderer(listableComboBoxRenderer1);

        jComboBoxEstablecimiento.setEnabled(false);
        jComboBoxEstablecimiento.setRenderer(listableComboBoxRenderer1);
        jComboBoxEstablecimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEstablecimientoActionPerformed(evt);
            }
        });

        jComboBoxDepositoSilo.setEnabled(false);
        jComboBoxDepositoSilo.setRenderer(listableComboBoxRenderer1);

        jTextFieldHumedad.setEditable(false);

        jTextFieldCantidad.setEditable(false);

        jTextFieldKgPorBolsa.setEditable(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getActionMap(PanelSemillasCereales.class, this);
        jButtonAgregarVariedad.setAction(actionMap.get("agregarVariedad")); // NOI18N
        jButtonAgregarVariedad.setFont(jButtonAgregarVariedad.getFont().deriveFont((float)10));
        jButtonAgregarVariedad.setText(resourceMap.getString("jButtonAgregarVariedad.text")); // NOI18N
        jButtonAgregarVariedad.setInheritsPopupMenu(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelKgOBolsa, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(jComboBoxSemillaCereal, 0, 482, Short.MAX_VALUE)
                    .addComponent(jComboBoxEstablecimiento, 0, 482, Short.MAX_VALUE)
                    .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(jTextFieldKgPorBolsa, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(jComboBoxDepositoSilo, 0, 482, Short.MAX_VALUE)
                    .addComponent(jTextFieldHumedad, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jComboBoxVariedad, 0, 340, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAgregarVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addContainerGap(162, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addContainerGap(162, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(singleDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxSemillaCereal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAgregarVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxEstablecimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBoxDepositoSilo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldHumedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelKgOBolsa)
                    .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldKgPorBolsa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonSemillaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonSemillaActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonSemillaActionPerformed
        enableFields(true);
        jComboBoxVariedad.setEnabled(true);
        almacenamientoController = new AlmacenamientoJpaController();
        fillAlmacenamientos();
        tipoIngreso = TipoGrano.SEMILLA;
    }//GEN-LAST:event_jRadioButtonSemillaActionPerformed

    private void jRadioButtonCerealActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonCerealActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonCerealActionPerformed
        enableFields(true);
        jComboBoxVariedad.setSelectedIndex(0);
        jComboBoxVariedad.setEnabled(false);
        almacenamientoController = new AlmacenamientoJpaController(Silo.class);
        fillAlmacenamientos();
        tipoIngreso = TipoGrano.CEREAL;
        //TODO: checkear esta funcionalidad
    }//GEN-LAST:event_jRadioButtonCerealActionPerformed

    private void jComboBoxSemillaCerealActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxSemillaCerealActionPerformed
    {//GEN-HEADEREND:event_jComboBoxSemillaCerealActionPerformed
        if (jComboBoxSemillaCereal.getSelectedItem() instanceof Cultivo) {
            Cultivo c = (Cultivo) jComboBoxSemillaCereal.getSelectedItem();
            GUIUtility.refreshComboBox(c.getVariedades(), jComboBoxVariedad);
            jButtonAgregarVariedad.setEnabled(true);
        } else {
            GUIUtility.refreshComboBox(new ArrayList<Cultivo>(), jComboBoxVariedad);
            jButtonAgregarVariedad.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxSemillaCerealActionPerformed

    private void jRadioButtonKilogramosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonKilogramosActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonKilogramosActionPerformed
        jTextFieldKgPorBolsa.setEditable(false);
        jTextFieldCantidad.setEditable(true);
        unidadIngreso = UnidadGrano.KILOGRAMO;
    }//GEN-LAST:event_jRadioButtonKilogramosActionPerformed

    private void jRadioButtonBolsasActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonBolsasActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonBolsasActionPerformed
        jTextFieldKgPorBolsa.setEditable(true);
        jTextFieldCantidad.setEditable(true);
        unidadIngreso = UnidadGrano.BOLSAS;
    }//GEN-LAST:event_jRadioButtonBolsasActionPerformed

    private void jComboBoxEstablecimientoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxEstablecimientoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxEstablecimientoActionPerformed
        fillAlmacenamientos();
    }//GEN-LAST:event_jComboBoxEstablecimientoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButtonAgregarVariedad;
    private javax.swing.JComboBox jComboBoxDepositoSilo;
    private javax.swing.JComboBox jComboBoxEstablecimiento;
    private javax.swing.JComboBox jComboBoxSemillaCereal;
    private javax.swing.JComboBox jComboBoxVariedad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelKgOBolsa;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonBolsas;
    private javax.swing.JRadioButton jRadioButtonCereal;
    private javax.swing.JRadioButton jRadioButtonKilogramos;
    private javax.swing.JRadioButton jRadioButtonSemilla;
    private javax.swing.JTextField jTextFieldCantidad;
    private javax.swing.JTextField jTextFieldHumedad;
    private javax.swing.JTextField jTextFieldKgPorBolsa;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.date.SingleDateChooser singleDateChooserFecha;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshUI() {
        GUIUtility.refreshComboBox(cultivoController.findEntities(), jComboBoxSemillaCereal);
        GUIUtility.refreshComboBox(establecimientoController.findEntities(), jComboBoxEstablecimiento);
    }

    @Override
    public boolean isNowVisible() {
        return this.isVisible();
    }

    public void enableFields(boolean var) {
        jComboBoxSemillaCereal.setEnabled(var);
        if (!var) {
            jComboBoxVariedad.setEnabled(var);
            jButtonAgregarVariedad.setEnabled(var);
        }
        jComboBoxEstablecimiento.setEnabled(var);
        jComboBoxDepositoSilo.setEnabled(var);
        jTextFieldCantidad.setEnabled(var);
        jTextFieldHumedad.setEditable(var);
    }

    public void disableFields() {
        singleDateChooserFecha.setEnabled(false);
        jRadioButtonBolsas.setEnabled(false);
        jRadioButtonCereal.setEnabled(false);
        jRadioButtonKilogramos.setEnabled(false);
        jRadioButtonSemilla.setEnabled(false);
        jComboBoxSemillaCereal.setEnabled(false);
        jComboBoxVariedad.setEnabled(false);
        jComboBoxEstablecimiento.setEnabled(false);
        jComboBoxDepositoSilo.setEnabled(false);
        jTextFieldCantidad.setEnabled(false);
        jTextFieldHumedad.setEnabled(false);
    }

    public void clear() {
        jComboBoxSemillaCereal.setSelectedIndex(0);
        jComboBoxVariedad.setSelectedIndex(0);
        jComboBoxEstablecimiento.setSelectedIndex(0);
        jComboBoxDepositoSilo.setSelectedIndex(0);
        jTextFieldCantidad.setText("");
        jTextFieldHumedad.setText("");
        jTextFieldKgPorBolsa.setText("");
        buttonGroup1.clearSelection();
        buttonGroup2.clearSelection();
        enableFields(false);
    }

    private void fillAlmacenamientos() {
        Campo c = getEstablecimiento();
        if (c != null) {
            List<Almacenamiento> almacs = new ArrayList<Almacenamiento>();
            if (jRadioButtonSemilla.isSelected()) {
                almacs.addAll(c.getDepositos());
                almacs.addAll(c.getSilos());
            } else {
                if (jRadioButtonCereal.isSelected()) {
                    almacs.addAll(c.getSilos());
                }
            }
            GUIUtility.refreshComboBox(almacs, jComboBoxDepositoSilo);
            return;
        } else {
            if (almacenamientoController != null) {
                GUIUtility.refreshComboBox(almacenamientoController.findEntities(), jComboBoxDepositoSilo);
            } else {
                GUIUtility.refreshComboBox(new ArrayList<Almacenamiento>(), jComboBoxDepositoSilo);
            }
        }
    }

    public enum TipoGrano {

        SEMILLA("Semilla"),
        CEREAL("Cereal"),
        SIN_SELECCION("Sin selección");
        private String name;

        private TipoGrano(String st) {
            name = st;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum UnidadGrano {

        BOLSAS("Bolsas"),
        KILOGRAMO("Kg"),
        SIN_SELECCION("Sin selección");
        private String name;

        private UnidadGrano(String st) {
            name = st;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Action
    public void agregarVariedad() {
        if (getSemillaCereal() != null) {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame(), getSemillaCereal(), true);
            d.setTitle(jButtonAgregarVariedad.getText());
            d.setVisible(true);
        } else {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame());
            d.setTitle(jButtonAgregarVariedad.getText());
            d.setVisible(true);
        }

        observable.notifyObservers();

        GUIUtility.refreshComboBox(cultivoController.findEntities(), jComboBoxSemillaCereal);
        jComboBoxSemillaCerealActionPerformed(null);
    }

    public void addObserver(Observer o) {
        observable.addObserver(o);
    }
}
