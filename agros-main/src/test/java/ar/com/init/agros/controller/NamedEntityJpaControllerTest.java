package ar.com.init.agros.controller;

import javax.persistence.NoResultException;
import org.hibernate.exception.ConstraintViolationException;
import ar.com.init.agros.controller.base.NamedEntityJpaController;
import ar.com.init.agros.model.FormaFumigacion;

import java.sql.Connection;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class NamedEntityJpaControllerTest extends BaseEntityJpaControllerTest {

    private static Logger log = Logger.getLogger(NamedEntityJpaControllerTest.class.getName());
    private static Connection connection;
    private static EntityManager em;
    private static NamedEntityJpaController<FormaFumigacion> controllerFormaFumig;

    public NamedEntityJpaControllerTest() {
        super();
        controllerFormaFumig = new NamedEntityJpaController<FormaFumigacion>(FormaFumigacion.class);
    }

    /**
     * Test of findByNombre method, of class NamedEntityJpaController.
     */
    @Test
    public void testFindByNombre() {
        try {
            FormaFumigacion t = new FormaFumigacion("nombre", "descr");
            controllerFormaFumig.persist(t);

            t = controllerFormaFumig.findByNombre("nombre");
            assertNotNull(t);

            try {
                t = controllerFormaFumig.findByNombre("nombre1");
                fail("Se encontro una entidad que no existe");
            } catch (NoResultException ex) {
            }

        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of existsNombre method, of class NamedEntityJpaController.
     */
    @Test
    public void testExistsNombre() {
        FormaFumigacion t = new FormaFumigacion("existente", "descr");
        controllerFormaFumig.persist(t);

        Boolean b = controllerFormaFumig.existsNombre("existente");
        assertTrue(b);

        b = controllerFormaFumig.existsNombre("NOexistente");
        assertFalse(b);
    }

    /**
     * Test of persist method, of class NamedEntityJpaController.
     */
    @Test
    public void testPersist() {

        controllerFormaFumig.persist(new FormaFumigacion("tipo1", "desc"));

        try {
            controllerFormaFumig.persist(new FormaFumigacion("tipo1", "desc"));
            fail("Nombre ya existe");
        } catch (ConstraintViolationException ex) {
        }

        FormaFumigacion f = controllerFormaFumig.findByNombre("tipo1");
        assertNotNull(f);
    }

    /**
     * Test of update method, of class NamedEntityJpaController.
     */
    @Test
    public void testUpdate() throws Exception {
    }
}
