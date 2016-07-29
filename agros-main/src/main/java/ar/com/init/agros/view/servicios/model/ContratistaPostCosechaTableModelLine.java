package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.BaseCampaniaTransaction;
import ar.com.init.agros.model.Siembra;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.costo.Costo;
import ar.com.init.agros.model.servicio.TipoServicio;

/**
 * Clase ContratistaPostCosechaTableModelLine
 *
 *
 * @author fbobbio
 * @version 20-dic-2010 
 */
public class ContratistaPostCosechaTableModelLine 
{
    private BaseCampaniaTransaction transaction;
    private Costo costo;

    public ContratistaPostCosechaTableModelLine(BaseCampaniaTransaction trans,Costo cos)
    {
        this.transaction = trans;
        this.costo = cos;
    }

    public BaseCampaniaTransaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(BaseCampaniaTransaction transaction)
    {
        this.transaction = transaction;
    }

    public Costo getCosto()
    {
        return costo;
    }

    public void setCosto(Costo costo)
    {
        this.costo = costo;
    }

    /** Método que calcula el importe según la unidad del tipoCosto */
    public ValorMoneda calcularImporte()
    {
        // Si es contratista u Otro, multiplicamos la cantidad de hectareas por el importe, por ser sus costos de tipo siempre U$S/ha
        if (costo.getServicio().getTipo().equals(TipoServicio.CONTRATISTA)
                || costo.getServicio().getTipo().equals(TipoServicio.OTROS))
        {
            return new ValorMoneda(transaction.calcularHectareasTotales().getValor() * costo.getImporte().getMonto());
        }
        else
        {
            //Si es de post-cosecha, hago los cálculos correspondientes si es en quintales o en toneladas
            if (costo.getServicio().getTipo().equals(TipoServicio.PROVEEDOR_POST_COSECHA))
            {
                Siembra s = (Siembra)transaction;
                //Si la unidad es en dólares por quintales
                if (costo.getTipoCosto().getUnidadMedida().getUnidad().equals(UnidadMedida.getDolarPorQuintal()))
                {
                    return new ValorMoneda(costo.getImporte().getMonto()
                            * s.calcularQuintalesTotales().getValor());
                }
                //Si la unidad es dólares por tonelada
                if (costo.getTipoCosto().getUnidadMedida().getUnidad().equals(UnidadMedida.getDolarPorTonelada()))
                {
                    return new ValorMoneda(costo.getImporte().getMonto() / 1000
                            * (s.calcularQuintalesTotales().getValor() * 100));
                }
                //Si la unidad es dólares por hectárea
                if (costo.getTipoCosto().getUnidadMedida().getUnidad().equals(UnidadMedida.getDolarPorHa()))
                {
                    return new ValorMoneda(costo.getImporte().getMonto()
                            * s.calcularHectareasTotales().getValor());
                }
            }
        }
        return new ValorMoneda(0.0);
    }
}
