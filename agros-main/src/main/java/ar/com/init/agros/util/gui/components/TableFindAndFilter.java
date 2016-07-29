/*
 * TableFindAndFilter.java
 *
 * Created on 1 de febrero de 2008, 22:37
 */
package ar.com.init.agros.util.gui.components;

import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

/**
 *
 * @author fbobbio
 */
public class TableFindAndFilter extends javax.swing.JPanel
{
    private FrameNotifier frameNotifier;
    private JXTable table;
    private Color notFoundBackgroundColor = Color.decode("#FF6666");
    private Color notFoundForegroundColor = Color.white;

    //PENDING: agregar la posibilidad de que distinga el tipo de dato de la columna y ofrezca la posibilidad de seleccionar los posibles valores en un combo o algo por el estilo

    /** Creates new form TableFindAndFilter */
    public TableFindAndFilter()
    {
        initComponents();
    }

    /** Creates new form TableFindAndFilter
     * @param pTable
     * @param fn 
     */
    public TableFindAndFilter(JXTable pTable, FrameNotifier fn)
    {
        initComponents();
        table = pTable;
        frameNotifier = fn;
        
        if (table != null)
        {
            /** Manejo del evento de agregar y quitar columnas mediante el column control */
            table.getColumnModel().addColumnModelListener(new TableColumnModelListener()
            {

                @Override
                public void columnAdded(TableColumnModelEvent e)
                {
                    refreshColumns();
                }

                @Override
                public void columnRemoved(TableColumnModelEvent e)
                {
                    refreshColumns();
                }

                @Override
                public void columnMoved(TableColumnModelEvent e)
                {
                }

                @Override
                public void columnMarginChanged(ChangeEvent e)
                {
                }

                @Override
                public void columnSelectionChanged(ListSelectionEvent e)
                {
                }
            });
        }
    }

    /** M�todo que se encarga de mostrar la barra de b�squeda */
    public void showFindBar()
    {
        refreshColumns();
        this.setVisible(true);
        this.requestFocus();
    }

    public void refreshColumns()
    {
        if (table != null)
        {
            List<TableColumn> list = new ArrayList<TableColumn>();
            for (TableColumn t : table.getColumns())
            {
                String name = table.getModel().getColumnClass(t.getModelIndex()).getName();
                if (!name.equals(Boolean.class.getName()))
                {
                    list.add(t);
                }
            }
            Object[] a = list.toArray();
            cbxEn.setModel(new DefaultComboBoxModel(a));
        }
    }

    public JXTable getTable()
    {
        return table;
    }

    public void setTable(JXTable table)
    {
        this.table = table;
    }

    public void clean()
    {
        txtBuscar.setText("");
        cbxEn.setSelectedIndex(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findAndFilterTableComboBoxRenderer1 = new ar.com.init.agros.util.gui.components.FindAndFilterTableComboBoxRenderer();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblEn = new javax.swing.JLabel();
        cbxEn = new javax.swing.JComboBox();

        findAndFilterTableComboBoxRenderer1.setText("findAndFilterTableComboBoxRenderer1");

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblBuscar.setFont(new java.awt.Font("Verdana", 0, 11));
        lblBuscar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBuscar.setText("Buscar:");

        txtBuscar.setFont(new java.awt.Font("Verdana", 0, 11));
        txtBuscar.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtBuscar.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBuscarCaretUpdate(evt);
            }
        });
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBuscarFocusLost(evt);
            }
        });

        lblEn.setFont(new java.awt.Font("Verdana", 0, 11));
        lblEn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEn.setText("En:");

        cbxEn.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        cbxEn.setRenderer(findAndFilterTableComboBoxRenderer1);
        cbxEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxEnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxEn, 0, 94, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(lblEn)
                    .addComponent(cbxEn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBuscarCaretUpdate
        filter();
    }//GEN-LAST:event_txtBuscarCaretUpdate

    public void filter()
    {
        int col = ((TableColumn) cbxEn.getSelectedItem()).getModelIndex();
        String text = txtBuscar.getText().trim();
        if (text == null || text.equals(""))
        {
            text = ".";
        }
        String regEx = text;
        PatternFilter pf = new PatternFilter(regEx, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE, col);
        FilterPipeline fp = new FilterPipeline(pf);
        table.setFilters(fp);
        int cantFiltradas = table.getModel().getRowCount() - table.getRowCount();
        if (cantFiltradas == table.getModel().getRowCount())
        {
            txtBuscar.setBackground(notFoundBackgroundColor);
            txtBuscar.setForeground(notFoundForegroundColor);
            Toolkit.getDefaultToolkit().beep();
        }
        else
        {
            txtBuscar.setBackground(Color.WHITE);
            txtBuscar.setForeground(Color.BLACK);
        }
        if (!text.equals("."))
        {
            frameNotifier.showWarningMessage("Aplicando filtro... Hay " + cantFiltradas + " filas sin mostrar");
        }
        else
        {
            frameNotifier.showOkMessage();
        }
    }
    private void txtBuscarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBuscarFocusLost
        if (frameNotifier.getCurrentState() != FrameNotifier.OK_MESSAGE)
        {
            frameNotifier.showOkMessage();
        }
    }//GEN-LAST:event_txtBuscarFocusLost

    private void cbxEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxEnActionPerformed
        filter();
    }//GEN-LAST:event_cbxEnActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbxEn;
    private ar.com.init.agros.util.gui.components.FindAndFilterTableComboBoxRenderer findAndFilterTableComboBoxRenderer1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblEn;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}