package ar.com.init.agros.model.base;

import ar.com.init.agros.util.ComparableEntity;
import ar.com.init.agros.util.gui.Listable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

/**
 * Clase NamedEntity
 *
 * Para que hereden las entidades que poseen nombre y descripcion entre sus atributos
 *
 * @author gmatheu
 * @version 02/06/2009 
 */
@MappedSuperclass
public abstract class NamedEntity extends TablizableEntity implements Listable, Comparable<NamedEntity>,
        ComparableEntity<NamedEntity>
{

    public static final String[] TABLE_HEADERS = {"Nombre", "Descripci�n"};
    private String descripcion;
    private String nombre;

    public NamedEntity()
    {
        super();
    }

    public NamedEntity(UUID uuid)
    {
        super(uuid);
    }

    public NamedEntity(UUID uuid, String nombre, String desc)
    {
        super(uuid);
        setNombre(nombre);
        setDescripcion(desc);
    }

    public NamedEntity(String nombre, String desc)
    {
        super();
        setNombre(nombre);
        setDescripcion(desc);
    }

    @Override
    public void setId(String id)
    {
        super.setId(id);
    }

    @Length(max = 255)
    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String desc)
    {
        this.descripcion = desc;
    }

    @NotEmpty
    @Length(min = 3, max = 40)
    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return nombre;
    }

    @Override
    public abstract String entityName();

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return nombre;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = new ArrayList<Object>();
        r.add(nombre);
        r.add(descripcion);
        return r;
    }

    @Override
    public boolean equalToEntity(NamedEntity entity)
    {
        return this.nombre.equalsIgnoreCase(entity.nombre);
    }

    @Override
    public int compareTo(NamedEntity o)
    {
       return this.nombre.compareToIgnoreCase(o.nombre);
    }


}
