package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.controller.AgroquimicoJpaController;
import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.inventario.DetalleMovimientoStockAlmacenamiento;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import java.util.Date;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

/**
 * Clase DepositoJpaController
 *
 *
 * @author gmatheu
 * @version 12/07/2009 
 */
public class DepositoJpaController extends NamedEntityJpaController<Deposito>
{

    /** Constructor por defecto de CultivoJpaController */
    public DepositoJpaController()
    {
        super(Deposito.class);
    }

    @Override
    public void persistOrUpdate(Deposito entity) throws InvalidStateException, ConstraintViolationException, Exception
    {
        AgroquimicoJpaController agroCont = new AgroquimicoJpaController();

        super.persistOrUpdate(entity);

//        for (Agroquimico agroquimico : agroCont.findAllEntities()) {
//            getEntityManager().merge(entity.getValorDeposito(agroquimico));
//            getEntityManager().merge(agroquimico);
//        }
    }

    @SuppressWarnings("unchecked")
    public ValorUnidad calcularStock(Deposito deposito, Agroquimico agroquimico, Date fecha)
    {
        //TODO: Ver como manejar el tema de las fechas
        Double valor = 0D;

        List<DetalleMovimientoStockAlmacenamiento> movs =
                createQuery(
                " FROM " + DetalleMovimientoStockAlmacenamiento.class.getName() + " WHERE valorDeposito.deposito=:deposito AND valorDeposito.agroquimico=:agroquimico").setParameter(
                "deposito", deposito).setParameter("agroquimico", agroquimico).getResultList();

        UnidadMedida unidad = agroquimico.getUnidad();
        for (DetalleMovimientoStockAlmacenamiento m : movs) {
            valor += m.getCantidadRelativa(unidad);
        }

        ValorUnidad v = new ValorUnidad(valor, unidad);
        return v;
    }
}
