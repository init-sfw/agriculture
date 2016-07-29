/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CultivosReportCriteria.java
 *
 * Created on 08/07/2009, 22:22:11
 */
package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.reporting.components.ReportCriteria;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.list.DualList;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author gmatheu
 */
public class CultivosReportCriteria extends javax.swing.JPanel implements ReportCriteria
{

    private static final long serialVersionUID = -1L;
    private FrameNotifier frameNotifier;
    private CampaniasReportCriteria campaniaReportCriteria;
    private boolean useTrabajos;
    private boolean usePlanificaciones;
    private boolean useSiembras;
    private String title = "Cultivos - Variedades";
    private boolean showVariedades;
    private boolean requireVariedades;

    /** Creates new form CultivosReportCriteria */
    @SuppressWarnings("unchecked")
    public CultivosReportCriteria()
    {
        initComponents();

        setVariedadesVisible(true);
        requireVariedades = false;

        try {
            CultivoJpaController cultivoJpaController = new CultivoJpaController();
            dualListCultivos.addAvailable(cultivoJpaController.findEntities());
        }
        catch (PersistenceException ex) {
            if (frameNotifier != null) {
                frameNotifier.showErrorMessage(ex.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(CultivosReportCriteria.class, ex);
        }

        dualListCultivos.addSelectedDataListener(new ListDataListener()
        {

            @Override
            public void intervalAdded(ListDataEvent e)
            {
                for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                    Cultivo cultivo = (Cultivo) dualListCultivos.getSelected(i);
                    if (showVariedades) {
                        dualListVariedades.addAvailable(cultivo.getVariedades());
                    }
                }
            }

            @Override
            public void intervalRemoved(ListDataEvent e)
            {
            }

            @Override
            public void contentsChanged(ListDataEvent e)
            {
            }
        });

        dualListCultivos.addAvailableDataListener(new ListDataListener()
        {

            @Override
            public void intervalAdded(ListDataEvent e)
            {
                if (showVariedades) {
                    for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                        Cultivo cultivo = (Cultivo) dualListCultivos.getAvailable(i);
                        dualListVariedades.deleteSelected(cultivo.getVariedades());
                        dualListVariedades.deleteAvailable(cultivo.getVariedades());
                    }
                }
            }

            @Override
            public void intervalRemoved(ListDataEvent e)
            {
            }

            @Override
            public void contentsChanged(ListDataEvent e)
            {
            }
        });
    }

    /** Creates new form CultivosReportCriteria */
    @SuppressWarnings("unchecked")
    public CultivosReportCriteria(boolean showVariedades)
    {
        this();
        if (!showVariedades) {
            dualListVariedades.removeAllAvailableDataListeners();
            dualListVariedades.setVisible(false);
            title = "Cultivos";
        }
    }

    public CultivosReportCriteria(CampaniasReportCriteria campaniaReportCriteria, boolean useTrabajos, boolean usePlanificaciones, boolean useSiembras)
    {
        this();
        this.campaniaReportCriteria = campaniaReportCriteria;
        this.usePlanificaciones = usePlanificaciones;
        this.useTrabajos = useTrabajos;
        this.useSiembras = useSiembras;
    }

    public CultivosReportCriteria(boolean showVariedades, CampaniasReportCriteria campaniaReportCriteria, boolean useTrabajos, boolean usePlanificaciones, boolean useSiembras)
    {
        this(campaniaReportCriteria, useTrabajos, usePlanificaciones, useSiembras);

        if (!showVariedades) {
            dualListVariedades.removeAllAvailableDataListeners();
            dualListVariedades.setVisible(false);
            title = "Cultivos";
        }
    }

    public void setVariedadesVisible(boolean b)
    {
        showVariedades = b;
        dualListVariedades.setVisible(b);
        if (b) {
            title = "Cultivos - Variedades";
        }
        else {
            title = "Cultivos";

        }
    }

    public DualList getDualListCultivos()
    {
        return dualListCultivos;
    }

    public void setDualListCultivos(DualList dualListCultivos)
    {
        this.dualListCultivos = dualListCultivos;
    }

    public DualList getDualListVariedades()
    {
        return dualListVariedades;
    }

    public void setDualListVariedades(DualList dualListVariedades)
    {
        this.dualListVariedades = dualListVariedades;
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
    }

    public void setRequireVariedades(boolean requireVariedades)
    {
        this.requireVariedades = requireVariedades;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dualListVariedades = new ar.com.init.agros.util.gui.components.list.DualList();
        dualListCultivos = new ar.com.init.agros.util.gui.components.list.DualList();

        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(CultivosReportCriteria.class);
        dualListVariedades.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("dualListVariedades.border.title"))); // NOI18N
        dualListVariedades.setName("dualListVariedades"); // NOI18N

        dualListCultivos.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("dualListCultivos.border.title"))); // NOI18N
        dualListCultivos.setName("dualListCultivos"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dualListCultivos, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
            .addComponent(dualListVariedades, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(dualListCultivos, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dualListVariedades, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentShown
    {//GEN-HEADEREND:event_formComponentShown
        if (campaniaReportCriteria != null && campaniaReportCriteria.validateSelection()) {
            List<Campania> campanias = campaniaReportCriteria.getSelected();

            dualListCultivos.clearAvailable();
            CultivoJpaController cultivoController = new CultivoJpaController();

            dualListCultivos.addAvailable(cultivoController.findByCampanias(campanias, useTrabajos,
                    usePlanificaciones, useSiembras));

            for (Object o : dualListCultivos.getSelected()) {

                List selectedVariedades = dualListVariedades.getSelected();

                Cultivo c = (Cultivo) o;
                dualListVariedades.addAvailable(c.getVariedades());

                dualListVariedades.addSelected(selectedVariedades);
            }
        }
    }//GEN-LAST:event_formComponentShown

    @Override
    public boolean validateSelection()
    {
        if (showVariedades) {
            if (requireVariedades) {
                return getSelectedVariedades().size() > 0;
            }
            else {
                return getSelectedCultivos().size() > 0 || getSelectedVariedades().size() > 0;
            }
        }
        else {
            return getSelectedCultivos().size() > 0;
        }
    }

    @Override
    public void clear()
    {
        dualListCultivos.removeAllSelected();
        dualListVariedades.removeAllSelected();
    }

    @Override
    public String getTabTitle()
    {
        return title;
    }

    @Override
    public String getErrorMessage()
    {
        return "Debe seleccionar por lo menos un cultivo.";
    }

    @SuppressWarnings("unchecked")
    public List<Cultivo> getSelectedCultivos()
    {
        return dualListCultivos.getSelected();
    }

    @SuppressWarnings("unchecked")
    public List<VariedadCultivo> getSelectedVariedades()
    {
        if (showVariedades) {
            return dualListVariedades.getSelected();
        }
        else {
            return null;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.list.DualList dualListCultivos;
    private ar.com.init.agros.util.gui.components.list.DualList dualListVariedades;
    // End of variables declaration//GEN-END:variables
}
