
/*
 * PanelSeleccionLotesSublotes.java
 *
 * Created on 11-jun-2009, 19:39:23
 */
package ar.com.init.agros.view.components.panels;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.terreno.Superficie;
import ar.com.init.agros.view.components.*;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.terreno.SubLote;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.ListableTableRenderer;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.util.gui.validation.inputverifiers.DecimalInputVerifier;
import ar.com.init.agros.view.components.editors.SuperficieTableCellEditor;
import ar.com.init.agros.view.components.model.SeleccionLotesTableModel;
import ar.com.init.agros.view.components.model.SeleccionSubLotesTableModel;
import ar.com.init.agros.view.components.model.SeleccionSuperficieLotesTableModel;
import ar.com.init.agros.view.components.model.SeleccionSuperficieSubLotesTableModel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author fbobbio
 */
public class PanelSeleccionLotesSublotes extends javax.swing.JPanel
{

    private FrameNotifier frameNotifier;
    private Campo campoSeleccionado;
    private double superficieTotal;
    private List<SuperficieListener> listenersList;
    private SeleccionLotesTableModel seleccionLotesTableModel1;
    private SeleccionSubLotesTableModel seleccionSubLotesTableModel1;
    private boolean consulta;

    /** Creates new form PanelSeleccionLotesSublotes */
    public PanelSeleccionLotesSublotes()
    {
        this(new SeleccionLotesTableModel(), new SeleccionSubLotesTableModel());
    }

    public void setSeleccionTableModels(SeleccionLotesTableModel seleccionLotesTableModel1, SeleccionSubLotesTableModel seleccionSubLotesTableModel1)
    {
        this.seleccionLotesTableModel1 = seleccionLotesTableModel1;
        jXTableLote.setModel(seleccionLotesTableModel1);
        this.seleccionSubLotesTableModel1 = seleccionSubLotesTableModel1;
        jXTableSubLote.setModel(seleccionSubLotesTableModel1);
        setEditors();
        addListenersToTableModels();

        jXTableLote.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 10));
        jXTableSubLote.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 10));
    }

    public PanelSeleccionLotesSublotes(SeleccionLotesTableModel lotesTableModel, SeleccionSubLotesTableModel subLotesTableModel)
    {
        seleccionLotesTableModel1 = lotesTableModel;
        seleccionSubLotesTableModel1 = subLotesTableModel;
        initComponents();
        seleccionLotesTableModel1.getColumnCount();
        jXTableLote.setModel(seleccionLotesTableModel1);
        jXTableLote.getColumn(SeleccionLotesTableModel.LOTE_COLUMN_IDX).setCellRenderer(listableTableRenderer1);
        jXTableSubLote.setModel(seleccionSubLotesTableModel1);
        jXTableSubLote.getColumn(SeleccionSubLotesTableModel.LOTE_COLUMN_IDX).setCellRenderer(listableTableRenderer1);
        jXTableSubLote.getColumn(SeleccionSubLotesTableModel.SUBLOTE_COLUMN_IDX).setCellRenderer(listableTableRenderer1);

        addListenersToTableModels();
        jXTableLote.packAll();
        jXTableSubLote.packAll();
    }

    public double getSuperficieDeSeleccion()
    {
        return 0;

    }

    public double getSuperficieTotal()
    {
        return superficieTotal;
    }

    public void setSuperficieTotal(double superficieTotal)
    {
        this.superficieTotal = superficieTotal;
        notifySuperficieListeners();
    }

    public void seleccionarTodoElCampo(boolean selected)
    {
        seleccionLotesTableModel1.changeAll(selected);
        seleccionSubLotesTableModel1.setData(seleccionLotesTableModel1.getSubLotesFromCheckedLotes());
        seleccionSubLotesTableModel1.changeAll(selected);
        calcularSuperficieTotal();
    }

    public boolean isAllCampoSelected()
    {
        return (seleccionLotesTableModel1.isAllSelected() && seleccionSubLotesTableModel1.isAllSelected());
    }

    public void setRedBorders()
    {
        GUIUtility.setRedBorder(jXTableLote);
        GUIUtility.setRedBorder(jXTableSubLote);
    }

    public void restoreRedBorders()
    {
        GUIUtility.restoreBorder(jXTableLote);
        GUIUtility.restoreBorder(jXTableSubLote);
    }

    public void setSelectedSuperficies(List<Superficie> superficies, double superficieSeleccionada)
    {
        setSelectedSuperficies(superficies);
        setSuperficieTotal(superficieSeleccionada);
    }

    public void setSelectedSuperficies(List<Superficie> superficies)
    {
        List<Lote> lotes = new ArrayList<Lote>();
        List<SubLote> subLotes = new ArrayList<SubLote>();
        setCampoSeleccionado(campoSeleccionado); // seteamos el campo sobre el cual se trabajará
        for (Superficie s : superficies)
        {
            if (s instanceof Campo)
            {
                Campo c = (Campo) s;
                if (!c.equals(campoSeleccionado))
                {
                    Logger.getLogger(PanelSeleccionLotesSublotes.class.getName()).log(Level.SEVERE, "El campo seleccionado difiere del campo guardado en la base de datos\n" + c.getNombre() + " es distinto de " + campoSeleccionado.getNombre());
                }
                setLotesSeleccionados(c.getLotes());
                setSubLotesSeleccionados(c.getSubLotes());
                return;
            }
            if (s instanceof Lote)
            {
                lotes.add((Lote) s);
            }
            if (s instanceof SubLote)
            {
                subLotes.add((SubLote) s);
            }
        }
        if (lotes.size() > 0)
        {
            setLotesSeleccionados(lotes);
            for (Lote l : lotes)
            {
                subLotes.addAll(l.getSubLotes());
            }
        }
        if (subLotes.size() > 0)
        {
            seleccionLotesTableModel1.checkLotesFromSublotes(subLotes);
            setSubLotesSeleccionados(subLotes);
        }
        calcularSuperficieTotal();
    }

    public List<Superficie> getSelectedSuperficies()
    {
        List<Superficie> ret = new ArrayList<Superficie>();
        if (isAllCampoSelected())
        {
            ret.add(campoSeleccionado);
            return ret;
        }
        List<Lote> lotes = seleccionLotesTableModel1.getCheckedData();
        List<SubLote> sublotes = seleccionSubLotesTableModel1.getCheckedData();
        for (Lote l : lotes)
        {
            if (sublotes.containsAll(l.getSubLotes())) // si todos los sublotes del lote están seleccionados
            {
                ret.add(l); // agrego a la selección el lote
            }
            else
            {
                ret.addAll(seleccionSubLotesTableModel1.getCheckedSubLotesFromSelectedLote(l)); // agrego a la selección los sublotes de ese lote
            }
        }
        return ret;
    }

    private void addListenersToTableModels()
    {
        jXTableLote.getModel().addTableModelListener(new TableModelListener()
        {

            @Override
            public void tableChanged(TableModelEvent e)
            {
                if (jXTableLote.getRowCount() > 0 && jXTableLote.getSelectedColumn() == SeleccionLotesTableModel.SELECCION_COLUMN_IDX)
                {
                    seleccionSubLotesTableModel1.setData(seleccionLotesTableModel1.getSubLotesFromCheckedLotes(), seleccionSubLotesTableModel1.getCheckedData());
                    calcularSuperficieTotal();
                }
                if (jXTableLote.getRowCount() > 0 && seleccionLotesTableModel1.isAllSelected())
                {
                    jCheckBoxLote.setSelected(true);
                }
                else
                {
                    jCheckBoxLote.setSelected(false);
                }
            }
        });

        jXTableSubLote.getModel().addTableModelListener(new TableModelListener()
        {

            @Override
            public void tableChanged(TableModelEvent e)
            {
                if (jXTableSubLote.getRowCount() > 0 && jXTableSubLote.getSelectedColumn() == SeleccionSubLotesTableModel.SELECCION_COLUMN_IDX)
                {
                    calcularSuperficieTotal();
                }
                if (jXTableSubLote.getRowCount() > 0 && seleccionSubLotesTableModel1.getSubLotes() != null && seleccionSubLotesTableModel1.isAllSelected())
                {
                    jCheckBoxSublotes.setSelected(true);
                }
                else
                {
                    jCheckBoxSublotes.setSelected(false);
                }
            }
        });
    }

    public void calcularSuperficieTotal()
    {
        superficieTotal = calcularSuperficieSeleccionadaSinNotificar();
        if (isAllCampoSelected())
        {
            jCheckBoxSeleccionarCampo.setSelected(true);
        }
        else
        {
            jCheckBoxSeleccionarCampo.setSelected(false);
        }
        notifySuperficieListeners();
    }

    public double calcularSuperficieSeleccionadaSinNotificar()
    {
        double superficie = 0;
        List<Superficie> aux = getSelectedSuperficies();
        if (isTrabajo())
        {//si es un trabajo vamos a calcular con los valores ingresados de superficie
            for (Superficie sup : aux)
            {
                if (sup instanceof Campo)
                {
                    for (Lote l : campoSeleccionado.getLotes())
                    {
                        if (l.hasChildren())
                        {
                            superficie += ((SeleccionSuperficieSubLotesTableModel) seleccionSubLotesTableModel1).getSumatoriaSuperficies(l);
                        }
                        else
                        {
                            superficie += ((SeleccionSuperficieLotesTableModel) seleccionLotesTableModel1).getValorSuperficie(l);
                        }
                    }

                }
                if (sup instanceof Lote)
                {
                    superficie += ((SeleccionSuperficieLotesTableModel) seleccionLotesTableModel1).getValorSuperficie(sup);
                }
                if (sup instanceof SubLote)
                {
                    superficie += ((SeleccionSuperficieSubLotesTableModel) seleccionSubLotesTableModel1).getValorSuperficie(sup);
                }
            }
        }
        else
        {// si es una siembra o una planificación se calcula con las superficies fijas
            for (Superficie sup : aux)
            {
                superficie += sup.getSuperficie().getValor().doubleValue();
            }
        }
        return superficie;
    }

    private void cleanSubLotes()
    {
        GUIUtility.clearTable(jXTableSubLote);
    }

    public void clear()
    {
        clean();
    }

    public JXTable getJXTableLote()
    {
        return jXTableLote;
    }

    public void setJXTableLote(JXTable jXTableLote)
    {
        this.jXTableLote = jXTableLote;
    }

    public ListableTableRenderer getListableTableRenderer1()
    {
        return listableTableRenderer1;
    }

    public void setListableTableRenderer1(ListableTableRenderer listableTableRenderer1)
    {
        this.listableTableRenderer1 = listableTableRenderer1;
    }

    private void clean()
    {
        campoSeleccionado = null;
        seleccionLotesTableModel1.setData(new ArrayList<Lote>());
        seleccionSubLotesTableModel1.setData(new ArrayList<SubLote>());
        jCheckBoxSeleccionarCampo.setSelected(false);
    }

    public void disableFields()
    {
        consulta = true;
        jXTableLote.setEnabled(false);
        jXTableSubLote.setEnabled(false);
        removeTableListeners();
        jCheckBoxLote.setEnabled(false);
        jCheckBoxSublotes.setEnabled(false);
        jCheckBoxSeleccionarCampo.setEnabled(false);
    }

    public void setCheckBoxesEnabled(boolean enabled)
    {
        jCheckBoxSeleccionarCampo.setEnabled(enabled);
        jCheckBoxLote.setEnabled(enabled);
        jCheckBoxSublotes.setEnabled(enabled);
    }

    public Campo getCampoSeleccionado()
    {
        return campoSeleccionado;
    }

    public void setCampoSeleccionado(Campo campoSeleccionado)
    {
        this.campoSeleccionado = campoSeleccionado;
        if (campoSeleccionado != null)
        {
            seleccionLotesTableModel1.setData(campoSeleccionado.getLotes());
            if (!consulta)
            {
                setCheckBoxesEnabled(true);
            }
        }
        else
        {
            seleccionLotesTableModel1.clear();
            setCheckBoxesEnabled(false);
        }
    }

    public FrameNotifier getFrameNotifier()
    {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier)
    {
        this.frameNotifier = frameNotifier;
        if (isTrabajo())
        {
            setEditors();
        }
        calcularSuperficieTotal();
    }

    public List<Lote> getLotesSeleccionados()
    {
        return seleccionLotesTableModel1.getCheckedData();
    }

    public void setLotesSeleccionados(List<Lote> lotesSeleccionados)
    {
        if (campoSeleccionado != null)
        {
            seleccionLotesTableModel1.setData(campoSeleccionado.getLotes(), lotesSeleccionados);
        }
        else
        {
            seleccionLotesTableModel1.clear();
        }
    }

    public List<SubLote> getSubLotesSeleccionados()
    {
        return seleccionSubLotesTableModel1.getCheckedData();
    }

    public void setSubLotesSeleccionados(List<SubLote> subLotesSeleccionados)
    {
        seleccionSubLotesTableModel1.setData(seleccionLotesTableModel1.getSubLotesFromCheckedLotes(), subLotesSeleccionados);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableTableRenderer1 = new ar.com.init.agros.util.gui.ListableTableRenderer();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableLote = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTableSubLote = new org.jdesktop.swingx.JXTable();
        jCheckBoxLote = new javax.swing.JCheckBox();
        jCheckBoxSublotes = new javax.swing.JCheckBox();
        jCheckBoxSeleccionarCampo = new javax.swing.JCheckBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(PanelSeleccionLotesSublotes.class);
        jXTableLote.setFont(resourceMap.getFont("jXTableLote.font")); // NOI18N
        jXTableLote.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableLote);

        jXTableSubLote.setFont(resourceMap.getFont("jXTableLote.font")); // NOI18N
        jXTableSubLote.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jXTableSubLote);

        jCheckBoxLote.setFont(resourceMap.getFont("jCheckBoxSublotes.font")); // NOI18N
        jCheckBoxLote.setText(resourceMap.getString("jCheckBoxLote.text")); // NOI18N
        jCheckBoxLote.setEnabled(false);
        jCheckBoxLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLoteActionPerformed(evt);
            }
        });

        jCheckBoxSublotes.setFont(resourceMap.getFont("jCheckBoxSublotes.font")); // NOI18N
        jCheckBoxSublotes.setText(resourceMap.getString("jCheckBoxSublotes.text")); // NOI18N
        jCheckBoxSublotes.setEnabled(false);
        jCheckBoxSublotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSublotesActionPerformed(evt);
            }
        });

        jCheckBoxSeleccionarCampo.setFont(resourceMap.getFont("jCheckBoxSeleccionarCampo.font")); // NOI18N
        jCheckBoxSeleccionarCampo.setText(resourceMap.getString("jCheckBoxSeleccionarCampo.text")); // NOI18N
        jCheckBoxSeleccionarCampo.setEnabled(false);
        jCheckBoxSeleccionarCampo.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jCheckBoxSeleccionarCampo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSeleccionarCampoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxSeleccionarCampo)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxLote)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxSublotes)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jCheckBoxSeleccionarCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxLote, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxSublotes, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    public boolean isTrabajo()
    {
        return seleccionLotesTableModel1 instanceof SeleccionSuperficieLotesTableModel && seleccionSubLotesTableModel1 instanceof SeleccionSuperficieSubLotesTableModel;
    }

    private void jCheckBoxLoteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxLoteActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxLoteActionPerformed
        if (jCheckBoxLote.isSelected())
        {
            seleccionLotesTableModel1.changeAll(true);
            seleccionSubLotesTableModel1.setData(seleccionLotesTableModel1.getSubLotesFromCheckedLotes());
            calcularSuperficieTotal();
        }
        else
        {
            seleccionLotesTableModel1.changeAll(false);
            seleccionSubLotesTableModel1.setData(new ArrayList<SubLote>());
            calcularSuperficieTotal();
        }
    }//GEN-LAST:event_jCheckBoxLoteActionPerformed

    private void jCheckBoxSublotesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxSublotesActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxSublotesActionPerformed
        if (jCheckBoxSublotes.isSelected())
        {
            seleccionSubLotesTableModel1.changeAll(true);
            calcularSuperficieTotal();
        }
        else
        {
            seleccionSubLotesTableModel1.changeAll(false);
            calcularSuperficieTotal();
        }
    }//GEN-LAST:event_jCheckBoxSublotesActionPerformed

    private void jCheckBoxSeleccionarCampoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxSeleccionarCampoActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxSeleccionarCampoActionPerformed
        if (jCheckBoxSeleccionarCampo.isSelected())
        {
            seleccionarTodoElCampo(true);
        }
        else
        {
            seleccionarTodoElCampo(false);
        }
}//GEN-LAST:event_jCheckBoxSeleccionarCampoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxLote;
    private javax.swing.JCheckBox jCheckBoxSeleccionarCampo;
    private javax.swing.JCheckBox jCheckBoxSublotes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable jXTableLote;
    private org.jdesktop.swingx.JXTable jXTableSubLote;
    private ar.com.init.agros.util.gui.ListableTableRenderer listableTableRenderer1;
    // End of variables declaration//GEN-END:variables

    /** Método que se encarga de añadir un listener de cambio de selección de superficie
     *  @param listener el objeto que depende del sujeto
     */
    public void addSuperficieListener(SuperficieListener listener)
    {
        if (listener == null)
        {
            return;
        }
        if (listenersList == null || listenersList.size() == 0)
        {
            listenersList = new ArrayList<SuperficieListener>();
        }
        listenersList.add(listener);
    }

    public boolean removeSuperficieListener(SuperficieListener listener)
    {
        if (listenersList == null || listenersList.size() == 0)
        {
            return false;
        }
        else
        {
            return listenersList.remove(listener);
        }
    }

    public void notifySuperficieListeners()
    {
        for (int i = 0; listenersList != null && i < listenersList.size(); i++)
        {
            if (listenersList.get(i) != null)
            {
                listenersList.get(i).updateSuperficie(superficieTotal);
            }
        }
    }

    private void setEditors()
    {
        jXTableLote.setDefaultEditor(ValorUnidad.class, new SuperficieTableCellEditor(frameNotifier, new DecimalInputVerifier(frameNotifier, false), SeleccionSuperficieLotesTableModel.LOTE_COLUMN_IDX, this));
        jXTableSubLote.setDefaultEditor(ValorUnidad.class, new SuperficieTableCellEditor(frameNotifier, new DecimalInputVerifier(frameNotifier, false), SeleccionSuperficieSubLotesTableModel.SUBLOTE_COLUMN_IDX, this));
    }

    public boolean seRepitenCultivos()
    {
        if (isTrabajo())
        {
            Set<Cultivo> cultivos = ((SeleccionSuperficieLotesTableModel)seleccionLotesTableModel1).getSelectedCultivos();
            cultivos.addAll(((SeleccionSuperficieSubLotesTableModel)seleccionSubLotesTableModel1).getSelectedCultivos());
            return (cultivos.size() > 1);
        }
        return false;
    }

    public void reloadSuperficies()
    {
        if (seleccionLotesTableModel1 instanceof SeleccionSuperficieLotesTableModel)
        {
            ((SeleccionSuperficieLotesTableModel)seleccionLotesTableModel1).reloadSuperficies();
        }
        if (seleccionSubLotesTableModel1 instanceof SeleccionSuperficieSubLotesTableModel)
        {
            ((SeleccionSuperficieSubLotesTableModel)seleccionSubLotesTableModel1).reloadSuperficies();
        }
    }

    private void removeTableListeners()
    {
        for (TableModelListener t : ((DefaultTableModel)jXTableLote.getModel()).getTableModelListeners())
        {
            jXTableLote.getModel().removeTableModelListener(t);
        }
        for (TableModelListener t : ((DefaultTableModel)jXTableSubLote.getModel()).getTableModelListeners())
        {
            jXTableSubLote.getModel().removeTableModelListener(t);
        }
    }
}