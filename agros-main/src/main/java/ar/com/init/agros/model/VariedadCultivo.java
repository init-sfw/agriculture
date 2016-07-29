package ar.com.init.agros.model;

import ar.com.init.agros.model.base.NamedEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Clase VariedadCultivo
 *
 *
 * @author fbobbio
 * @version 24-jun-2009 
 */
@Entity
public class VariedadCultivo extends NamedEntity
{
    public static String ENTITY_NAME = "Variedad de Cultivo";

    private static final long serialVersionUID = -1L;

    private Cultivo cultivo;

    public VariedadCultivo(String nombre, String desc)
    {
        super(nombre, desc);
    }
    
    /** Constructor por defecto de VariedadCultivo */
    public VariedadCultivo()
    {
    }

    @ManyToOne
    public Cultivo getCultivo()
    {
        return cultivo;
    }

    public void setCultivo(Cultivo cultivo)
    {
        this.cultivo = cultivo;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return  getCultivo().toString() + " - " + super.toString();
    }

    @Override   
    public String entityName()
    {
        return ENTITY_NAME;
    }
}
