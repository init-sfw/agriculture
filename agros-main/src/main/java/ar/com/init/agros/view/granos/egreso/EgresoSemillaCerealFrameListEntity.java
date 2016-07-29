package ar.com.init.agros.view.granos.egreso;

import ar.com.init.agros.controller.inventario.granos.MovimientoGranoJpaController;
import ar.com.init.agros.model.inventario.cereales.DetalleCancelacionEgresoCereal;
import ar.com.init.agros.model.inventario.cereales.DetalleEgresoCereal;
import ar.com.init.agros.model.inventario.granos.CancelacionEgresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleCancelacionEgresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.model.inventario.granos.EgresoGrano;
import ar.com.init.agros.model.inventario.granos.MovimientoGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleCancelacionEgresoSemilla;
import ar.com.init.agros.model.inventario.semillas.DetalleEgresoSemilla;
import ar.com.init.agros.util.gui.GUIUtility;
import ar.com.init.agros.util.gui.components.FrameListEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.jdesktop.application.ResourceMap;

/**
 * Clase EgresoSemillaCerealFrameListEntity
 *
 *
 * @author fbobbio
 * @version 15-dic-2010 
 */
public class EgresoSemillaCerealFrameListEntity extends FrameListEntity<MovimientoGrano>
{
    /** Constructor por defecto de IngresoStockFrameListEntity */
    public EgresoSemillaCerealFrameListEntity(ResourceMap map)
    {
        super(MovimientoGrano.class,new MovimientoGranoJpaController(EgresoGrano.class),EgresoGrano.TABLE_HEADERS_EGRESO);
        setResourceMap(map);
        btnModificar.setText("Cancelar");
        setVisibleButtons(true, false, true, true, false);
    }

    @Override
    public void list(MovimientoGrano entity)
    {
        FrameEgresoSemillaCereal frame;
        MovimientoGrano p = getSelectedEntities().get(0);
        if (p != null) {
            frame = new FrameEgresoSemillaCereal((EgresoGrano)p);
            frame.setTitle(getResourceMap().getString("egreso.semcer.consulta.title"));
            frame.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un egreso para consultar");
        }
    }
    private FrameEgresoSemillaCereal dialog;

    @Override
    public void newEntity()
    {
        if (dialog == null) {
            dialog = new FrameEgresoSemillaCereal();
            dialog.setTitle(getResourceMap().getString("egreso.semcer.alta.title"));
        }
        dialog.setVisible(true);
    }

    @Override
    public void update(MovimientoGrano entity)
    {
        if (askCancel())
        {
            CancelacionEgresoGrano cancel = new CancelacionEgresoGrano();
            cancel.setFecha(entity.getFechaCreacion());
            cancel.setDetalles(createDetallesCancelacion(entity.getDetalles()));
            try
            {
                MovimientoGranoJpaController cancController = new MovimientoGranoJpaController();
                cancController.persist(cancel);

                showInformationMessage("Se canceló el egreso con fecha: " + GUIUtility.toMediumDate(cancel.getFecha()));
            }
            catch (InvalidStateException ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(EgresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (ConstraintViolationException ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(EgresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(EgresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private List<DetalleCancelacionEgresoGrano> createDetallesCancelacion(List<DetalleEgresoGrano> detalles)
    {
        List<DetalleCancelacionEgresoGrano> ret = new ArrayList<DetalleCancelacionEgresoGrano>(detalles.size());
        for (DetalleEgresoGrano d : detalles)
        {
            if (d instanceof DetalleEgresoSemilla)
            {
                ret.add(new DetalleCancelacionEgresoSemilla((DetalleEgresoSemilla) d));
            }
            if (d instanceof DetalleEgresoCereal)
            {
                ret.add(new DetalleCancelacionEgresoCereal((DetalleEgresoCereal) d));
            }
        }
        return ret;
    }

    private boolean askCancel()
    {
        int ret =
                GUIUtility.askQuestionMessage(this, "Cancelando Ingreso",
                "¿Está seguro que desea cancelar el ingreso seleccionado?");
        if (ret == JOptionPane.YES_OPTION) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        refreshTable();
    }
}
