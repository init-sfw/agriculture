package ar.com.init.agros.model.inventario.cereales;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
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
@DiscriminatorValue(TipoMovimientoStock.CANCELACION_VALUE + "_" + TipoMovimientoStock.INGRESO_VALUE + "_C")
public class DetalleCancelacionIngresoCereal extends DetalleCancelacionIngresoGrano<ValorCereal, Silo, Cultivo> {

    private static final long serialVersionUID = -1L;

    protected DetalleCancelacionIngresoCereal() {
        super();

    }

    /** Constructor por defecto de DetalleCancelacionIngresoStock */
    public DetalleCancelacionIngresoCereal(DetalleIngresoCereal detalleIngreso) {
        super(detalleIngreso);
    }

    @Transient
    @Override
    public Cultivo getDetalle() {
        return (getValor() != null ? getValor().getCereal() : null);
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @Override
    public ValorCereal getValor() {
        return valor;
    }
}
