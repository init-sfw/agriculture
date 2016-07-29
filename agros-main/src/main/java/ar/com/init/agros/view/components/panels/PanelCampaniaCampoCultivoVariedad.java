package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.view.components.*;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.controller.CampaniaJpaController;
import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.Listable;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.campo.inputverifier.SuperficieInputVerifier;
import ar.com.init.agros.view.components.model.SeleccionLotesTableModel;
import ar.com.init.agros.view.components.model.SeleccionSubLotesTableModel;
import ar.com.init.agros.view.cultivos.DialogCultivo;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.JComboBox;

/**
 * PanelCampaniaCampoCultivoVariedad
 * @author fbobbio
 */
public class PanelCampaniaCampoCultivoVariedad extends javax.swing.JPanel implements UpdatableListener, SuperficieListener, FocusPanelValorUnidadListener
{

    public static final int TRABAJO = 0, SIEMBRA = 1, PLANIFICACION = 2;
    private int transaccion = -1;
    private FrameNotifier frameNotifier;
    private CampaniaJpaController jpaCampania;
    private CampoJpaController jpaCampo;
    private BaseEntityJpaController jpaCultivo;
    private List<Campania> campanias;
    private List<Campo> campos;
    private List<Cultivo> cultivos;
    private CampoSelectionChangeListener campoListener;
    private List<CampaniaSelectionChangeListener> campaniaListeners;
    /** Atributo para saber la estrategia de recuperación de datos a usar */
    private boolean consulta;

    /** Creates new form PanelCampaniaCampoCultivoVariedad */
    public PanelCampaniaCampoCultivoVariedad()
    {
        this(null, null);
    }

    public PanelCampaniaCampoCultivoVariedad(SeleccionLotesTableModel lotesTM, SeleccionSubLotesTableModel subLotesTM)
    {
        initComponents();
        jButtonAgregarVariedad.setVisible(false);
        if (lotesTM != null && subLotesTM != null) // si se cargaron estos tablemodel se trata de un trabajo
        {
            panelSeleccionLotes1.setSeleccionTableModels(lotesTM, subLotesTM);
            panelValorUnidadSuperficie.setEnabled(true, false);
        }
        panelSeleccionLotes1.addSuperficieListener(this);
        panelValorUnidadSuperficie.addFocusPanelValorUnidadListener(this);
        UpdatableSubject.addUpdatableListener(this);
        /* Listener para deshabilitar el checkbox de selección de todo el campo cuando se deselecciona algún lote/sublote */
        jpaCampania = new CampaniaJpaController();
        jpaCampo = new CampoJpaController();
        jpaCultivo = new BaseEntityJpaController<Cultivo>(Cultivo.class);
        campanias = new ArrayList<Campania>();
        campos = new ArrayList<Campo>();
        cultivos = new ArrayList<Cultivo>();
        panelValorUnidadSuperficie.setEnabled(false, false);
        try
        {
            refreshUI();
            panelValorUnidadSuperficie.setValorUnidad(new ValorUnidad(0.0, MagnitudEnum.SUPERFICIE.patron()));
        }
        catch (PersistenceException e)
        {
            if (frameNotifier != null)
            {
                frameNotifier.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelCampaniaCampoCultivoVariedad.class, e);
        }
        if (!panelSeleccionLotes1.isTrabajo())
        {
            jLabelAvisoSuperficie.setVisible(false);
        }
    }

    public void setValorSuperficie(ValorUnidad sup)
    {
        panelValorUnidadSuperficie.setValorUnidad(sup);
    }

    public void hideFieldsForTrabajo()
    {
        jComboBoxVariedad.setVisible(false);
        jLabelVariedad.setVisible(false);
        jComboBoxCultivo.setVisible(false);
        jLabelCultivo.setVisible(false);
    }

    public void disableFields()
    {
        consulta = true;
        refreshUI();
        panelValorUnidadSuperficie.setEnabled(false, false);
        panelSeleccionLotes1.disableFields();
        jComboBoxCampania.setEnabled(false);
        jComboBoxCampo.setEnabled(false);
        jComboBoxCultivo.setEnabled(false);
        jComboBoxVariedad.setEnabled(false);
    }

    public void clear()
    {
        jComboBoxCampania.setSelectedIndex(0);
        jComboBoxCampo.setSelectedIndex(0);
        jComboBoxCultivo.setSelectedIndex(0);
        jComboBoxVariedad.setSelectedIndex(0);
        panelSeleccionLotes1.clear();
        panelValorUnidadSuperficie.clear(true, false);
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
        panelSeleccionLotes1.setFrameNotifier(frameNotifier);
        panelValorUnidadSuperficie.setFrameNotifier(frameNotifier);
    }

    public void setRedBorders()
    {
        GUIUtility.setRedBorder(jComboBoxCampania);
        GUIUtility.setRedBorder(jComboBoxCampo);
        panelSeleccionLotes1.setRedBorders();
    }

    public void restoreRedBorders()
    {
        GUIUtility.restoreBorder(jComboBoxCampania);
        GUIUtility.restoreBorder(jComboBoxCampo);
        panelSeleccionLotes1.restoreRedBorders();
    }

    public void setSelectedCampania(Campania campania)
    {
        jComboBoxCampania.setSelectedItem(campania);
    }

    public void setSelectedCampo(Campo campo)
    {
        jComboBoxCampo.setSelectedItem(campo);
        eventCampo();
    }

    public void setSelectedCultivo(Cultivo cultivo)
    {
        jComboBoxCultivo.setSelectedItem(cultivo);
        eventCultivo();
    }

    public void setSelectedLotes(List<Lote> lotes)
    {
        panelSeleccionLotes1.setLotesSeleccionados(lotes);
    }

    public void setSelectedSubLotes(List<SubLote> subLotes)
    {
        panelSeleccionLotes1.setSubLotesSeleccionados(subLotes);
    }

    public void setSelectedSuperficies(List<Superficie> superficies, double supSeleccionada)
    {
        panelSeleccionLotes1.setSelectedSuperficies(superficies,supSeleccionada);
    }

    public void setSelectedSuperficies(List<Superficie> superficies)
    {
        panelSeleccionLotes1.setSelectedSuperficies(superficies);
    }

    public List<Superficie> getSuperficiesSeleccionadas()
    {
        return panelSeleccionLotes1.getSelectedSuperficies();
    }

    public PanelSeleccionLotesSublotes getPanelSeleccionLotes1()
    {
        return panelSeleccionLotes1;
    }

    public void setPanelSeleccionLotes1(PanelSeleccionLotesSublotes panelSeleccionLotes1)
    {
        this.panelSeleccionLotes1 = panelSeleccionLotes1;
    }

    public void setSelectedVariedad(VariedadCultivo variedadCultivo)
    {
        jComboBoxVariedad.setSelectedItem(variedadCultivo);
    }

    private void eventCampo()
    {
        Campo oldSelection = panelSeleccionLotes1.getCampoSeleccionado();
        Campo aux = getCampo();
        if (consulta)
        {
            setCampo(aux);
            return;
        }
        if ((oldSelection == null && aux == null) || (oldSelection != null && oldSelection.equals(aux))) // Si no hay cambio de selección no procesamos
        {
            return;
        }
        if (notifyCampoSelectionChangeListener()) // si es posible o se acepta el cambio
        {
            setCampo(aux);
        }
        else
        {
            setCampo(oldSelection);
        }
    }

    private void setCampo(Object aux)
    {
        if (aux instanceof Campo)
        {
            panelValorUnidadSuperficie.clear();
            panelSeleccionLotes1.clear();
            Campo c = (Campo) aux;
            panelSeleccionLotes1.setCampoSeleccionado(c);
            jComboBoxCampo.setSelectedItem(c);
            if (panelSeleccionLotes1.isTrabajo() && !consulta)
            {
                panelSeleccionLotes1.setCheckBoxesEnabled(true);
                panelValorUnidadSuperficie.setEnabled(true, false);
                panelValorUnidadSuperficie.setInputVerifier(new SuperficieInputVerifier(frameNotifier, 2, false, c.getSuperficie().getValor(), panelSeleccionLotes1));
            }
        }
        else
        {
            panelSeleccionLotes1.clear();
            panelSeleccionLotes1.setCheckBoxesEnabled(false);
            jComboBoxCampo.setSelectedIndex(0);
            panelValorUnidadSuperficie.setEnabled(false, false);
        }
    }

    public boolean seRepitenCultivos()
    {
        return panelSeleccionLotes1.seRepitenCultivos();
    }

    private void eventCultivo()
    {
        Object sel = jComboBoxCultivo.getSelectedItem();
        if (sel instanceof Cultivo)
        {
            fillVariedad((Cultivo) sel);
        }
        else
        {
            restoreVariedad();
        }
    }

    private void fillCombos()
    {
        GUIUtility.refreshComboBox(campanias, jComboBoxCampania);
        GUIUtility.refreshComboBox(campos, jComboBoxCampo);
        GUIUtility.refreshComboBox(cultivos, jComboBoxCultivo);
        panelValorUnidadSuperficie.addUnidades(MagnitudEnum.SUPERFICIE.patron());
    }

    private void fillVariedad(Cultivo c)
    {
        List<VariedadCultivo> variedades = c.getVariedades();
        Collections.sort(variedades);
        GUIUtility.refreshComboBox(variedades, jComboBoxVariedad);
    }

    private void findEntities()
    {
        cultivos = jpaCultivo.findEntities();
        if (!consulta)
        {
            campos = jpaCampo.findEntities();
            campanias = jpaCampania.findCampaniasPendientesDeCierre();
        }
        else
        {
            campos = jpaCampo.findAllEntities();
            campanias = jpaCampania.findAllEntities();
        }
    }

    public Cultivo getCultivo()
    {
        return (Cultivo) getSelection(jComboBoxCultivo);
    }

    public Campo getCampo()
    {
        return (Campo) getSelection(jComboBoxCampo);
    }

    public Campania getCampania()
    {
        return (Campania) getSelection(jComboBoxCampania);
    }

    public VariedadCultivo getVariedad()
    {
        return (VariedadCultivo) getSelection(jComboBoxVariedad);
    }

    public List<Lote> getLotesSeleccionados()
    {
        return panelSeleccionLotes1.getLotesSeleccionados();
    }

    public List<SubLote> getSubLotesSeleccionados()
    {
        return panelSeleccionLotes1.getSubLotesSeleccionados();
    }

    private Listable getSelection(JComboBox cbx)
    {
        Object aux;
        aux = cbx.getSelectedItem();
        if (aux instanceof Listable)
        {
            return (Listable) aux;
        }
        else
        {
            return null;
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

        listableComboBoxRenderer1 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        listableComboBoxRenderer2 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        listableComboBoxRenderer3 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        listableComboBoxRenderer4 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        jLabelCampania = new javax.swing.JLabel();
        jLabelCampo = new javax.swing.JLabel();
        jComboBoxCampania = new javax.swing.JComboBox();
        jComboBoxCampo = new javax.swing.JComboBox();
        jLabelCultivo = new javax.swing.JLabel();
        jLabelVariedad = new javax.swing.JLabel();
        jComboBoxCultivo = new javax.swing.JComboBox();
        jComboBoxVariedad = new javax.swing.JComboBox();
        jLabelSuperficie = new javax.swing.JLabel();
        panelSeleccionLotes1 = new ar.com.init.agros.view.components.panels.PanelSeleccionLotesSublotes();
        panelValorUnidadSuperficie = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jButtonAgregarVariedad = new javax.swing.JButton();
        jLabelAvisoSuperficie = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelCampaniaCampoCultivoVariedad.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N
        listableComboBoxRenderer1.setFont(resourceMap.getFont("listableComboBoxRenderer4.font")); // NOI18N

        listableComboBoxRenderer2.setText(resourceMap.getString("listableComboBoxRenderer2.text")); // NOI18N
        listableComboBoxRenderer2.setFont(resourceMap.getFont("listableComboBoxRenderer4.font")); // NOI18N

        listableComboBoxRenderer3.setText(resourceMap.getString("listableComboBoxRenderer3.text")); // NOI18N
        listableComboBoxRenderer3.setFont(resourceMap.getFont("listableComboBoxRenderer4.font")); // NOI18N

        listableComboBoxRenderer4.setText(resourceMap.getString("listableComboBoxRenderer4.text")); // NOI18N
        listableComboBoxRenderer4.setFont(resourceMap.getFont("listableComboBoxRenderer4.font")); // NOI18N

        jLabelCampania.setFont(jLabelCampania.getFont().deriveFont((float)10));
        jLabelCampania.setText(resourceMap.getString("jLabelCampania.text")); // NOI18N

        jLabelCampo.setFont(jLabelCampo.getFont().deriveFont((float)10));
        jLabelCampo.setText(resourceMap.getString("jLabelCampo.text")); // NOI18N

        jComboBoxCampania.setFont(jComboBoxCampania.getFont().deriveFont((float)11));
        jComboBoxCampania.setRenderer(listableComboBoxRenderer1);
        jComboBoxCampania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampaniaActionPerformed(evt);
            }
        });

        jComboBoxCampo.setFont(jComboBoxCampo.getFont().deriveFont((float)11));
        jComboBoxCampo.setEnabled(false);
        jComboBoxCampo.setRenderer(listableComboBoxRenderer2);
        jComboBoxCampo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampoActionPerformed(evt);
            }
        });

        jLabelCultivo.setFont(jLabelCultivo.getFont().deriveFont((float)10));
        jLabelCultivo.setText(resourceMap.getString("jLabelCultivo.text")); // NOI18N

        jLabelVariedad.setFont(jLabelVariedad.getFont().deriveFont((float)10));
        jLabelVariedad.setText(resourceMap.getString("jLabelVariedad.text")); // NOI18N

        jComboBoxCultivo.setFont(jComboBoxCultivo.getFont().deriveFont((float)11));
        jComboBoxCultivo.setRenderer(listableComboBoxRenderer3);
        jComboBoxCultivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCultivoActionPerformed(evt);
            }
        });

        jComboBoxVariedad.setFont(jComboBoxVariedad.getFont().deriveFont((float)11));
        jComboBoxVariedad.setRenderer(listableComboBoxRenderer4);

        jLabelSuperficie.setFont(jLabelSuperficie.getFont().deriveFont((float)10));
        jLabelSuperficie.setText(resourceMap.getString("jLabelSuperficie.text")); // NOI18N

        panelSeleccionLotes1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panelSeleccionLotes1.setFont(resourceMap.getFont("panelSeleccionLotes1.font")); // NOI18N

        panelValorUnidadSuperficie.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                panelValorUnidadSuperficieFocusLost(evt);
            }
        });

        jButtonAgregarVariedad.setFont(resourceMap.getFont("jButtonAgregarVariedad.font")); // NOI18N
        jButtonAgregarVariedad.setText(resourceMap.getString("jButtonAgregarVariedad.text")); // NOI18N
        jButtonAgregarVariedad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarVariedadActionPerformed(evt);
            }
        });

        jLabelAvisoSuperficie.setFont(resourceMap.getFont("jLabelAvisoSuperficie.font")); // NOI18N
        jLabelAvisoSuperficie.setForeground(resourceMap.getColor("jLabelAvisoSuperficie.foreground")); // NOI18N
        jLabelAvisoSuperficie.setText(resourceMap.getString("jLabelAvisoSuperficie.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSeleccionLotes1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelCampo)
                            .addComponent(jLabelSuperficie)
                            .addComponent(jLabelCampania))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panelValorUnidadSuperficie, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                                    .addComponent(jComboBoxCampo, 0, 266, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jComboBoxCampania, 0, 272, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelVariedad, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelCultivo, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBoxCultivo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBoxVariedad, 0, 210, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jButtonAgregarVariedad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelAvisoSuperficie, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCampania, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCampania)
                    .addComponent(jComboBoxCultivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCultivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelVariedad)
                    .addComponent(jLabelCampo)
                    .addComponent(jComboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAgregarVariedad, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabelSuperficie, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelValorUnidadSuperficie, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAvisoSuperficie)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSeleccionLotes1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCultivoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCultivoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCultivoActionPerformed
        eventCultivo();
    }//GEN-LAST:event_jComboBoxCultivoActionPerformed

    private void jComboBoxCampoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampoActionPerformed
        eventCampo();
    }//GEN-LAST:event_jComboBoxCampoActionPerformed

    private void jComboBoxCampaniaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampaniaActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampaniaActionPerformed
        jComboBoxCampo.setEnabled(getCampania() != null);
        if (consulta)
        {
            jComboBoxCampo.setEnabled(false);
        }
        jComboBoxCampo.setSelectedIndex(0);
        eventCampo();
        notifyCampaniaSelectionChangeListeners();
    }//GEN-LAST:event_jComboBoxCampaniaActionPerformed

    private void panelValorUnidadSuperficieFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_panelValorUnidadSuperficieFocusLost
    {//GEN-HEADEREND:event_panelValorUnidadSuperficieFocusLost
    }//GEN-LAST:event_panelValorUnidadSuperficieFocusLost

    public void setTransaccion(int trans)
    {
        this.transaccion = trans;
        if (this.transaccion == SIEMBRA)
        {
            jButtonAgregarVariedad.setVisible(true);
        }
    }
    
    private void jButtonAgregarVariedadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAgregarVariedadActionPerformed
    {//GEN-HEADEREND:event_jButtonAgregarVariedadActionPerformed
        if (getCultivo() != null)
        {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame(), getCultivo(), true);
            d.setVisible(true);
        }
        else
        {
            DialogCultivo d = new DialogCultivo((Frame) GUIUtility.getApplicationFrame());
            d.setVisible(true);
        }
    }//GEN-LAST:event_jButtonAgregarVariedadActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAgregarVariedad;
    private javax.swing.JComboBox jComboBoxCampania;
    private javax.swing.JComboBox jComboBoxCampo;
    private javax.swing.JComboBox jComboBoxCultivo;
    private javax.swing.JComboBox jComboBoxVariedad;
    private javax.swing.JLabel jLabelAvisoSuperficie;
    private javax.swing.JLabel jLabelCampania;
    private javax.swing.JLabel jLabelCampo;
    private javax.swing.JLabel jLabelCultivo;
    private javax.swing.JLabel jLabelSuperficie;
    private javax.swing.JLabel jLabelVariedad;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer2;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer3;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer4;
    private ar.com.init.agros.view.components.panels.PanelSeleccionLotesSublotes panelSeleccionLotes1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadSuperficie;
    // End of variables declaration//GEN-END:variables

    private void restoreVariedad()
    {
        GUIUtility.restoreComboBox(jComboBoxVariedad);
    }

    /** Método que se encarga de añadir un listener de cambio de selección de superficie
     *  @param listener el objeto que depende del sujeto
     */
    public void addSuperficieListener(SuperficieListener listener)
    {
        panelSeleccionLotes1.addSuperficieListener(listener);
    }

    public boolean removeSuperficieListener(SuperficieListener listener)
    {
        return panelSeleccionLotes1.removeSuperficieListener(listener);
    }

    public void notifySuperficieListeners()
    {
        panelSeleccionLotes1.notifySuperficieListeners();
    }

    /** Método que se encarga de añadir un listener de cambio de selección de campaña
     *  @param listener el objeto que depende del sujeto
     */
    public void addCampaniaSelectionChangeListener(CampaniaSelectionChangeListener listener)
    {
        if (listener == null)
        {
            return;
        }
        if (campaniaListeners == null || campaniaListeners.size() == 0)
        {
            campaniaListeners = new ArrayList<CampaniaSelectionChangeListener>();
        }
        campaniaListeners.add(listener);
    }

    public boolean removeCampaniaSelectionChangeListener(CampaniaSelectionChangeListener listener)
    {
        if (campaniaListeners == null || campaniaListeners.size() == 0)
        {
            return false;
        }
        else
        {
            return campaniaListeners.remove(listener);
        }
    }

    public void notifyCampaniaSelectionChangeListeners()
    {
        for (int i = 0; campaniaListeners != null && i < campaniaListeners.size(); i++)
        {
            if (campaniaListeners.get(i) != null)
            {
                campaniaListeners.get(i).campaniaSelectionChanged(getCampania());
            }
        }
    }

    @Override
    public void refreshUI()
    {
        findEntities();
        fillCombos();
    }

    /** Método que se encarga de añadir un listener de cambio de selección de superficie
     *  @param listener el objeto que depende del sujeto
     */
    public void addCampoSelectionChangeListener(CampoSelectionChangeListener listener)
    {
        campoListener = listener;
    }

    public boolean notifyCampoSelectionChangeListener()
    {
        if (campoListener == null)
        {
            return true;
        }
        return campoListener.campoSelectionChanged(getCampo());
    }

    @Override
    public void updateSuperficie(double sup)
    {
        panelValorUnidadSuperficie.setValorUnidad(new ValorUnidad(sup, MagnitudEnum.SUPERFICIE.patron()));
        if (panelValorUnidadSuperficie.getInputVerifier() instanceof SuperficieInputVerifier)
        {
            if (panelSeleccionLotes1.calcularSuperficieSeleccionadaSinNotificar() < sup)
            {
                ((SuperficieInputVerifier)panelValorUnidadSuperficie.getInputVerifier()).setMaxValue(sup);
            }
            else
            {
                ((SuperficieInputVerifier)panelValorUnidadSuperficie.getInputVerifier()).setMaxValue(panelSeleccionLotes1.calcularSuperficieSeleccionadaSinNotificar());
            }
        }
    }

    @Override
    public void focusLost(ValorUnidad superficie)
    {
        panelSeleccionLotes1.reloadSuperficies();
        if (superficie != null && superficie.getValor() != null)
        {
            panelSeleccionLotes1.setSuperficieTotal(superficie.getValor());
        }
        else
        {
            panelSeleccionLotes1.setSuperficieTotal(0);
        }
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
