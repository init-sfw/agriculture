package ar.com.init.agros.model;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import java.util.UUID;

/**
 * Enumeración de las magnitudes físicas permitidas en Unidades de medida.
 *
 *
 * @author gmatheu
 * @version 31/05/2009 
 */
public enum MagnitudEnum
{

    LONGITUD("Longitud", new UnidadMedida(new UUID(0, 1), "m", "metro", "Metro")),
    PESO("Peso", new UnidadMedida(new UUID(0, 2), "Kg", "Kilogramo", "Kilogramo")),
    TIEMPO("Tiempo", new UnidadMedida(new UUID(0, 3), "s", "segundo", "Segundo")),
    SUPERFICIE("Superficie", new UnidadMedida(new UUID(0, 4), "ha", "hectárea", "Hectárea")),
    VOLUMEN("Volumen", new UnidadMedida(new UUID(0, 5), "l", "litro", "Litro")),
    LLUVIA_CAIDA("Lluvia", new UnidadMedida(new UUID(0, 6), "mm", "milímetro", "Milímetro"));
    private final String descripcion;
    private final UnidadMedida patron;

    MagnitudEnum(String desc, UnidadMedida patron)
    {
        this.descripcion = desc;
        patron.setMagnitud(this);
        this.patron = patron;
    }

    public String descripcion()
    {
        return descripcion;
    }

    /**
     * Es la unidad que se usara como base para realizar los calculos de esta magnitus
     *
     * @return la unidad de medida por defecto de la aplicacion.
     */
    public UnidadMedida patron()
    {
        return EntityManagerUtil.createEntityManager().merge(patron);
    }
}
