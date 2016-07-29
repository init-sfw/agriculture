package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.terreno.DivisionCampo;
import java.util.List;

/**
 * Clase DivisionCampoJpaController
 *
 *
 * @author fbobbio
 * @version 15-jul-2009 
 */
public class DivisionCampoJpaController extends BaseEntityJpaController<DivisionCampo>
{

    /** Constructor por defecto de AgroquimicoJpaController */
    public DivisionCampoJpaController()
    {
        super(DivisionCampo.class);
    }

    /*public boolean exists(DivisionCampo div)
    {
        List<DivisionCampo> list = findEntities();
        for (DivisionCampo d : list) {
            if (div.equalToEntity(d)) {
                return true;
            }
        }
        return false;
    }*/
}
