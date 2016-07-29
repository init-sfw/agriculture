/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DialogSiembraCampania.java
 *
 * Created on 06/08/2009, 00:08:32
 */
package ar.com.init.agros.view.siembras;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author gmatheu
 */
public class DialogSiembraCampania extends javax.swing.JFrame implements UpdatableListener
{

    private static final long serialVersionUID = -1L;
    private static final String NO_SEMBRADO_VALUE = "No sembrado";
    private static DialogSiembraCampania instance;
    private BaseEntityJpaController<Campania> jpaCampania;
    private CampoJpaController jpaCampo;

    /** Creates new form DialogSiembraCampania */
    public DialogSiembraCampania()
    {
        super();
        initComponents();
        oKCancelCleanPanel.disableForList();
        oKCancelCleanPanel.setListenerToButtons(new OkCleanCancelEventControl());
        oKCancelCleanPanel.setOwner(this);

        try {
            jpaCampania = new BaseEntityJpaController<Campania>(Campania.class);
            jpaCampo = new CampoJpaController();
            refreshUI();
        }
        catch (PersistenceException e) {
            GUIUtility.logPersistenceError(DialogSiembraCampania.class, e);
        }
    }

    public static void showInstance()
    {
        if (instance == null) {
            instance = new DialogSiembraCampania();
        }

        if (!instance.isVisible()) {
            instance.refreshUI();
            instance.setVisible(true);
        }
        instance.toFront();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void refreshUI()
    {
        GUIUtility.refreshComboBox(jpaCampania.findEntities(), jComboBoxCampania);
        fillComboCampo();
    }

    private void fillComboCampo()
    {
        if (jComboBoxCampania.getSelectedItem() instanceof Campania) {
            Campania c = (Campania) jComboBoxCampania.getSelectedItem();

            GUIUtility.refreshComboBox(jpaCampo.findAllEntities(), jComboBoxCampo);
        }
        else {
            jComboBoxCampo.removeAllItems();
        }
    }

    private void refreshGrafico()
    {
        jPanelGrafico.removeAll();

        if (jComboBoxCampania.getSelectedItem() instanceof Campania && jComboBoxCampo.getSelectedItem() instanceof Campo) {

            Campania campania = (Campania) jComboBoxCampania.getSelectedItem();
            Campo campo = (Campo) jComboBoxCampo.getSelectedItem();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<Superficie> sembradas = new ArrayList<Superficie>();
            for (Siembra siembra : campania.getSiembras()) {
                if (siembra.getCampo().equals(campo)) {
                    for (Superficie sup : siembra.getSuperficies()) {
                        dataset.setValue(sup.getSuperficie().getValor(), sup.toString(),
                                siembra.getCultivo().toString());
                        sembradas.add(sup);
                    }
                }
            }

            if (!(sembradas.size() == 1 && sembradas.get(0) instanceof Campo)) {
                List<SubLote> sublotes = new ArrayList<SubLote>(campo.getSubLotes());
                List<Lote> lotes = new ArrayList<Lote>(campo.getLotes());

                for (SubLote sl : sublotes) {
                    lotes.remove(sl.getPadre());
                    if (!sembradas.contains(sl)) {
                        dataset.setValue(sl.getSuperficie().getValor(), sl.toString(), NO_SEMBRADO_VALUE);
                    }
                }

                for (Lote lote : lotes) {
                    if (!sembradas.contains(lote)) {
                        dataset.setValue(lote.getSuperficie().getValor(), lote.toString(), NO_SEMBRADO_VALUE);
                    }
                }

                if (sublotes.size() == 0 && lotes.size() == 0) {
                    dataset.setValue(campo.getSuperficie().getValor(), campo.toString(), NO_SEMBRADO_VALUE);
                }
            }




            JFreeChart chart = ChartFactory.createStackedBarChart(
                    "Siembras de la Campaña: '" + campania.toString() + "' para el campo '" + campo.toString() + "'", // chart title
                    "Cutivos", // domain axis label
                    "Superficie (" + MagnitudEnum.SUPERFICIE.patron().getAbreviatura() + ")", // range axis label
                    dataset, // data
                    PlotOrientation.HORIZONTAL, // the plot orientation
                    true, // legend
                    false, // tooltips
                    false // urls
                    );
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryItemRenderer renderer = new StackedBarRenderer(false);
            renderer.setBaseItemLabelsVisible(true);
            renderer.setBaseItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator());
            renderer.setBaseToolTipGenerator(
                    new StandardCategoryToolTipGenerator());            
            renderer.setBaseItemLabelFont(new Font("Arial", Font.PLAIN, 14));           
            renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                    ItemLabelAnchor.CENTER, TextAnchor.HALF_ASCENT_CENTER));

            plot.setRenderer(renderer);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setLowerMargin(0.15);
            rangeAxis.setUpperMargin(0.15);

            jPanelGrafico.add(new ChartPanel(chart));
        }

        jPanelGrafico.updateUI();
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }

    private class OkCleanCancelEventControl extends AbstractEventControl
    {

        @Override
        public FrameNotifier getFrameNotifier()
        {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == oKCancelCleanPanel.getBtnCancelar()) {
                DialogSiembraCampania.this.dispose();
            }

        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oKCancelCleanPanel = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jLabelCampania = new javax.swing.JLabel();
        jLabelCampo = new javax.swing.JLabel();
        jComboBoxCampania = new javax.swing.JComboBox();
        jComboBoxCampo = new javax.swing.JComboBox();
        jPanelGrafico = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogSiembraCampania.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        oKCancelCleanPanel.setName("oKCancelCleanPanel"); // NOI18N

        jLabelCampania.setText(resourceMap.getString("jLabelCampania.text")); // NOI18N
        jLabelCampania.setName("jLabelCampania"); // NOI18N

        jLabelCampo.setText(resourceMap.getString("jLabelCampo.text")); // NOI18N
        jLabelCampo.setName("jLabelCampo"); // NOI18N

        jComboBoxCampania.setName("jComboBoxCampania"); // NOI18N
        jComboBoxCampania.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampaniaActionPerformed(evt);
            }
        });

        jComboBoxCampo.setName("jComboBoxCampo"); // NOI18N
        jComboBoxCampo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCampoActionPerformed(evt);
            }
        });

        jPanelGrafico.setName("jPanelGrafico"); // NOI18N
        jPanelGrafico.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelCampania, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelCampo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxCampania, 0, 481, Short.MAX_VALUE)
                    .addComponent(jComboBoxCampo, 0, 481, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCampania)
                    .addComponent(jComboBoxCampania, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCampo)
                    .addComponent(jComboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oKCancelCleanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxCampaniaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampaniaActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampaniaActionPerformed
        fillComboCampo();

        refreshGrafico();
    }//GEN-LAST:event_jComboBoxCampaniaActionPerformed

    private void jComboBoxCampoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxCampoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxCampoActionPerformed
        refreshGrafico();
    }//GEN-LAST:event_jComboBoxCampoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxCampania;
    private javax.swing.JComboBox jComboBoxCampo;
    private javax.swing.JLabel jLabelCampania;
    private javax.swing.JLabel jLabelCampo;
    private javax.swing.JPanel jPanelGrafico;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel;
    // End of variables declaration//GEN-END:variables
}
