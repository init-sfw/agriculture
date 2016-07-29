package ar.com.init.agros.model.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import java.util.UUID;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * 
 * @author gmatheu
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1446169700230534445L;
    protected String id;
    private Integer version;
    private BaseEntityStateEnum status;
    /** Variable para conocer la limitación de la entidad, valor null equivale a que no posee limitación */
    protected Integer limitation = null;

    public BaseEntity() {
        super();

        id = UUID.randomUUID().toString();
        status = BaseEntityStateEnum.ACTIVE;
    }

    public BaseEntity(UUID uuid) {
        super();

        this.id = uuid.toString();
        status = BaseEntityStateEnum.ACTIVE;
    }

    public abstract String entityName();

    @Id
    public String getId() {
        return id;
    }

    @Transient
    public UUID getUUID() {
        return UUID.fromString(id);
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    @Enumerated(EnumType.STRING)
    public BaseEntityStateEnum getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    protected void setVersion(Integer version) {
        this.version = version;
    }

    public void setStatus(BaseEntityStateEnum status) {
        this.status = status;
    }

    public Integer getLimitation() {
        return limitation;
    }

    public void setLimitation(Integer limitation) {
        this.limitation = limitation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BaseEntity)) {
            return false;
        }
        BaseEntity other = (BaseEntity) object;
        if (this.id == null && other.id == null) {
            return this == other;
        }
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[id=" + (id != null ? id : "NOT PERSISTED") + "]";
    }
    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
