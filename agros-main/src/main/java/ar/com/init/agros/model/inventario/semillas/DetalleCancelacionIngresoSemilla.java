package ar.com.init.agros.model.inventario.semillas;

import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.granos.DetalleCancelacionIngresoGrano;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleCancelacionIngresoStock
 *
 *
 * @author gmatheu
 * @version 24/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.CANCELACION_VALUE + "_" + TipoMovimientoStock.INGRESO_VALUE + "_S")
public class DetalleCancelacionIngresoSemilla
        extends DetalleCancelacionIngresoGrano<ValorSemilla, Almacenamiento, VariedadCultivo> {

    private static final long serialVersionUID = -1L;

    protected DetalleCancelacionIngresoSemilla() {
        super();
    }

    /** Constructor por defecto de DetalleCancelacionIngresoStock */
    public DetalleCancelacionIngresoSemilla(DetalleIngresoSemilla detalleIngreso) {
        super(detalleIngreso);
    }

    @Transient
    @Override
    public VariedadCultivo getDetalle() {
        return (getValor() != null ? getValor().getSemilla() : null);
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @Override
    public ValorSemilla getValor() {
        return valor;
    }
}
