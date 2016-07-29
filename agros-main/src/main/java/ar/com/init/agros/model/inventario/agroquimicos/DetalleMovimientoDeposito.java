package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase DetalleMovimientoDeposito
 *
 *
 * @author gmatheu
 * @version 15/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.MOVIMIENTO_DEPOSITO_VALUE)
public class DetalleMovimientoDeposito extends DetalleMovimientoStock
{

    private static final long serialVersionUID = -1L;
    public static String[] TABLE_HEADERS = {"Agroquímico", "Deposito Origen", "Deposito Destino", "Cantidad"};
    private ValorAgroquimico valorDepositoOrigen;

    /** Constructor por defecto de DetalleMovimientoDeposito */
    public DetalleMovimientoDeposito()
    {
        super();
        setTipo(TipoMovimientoStockEnum.MOVIMIENTO_DEPOSITO.tipo());
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    public ValorAgroquimico getValorDepositoOrigen()
    {
        return valorDepositoOrigen;
    }

    public void setValorDepositoOrigen(ValorAgroquimico valorDepositoOrigen)
    {
        this.valorDepositoOrigen = valorDepositoOrigen;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = new ArrayList<Object>();
        r.add(this.getValorDeposito().getAgroquimico());
        r.add(this.getValorDepositoOrigen().getDeposito());
        r.add(this.getValorDeposito().getDeposito());
        r.add(createCantidadConditionalStyle(this.getCantidad()));

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS;
    }
}
