package ar.com.init.agros.model;

import ar.com.init.agros.model.base.NamedEntity;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Clase Accion
 *
 *
 * @author gmatheu
 * @version 11/06/2009 
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nombre"})})
public class Accion extends NamedEntity
{

    public static String ENTITY_NAME = "Accion";
    private static final long serialVersionUID = -1L;
    private List<InformacionAgroquimico> informacionesAgroquimicos;

    /** Constructor por defecto de Accion */
    public Accion()
    {
        super();
    }

    public Accion(UUID uUID)
    {
        super(uUID);
    }

    @Override
    public void setId(String id)
    {
        super.setId(id);
    }

    @ManyToMany(mappedBy = "acciones", fetch = FetchType.LAZY)
    public List<InformacionAgroquimico> getInformacionesAgroquimicos()
    {
        return informacionesAgroquimicos;
    }

    public void setInformacionesAgroquimicos(List<InformacionAgroquimico> informacionesAgroquimicos)
    {
        this.informacionesAgroquimicos = informacionesAgroquimicos;
    }

    @Override    
    public String entityName()
    {
        return ENTITY_NAME;
    }
}
