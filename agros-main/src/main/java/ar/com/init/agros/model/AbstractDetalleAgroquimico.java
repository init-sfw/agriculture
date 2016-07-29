package ar.com.init.agros.model;

import ar.com.init.agros.model.base.TablizableEntity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.ManyToOne;
import org.hibernate.validator.NotNull;

/**
 * Clase AbstractDetalleAgroquimico
 *
 * Base para las clases de detalles que usan agroquimicos.
 * Aparentemente todas tienen la misma estructura.
 * Probablemente con similar comportamiento.
 *
 * @author gmatheu
 * @version 02/06/2009 
 */
@MappedSuperclass
public abstract class AbstractDetalleAgroquimico extends TablizableEntity
{

    private static final long serialVersionUID = -1L;
    private Agroquimico agroquimico;
    private MomentoAplicacion momentoAplicacion;
    private double superficiePlanificada;

    /** Constructor por defecto de AbstractDetalleAgroquimico */
    public AbstractDetalleAgroquimico()
    {
        super();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public MomentoAplicacion getMomentoAplicacion()
    {
        return momentoAplicacion;
    }

    public void setMomentoAplicacion(MomentoAplicacion momentoAplicacion)
    {
        this.momentoAplicacion = momentoAplicacion;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    public Agroquimico getAgroquimico()
    {
        return agroquimico;
    }

    public void setAgroquimico(Agroquimico Agroquimico)
    {
        this.agroquimico = Agroquimico;
    }

    @NotNull
    public double getSuperficiePlanificada()
    {
        return superficiePlanificada;
    }

    public void setSuperficiePlanificada(double superficiePlanificada)
    {
        this.superficiePlanificada = superficiePlanificada;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return super.toString();
    }
}
