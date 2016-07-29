package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.model.*;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Clase DetalleMovimientoStock
 *
 *
 * @author gmatheu
 * @version 12/06/2009 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "dtipo")
@DiscriminatorValue(TipoMovimientoStock.OTRO_VALUE)
public class DetalleMovimientoStock extends DetalleMovimientoStockAlmacenamiento<ValorAgroquimico, Deposito, Agroquimico> {

    public static String[] TABLE_HEADERS = {"Agroquímico", "Depósito", "Cantidad"};
    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DetalleMovimientoStock */
    public DetalleMovimientoStock() {
        super();
    }

//    @Override
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @Override
    public ValorAgroquimico getValor() {
        return valor;
    }

    @Deprecated
    @Transient
    public ValorAgroquimico getValorDeposito() {
        return getValor();
    }

    @Deprecated
    public void setValorDeposito(ValorAgroquimico valorDeposito) {
        setValor(valorDeposito);
    }

    @Deprecated
    @Transient
    public Deposito getDepositoDestino() {
        return (Deposito) getDestino();
    }

    @Deprecated
    @Transient
    public Agroquimico getAgroquimico() {
        return getDetalle();
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Transient
    @Override
    public Agroquimico getDetalle() {
        return (getValor() != null ? getValor().getAgroquimico() : null);
    }
}
