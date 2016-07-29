/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelSemillas.java
 *
 * Created on 27-jul-2009, 15:58:12
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.Semilla;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorMonedaMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.components.FocusPanelValorMonedaListener;
import ar.com.init.agros.view.components.FocusPanelValorMonedaUnidadListener;
import ar.com.init.agros.view.components.FocusPanelValorUnidadListener;
import ar.com.init.agros.view.components.SuperficieListener;
import javax.persistence.PersistenceException;

/**
 *
 * @author fbobbio
 */
public class PanelSemillas extends javax.swing.JPanel implements SuperficieListener, FocusPanelValorUnidadListener, FocusPanelValorMonedaListener, FocusPanelValorMonedaUnidadListener
{

    private FrameNotifier frameNotifier1;
    private Semilla semilla;
    private double superficiePlanificada;

    /** Creates new form PanelSemillas */
    public PanelSemillas()
    {
        initComponents();

        try
        {
            panelValorUnidadCantidadPorHectarea.addFocusPanelValorUnidadListener(this);
            panelValorMonedaPrecioReferencia.addFocusPanelValorMonedaListener(this);
            panelValorMonedaPrecioInoculante.addFocusPanelValorMonedaListener(this);
            panelValorUnidadCantidadPorHectarea.setEnabled(true, false);
            panelValorUnidadCantidadPorHectarea.showUnidadesPorMagnitud(MagnitudEnum.PESO);
            panelValorUnidadCantidadPorHectarea.selectUnidad(MagnitudEnum.PESO.patron());
            panelValorMonedaCostoTotalSemillas.setEnabled(false);
            panelValorMonedaCostoTotalInoculante.setEnabled(false);
            panelValorUnidadCantidadSemillas.setEnabled(false, false);
            panelValorUnidadCantidadSemillas.showUnidadesPorMagnitud(MagnitudEnum.PESO);
            panelValorUnidadCantidadSemillas.selectUnidad(MagnitudEnum.PESO.patron());
        }
        catch(PersistenceException e)
        {
            if (frameNotifier1 != null)
            {
                frameNotifier1.showErrorMessage(e.getLocalizedMessage());
            }
            GUIUtility.logPersistenceError(PanelSemillas.class, e);
        }
    }

    public Semilla getSemillaCargadas(Cultivo cultivo, VariedadCultivo variedad)
    {
        getSemilla().setCultivo(cultivo);
        semilla.setVariedad(variedad);
        calcularCostosEnSemillas();
        semilla.setCantTotalSemillas(panelValorUnidadCantidadSemillas.getValorUnidad());
        semilla.setCantidadPorHectarea(panelValorUnidadCantidadPorHectarea.getValorUnidad());
        semilla.setCostoTotalInoculante(panelValorMonedaCostoTotalInoculante.getValorMoneda());
        semilla.setCostoTotalSemillas(panelValorMonedaCostoTotalSemillas.getValorMoneda());
        if (semilla.getCantidadPorHectarea() == null && semilla.getPrecioReferencia() == null && semilla.getPrecioReferenciaInoculante() == null)
        {
            return null;
        }
        return semilla;
    }

    public void setSemillasCargadas(Semilla semilla)
    {
        setSemilla(semilla);
    }

    public void disableFields()
    {
        panelValorMonedaPrecioInoculante.setEnabled(false);
        panelValorMonedaPrecioReferencia.setEnabled(false);
        panelValorUnidadCantidadPorHectarea.setEnabled(false, false);
    }

    private ValorUnidad getCantidadPorHectarea()
    {
        if (panelValorUnidadCantidadPorHectarea.getValorUnidad() != null && panelValorUnidadCantidadPorHectarea.getValorUnidad().isValid())
        {
            return panelValorUnidadCantidadPorHectarea.getValorUnidad();
        }
        else
        {
            return null;
        }
    }

    private ValorMoneda getPrecioReferencia()
    {
        if (panelValorMonedaPrecioReferencia.getValorMoneda() != null)
        {
            return panelValorMonedaPrecioReferencia.getValorMoneda();
        }
        else
        {
            return null;
        }
    }

    private ValorMoneda getPrecioReferenciaInoculante()
    {
        if (panelValorMonedaPrecioInoculante.getValorMoneda() != null)
        {
            return panelValorMonedaPrecioInoculante.getValorMoneda();
        }
        else
        {
            return null;
        }
    }

    private Semilla getSemilla()
    {
        if (semilla == null)
        {
            semilla = new Semilla();
        }
        semilla.setCantidadPorHectarea(getCantidadPorHectarea());
            semilla.setPrecioReferencia(getPrecioReferencia());
        semilla.setPrecioReferenciaInoculante(getPrecioReferenciaInoculante());
        return semilla;
    }

    private void setSemilla(Semilla semilla)
    {
        this.semilla = semilla;
        if (this.semilla != null)
        {
            panelValorUnidadCantidadPorHectarea.setValorUnidad(this.semilla.getCantidadPorHectarea());
            panelValorMonedaPrecioReferencia.setValorMoneda(this.semilla.getPrecioReferencia());
            panelValorMonedaPrecioInoculante.setValorMoneda(this.semilla.getPrecioReferenciaInoculante());
        }
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier1 = frameNotifier;
        panelValorMonedaPrecioInoculante.setFrameNotifier(this.frameNotifier1);
        panelValorMonedaPrecioReferencia.setFrameNotifier(this.frameNotifier1);
        panelValorUnidadCantidadPorHectarea.setFrameNotifier(this.frameNotifier1);
    }

    public void clear()
    {
        clearFields();
    }

    private void clearFields()
    {
        panelValorMonedaCostoTotalInoculante.clear();
        panelValorMonedaPrecioReferencia.clear();
        panelValorUnidadCantidadPorHectarea.clear(true, false);
        panelValorMonedaCostoTotalSemillas.clear();
        panelValorMonedaPrecioInoculante.clear();
        panelValorUnidadCantidadSemillas.clear(true, false);
        semilla = null;
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelValorUnidadCantidadPorHectarea = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        panelValorMonedaPrecioInoculante = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        panelValorMonedaPrecioReferencia = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelValorMonedaCostoTotalSemillas = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        panelValorMonedaCostoTotalInoculante = new ar.com.init.agros.view.components.valores.PanelValorMoneda();
        panelValorUnidadCantidadSemillas = new ar.com.init.agros.view.components.valores.PanelValorUnidad();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelSemillas.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelValorMonedaPrecioInoculante, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(panelValorUnidadCantidadPorHectarea, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(panelValorMonedaPrecioReferencia, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(panelValorUnidadCantidadPorHectarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(panelValorMonedaPrecioReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(panelValorMonedaPrecioInoculante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelValorMonedaCostoTotalSemillas, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                            .addComponent(panelValorMonedaCostoTotalInoculante, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelValorUnidadCantidadSemillas, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(panelValorMonedaCostoTotalSemillas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(panelValorMonedaCostoTotalInoculante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(panelValorUnidadCantidadSemillas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaCostoTotalInoculante;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaCostoTotalSemillas;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaPrecioInoculante;
    private ar.com.init.agros.view.components.valores.PanelValorMoneda panelValorMonedaPrecioReferencia;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadCantidadPorHectarea;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadCantidadSemillas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void updateSuperficie(double sup)
    {
        superficiePlanificada = sup;
        calcularCostosEnSemillas();
    }

    public void calcularCostosEnSemillas()
    {
        double costoInoculantePorHa = 0;
        double costoReferenciaPorHa = 0;
        double cantidadSemillas = 0;
        if (panelValorMonedaPrecioReferencia.getValorMoneda() != null)
        {
            costoReferenciaPorHa = panelValorMonedaPrecioReferencia.getValorMoneda().getMonto();
        }
        if (panelValorMonedaPrecioInoculante.getValorMoneda() != null)
        {
            costoInoculantePorHa = panelValorMonedaPrecioInoculante.getValorMoneda().getMonto();
        }
        if (panelValorUnidadCantidadPorHectarea.getValorUnidad() != null)
        {
            cantidadSemillas = panelValorUnidadCantidadPorHectarea.getValorUnidad().getValor();
        }
        double costoTotalEnSemillas = costoReferenciaPorHa * superficiePlanificada;
        double cantidadTotalSemillas = cantidadSemillas * superficiePlanificada;
        panelValorMonedaCostoTotalSemillas.setValorMoneda(new ValorMoneda(costoTotalEnSemillas));
        panelValorUnidadCantidadSemillas.setValorUnidad(new ValorUnidad(cantidadTotalSemillas, MagnitudEnum.PESO.patron()));
        panelValorMonedaCostoTotalInoculante.setValorMoneda(new ValorMoneda(costoInoculantePorHa * superficiePlanificada));
    }

    @Override
    public void focusLost(ValorUnidad superficie)
    {
        calcularCostosEnSemillas();
    }

    @Override
    public void focusLost(ValorMoneda valorMoneda)
    {
        calcularCostosEnSemillas();
    }

    @Override
    public void focusLost(ValorMonedaMedida valorMonedaMedida)
    {
        calcularCostosEnSemillas();
    }
}
