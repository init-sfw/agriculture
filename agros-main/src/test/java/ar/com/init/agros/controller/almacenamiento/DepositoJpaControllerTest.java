package ar.com.init.agros.controller.almacenamiento;

import ar.com.init.agros.model.almacenamiento.Deposito;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class DepositoJpaControllerTest extends AlmacenamientoJpaControllerTest {

    public DepositoJpaControllerTest() {
    }

    @Test
    public void persist() {
        DepositoJpaController controller = new DepositoJpaController();

        Deposito deposito = new Deposito();
        deposito.setNombre("depo");
        controller.persist(deposito);

        deposito = new Deposito();
        deposito.setNombre("depo2");
        controller.persist(deposito);

        deposito = controller.findByNombre("depo");
        assertNotNull(deposito);

        List<Deposito> depositos = controller.findEntities();
        assertEquals(2, depositos.size());
    }
}
