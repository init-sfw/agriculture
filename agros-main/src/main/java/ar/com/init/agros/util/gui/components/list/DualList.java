/*
 * DualList.java
 *
 * Created on 19/06/2009, 00:37:55
 */
package ar.com.init.agros.util.gui.components.list;

import ar.com.init.agros.util.gui.Listable;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.event.ListDataListener;

/**
 *
 * @author gmatheu
 */
public class DualList<T extends Listable> extends javax.swing.JPanel {

    private static final long serialVersionUID = -1L;
    protected FrameNotifier frameNotifier;

    /** Creates new form DualList */
    public DualList() {
        initComponents();

        jXListAvailable.setModel(availableListModel);
        jXListSelected.setModel(selectedListModel);
    }

    /**
     * Devuelve todos los elementos seleccionados.
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> getSelected() {
        return (List<T>) Collections.list(selectedListModel.elements());
    }

    /**
     * Devuelve el elemento idx de la lista Seleccionados
     * @param idx indice del elemento a devolver
     * @return elemento de orden idx de la lista Seleccionados
     */
    @SuppressWarnings("unchecked")
    public T getSelected(int idx) {
        return (T) selectedListModel.getElementAt(idx);
    }

    /**
     * Devuelve todos los elementos Disponibles.
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public List<T> getAvailable() {
        return (List<T>) Collections.list(availableListModel.elements());
    }

    /**
     * Devuelve el elemento idx de la lista Disponibles
     * @param idx indice del elemento a devolver
     * @return elemento de orden idx de la lista Disponibles
     */
    @SuppressWarnings("unchecked")
    public T getAvailable(int idx) {
        return (T) availableListModel.getElementAt(idx);
    }

    /**
     * Remueve todos los elementos de los Seleccionados y los coloca en la lista Disponibles.
     */
    public void clearSelected() {
        addAvailable(getSelected());
        selectedListModel.clear();
    }

    /**
     * Remueve todos los elementos de los Disponibles
     */
    public void clearAvailable() {
        availableListModel.clear();
    }

    /**
     * Agrega elementos a la lista de Seleccionados, y remueve aquellos que tambien esten en la lista de Disponibles.
     * @param selected
     */
    public void addSelected(List<T> selected) {
        for (T l : selected) {
            if (!selectedListModel.contains(l)) {
                selectedListModel.addElement(l);
            }
        }
        removeSelectedFromAvailable();
    }

    /**
     * Agrega elementos a la lista de Disponibles, exceptuando aquellos que esten en la lista de Seleccionados
     * @param available
     */
    public void addAvailable(List<T> available) {
        addAvailable(available, true);
    }

    private void addAvailable(List<T> available, boolean removeSelected) {
        for (T l : available) {
            if (!availableListModel.contains(l)) {
                availableListModel.addElement(l);
            }
        }
        if (removeSelected) {
            removeSelectedFromAvailable();
        }
    }

    @SuppressWarnings("unchecked")
    private void removeSelectedFromAvailable() {
        Enumeration<T> en = (Enumeration<T>) selectedListModel.elements();

        while (en.hasMoreElements()) {
            Listable l = en.nextElement();
            availableListModel.removeElement(l);
        }
    }

    private void remove() {
        for (Object l : jXListSelected.getSelectedValues()) {
            selectedListModel.removeElement(l);
            availableListModel.addElement(l);
        }
    }

    private void add() {
        for (Object l : jXListAvailable.getSelectedValues()) {
            selectedListModel.addElement(l);
            availableListModel.removeElement(l);
        }
    }

    /**
     * Remueve los elementos de la lista de Disponibles. Sin colocarlos en la otra.
     *
     * @param list
     */
    public void deleteAvailable(List<T> list) {
        for (T l : list) {
            availableListModel.removeElement(l);
        }
    }

    /**
     * Remueve los elementos de la lista de Seleccionados. Sin colocarlos en la otra
     * @param list
     */
    public void deleteSelected(List<T> list) {
        for (T l : list) {
            selectedListModel.removeElement(l);
        }
    }

    /**
     * Mueve los Seleccionados a la lista de Disponibles.
     */
    public void removeAllSelected() {
        List<T> aux = getSelected();
        selectedListModel.clear();
        addAvailable(aux, false);
    }

    public void addSelectedDataListener(ListDataListener listDataListener) {
        selectedListModel.addListDataListener(listDataListener);
    }

    public void addAvailableDataListener(ListDataListener listDataListener) {
        availableListModel.addListDataListener(listDataListener);
    }

    public void removeAllAvailableDataListeners() {
        for (ListDataListener list : availableListModel.getListDataListeners()) {
            availableListModel.removeListDataListener(list);
        }
    }

    public FrameNotifier getFrameNotifier() {
        return frameNotifier;
    }

    public void setFrameNotifier(FrameNotifier frameNotifier) {
        this.frameNotifier = frameNotifier;
    }

    public void setTitle(String title) {
        setBorder(BorderFactory.createTitledBorder(title));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        availableListModel = new javax.swing.DefaultListModel();
        selectedListModel = new javax.swing.DefaultListModel();
        jScrollPaneAvailable = new javax.swing.JScrollPane();
        jXListAvailable = new org.jdesktop.swingx.JXList();
        jScrollPaneSelected = new javax.swing.JScrollPane();
        jXListSelected = new org.jdesktop.swingx.JXList();
        jButtonAdd = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonAddAll = new javax.swing.JButton();
        jButtonRemoveAll = new javax.swing.JButton();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DualList.class);
        jScrollPaneAvailable.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPaneAvailable.border.border.title")))); // NOI18N
        jScrollPaneAvailable.setName("jScrollPaneAvailable"); // NOI18N

        jXListAvailable.setName("jXListAvailable"); // NOI18N
        jXListAvailable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jXListAvailableMouseClicked(evt);
            }
        });
        jScrollPaneAvailable.setViewportView(jXListAvailable);

        jScrollPaneSelected.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPaneSelected.border.title"))); // NOI18N
        jScrollPaneSelected.setName("jScrollPaneSelected"); // NOI18N

        jXListSelected.setName("jXListSelected"); // NOI18N
        jXListSelected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jXListSelectedMouseClicked(evt);
            }
        });
        jScrollPaneSelected.setViewportView(jXListSelected);

        jButtonAdd.setText(resourceMap.getString("jButtonAdd.text")); // NOI18N
        jButtonAdd.setName("jButtonAdd"); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonRemove.setText(resourceMap.getString("jButtonRemove.text")); // NOI18N
        jButtonRemove.setName("jButtonRemove"); // NOI18N
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jButtonAddAll.setText(resourceMap.getString("jButtonAddAll.text")); // NOI18N
        jButtonAddAll.setName("jButtonAddAll"); // NOI18N
        jButtonAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAllActionPerformed(evt);
            }
        });

        jButtonRemoveAll.setText(resourceMap.getString("jButtonRemoveAll.text")); // NOI18N
        jButtonRemoveAll.setName("jButtonRemoveAll"); // NOI18N
        jButtonRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneAvailable, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRemoveAll, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(jButtonRemove, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(jButtonAddAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneSelected, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneSelected, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
            .addComponent(jScrollPaneAvailable, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddAll, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRemoveAll, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAdd, jButtonAddAll, jButtonRemove, jButtonRemoveAll});

    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAddActionPerformed
    {//GEN-HEADEREND:event_jButtonAddActionPerformed
        add();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRemoveActionPerformed
    {//GEN-HEADEREND:event_jButtonRemoveActionPerformed
        remove();
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonAddAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAddAllActionPerformed
    {//GEN-HEADEREND:event_jButtonAddAllActionPerformed
        List<T> available = getAvailable();
        availableListModel.clear();
        addSelected(available);
    }//GEN-LAST:event_jButtonAddAllActionPerformed

    private void jButtonRemoveAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRemoveAllActionPerformed
    {//GEN-HEADEREND:event_jButtonRemoveAllActionPerformed
        removeAllSelected();
    }//GEN-LAST:event_jButtonRemoveAllActionPerformed

    private void jXListAvailableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jXListAvailableMouseClicked
    {//GEN-HEADEREND:event_jXListAvailableMouseClicked
        if (evt.getClickCount() == 2) {
            add();
        }
    }//GEN-LAST:event_jXListAvailableMouseClicked

    private void jXListSelectedMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jXListSelectedMouseClicked
    {//GEN-HEADEREND:event_jXListSelectedMouseClicked
        if (evt.getClickCount() == 2) {
            remove();
        }
    }//GEN-LAST:event_jXListSelectedMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.DefaultListModel availableListModel;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAddAll;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonRemoveAll;
    private javax.swing.JScrollPane jScrollPaneAvailable;
    private javax.swing.JScrollPane jScrollPaneSelected;
    private org.jdesktop.swingx.JXList jXListAvailable;
    private org.jdesktop.swingx.JXList jXListSelected;
    private javax.swing.DefaultListModel selectedListModel;
    // End of variables declaration//GEN-END:variables
}
