package ar.com.init.agros.model.ingreso;

import ar.com.init.agros.model.base.NamedEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * Clase TipoIngreso
 *
 *
 * @author fbobbio
 * @version 22-nov-2009 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nombre"}))
public class TipoIngreso extends NamedEntity
{

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS_TIPO_INGRESO = {"Nombre", "Descripción", "Unidad Medida"};
    public static final String ENTITY_NAME = "Tipo de Ingreso";
    /** Si es verdadero el tipo de ingreso es en dólar por hectárea, si es falso es en dólares totales */
    private boolean dolarPorHectarea;


    /** Constructor por defecto de TipoIngreso */
    public TipoIngreso()
    {
        super();
    }

    public TipoIngreso(UUID uuid, String nombre, String desc)
    {
        super(uuid, nombre, desc);
    }

    public boolean isDolarPorHectarea()
    {
        return dolarPorHectarea;
    }

    public void setDolarPorHectarea(boolean dolarPorHectarea)
    {
        this.dolarPorHectarea = dolarPorHectarea;
    }

    @Override
    public String entityName()
    {
        return ENTITY_NAME;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS;
    }

    public String unidadMedida()
    {
        String aux;
        if (isDolarPorHectarea())
        {
            aux = "[U$S/ha]";
        }
        else
        {
            aux = "[U$S]";
        }
        return aux;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return getNombre();
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = new ArrayList<Object>();
        r.add(getNombre());
        r.add(getDescripcion());
        if (isDolarPorHectarea())
        {
            r.add("U$S/ha");
        }
        else
        {
            r.add("U$S");
        }
        return r;
    }
}
