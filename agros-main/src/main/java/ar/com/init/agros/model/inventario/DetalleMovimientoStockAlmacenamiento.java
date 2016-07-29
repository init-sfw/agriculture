package ar.com.init.agros.model.inventario;

import ar.com.init.agros.model.*;
import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.util.gui.styles.BooleanCondition;
import ar.com.init.agros.util.gui.styles.ConditionalStyle;
import ar.com.init.agros.util.gui.styles.LightCondition;
import ar.com.init.agros.util.gui.styles.Style;
import java.awt.Color;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.swing.SwingConstants;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleMovimientoStock
 *
 *
 * @author gmatheu
 * @version 08/12/2010
 */
@MappedSuperclass
public abstract class DetalleMovimientoStockAlmacenamiento<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity> extends TablizableEntity {

    public static String[] TABLE_HEADERS = {"Fecha", "Detalle", "Almacenamiento", "Cantidad"};
    private static final long serialVersionUID = -1L;
    private Date fecha;
    private ValorUnidad cantidad;
    private boolean positivo;
    private TipoMovimientoStock tipo;
    protected T valor;
    private Boolean cancelado;

    /** Constructor por defecto de DetalleMovimientoStock */
    public DetalleMovimientoStockAlmacenamiento() {
        super();
        setPositivo(true);
    }

    @Transient
    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    public TipoMovimientoStock getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimientoStock tipo) {
        this.tipo = tipo;
    }

    @Transient
    public Almacenamiento getDestino() {
        return (getValor() != null ? getValor().getAlmacenamiento() : null);
    }

    /**
     *
     * @param <D>
     * @return devuelve el objeto sobre el que se está haciendo el movimiento.
     */
    @Transient
    public abstract D getDetalle();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column =
        @Column(name = "valorCantidad"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns =
        @JoinColumn(name = "unidadCantidad_id"))
    })
    @NotNull
    public ValorUnidad getCantidad() {
        return cantidad;
    }

    public void setCantidad(ValorUnidad cantidad) {
        this.cantidad = cantidad;
    }

    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @NotNull
    public boolean isPositivo() {
        return positivo;
    }

    public void setPositivo(boolean positivo) {
        this.positivo = positivo;
    }

    public Boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = new ArrayList<Object>();
        r.add(this.getFecha());
        r.add(this.getDetalle());
        r.add(this.getDestino());
        r.add(createCantidadConditionalStyle(cantidad));

        return r;
    }

    public static ConditionalStyle createCantidadConditionalStyle(ValorUnidad cantidad) {
        return new CantidadConditionalStyle(cantidad);
    }

    @Transient
    public ConditionalStyle getConditionalStyledCantidad() {
        return createCantidadConditionalStyle(cantidad);
    }

    /////////////////////////////METODOS DE NEGOCIO/////////////////////////////////////////
    /**
     * Devuelve la cantidad del movimiento en la unidad pasada por parametro.
     * Si esIngreso es true, devuelve el mismo valor. En caso de ser false, devuelve el valor opuesto.
     * @param unidad
     * @return
     */
    public Double getCantidadRelativa(UnidadMedida unidad) {
        return cantidad.getValor(unidad) * (positivo ? 1 : -1);
    }

    @Override
    public String entityName() {
        return "Detalle de movimiento de stock";
    }
}

class UnmodifiableRowConditionalStyle extends ConditionalStyle<Object> {

    public UnmodifiableRowConditionalStyle(Object o) {
        super(o,
                new BooleanCondition(new Style(Style.StyleParam.TEXT_COLOR, Color.GRAY)));
    }
}

class CantidadConditionalStyle extends ConditionalStyle<ValorUnidad> {

    public CantidadConditionalStyle(ValorUnidad cantidad) {
        super(cantidad,
                new LightCondition<ValorUnidad>(new ValorUnidad(0D, null),
                new Style(Style.StyleParam.TEXT_COLOR, Color.RED),
                new Style(Style.StyleParam.HORIZONTAL_ALIGNMENT, SwingConstants.RIGHT)),
                new LightCondition<ValorUnidad>(new ValorUnidad(0D, null), new ValorUnidad(Double.MAX_VALUE,
                null),
                new Style(Style.StyleParam.PREFIX, "+"),
                new Style(Style.StyleParam.HORIZONTAL_ALIGNMENT, SwingConstants.RIGHT)));
    }
}
