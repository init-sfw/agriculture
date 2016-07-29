package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorMonedaMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;


import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.terreno.Campo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Clase DetalleOperacionGrano
 *
 *
 * @author gmatheu
 * @version 09/12/2010
 */
@MappedSuperclass
public abstract class DetalleOperacionGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity> 
        extends DetalleMovimientoGrano<T, A, D> {

    public static final String[] TABLE_HEADERS = {"Fecha", VALUE_TITLE, "Establecimiento", "Almacenamiento", "Bolsas", "[Kg/bolsa]", "Cantidad [Kg]", "Humedad [%]", "Servicio", "Importe [U$S]"};
    protected static final int SERVICIO_COLUMN = 8;
    protected static final int CANTIDAD_COLUMN = 6;
    private static final long serialVersionUID = -1L;
    private Campo establecimiento;
    private Double bolsas;
    private Double humedad;
    private Double kgPorBolsa;
    private Servicio servicio;
    private ValorMoneda importe;

    /** Constructor por defecto de DetalleMovimientoStock */
    public DetalleOperacionGrano() {
        super();
    }

    public Double getBolsas() {
        return bolsas;
    }

    public void setBolsas(Double bolsas, ValorUnidad pesoBolsa, ValorMoneda valorBolsa) {
        setBolsas(bolsas);
        setImporte(new ValorMoneda(bolsas * valorBolsa.getMonto()));
    }

    public void setBolsas(Double bolsas) {
        this.bolsas = bolsas;
    }

    public Double getKgPorBolsa()
    {
        return kgPorBolsa;
    }

    public void setKgPorBolsa(Double kgPorBolsa)
    {
        this.kgPorBolsa = kgPorBolsa;
    }

    @ManyToOne
    public Campo getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Campo establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    @AttributeOverrides(value = {
        @AttributeOverride(name = "monto", column =
        @Column(name = "importe"))})
    @AssociationOverrides(value = {
        @AssociationOverride(name = "divisa", joinColumns =
        @JoinColumn(name =
        "divisaImporte_id"))})
    public ValorMoneda getImporte() {
        return importe;
    }

    @Transient
    public ValorMonedaMedida getCostoUnitario() {
        if (getImporte() != null && getCantidad() != null) {
            ValorUnidad vu = getCantidad();

            Double cu = getImporte().getMonto() / vu.getValor();

            ValorMonedaMedida r = new ValorMonedaMedida(cu, vu.getUnidad());

            return r;
        } else {
            return null;
        }
    }

    public void setImporte(ValorMoneda importe) {
        this.importe = importe;
    }

    @ManyToOne
    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    @Transient
    @Override
    public List<Object> getTableLine() {
        List r = new ArrayList();
        r.add(getFecha());
        r.add(getDetalle());
        r.add(getEstablecimiento());
        r.add(getDestino());
        r.add(getBolsas());
        r.add(getKgPorBolsa());
        r.add(createCantidadConditionalStyle(getCantidad()));
        r.add(getHumedad());
        r.add(getServicio());
        r.add(getImporte());

        return r;
    }    
}
