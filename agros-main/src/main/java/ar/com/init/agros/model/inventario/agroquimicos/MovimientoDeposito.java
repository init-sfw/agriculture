package ar.com.init.agros.model.inventario.agroquimicos;

import ar.com.init.agros.email.EmailHelper;
import ar.com.init.agros.email.Emailable;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.inventario.TipoMovimientoStock;
import ar.com.init.agros.model.util.EMailMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase MovimientoDeposito
 *
 *
 * @author gmatheu
 * @version 17/07/2009 
 */
@Entity
@DiscriminatorValue(TipoMovimientoStock.MOVIMIENTO_DEPOSITO_VALUE)
public class MovimientoDeposito extends MovimientoStock<DetalleMovimientoDeposito> implements Emailable
{

    private static final long serialVersionUID = -1L;
    private Deposito depositoOrigen;
    private Deposito depositoDestino;
    public static final String[] TABLE_HEADERS_MOV_DEPO = {"Fecha del movimiento", "Fecha de registro", "Depósito origen", "Depósito destino"};

    /** Constructor por defecto de MovimientoDeposito */
    public MovimientoDeposito()
    {
        super();
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    public Deposito getDepositoOrigen()
    {
        return depositoOrigen;
    }

    public void setDepositoOrigen(Deposito depositoOrigen)
    {
        this.depositoOrigen = depositoOrigen;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    public Deposito getDepositoDestino()
    {
        return depositoDestino;
    }

    public void setDepositoDestino(Deposito depositoDestino)
    {
        this.depositoDestino = depositoDestino;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS_MOV_DEPO;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> aux = new ArrayList<Object>();
        aux.add(getFecha());
        aux.add(getFechaCreacion());
        aux.add(depositoOrigen);
        aux.add(depositoDestino);
        return aux;
    }

    @Override
    public EMailMessage createEmailMessage()
    {
        try {
            EmailHelper helper = new EmailHelper();
            helper.writeLogo();
            helper.writeTitle("Alertas de Stock");
            helper.writeBlankLine();
            helper.writeLine("Se ha realizado un movimiento de stock entre depositos.");
            helper.writeBlankLine();
            helper.writeLine(String.format("Fecha: %s", EmailHelper.formatDate(getFecha())));
            helper.writeLine(String.format("Deposito Origen: %s", getDepositoOrigen()));
            helper.writeLine(String.format("Deposito Destino: %s", getDepositoDestino()));
            helper.writeBlankLine();

            helper.writeLine("Detalle:");
            helper.writeBlankLine();

            helper.writeTable(DetalleMovimientoDeposito.TABLE_HEADERS, getCastedDetalles());

            helper.writeBlankLine();
            helper.writeBlankLine();

            helper.writeLine("Observaciones:");
            helper.writeLine(getObservaciones());

            EMailMessage r = new EMailMessage(
                    "Alertas de Stock: Movimiento entre Depósitos - " + EmailHelper.formatDate(getFecha()),
                    helper.buildContent());
            return r;
        }
        catch (Exception ex) {
            Logger.getLogger(AjusteInventario.class.getName()).info("Exception while creating email");
        }

        return null;
    }
}
