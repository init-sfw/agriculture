package ar.com.init.agros.model.inventario.semillas;

import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
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
@DiscriminatorValue(TipoMovimientoStock.INGRESO_VALUE + "_S")
public class DetalleIngresoSemilla extends DetalleIngresoGrano<ValorSemilla, Almacenamiento, VariedadCultivo> {

    private static final long serialVersionUID = -1L;   

    /** Constructor por defecto de DetalleIngresoSemilla */
    public DetalleIngresoSemilla() {
        super();       
    }

    @Transient
    @Override
    public VariedadCultivo getDetalle() {
        return (getValor() != null ? getValor().getSemilla() : null);
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
    public ValorSemilla getValor() {
        return valor;
    }


}
