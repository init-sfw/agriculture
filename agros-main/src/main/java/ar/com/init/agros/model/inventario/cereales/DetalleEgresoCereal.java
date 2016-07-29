package ar.com.init.agros.model.inventario.cereales;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.granos.DetalleEgresoGrano;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleIngresoStock
 *
 *
 * @author gmatheu
 * @version 29/06/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.EGRESO_VALUE + "_C")
public class DetalleEgresoCereal extends DetalleEgresoGrano<ValorCereal, Silo, Cultivo> {

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DetalleIngresoStock */
    public DetalleEgresoCereal() {
        super();

    }

    @Override
    public void setServicio(Servicio servicio) {
        if (servicio != null && !servicio.getTipo().equals(TipoServicio.COMPRADOR)) {
            throw new IllegalArgumentException("Sólo soporta servicios de tipo COMPRADOR");
        }
        super.setServicio(servicio);
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
