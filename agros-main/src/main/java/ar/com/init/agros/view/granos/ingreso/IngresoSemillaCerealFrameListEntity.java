package ar.com.init.agros.view.granos.ingreso;

import ar.com.init.agros.controller.inventario.granos.MovimientoGranoJpaController;
import ar.com.init.agros.model.inventario.cereales.DetalleCancelacionIngresoCereal;
import ar.com.init.agros.model.inventario.cereales.DetalleIngresoCereal;
import ar.com.init.agros.model.inventario.granos.CancelacionIngresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleCancelacionIngresoGrano;
import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
import ar.com.init.agros.model.inventario.granos.IngresoGrano;
import ar.com.init.agros.model.inventario.granos.MovimientoGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleCancelacionIngresoSemilla;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
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
 * Clase IngresoSemillaCerealFrameListEntity
 *
 *
 * @author fbobbio
 * @version 13-dic-2010 
 */
public class IngresoSemillaCerealFrameListEntity extends FrameListEntity<MovimientoGrano>
{
    /** Constructor por defecto de IngresoStockFrameListEntity */
    public IngresoSemillaCerealFrameListEntity(ResourceMap map)
    {
        super(MovimientoGrano.class,new MovimientoGranoJpaController(IngresoGrano.class),MovimientoGrano.TABLE_HEADERS);
        setResourceMap(map);
        getBtnModificar().setText("Cancelar");
        setVisibleButtons(true, false, true, true, false);

    }

    @Override
    public void list(MovimientoGrano entity)
    {
        FrameIngresoSemillaCereal frame;
        MovimientoGrano p = getSelectedEntities().get(0);
        if (p != null) {
            frame = new FrameIngresoSemillaCereal((IngresoGrano)p);
            frame.setTitle(getResourceMap().getString("ingreso.semcer.consulta.title"));
            frame.setVisible(true);
        }
        else {
            showErrorMessage("No se ha seleccionado un ingreso para consultar");
        }
    }
    private FrameIngresoSemillaCereal dialog;

    @Override
    public void newEntity()
    {
        if (dialog == null) {
            dialog = new FrameIngresoSemillaCereal();
            dialog.setTitle(getResourceMap().getString("ingreso.semcer.alta.title"));
        }
        dialog.setVisible(true);
    }

    @Override
    public void update(MovimientoGrano entity)
    {
        if (askCancel())
        {
            CancelacionIngresoGrano cancel = new CancelacionIngresoGrano();
            cancel.setFecha(entity.getFechaCreacion());
            cancel.setDetalles(createDetallesCancelacion(entity.getDetalles()));
            try
            {
                MovimientoGranoJpaController cancController = new MovimientoGranoJpaController();
                cancController.persist(cancel);

                showInformationMessage("Se canceló el ingreso con fecha: " + GUIUtility.toMediumDate(cancel.getFecha()));
            }
            catch (InvalidStateException ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(IngresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (ConstraintViolationException ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(IngresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex) {
                showErrorMessage(ex.getLocalizedMessage());
                Logger.getLogger(IngresoSemillaCerealFrameListEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private List<DetalleCancelacionIngresoGrano> createDetallesCancelacion(List<DetalleIngresoGrano> detalles)
    {
        List<DetalleCancelacionIngresoGrano> ret = new ArrayList<DetalleCancelacionIngresoGrano>(detalles.size());
        for (DetalleIngresoGrano d : detalles)
        {
            if (d instanceof DetalleIngresoSemilla)
            {
                ret.add(new DetalleCancelacionIngresoSemilla((DetalleIngresoSemilla) d));
            }
            if (d instanceof DetalleIngresoCereal)
            {
                ret.add(new DetalleCancelacionIngresoCereal((DetalleIngresoCereal) d));
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
