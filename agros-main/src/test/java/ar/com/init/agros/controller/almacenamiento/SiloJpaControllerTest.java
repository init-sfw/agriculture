package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.model.almacenamiento.Silo;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class SiloJpaControllerTest extends AlmacenamientoJpaControllerTest {

    public SiloJpaControllerTest() {
    }

    @Test
    public void persist() {

        SiloJpaController controller = new SiloJpaController();

        Silo silo = new Silo();
        silo.setNombre("silo");
        controller.persist(silo);

        silo = new Silo();
        silo.setNombre("silo2");
        controller.persist(silo);

        silo = controller.findByNombre("silo");
        assertNotNull(silo);

        List<Silo> silos = controller.findEntities();
        assertEquals(2, silos.size());
    }
}
