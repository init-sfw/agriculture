package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.base.TablizableEntity;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.util.gui.Listable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase ValorAlmacenamiento
 * Esta clase contiene los valores de inventario actual genericos.
 * Para evitar tener que recalcular los valores acutales cada vez que se necesiten.
 *
 * @author gmatheu
 * @version 07/12/2010
 */
@MappedSuperclass
public abstract class ValorAlmacenamiento<T extends Almacenamiento> extends TablizableEntity implements Listable {

    private static final long serialVersionUID = -1L;
    private ValorUnidad stockActual;
    private T almacenamiento;
    private ValorUnidad stockMinimo;

    /** Constructor por defecto de ValorAlmacenamiento */
    public ValorAlmacenamiento() {
        super();
    }

    public ValorAlmacenamiento(T almacenamiento, UnidadMedida unidad) {
        super();
        this.almacenamiento = almacenamiento;
        stockActual = new ValorUnidad(0D, unidad);
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column =
        @Column(name = "valorStockMin"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns =
        @JoinColumn(name = "unidadStockMin_id"))
    })
    public ValorUnidad getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(ValorUnidad stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    @NotNull
    @ManyToOne(targetEntity = Almacenamiento.class)
    public T getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(T almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

//    @Embedded
//    @AttributeOverrides({
//        @AttributeOverride(name = "valor", column =
//        @Column(name = "valorStockActual"))
//    })
//    @AssociationOverrides({
//        @AssociationOverride(name = "unidad", joinColumns =
//        @JoinColumn(name = "unidadStockActual_id"))
//    })
    @Transient
    public ValorUnidad getStockActual() {
        return stockActual;
    }

    public void setStockActual(ValorUnidad stockActual) {
        this.stockActual = stockActual;
    }

    @Transient
    public abstract BaseEntity getDetalle();

    public void sumar(DetalleMovimientoStockAlmacenamiento detalle) {
        getStockActual().sumar(detalle.getCantidadRelativa(stockActual.getUnidad()));
    }

    public void restar(DetalleMovimientoStockAlmacenamiento detalle) {
        getStockActual().sumar(-1 * detalle.getCantidadRelativa(stockActual.getUnidad()));
    }

    @Transient
    public boolean isMayorMinimo() {
        return getStockActual().compareTo(getStockMinimo()) > 0;
    }

    @Override
    public String entityName() {
        return "Valor Almacenamiento";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValorAlmacenamiento<T> other = (ValorAlmacenamiento<T>) obj;
        if (this.almacenamiento != other.almacenamiento && (this.almacenamiento == null || !this.almacenamiento.equals(other.almacenamiento))) {
            return false;
        }
        if (this.getDetalle() != other.getDetalle() && (this.getDetalle() == null || !this.getDetalle().equals(other.getDetalle()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.almacenamiento != null ? this.almacenamiento.hashCode() : 0);
        hash = 53 * hash + (this.getDetalle() != null ? this.getDetalle().hashCode() : 0);
        return hash;
    }
}
