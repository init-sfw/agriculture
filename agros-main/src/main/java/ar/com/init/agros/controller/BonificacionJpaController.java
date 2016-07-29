package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.Bonificacion;

/**
 * Clase BonificacionJpaController
 *
 *
 * @author fbobbio
 * @version 29-nov-2009 
 */
public class BonificacionJpaController extends BaseEntityJpaController<Bonificacion>
{
    /** Constructor por defecto de BonificacionJpaController */
    public BonificacionJpaController()
    {
        super(Bonificacion.class);
    }
}
