package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorMonedaMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleIngresoStock
 *
 *
 * @author gmatheu
 * @version 29/06/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.INGRESO_VALUE)
public class DetalleIngresoStock extends DetalleMovimientoStock {

    private static final long serialVersionUID = -1L;
    public static String[] TABLE_HEADERS = {"Fecha", "Deposito", "Agroquímico", "Stock Mínimo", "Proveedor", "Remito", "Cantidad", "Costo Unitario", "Costo Total", "Observaciones"};
    private String remito;
    private Servicio proveedor;
    private ValorMoneda costoTotal;
    private String observaciones;

    /** Constructor por defecto de DetalleIngresoStock */
    public DetalleIngresoStock() {
        super();
        setTipo(TipoMovimientoStockEnum.INGRESO.tipo());
        setCancelado(false);
    }

    @Embedded
    @AttributeOverrides(value = {
        @AttributeOverride(name = "monto", column =
        @Column(name = "montoCostoTotal"))})
    @AssociationOverrides(value = {
        @AssociationOverride(name = "divisa", joinColumns =
        @JoinColumn(name =
        "divisaCostoTotal_id"))})
    @NotNull
    public ValorMoneda getCostoTotal() {
        return costoTotal;
    }

    @Transient
    public ValorMonedaMedida getCostoUnitario() {
        if (getCostoTotal() != null && getCantidad() != null) {
            ValorUnidad vu = getCantidad();

            Double cu = getCostoTotal().getMonto() / vu.getValor();

            ValorMonedaMedida r = new ValorMonedaMedida(cu, vu.getUnidad());

            return r;
        } else {
            return null;
        }
    }

    public void setCostoTotal(ValorMoneda costoTotal) {
        this.costoTotal = costoTotal;
        if (getAgroquimico() != null && getCostoUnitario() != null) {
            getAgroquimico().setCostoActual((ValorMonedaMedida) getCostoUnitario().clone());
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    public Servicio getProveedor() {
        return proveedor;
    }

    public void setProveedor(Servicio proveedor) {
        if (proveedor != null && !proveedor.getTipo().equals(TipoServicio.PROVEEDOR_INSUMOS)) {
            throw new IllegalArgumentException("Sólo soporta tipo PROVEEDOR_INSUMOS");
        }
        this.proveedor = proveedor;
    }

    public String getRemito() {
        return remito;
    }

    public void setRemito(String remito) {
        this.remito = remito;
    }

    @Length(min = 0, max = 1024)
    @Column(length = 1024)
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = new ArrayList<Object>();
        r.add(getFecha());
        r.add(this.getValorDeposito().getDeposito());
        r.add(this.getValorDeposito().getAgroquimico());
        r.add(this.getValorDeposito().getStockMinimo());
        r.add(this.getProveedor());
        r.add(this.getRemito());
        r.add(createCantidadConditionalStyle(this.getCantidad()));
        r.add(this.getCostoUnitario());
        r.add(this.getCostoTotal());
        r.add(this.observaciones);

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
