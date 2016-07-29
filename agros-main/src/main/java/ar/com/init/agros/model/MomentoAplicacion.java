package ar.com.init.agros.model;

import ar.com.init.agros.model.base.NamedEntity;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * Clase MomentoAplicacion
 *
 *
 * @author gmatheu
 * @version 22/06/2009 
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nombre"})})
public class MomentoAplicacion extends NamedEntity
{

    public static String ENTITY_NAME = "Momento de Aplicacion";
    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de MomentoAplicacion */
    public MomentoAplicacion()
    {
        super();
    }

    public MomentoAplicacion(UUID uuid, String nombre, String desc)
    {
        super(uuid, nombre, desc);
    }

    @Override
    @Transient
    public String entityName()
    {
        return ENTITY_NAME;
    }
}
