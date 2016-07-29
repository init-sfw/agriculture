package ar.com.init.agros.model.servicio;

import ar.com.init.agros.model.base.type.IdentifiableEnum;

/**
 *
 * @author gmatheu
 */
public enum TipoServicio implements IdentifiableEnum {

    PROVEEDOR_INSUMOS("PI", "Proveedor de Insumos"),
    CONTRATISTA("CONT", "Contratista"),
    COMPRADOR("COMP", "Comprador"),
    PROVEEDOR_POST_COSECHA("PPC", "Proveedor de Servicios Post Cosecha"),
    OTROS("O", "Otros");
    private final String id;
    private final String nombre;

    private TipoServicio(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre();
    }
}
