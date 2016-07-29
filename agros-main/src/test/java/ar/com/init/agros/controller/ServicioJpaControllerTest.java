package ar.com.init.agros.controller;

import java.util.List;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import javax.persistence.PersistenceException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class ServicioJpaControllerTest extends BaseEntityJpaControllerTest {

    @Test
    public void testFindByTipo() {

        ServicioJpaController controller = new ServicioJpaController();

        for (TipoServicio tipo : TipoServicio.values()) {
            Servicio servicio = new Servicio(tipo.nombre(), "dom", null, tipo);
            controller.persist(servicio);
        }

        List<Servicio> servicios = controller.findEntities();
        assertEquals(TipoServicio.values().length, servicios.size());

        servicios = controller.findByTipo(TipoServicio.values());
        assertEquals(TipoServicio.values().length, servicios.size());

        for (TipoServicio tipo : TipoServicio.values()) {

            servicios = controller.findByTipo(tipo);
            assertEquals(1, servicios.size());
        }
    }

    @Test(expected = PersistenceException.class)
    public void testValidation() {

        ServicioJpaController controller = new ServicioJpaController();

        Servicio servicio = new Servicio();
        controller.persist(servicio);
        
    }
}
