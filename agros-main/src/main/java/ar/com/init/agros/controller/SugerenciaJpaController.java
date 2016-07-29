package ar.com.init.agros.controller;

import ar.com.init.agros.model.util.Sugerencia;
import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.email.EmailManager;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 *
 * @author gmatheu
 */
public class SugerenciaJpaController  extends BaseEntityJpaController<Sugerencia>{

    public SugerenciaJpaController() {
        super(Sugerencia.class);
    }

    @Override
    public void persist(Sugerencia entity) throws InvalidStateException, ConstraintViolationException {
        super.persist(entity);

        EmailManager.getInstance().postSystemMail(
                            entity.createEmailMessage());
    }

}
