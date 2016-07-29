package ar.com.init.agros.view.reporting;

import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.CultivoJpaController;
import ar.com.init.agros.controller.VariedadCultivoJpaController;
import ar.com.init.agros.controller.almacenamiento.AlmacenamientoJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.reporting.components.ReportCriteria;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.list.DualList;
import java.awt.GridLayout;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author gmatheu
 */
public class ValoresAlmacenamientoReportCriteria extends JPanel implements ReportCriteria {

    private DualList<Almacenamiento> almacenamientos;
    private DualList<Agroquimico> agroquimicos;
    private DualList<VariedadCultivo> semillas;
    private DualList<Cultivo> cereales;
    private AlmacenamientoJpaController almacenamientoController;
    private AgroquimicoJpaController agroquimicoController;
    private VariedadCultivoJpaController variedadController;
    private CultivoJpaController cultivoController;

    public ValoresAlmacenamientoReportCriteria() {
        super(new GridLayout(1, 1));
        
        almacenamientos = new DualList<Almacenamiento>();
        almacenamientos.setTitle("Almacenamientos");

        try {
            almacenamientoController = new AlmacenamientoJpaController();
            almacenamientos.addAvailable(almacenamientoController.findEntities());
        } catch (PersistenceException e) {
            GUIUtility.logPersistenceError(ValoresAlmacenamientoReportCriteria.class, e);
        }

        agroquimicoController = new AgroquimicoJpaController();
        variedadController = new VariedadCultivoJpaController();
        cultivoController = new CultivoJpaController();

        agroquimicos = new DualList<Agroquimico>();
        agroquimicos.setTitle("Agroquímicos");
        semillas = new DualList<VariedadCultivo>();
        semillas.setTitle("Semillas");
        cereales = new DualList<Cultivo>();
        cereales.setTitle("Cereales");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);

        mainPanel.add(almacenamientos);
        mainPanel.add(agroquimicos);
        mainPanel.add(semillas);
        mainPanel.add(cereales);

        almacenamientos.addSelectedDataListener(new ListDataListener() {

            @Override
            @SuppressWarnings("unchecked")
            public void intervalAdded(ListDataEvent e) {
                List<Almacenamiento> alms = almacenamientos.getSelected();

                agroquimicos.addAvailable(agroquimicoController.findByAlmacenamiento(alms));
                semillas.addAvailable(variedadController.findByAlmacenamiento(alms));
                cereales.addAvailable(cultivoController.findByAlmacenamiento(alms));
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        });

        almacenamientos.addAvailableDataListener(new ListDataListener() {

            @Override
            @SuppressWarnings("unchecked")
            public void intervalAdded(ListDataEvent e) {
                List<Almacenamiento> alms = almacenamientos.getSelected();

                List<Agroquimico> agrosSelected = agroquimicos.getSelected();
                List<VariedadCultivo> semisSelected = semillas.getSelected();
                List<Cultivo> cerealesSelected = cereales.getSelected();

                List<Agroquimico> agrosAvailable = agroquimicoController.findByAlmacenamiento(alms);
                List<VariedadCultivo> semisAvailable = variedadController.findByAlmacenamiento(alms);
                List<Cultivo> cerealesAvailable = cultivoController.findByAlmacenamiento(alms);

                agrosSelected.retainAll(agrosAvailable);
                semisSelected.retainAll(semisAvailable);
                cerealesSelected.retainAll(cerealesAvailable);
                agroquimicos.clearSelected();
                semillas.clearSelected();
                cereales.clearSelected();
                agroquimicos.addSelected(agrosSelected);
                semillas.addSelected(semisSelected);
                cereales.addSelected(cerealesSelected);

                agroquimicos.addAvailable(agrosAvailable);
                semillas.addAvailable(semisAvailable);
                cereales.addAvailable(cerealesAvailable);
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        });
    }

    @Override
    public boolean validateSelection() {
        return agroquimicos.getSelected().size() > 0 || semillas.getSelected().size() > 0 || cereales.getSelected().size() > 0;
    }

    @Override
    public void clear() {
        almacenamientos.removeAllSelected();
        agroquimicos.removeAllSelected();
        semillas.removeAllSelected();
        cereales.removeAllSelected();
    }

    @Override
    public String getTabTitle() {
        return "Depositos - Agroquímicos - Semillas - Cereales";
    }

    @Override
    public String getErrorMessage() {
        return "Debe seleccionar por lo menos un Agroquímico, Semilla o Cereal";
    }

    public List<Agroquimico> getAgroquimicos() {
        return agroquimicos.getSelected();
    }

    public List<Almacenamiento> getAlmacenamientos() {
        return almacenamientos.getSelected();
    }

    public List<Cultivo> getCereales() {
        return cereales.getSelected();
    }

    public List<VariedadCultivo> getSemillas() {
        return semillas.getSelected();
    }
}
