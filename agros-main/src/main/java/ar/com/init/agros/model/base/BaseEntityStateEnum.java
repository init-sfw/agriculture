package ar.com.init.agros.model.base;

import ar.com.init.agros.model.base.type.IdentifiableEnum;

public enum BaseEntityStateEnum implements IdentifiableEnum
{

    ACTIVE("A", "Activo"),
    INACTIVE("I", "Inactivo"),
    DELETED("D", "Borrado");
    private final String name;
    private final String id;

    private BaseEntityStateEnum(String id, String name)
    {
        this.name = name;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String id()
    {
        return id;
    }
}
