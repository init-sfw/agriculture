package ar.com.init.agros.controller;

import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.servicio.TipoServicio;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.controller.almacenamiento.SiloJpaController;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.Silo;
import org.hibernate.validator.InvalidStateException;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.preload.Preload;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class BaseEntityJpaControllerTest {

    protected static final Logger logger = Logger.getLogger(BaseEntityJpaControllerTest.class.getName());
    private static EntityManager em;

    public BaseEntityJpaControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            logger.info("Building JPA EntityManager for unit tests");
            em = EntityManagerUtil.createEntityManager();

            Preload preload = new Preload();
            preload.loadApplicationEntities(em);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            fail("Exception during JPA EntityManager instantiation.");
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        logger.info("Shutting down Hibernate JPA layer.");
        if (em != null) {
            em.close();
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    protected Deposito persistDeposito(String name) throws InvalidStateException {
        DepositoJpaController depoController = new DepositoJpaController();
        Deposito deposito = new Deposito();
        deposito.setNombre(name);
        depoController.persist(deposito);
        depoController.close();
        return deposito;
    }

    protected Silo persistSilo(String name) throws InvalidStateException {
        SiloJpaController siloController = new SiloJpaController();
        Silo silo = new Silo();
        silo.setNombre(name);
        siloController.persist(silo);
        siloController.close();
        return silo;
    }

    protected Agroquimico persistAgroquimico() {
        Agroquimico a = new Agroquimico();
        InformacionAgroquimico ia = new InformacionAgroquimico(true);
        ia.setNombreComercial("nombre" + Math.random());
        ia.setConcentracion("conc");
        ia.setCompatibilidad("cimp");
        ia.setPrincipioActivo("pp");
        InformacionAgroquimicoJpaController informacionController = new InformacionAgroquimicoJpaController();
        informacionController.persist(ia);

        AgroquimicoJpaController controller = new AgroquimicoJpaController();


        a.setInformacion(ia);
        a.setUnidad(MagnitudEnum.PESO.patron());
        a.setStockMinimo(new ValorUnidad(10D, MagnitudEnum.PESO.patron()));
        controller.persist(a);
        controller.close();
        return a;
    }

    protected Servicio persistServicio(String nombre, TipoServicio tipoServicio) {
        Servicio s = new Servicio(nombre, "dom1", null, tipoServicio);
        s.setTipo(tipoServicio);

        ServicioJpaController controller = new ServicioJpaController();
        controller.persist(s);
        controller.close();
        return s;
    }

    protected Cultivo persistCultivo(String nombre) {
        Cultivo c = new Cultivo();
        c.setNombre(nombre);
        c.setDescripcion(nombre);

        CultivoJpaController controller = new CultivoJpaController();
        controller.persist(c);
        controller.close();
        return c;
    }

    protected VariedadCultivo persistVariedadCultivo(String cultivo, String variedad) {
        Cultivo c = persistCultivo(cultivo);
        VariedadCultivo v = new VariedadCultivo();
        v.setNombre(variedad);
        v.setDescripcion(variedad);
        v.setCultivo(c);

        VariedadCultivoJpaController controller = new VariedadCultivoJpaController();
        controller.persist(v);

        return v;
    }
}
