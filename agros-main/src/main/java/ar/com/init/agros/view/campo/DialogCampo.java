/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.view.campo;

import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.controller.almacenamiento.SiloJpaController;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.util.gui.validation.components.FrameNotifier;
import ar.com.init.agros.view.campo.exceptions.LotesAsociadosALluviasException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ar.com.init.agros.util.gui.AbstractEventControl;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.updateui.UpdatableListener;
import ar.com.init.agros.util.gui.updateui.UpdatableSubject;
import ar.com.init.agros.view.campo.model.AlmacenamientoTableModel;
import ar.com.init.agros.view.campo.model.LoteTableModel;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.application.ResourceMap;

/*
 * Clase GUI DialogCampo
 *
 * @author fbobbio
 * @version 04-jul-2009
 *
 *  //TODO: ocultar los botones de LIMPIAR cuando se llame a una modificación, o inhabilitarlos,
 *  // para que no haya problemas con los lotes asociados a lluvias. Y deshabilitar el botón de borrado según el caso.
 */
public class DialogCampo extends javax.swing.JDialog implements
		UpdatableListener {

	private static final long serialVersionUID = -1L;
	private ResourceMap resourceMap;
	private CampoJpaController campoJpaController;
	private Campo campo;
	/**
	 * Variable que se utilizará para desactivar el campo viejo cuando haya sido
	 * modificada teniendo transacciones cerradas
	 */
	private Campo oldCampo;
	private Frame parent;
	private String successMessage = "registró";
	private LoteTableModel loteTableModel1;
        private AlmacenamientoTableModel almacenamientoTableModel;
	private boolean update;

	/** Creates new form DialogCampo */
	public DialogCampo(java.awt.Frame parent, ResourceMap map) {
		this(parent, null, true, map);
	}

	/**
	 * Creates new form DialogCampo
	 * 
	 * @param parent
	 * @param campo
	 *            la instancia del campo a modificar/consultar
	 * @param isUpdate
	 *            true si se quiere utilizar para modificar, false para
	 *            consultar
	 */
	public DialogCampo(java.awt.Frame parent, Campo campo, boolean isUpdate,
			ResourceMap map) {
		super(parent, true);
		this.loteTableModel1 = new LoteTableModel();
		this.parent = parent;
		this.resourceMap = map;
		UpdatableSubject.addUpdatableListener(this);
		GUIUtility.initWindow(this);
		initComponents();
                almacenamientoTableModel = new AlmacenamientoTableModel();
                jXTableAlmacenamientos.setModel(almacenamientoTableModel);
                fillAlmacenamientos();
		jXTableLotes.setModel(loteTableModel1);
		campoJpaController = new CampoJpaController();
		refreshUI();
		panelValorUnidadSuperficie
				.addUnidades(MagnitudEnum.SUPERFICIE.patron());
		panelValorUnidadSuperficie.selectUnidad(MagnitudEnum.SUPERFICIE
				.patron());
		panelValorUnidadSuperficie.setEnabled(true, false);
		panelValorUnidadSuperficie.setFrameNotifier(frameNotifier1);

		this.update = isUpdate;
		if (campo != null) // Caso en el que no será utilizada como ventana de alta
		{
			setCampo(campo);
			if (!isUpdate) // Caso en el que será de consulta
			{
				disableFieldsAndButtons();
			} else
                        {
				successMessage = "modificó";
                                // No permito que se llame al método de limpiado ni de la tabla ni de la ventana general
                                addRemoveUpdatePanel1.getJButtonClean().setToolTipText("No se permite limpiar lotes en la modificación por si están asociados a lluvias");
                                addRemoveUpdatePanel1.getJButtonClean().setEnabled(false);
                                oKCancelCleanPanel1.getBtnClean().setToolTipText("No se permite limpiar lotes en la modificación por si están asociados a lluvias");
                                oKCancelCleanPanel1.getBtnClean().setEnabled(false);
			}
		}

		oKCancelCleanPanel1.setListenerToButtons(new EventControl());
		oKCancelCleanPanel1.setOwner(this);

		addRemoveUpdatePanel1.setVisible(true, true, true, true);
		addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
		addRemoveUpdatePanel1.addActionListener(new AddRemoveEventControl());

		jXTableLotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jXTableLotes.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (jXTableLotes.getSelectedRow() > -1)
                                                {
                                                    List<Lote> lotesSelecc = loteTableModel1.getSelectedLotesFromIndexes(GUIUtility.getModelSelectedRows(jXTableLotes));
                                                    //Para permitir el borrado checkeo que ningún lote tenga lluvias asociadas
                                                    if (checkLluviasAsociadas(lotesSelecc))
                                                    {
                                                        addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
                                                        frameNotifier1.showWarningMessage("No se pueden borrar lotes que tengan lluvias asociadas");
                                                    }
                                                    else
                                                    {
                                                        addRemoveUpdatePanel1.setBtnRemoveEnabled(true);
                                                        frameNotifier1.showOkMessage();
                                                    }
                                                    if (jXTableLotes.getSelectedRowCount() == 1)
                                                    {
                                                            addRemoveUpdatePanel1.setBtnUpdateEnabled(true);
                                                    }
                                                    else
                                                    {
                                                            addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
                                                    }
						} else {
							addRemoveUpdatePanel1.setBtnRemoveEnabled(false);
							addRemoveUpdatePanel1.setBtnUpdateEnabled(false);
						}
					}
				});
		jXTableLotes.packAll();
	}

        private boolean checkLluviasAsociadas(List<Lote> lotesSelecc)
        {
            for (Lote l : lotesSelecc)
            {
                if (l.getLluvias() != null && l.getLluvias().size() > 0)
                    return true;
            }
            return false;
        }

	public Campo getCampo() {
		if (campo == null) {
			campo = new Campo();
		}
		campo.setNombre(jTextFieldNombre.getText().trim());
		campo.setCantidadLote(loteTableModel1.getData().size());
		campo.setLotes(loteTableModel1.getData());
		campo.setSuperficie(panelValorUnidadSuperficie.getValorUnidad());
		campo.setAlmacenamientos(new HashSet<Almacenamiento>(getSelectedAlmacenamientos()));
		return campo;
	}

	public void setCampo(Campo campo) {
		jTextFieldNombre.setText(campo.getNombre());
		loteTableModel1.setData(campo.getLotes());
		panelValorUnidadSuperficie.setValorUnidad(campo.getSuperficie());
		almacenamientoTableModel.setAlmacenamientos(almacenamientoTableModel.getAlmacenamientos(), campo.getAlmacenamientosAsList());

		this.campo = campo;
	}

	private void disableFieldsAndButtons() {
		jTextFieldNombre.setEditable(false);
		panelValorUnidadSuperficie.setEnabled(false, false);
		addRemoveUpdatePanel1.setEnabled(false);
		addRemoveUpdatePanel1.setVisible(false);
		oKCancelCleanPanel1.disableForList();
		jXTableLotes.setEnabled(false);
                jXTableAlmacenamientos.setEnabled(false);
	}

	private List<Almacenamiento> getSelectedAlmacenamientos()
        {
            return almacenamientoTableModel.getCheckedAlmacenamientos();
	}

	public Campo getOldCampo() {
		return oldCampo;
	}

	public void setOldCampo(Campo oldCampo) {
		this.oldCampo = oldCampo;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listableComboBoxRenderer1 = new ar.com.init.agros.util.gui.ListableComboBoxRenderer();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTableAlmacenamientos = new org.jdesktop.swingx.JXTable();
        frameNotifier1 = new ar.com.init.agros.util.gui.validation.components.FrameNotifier();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        panelValorUnidadSuperficie = new ar.com.init.agros.view.components.valores.PanelValorUnidad();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTableLotes = new org.jdesktop.swingx.JXTable();
        addRemoveUpdatePanel1 = new ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel();
        oKCancelCleanPanel1 = new ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel();
        jLabel3 = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ar.com.init.agros.view.Application.class).getContext().getResourceMap(DialogCampo.class);
        listableComboBoxRenderer1.setText(resourceMap.getString("listableComboBoxRenderer1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane2.setViewportView(jXTableAlmacenamientos);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N

        jXTableLotes.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTableLotes);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
                    .add(addRemoveUpdatePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(addRemoveUpdatePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setForeground(resourceMap.getColor("jLabel3.foreground")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(frameNotifier1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(47, 47, 47)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelValorUnidadSuperficie, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                            .add(jTextFieldNombre, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(frameNotifier1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldNombre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(panelValorUnidadSuperficie, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oKCancelCleanPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	@Override
	public void refreshUI() 
        {
            fillAlmacenamientos();
	}

	@Override
	public boolean isNowVisible() {
		return this.isVisible();
	}

	public void setController(CampoJpaController jpaController) {
		this.campoJpaController = jpaController;
	}

    private void fillAlmacenamientos()
    {
        List<Almacenamiento> alms = new ArrayList<Almacenamiento>();
        SiloJpaController siloController = new SiloJpaController();
        DepositoJpaController depositoJpaController = new DepositoJpaController();
        alms.addAll(siloController.findEntities());
        alms.addAll(depositoJpaController.findEntities());
        almacenamientoTableModel.setAlmacenamientos(alms);
    }

	/**
	 * Clase de control de eventos que maneja los eventos de la GUI DialogCampo
	 * y las validaciones de la misma
	 */
	private class EventControl extends AbstractEventControl implements
			ActionListener {

		/**
		 * Método que maneja los eventos de la GUI DialogCampo
		 * 
		 * @param e el evento de acción lanzado por algún componente de la GUI
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == oKCancelCleanPanel1.getBtnCancelar()) {
				if (DialogCampo.this.update) {
					for (Lote lote : getCampo().getLotes()) {
						campoJpaController.refreshEntity(lote);
					}
				}

				closeWindow(DialogCampo.this);
			}
			if (e.getSource() == oKCancelCleanPanel1.getBtnClean())
                        {
                            clear();
                            frameNotifier1.showOkMessage();
			}
			if (e.getSource() == oKCancelCleanPanel1.getBtnAceptar()) {
				if (validateInput(getCampo()) && checkSuperficie()) {
					try {
						if (oldCampo == null) // si es un campo modificado sin
												// transacciones o uno nuevo
						{
							if (!campoJpaController.isNombreRepetido(campo)) {
								campoJpaController.persistOrUpdate(campo);
							} else {
								frameNotifier1
										.showErrorMessage("Ya existe un establecimiento con el nombre "
												+ campo.getNombre());
								return;
							}
						} else {
							campoJpaController.modifyCampo(oldCampo, campo);
						}
						frameNotifier1.showInformationMessage("Se "
								+ successMessage
								+ " con éxito el establecimiento "
								+ campo.getNombre());
						clear();
					} catch (LotesAsociadosALluviasException ex)
                                        {
                                            frameNotifier1.showWarningMessage(ex.getMessage());
                                        } catch (ConstraintViolationException ex) {
						frameNotifier1
								.showErrorMessage("Ya existe un establecimiento con el nombre "
										+ campo.getNombre());
					} catch (InvalidStateException ex) {
						frameNotifier1.showErrorMessage(ex.getMessage());
						Logger.getLogger(DialogCampo.class.getName()).log(
								Level.SEVERE, null, ex);
					} catch (Exception ex) {
						frameNotifier1.showErrorMessage(ex.getMessage());
						Logger.getLogger(DialogCampo.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}
			}
		}

		@Override
		public FrameNotifier getFrameNotifier() {
			return frameNotifier1;
		}
	}

	private boolean checkSuperficie() {
		if (!campo.isSuperficieConsistente()) {
			frameNotifier1
					.showErrorMessage("La superficie del establecimiento debe ser IGUAL a la suma de los lotes\nSuperficie del establecimiento: "
							+ campo.getSuperficie().getFormattedValue()
							+ "\nSuperficie total de lotes: "
							+ campo.calcularSuperficie().getFormattedValue());
			return false;
		}
		return true;
	}

	public void clear()
        {
            loteTableModel1.removeAllEntities();
            jTextFieldNombre.setText("");
            panelValorUnidadSuperficie.clear(true, false);
            almacenamientoTableModel.changeAll(false);
            campo = null;
	}

	private class AddRemoveEventControl extends AbstractEventControl {

		private DialogLote dialogLote;

		@Override
		public FrameNotifier getFrameNotifier() {
			return frameNotifier1;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addRemoveUpdatePanel1.getJButtonAdd()) {
				dialogLote = new DialogLote(parent, getCampo());
				dialogLote.setTitle(resourceMap.getString("lote.alta.title"));
				dialogLote.setVisible(true);
				Lote l = dialogLote.getLoteCargado();
				if (l != null && !exists(l)) {
					loteTableModel1.add(l);
					frameNotifier1.showInformationMessage("Se cargó el lote "
							+ l.toString());
				}
				dialogLote.dispose();
			} else if (e.getSource() == addRemoveUpdatePanel1
					.getJButtonRemove()) {
                            try
                            {
                                //Mando a borrar los lotes seleccionados
				loteTableModel1.borrarLotes(loteTableModel1.getSelectedLotesFromIndexes(GUIUtility
						.getModelSelectedRows(jXTableLotes)));
				frameNotifier1.showInformationMessage("Lotes eliminados");
                            }
                            catch (LotesAsociadosALluviasException ex)
                            {
                                frameNotifier1.showWarningMessage(ex.getMessage());
                            }
			} else if (e.getSource() == addRemoveUpdatePanel1.getJButtonClean()) 
                        {
                            loteTableModel1.removeAllEntities();
			} else if (e.getSource() == addRemoveUpdatePanel1
					.getJButtonUpdate()) {
				Lote selected = loteTableModel1.getData().get(
						jXTableLotes.convertRowIndexToModel(jXTableLotes
								.getSelectedRow()));
				dialogLote = new DialogLote(parent, selected, true, getCampo());
				dialogLote.setController(campoJpaController);
				dialogLote.setTitle(resourceMap
						.getString("lote.modificacion.title"));
				dialogLote.setVisible(true);
				Lote l = dialogLote.getLoteCargado();
				if (l != null) {
					loteTableModel1.remove(selected);
					if (!exists(l)) {
						loteTableModel1.add(l);
						frameNotifier1
								.showInformationMessage("Se cargó el lote "
										+ l.toString());
					} else {
						campoJpaController.refreshEntity(l);
						loteTableModel1.add(selected);
					}
				}
				dialogLote.dispose();
			}
		}

		private boolean exists(Lote l) {
			boolean existe = false;
			for (Lote lot : loteTableModel1.getData()) {
				if (lot.getNombre().equalsIgnoreCase(l.getNombre())) {
					existe = true;
					frameNotifier1
							.showErrorMessage("Ya existe un lote con el nombre "
									+ l.getNombre()
									+ " para el establecimiento");
					break;
				}
			}
			if (!existe && isSuperficieTotalSuperada(l)) {
				existe = true;
				frameNotifier1
						.showErrorMessage("La sumatoria de superficies de los lotes no puede superar la superficie del establecimiento");
			}
			return existe;
		}
	}

	/**
	 * Método que verifica si la sumatoria de superficies de los sublotes supera
	 * la del lote
	 */
	private boolean isSuperficieTotalSuperada(Lote toAdd) {
		Campo c = getCampo();
		double acum = 0;
		for (Lote l : loteTableModel1.getData()) {
			acum += l.getSuperficie().getValor();
		}
		acum += toAdd.getSuperficie().getValor();
		if (c.getSuperficie() == null || acum > c.getSuperficie().getValor()) {
			return true;
		} else {
			return false;
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ar.com.init.agros.util.gui.components.buttons.AddRemoveUpdatePanel addRemoveUpdatePanel1;
    private ar.com.init.agros.util.gui.validation.components.FrameNotifier frameNotifier1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldNombre;
    private org.jdesktop.swingx.JXTable jXTableAlmacenamientos;
    private org.jdesktop.swingx.JXTable jXTableLotes;
    private ar.com.init.agros.util.gui.ListableComboBoxRenderer listableComboBoxRenderer1;
    private ar.com.init.agros.util.gui.components.buttons.OKCancelCleanPanel oKCancelCleanPanel1;
    private ar.com.init.agros.view.components.valores.PanelValorUnidad panelValorUnidadSuperficie;
    // End of variables declaration//GEN-END:variables
}
