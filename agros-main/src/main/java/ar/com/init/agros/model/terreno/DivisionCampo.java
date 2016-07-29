package ar.com.init.agros.model.terreno;

import java.util.UUID;
import javax.persistence.MappedSuperclass;

/**
 * Clase DivisionCampo
 *
 *
 * @author gmatheu
 * @version 12/07/2009 
 */
@MappedSuperclass
public abstract class DivisionCampo extends Superficie
{
    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de DivisionCampo */
    public DivisionCampo()
    {
        super();
    }

    public DivisionCampo(UUID uuid)
    {
        super(uuid);
    }

    public DivisionCampo(DivisionCampo divCampo)
    {
        super(divCampo);
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return getNombre();
    }
}
