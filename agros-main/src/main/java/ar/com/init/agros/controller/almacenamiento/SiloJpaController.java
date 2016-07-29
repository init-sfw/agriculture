package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.almacenamiento.Silo;

/**
 *
 * @author gmatheu
 */
public class SiloJpaController extends NamedEntityJpaController<Silo>
{
    public SiloJpaController() {
        super(Silo.class);
    }

}
