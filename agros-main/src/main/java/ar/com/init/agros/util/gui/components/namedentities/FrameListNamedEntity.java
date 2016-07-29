package ar.com.init.agros.util.gui.components.namedentities;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.base.NamedEntity;
import ar.com.init.agros.util.gui.components.FrameListEntity;

/**
 * Clase FrameListNamedEntity
 *
 *
 * @author gmatheu
 * @version 11/07/2009 
 */
public class FrameListNamedEntity<T extends NamedEntity> extends FrameListEntity<T>
{

    private static final long serialVersionUID = -1L;
    private String title;

    public FrameListNamedEntity(Class<T> clazz, String title, BaseEntityJpaController<T> jpaController, String[] headers)
    {
        super(clazz, jpaController, headers);
        this.title = title;
    }

    public FrameListNamedEntity(Class<T> clazz, String title, String[] headers)
    {
        super(clazz, headers);
        this.title = title;
    }

    @Override
    public void list(T entity)
    {
        FrameNamedEntity<T> f = new FrameNamedEntity<T>(this, true, clazz, entity, false);
        f.setTitle("Consultando " + title);
        f.setVisible(true);
    }

    @Override
    public void newEntity()
    {
        FrameNamedEntity<T> f = new FrameNamedEntity<T>(this, true, clazz);
        f.setTitle("Nuevo " + title);
        f.setVisible(true);
    }

    @Override
    public void update(T entity)
    {
        FrameNamedEntity<T> f = new FrameNamedEntity<T>(this, true, clazz, entity, true);
        f.setTitle("Modificando " + title);
        f.setVisible(true);
    }

    @Override
    public boolean isNowVisible()
    {
        return this.isVisible();
    }
}
