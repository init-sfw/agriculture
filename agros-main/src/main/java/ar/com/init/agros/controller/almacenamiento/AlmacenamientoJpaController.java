package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;

/**
 * Clase DepositoJpaController
 *
 *
 * @author gmatheu
 * @version 12/07/2009 
 */
public class AlmacenamientoJpaController<T extends Almacenamiento> extends NamedEntityJpaController<Almacenamiento> {

    public AlmacenamientoJpaController(Class<Almacenamiento> clazz) {
        super(clazz);
    }

    public AlmacenamientoJpaController()
    {
        super(Almacenamiento.class);
    }
}
