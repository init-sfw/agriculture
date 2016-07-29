package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import java.util.ArrayList;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase ValorAgroquimico
 * Esta clase contiene los valores de inventario actual para cada agroquimico en cada deposito.
 * Para evitar tener que recalcular los valores acutales cada vez que se necesiten.
 *
 * @author gmatheu
 * @version 19/07/2009 
 */
@Entity
public class ValorAgroquimico extends ValorAlmacenamiento<Deposito> {

    private static final long serialVersionUID = -1L;
    public static String[] TABLE_HEADERS = {"Agroquímico", "Deposito", "Stock Actual Deposito", "Stock Minimo"};
    private Agroquimico agroquimico;

    /** Constructor por defecto de ValorDeposito */
    public ValorAgroquimico() {
        super();
    }

    public ValorAgroquimico(Agroquimico agroquimico, Deposito deposito, UnidadMedida unidad) {
        super(deposito, unidad);
        this.agroquimico = agroquimico;
    }

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    public Agroquimico getAgroquimico() {
        return agroquimico;
    }

    @Transient
    @Override
    public Agroquimico getDetalle() {
        return getAgroquimico();
    }

    public void setAgroquimico(Agroquimico agroquimico) {
        this.agroquimico = agroquimico;
        if (agroquimico != null) {
            this.setStockActual(new ValorUnidad(0D, agroquimico.getUnidad()));
        } else {
            this.setStockActual(null);
        }
    }

    @Deprecated
    public void setDeposito(Deposito deposito) {
        setAlmacenamiento(deposito);
    }

    @Deprecated
    @Transient
    public Deposito getDeposito() {
        return getAlmacenamiento();
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column =
        @Column(name = "valorStockActual"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns =
        @JoinColumn(name = "unidadStockActual_id"))
    })
    @Override
    public ValorUnidad getStockActual() {
        return super.getStockActual();
    }

    @Override
    public void sumar(DetalleMovimientoStockAlmacenamiento detalle) {
        super.sumar(detalle);
//        getAgroquimico().getStockActual().sumar(detalle.getCantidadRelativa(getStockActual().getUnidad()));
    }

    @Override
    public void restar(DetalleMovimientoStockAlmacenamiento detalle) {
        super.restar(detalle);
//        getAgroquimico().getStockActual().sumar(-1 * detalle.getCantidadRelativa(getStockActual().getUnidad()));
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = new ArrayList<Object>();
        r.add(this.getAgroquimico());
        r.add(this.getAlmacenamiento());
        r.add(this.getStockActual());
        r.add(this.getStockMinimo());

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Override
    public String toString() {
        return getAgroquimico() + " [" + getAgroquimico().getUnidad().getAbreviatura() + "] - " + getAlmacenamiento();
    }

    @Override
    @Transient
    public String getListLine() {
        return agroquimico.toString();
    }

    @Override
    public String entityName() {
        return "Valor depósito";
    }
}
