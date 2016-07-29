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

    /** M�todo que retorna una descripci�n de la clase
     *  @return descripci�n de la clase
     */
    @Override
    public String toString()
    {
        return getNombre();
    }
}
