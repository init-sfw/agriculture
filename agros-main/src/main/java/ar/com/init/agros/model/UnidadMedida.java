package ar.com.init.agros.model;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.base.NamedEntity;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.validator.Length;

/**
 * Clase UnidadMedida
 *
 *
 * @author gmatheu
 * @version 31/05/2009 
 */
@Entity
public class UnidadMedida extends NamedEntity
{

    private static final String ENTITY_NAME = "Unidad de medida";
    private static UnidadMedida QUINTAL;
    private static UnidadMedida DOLAR_POR_HA;
    private static UnidadMedida DOLAR_POR_TN;
    private static UnidadMedida DOLAR_POR_QQ;
    private static final long serialVersionUID = -1L;
    private String abreviatura;
    private MagnitudEnum magnitud;

    /** Constructor por defecto de UnidadMedida */
    public UnidadMedida()
    {
        super();
    }

    public UnidadMedida(String abreviatura, String descripcion, String nombre, MagnitudEnum magnitud)
    {
        this();
        this.abreviatura = abreviatura;
        this.setDescripcion(descripcion);
        this.setNombre(nombre);
        this.magnitud = magnitud;
    }

    public UnidadMedida(UUID id, String abreviatura, String descripcion, String nombre, MagnitudEnum magnitud)
    {
        super(id);
        this.abreviatura = abreviatura;
        setDescripcion(descripcion);
        setNombre(nombre);
        this.magnitud = magnitud;
    }

    public UnidadMedida(UUID id, String abreviatura, String descripcion, String nombre)
    {
        this(id, abreviatura, descripcion, nombre, null);
    }

    @Length(max = 6)
    public String getAbreviatura()
    {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura)
    {
        this.abreviatura = abreviatura;
    }

    @Enumerated(EnumType.STRING)
    public MagnitudEnum getMagnitud()
    {
        return magnitud;
    }

    public void setMagnitud(MagnitudEnum magnitud)
    {
        this.magnitud = magnitud;
    }

    public static UnidadMedida getQuintal()
    {
        if (QUINTAL == null) {
            QUINTAL = new UnidadMedida(new UUID(0, 7), "qq", "quintal", "Quintal", MagnitudEnum.PESO);
        }
        return EntityManagerUtil.createEntityManager().merge(QUINTAL);
    }

    public static UnidadMedida getDolarPorHa()
    {
        if (DOLAR_POR_HA == null) {
            DOLAR_POR_HA = new UnidadMedida(new UUID(0, 8), "U$S/ha", "dólar/ha", "Dólar por hectárea");
        }
        return EntityManagerUtil.createEntityManager().merge(DOLAR_POR_HA);
    }

    public static UnidadMedida getDolarPorQuintal()
    {
        if (DOLAR_POR_QQ == null) {
            DOLAR_POR_QQ = new UnidadMedida(new UUID(0, 10), "U$S/QQ", "dólar/QQ", "Dólar por quintal");
        }
        return EntityManagerUtil.createEntityManager().merge(DOLAR_POR_QQ);
    }

    public static UnidadMedida getDolarPorTonelada()
    {
        if (DOLAR_POR_TN == null) {
            DOLAR_POR_TN = new UnidadMedida(
                    new UUID(0, 9), "U$S/Tn", "dólar/Tn", "Dólar por tonelada");
        }
        return EntityManagerUtil.createEntityManager().merge(DOLAR_POR_TN);
    }

    @Override
    public String entityName()
    {
        return ENTITY_NAME;
    }
}
