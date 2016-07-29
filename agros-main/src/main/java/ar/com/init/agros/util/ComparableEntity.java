package ar.com.init.agros.util;

import ar.com.init.agros.model.base.BaseEntity;

/**
 * Interfaz ComparableEntity que nos brinda la funcionalidad para realizar las comparaciones
 * entre objetos que necesitan chequear su unicidad en la base de datos según las reglas de negocio
 *
 *
 * @author fbobbio
 * @version 11-jul-2009 
 */

public interface ComparableEntity<T extends BaseEntity>
{
    /** Método a redefinir que devolverá true si según las reglas de negocios dos entidades son iguales o poseen datos esenciales repetidos
     * 
     * @param entity
     * @return
     */
    boolean equalToEntity(T entity);
}
