package ar.com.init.agros.model.util;

import ar.com.init.agros.model.base.BaseEntity;
import javax.persistence.Entity;
import org.hibernate.validator.NotNull;

/**
 * Clase Configuration
 *
 *
 * @author gmatheu
 * @version 13/07/2009 
 */
@Entity
public class Configuration extends BaseEntity
{ 
    private static final long serialVersionUID = -1L;
    private String confKey;
    private String confValue;

    public Configuration()
    {
        super();
    }

    public Configuration(String confKey, String confValue)
    {
        super();
        this.confKey = confKey;
        this.confValue = confValue;
    }

    @NotNull    
    public String getConfKey()
    {
        return confKey;
    }

    public void setConfKey(String confKey)
    {
        this.confKey = confKey;
    }

    @NotNull
    public String getConfValue()
    {
        return confValue;
    }

    public void setConfValue(String confValue)
    {
        this.confValue = confValue;
    }

    @Override
    public String toString()
    {
        return confValue;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Configuration other = (Configuration) obj;
        if ((this.confKey == null) ? (other.confKey != null) : !this.confKey.equals(other.confKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + (this.confKey != null ? this.confKey.hashCode() : 0);
        return hash;
    }

    @Override
    public String entityName()
    {
        return "Configuración";
    }
}
