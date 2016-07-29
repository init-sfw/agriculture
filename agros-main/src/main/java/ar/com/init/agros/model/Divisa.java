package ar.com.init.agros.model;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.base.NamedEntity;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Clase Divisa
 *
 *
 * @author gmatheu
 * @version 11/06/2009 
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nombre"})})
public class Divisa extends NamedEntity
{
    private static final Divisa PATRON = new Divisa(new UUID(0,1),"[U$S]","Dólar Americano","Dólar Americano");

    public static String ENTITY_NAME = "Divisa";
    private static final long serialVersionUID = -1L;

    private String abreviatura;

    /** Constructor por defecto de Divisa */
    public Divisa()
    {
        super();
    }

     public Divisa(UUID id, String abreviatura, String descripcion, String nombre)
    {
        super(id);        
        setDescripcion(descripcion);
        setNombre(nombre);
        this.abreviatura = abreviatura;
      
    }

     public static Divisa getPatron()
     {
         return EntityManagerUtil.createEntityManager().merge(PATRON);
     }

    @NotNull
    @Length(min=1,max=5)
    public String getAbreviatura()
    {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura)
    {
        this.abreviatura = abreviatura;
    }

    @Override
    public String entityName()
    {
        return ENTITY_NAME;
    }

    @Override
    public String toString()
    {
        return abreviatura;
    }
}
