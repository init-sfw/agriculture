package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * Clase Deposito
 *
 *
 * @author gmatheu
 * @version 11/07/2009 
 */
@Entity
@DiscriminatorValue(value = "D")
public class Deposito extends Almacenamiento
{

    private static final long serialVersionUID = -1L;
    public static final String ENTITY_NAME = "Depósito";
    private List<ValorAgroquimico> valoresDeposito;

    /** Constructor por defecto de Deposito */
    public Deposito()
    {
        super();
    }

    /** Constructor por defecto de Deposito */
    public Deposito(UUID uuid, String nombre, String desc)
    {
        super(uuid, nombre, desc);
    }

    @OneToMany(mappedBy = "almacenamiento")
    public List<ValorAgroquimico> getValoresDeposito()
    {
        if (valoresDeposito == null) {
            valoresDeposito = new ArrayList<ValorAgroquimico>();
        }

        return valoresDeposito;
    }

    public void setValoresDeposito(List<ValorAgroquimico> valoresDeposito)
    {
        this.valoresDeposito = valoresDeposito;
    }

    public ValorAgroquimico getValorDeposito(Agroquimico agroquimico)
    {
        ValorAgroquimico r = new ValorAgroquimico();
        r.setAgroquimico(agroquimico);
        r.setAlmacenamiento(this);
        r.setStockMinimo(agroquimico.getStockMinimo());

        for (ValorAgroquimico vd : getValoresDeposito()) {
            if (vd.getAgroquimico().equals(agroquimico)) {
                r = vd;
                break;
            }
        }

        return r;
    }

    @Transient
    public List<Agroquimico> getAgroquimicos()
    {
        List<Agroquimico> r = new ArrayList<Agroquimico>();

        for (ValorAgroquimico valorDeposito : getValoresDeposito()) {
            r.add(valorDeposito.getAgroquimico());
        }

        return r;
    }

    @Override
    public String entityName()
    {
        return ENTITY_NAME;
    }

    @Transient
    @Override
    public String getTipoAlmacenamiento()
    {
        return ENTITY_NAME;
    }
}
