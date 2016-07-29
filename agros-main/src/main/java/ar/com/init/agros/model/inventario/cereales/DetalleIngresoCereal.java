package ar.com.init.agros.model.inventario.cereales;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.granos.DetalleIngresoGrano;
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
@DiscriminatorValue(TipoMovimientoStock.INGRESO_VALUE + "_C")
public class DetalleIngresoCereal extends DetalleIngresoGrano<ValorCereal, Silo, Cultivo>  {

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DetalleIngresoCereal */
    public DetalleIngresoCereal() {
        super();
    }

    @Transient
    @Override
    public Cultivo getDetalle() {
        return (getValor() != null ? getValor().getCereal() : null);
    }

    @Override
    public void setServicio(Servicio servicio) {
        if (servicio != null && !servicio.getTipo().equals(TipoServicio.PROVEEDOR_INSUMOS)) {
            throw new IllegalArgumentException("Sólo soporta servicios de tipo PROVEEDOR DE INSUMOS");
        }
        super.setServicio(servicio);
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @Override
    public ValorCereal getValor() {
        return valor;
    }


}
