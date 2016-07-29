package ar.com.init.agros.controller.inventario.granos;

import ar.com.init.agros.controller.almacenamiento.ValorAlmacenamientoJpaController;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import java.util.Date;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.controller.BaseEntityJpaControllerTest;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Almacenamiento;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.ValorCereal;
import ar.com.init.agros.model.almacenamiento.ValorSemilla;
import ar.com.init.agros.model.inventario.cereales.DetalleIngresoCereal;
import ar.com.init.agros.model.inventario.granos.CancelacionEgresoGrano;
import ar.com.init.agros.model.inventario.granos.CancelacionIngresoGrano;
import ar.com.init.agros.model.inventario.granos.EgresoGrano;
import ar.com.init.agros.model.inventario.granos.IngresoGrano;
import ar.com.init.agros.model.inventario.semillas.DetalleIngresoSemilla;
import ar.com.init.agros.model.servicio.TipoServicio;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class MovimientoGranoJpaControllerTest extends BaseEntityJpaControllerTest {

    public MovimientoGranoJpaControllerTest() {
    }

    /**
     * Test of persist method, of class MovimientoGranoJpaController.
     */
    @Test
    public void testPersist_IngresoGrano() throws Exception {

        Cultivo cultivo = persistCultivo("cultivo");
        VariedadCultivo variedad = persistVariedadCultivo("var", "var1");

        Deposito depo = persistDeposito("depoG");
        Silo silo = persistSilo("siloG");

        Servicio proveedor = persistServicio("provG", TipoServicio.PROVEEDOR_INSUMOS);

        IngresoGrano ingreso = new IngresoGrano();
        DetalleIngresoCereal detalleCereal1 = createDetalleIngresoCereal(proveedor, cultivo, silo);
        DetalleIngresoCereal detalleCereal2 = createDetalleIngresoCereal(proveedor, cultivo, silo);
        DetalleIngresoSemilla detalleSemilla1 = createDetalleIngresoSemilla(proveedor, variedad, depo);
        DetalleIngresoSemilla detalleSemilla2 = createDetalleIngresoSemilla(proveedor, variedad, depo);
        DetalleIngresoSemilla detalleSemillaSilo = createDetalleIngresoSemilla(proveedor, variedad, silo);


        ingreso.addDetalle(detalleCereal1);
        ingreso.addDetalle(detalleCereal2);
        ingreso.addDetalle(detalleSemilla1);
        ingreso.addDetalle(detalleSemilla2);
        ingreso.addDetalle(detalleSemillaSilo);
        ingreso.setFecha(new Date());

//        DetalleMovimientoGranoJpaController controller = new DetalleMovimientoGranoJpaController();
//        controller.persist(detalleSemilla);
//        controller.persist(detalleCereal);

        MovimientoGranoJpaController controller = new MovimientoGranoJpaController(IngresoGrano.class);
        controller.persist(ingreso);

        ValorAlmacenamientoJpaController vaController = new ValorAlmacenamientoJpaController(ValorAgroquimico.class);
        ValorCereal valorCereal = vaController.find(silo, cultivo);
        assertEquals(new Double(20), valorCereal.getStockActual().getValor());

        ValorSemilla valorsemilla = vaController.find(depo, variedad);
        assertEquals(new Double(40), valorsemilla.getStockActual().getValor());

       assertEquals(1, controller.findEntities().size());
       assertEquals(5, controller.findEntities().get(0).getDetalles().size());
    }

    protected DetalleIngresoSemilla createDetalleIngresoSemilla(Servicio proveedor, VariedadCultivo variedad, Almacenamiento alm) {
        DetalleIngresoSemilla detalleSemilla = new DetalleIngresoSemilla();
        detalleSemilla.setCantidad(new ValorUnidad(20D, MagnitudEnum.PESO.patron()));
        detalleSemilla.setServicio(proveedor);
        detalleSemilla.setValor(new ValorSemilla(variedad, alm, MagnitudEnum.PESO.patron()));
        detalleSemilla.setFecha(new Date());
        return detalleSemilla;
    }

    protected DetalleIngresoCereal createDetalleIngresoCereal(Servicio proveedor, Cultivo cultivo, Silo silo) {
        DetalleIngresoCereal detalleCereal = new DetalleIngresoCereal();
        detalleCereal.setCantidad(new ValorUnidad(10D, MagnitudEnum.PESO.patron()));
        detalleCereal.setServicio(proveedor);
        detalleCereal.setValor(new ValorCereal(cultivo, silo, MagnitudEnum.PESO.patron()));
        detalleCereal.setFecha(new Date());
        return detalleCereal;
    }

    /**
     * Test of persist method, of class MovimientoGranoJpaController.
     */
    @Test
    public void testPersist_EgresoGrano() throws Exception {
        System.out.println("persist");
        EgresoGrano entity = null;
        MovimientoGranoJpaController instance = new MovimientoGranoJpaController();
        instance.persist(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of persist method, of class MovimientoGranoJpaController.
     */
    @Test
    public void testPersist_CancelacionIngresoGrano() throws Exception {
        System.out.println("persist");
        CancelacionIngresoGrano entity = null;
        MovimientoGranoJpaController instance = new MovimientoGranoJpaController();
        instance.persist(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of persist method, of class MovimientoGranoJpaController.
     */
    @Test
    public void testPersist_CancelacionEgresoGrano() throws Exception {
        System.out.println("persist");
        CancelacionEgresoGrano entity = null;
        MovimientoGranoJpaController instance = new MovimientoGranoJpaController();
        instance.persist(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
