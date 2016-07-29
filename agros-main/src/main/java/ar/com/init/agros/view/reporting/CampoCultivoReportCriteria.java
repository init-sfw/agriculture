/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CampoCultivoReportCriteria.java
 *
 * Created on 05-sep-2009, 19:14:41
 */
package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.FormaFumigacion;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.reporting.components.ReportCriteria;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author fbobbio
 */
public class CampoCultivoReportCriteria extends javax.swing.JPanel implements ReportCriteria
{

    private static final long serialVersionUID = -1L;
    private FrameNotifier frameNotifier;
    private CampaniasReportCriteria campaniaReportCriteria;
    private boolean useTrabajos;
    private boolean usePlanificaciones;
    private boolean useSiembras;
    private final String[] OPS = {"Establecimiento", "Cultivo"};

    /** Creates new form CampoCultivoReportCriteria */
    public CampoCultivoReportCriteria()
    {
        initComponents();
        jComboBoxFiltro.setModel(new DefaultComboBoxModel(OPS));
        CampoReportCriteria cardCampo = campoReportCriteria1;
        CultivosReportCriteria cardCultivo = cultivosReportCriteria1;
        panelDePaneles.add(cardCampo, OPS[0]);
        panelDePaneles.add(cardCultivo, OPS[1]);
        jComboBoxFiltro.addItemListener(new CardLayoutEvent());

        setFormaFumigacionVisible(false);
    }

    /**
     * Crea un CampoCultivoReportCriteria que mostrará como elementos disponibles aquellos que hayan sido usados
     * en las campanias pasadas como parametro.
     * @param campaniaReportCriteria campanias cuyos elementos se mostraran
     * @param useTrabajos buscar elementos en trabajos
     * @param usePlanificaciones buscar elementos en planificaciones
     */
    public CampoCultivoReportCriteria(CampaniasReportCriteria campaniaReportCriteria, boolean useTrabajos, boolean usePlanificaciones, boolean useSiembras)
    {
        this();
        this.campaniaReportCriteria = campaniaReportCriteria;
        this.usePlanificaciones = usePlanificaciones;
        this.useTrabajos = useTrabajos;
        this.useSiembras = useSiembras;
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
    }

    public void setFormaFumigacionVisible(boolean b)
    {
        jLabelFormaFumigacion.setVisible(b);
        jComboBoxFormaFumigacion.setVisible(b);

        if (b) {
            NamedEntityJpaController<FormaFumigacion> formaFumigacionJpaController =
                    new NamedEntityJpaController<FormaFumigacion>(FormaFumigacion.class);
            GUIUtility.refreshComboBox(formaFumigacionJpaController.findEntities(), jComboBoxFormaFumigacion);
        }
    }

    public void setVariedadesVisible(boolean b)
    {
        cultivosReportCriteria1.setVariedadesVisible(b);
    }

    @Override
    public boolean validateSelection()
    {
        boolean campo = campoReportCriteria1.validateSelection();
        boolean cultivo = cultivosReportCriteria1.validateSelection();
        boolean res = campo ^ cultivo;
        return res;
    }

    @Override
    public void clear()
    {
        campoReportCriteria1.clear();
        cultivosReportCriteria1.clear();
    }

    @Override
    public String getTabTitle()
    {
        return "Establecimientos y Cultivos";
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar al menos un campo o un cultivo";
    }

    public List<Campo> getCampos()
    {
        return campoReportCriteria1.getSelected();
    }

    public List<Cultivo> getCultivos()
    {
        return cultivosReportCriteria1.getSelectedCultivos();
    }

    public List<VariedadCultivo> getVariedades()
    {
        return cultivosReportCriteria1.getSelectedVariedades();
    }

    public FormaFumigacion getSelectedFormaFumigacion()
    {
        if (jComboBoxFormaFumigacion.getSelectedItem() instanceof FormaFumigacion) {
            return (FormaFumigacion) jComboBoxFormaFumigacion.getSelectedItem();
        }
        else {
            return null;
        }
    }

    public void setRequireVariedades(boolean requireVariedades)
    { 
        cultivosReportCriteria1.setRequireVariedades(requireVariedades);
    }

    class CardLayoutEvent implements ItemListener
    {

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            CardLayout cl = (CardLayout) (panelDePaneles.getLayout());
            cl.show(panelDePaneles, (String) e.getItem());
            clear();
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

        panelDePaneles = new javax.swing.JPanel();
        campoReportCriteria1 = new ar.com.init.agros.view.reporting.CampoReportCriteria();
        cultivosReportCriteria1 = new ar.com.init.agros.view.reporting.CultivosReportCriteria();
        jComboBoxFiltro = new javax.swing.JComboBox();
        jLabelFiltrar = new javax.swing.JLabel();
        jLabelFormaFumigacion = new javax.swing.JLabel();
        jComboBoxFormaFumigacion = new javax.swing.JComboBox();

        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelDePaneles.setName("panelDePaneles"); // NOI18N
        panelDePaneles.setLayout(new java.awt.CardLayout());

        campoReportCriteria1.setName("campoReportCriteria1"); // NOI18N
        panelDePaneles.add(campoReportCriteria1, "card2");

        cultivosReportCriteria1.setName("cultivosReportCriteria1"); // NOI18N
        panelDePaneles.add(cultivosReportCriteria1, "card3");

        jComboBoxFiltro.setName("jComboBoxFiltro"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(CampoCultivoReportCriteria.class);
        jLabelFiltrar.setText(resourceMap.getString("jLabelFiltrar.text")); // NOI18N
        jLabelFiltrar.setName("jLabelFiltrar"); // NOI18N

        jLabelFormaFumigacion.setText(resourceMap.getString("jLabelFormaFumigacion.text")); // NOI18N
        jLabelFormaFumigacion.setName("jLabelFormaFumigacion"); // NOI18N

        jComboBoxFormaFumigacion.setName("jComboBoxFormaFumigacion"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDePaneles, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFiltrar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelFormaFumigacion, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxFormaFumigacion, 0, 561, Short.MAX_VALUE)
                    .addComponent(jComboBoxFiltro, 0, 561, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFormaFumigacion)
                    .addComponent(jComboBoxFormaFumigacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFiltrar)
                    .addComponent(jComboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDePaneles, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentShown
    {//GEN-HEADEREND:event_formComponentShown
        if (campaniaReportCriteria != null && campaniaReportCriteria.validateSelection()) {
            List<Campania> campanias = campaniaReportCriteria.getSelected();

            campoReportCriteria1.clearAvailable();
            CampoJpaController campController = new CampoJpaController();

            campoReportCriteria1.addAvailable(campController.findByCampanias(campanias, useTrabajos,
                    usePlanificaciones, useSiembras));

            cultivosReportCriteria1.getDualListCultivos().clearAvailable();
            CultivoJpaController cultivoController = new CultivoJpaController();

            cultivosReportCriteria1.getDualListCultivos().addAvailable(cultivoController.findByCampanias(
                    campanias, useTrabajos,
                    usePlanificaciones, useSiembras));

            cultivosReportCriteria1.getDualListVariedades().clearAvailable();
            List<VariedadCultivo> variedades = new ArrayList<VariedadCultivo>();
            for (Cultivo c : cultivosReportCriteria1.getSelectedCultivos()) {
                variedades.addAll(c.getVariedades());
            }

            cultivosReportCriteria1.getDualListVariedades().addAvailable(variedades);
        }
    }//GEN-LAST:event_formComponentShown
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.view.reporting.CampoReportCriteria campoReportCriteria1;
    private ar.com.init.agros.view.reporting.CultivosReportCriteria cultivosReportCriteria1;
    private javax.swing.JComboBox jComboBoxFiltro;
    private javax.swing.JComboBox jComboBoxFormaFumigacion;
    private javax.swing.JLabel jLabelFiltrar;
    private javax.swing.JLabel jLabelFormaFumigacion;
    private javax.swing.JPanel panelDePaneles;
    // End of variables declaration//GEN-END:variables
}
