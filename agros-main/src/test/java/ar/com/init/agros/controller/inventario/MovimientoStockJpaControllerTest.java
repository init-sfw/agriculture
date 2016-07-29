package ar.com.init.agros.controller.inventario;

import ar.com.init.agros.controller.BaseEntityJpaControllerTest;
import ar.com.init.agros.controller.almacenamiento.DepositoJpaController;
import ar.com.init.agros.controller.almacenamiento.ValorAlmacenamientoJpaController;
import ar.com.init.agros.controller.inventario.agroquimicos.MovimientoStockJpaController;
import ar.com.init.agros.model.inventario.agroquimicos.DetalleIngresoStock;
import ar.com.init.agros.model.inventario.agroquimicos.IngresoStock;
import ar.com.init.agros.model.servicio.Servicio;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.ValorAgroquimico;
import ar.com.init.agros.model.servicio.TipoServicio;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class MovimientoStockJpaControllerTest extends BaseEntityJpaControllerTest {

    private static final Logger logger = Logger.getLogger(MovimientoStockJpaControllerTest.class.getName());
    private static Agroquimico a1;
    private static Agroquimico a2;
    private static Deposito d1;
    private static Deposito d2;
    private static Servicio prov1;

    public MovimientoStockJpaControllerTest() {
    }

    /**
     * Test of persist method, of class MovimientoStockJpaController.
     */
    @Test
    public void testPersist_IngresoStock() {
        a1 = persistAgroquimico();
        a2 = persistAgroquimico();
        d1 = persistDeposito("depoIngreso1");
        d2 = persistDeposito("depoIngreso2");
        prov1 = persistServicio("provIngreso1", TipoServicio.PROVEEDOR_INSUMOS);

        IngresoStock ingreso1 = new IngresoStock();
        ingreso1.setFecha(new Date());

        DetalleIngresoStock di1 = new DetalleIngresoStock();
        di1.setCantidad(new ValorUnidad(10D, a1.getUnidad()));
        di1.setValor(a1.getValorDeposito(d1));
        di1.setFecha(new Date());
        di1.setProveedor(prov1);
        di1.setCostoTotal(new ValorMoneda(10D));

        DetalleIngresoStock di2 = new DetalleIngresoStock();
        di2.setCantidad(new ValorUnidad(20D, a1.getUnidad()));
        di2.setValor(a1.getValorDeposito(d2));
        di2.setFecha(new Date());
        di2.setProveedor(prov1);
        di2.setCostoTotal(new ValorMoneda(10D));

        ingreso1.getDetalles().add(di1);
        ingreso1.getDetalles().add(di2);

        MovimientoStockJpaController controller = new MovimientoStockJpaController();
        try {
            controller.persist(ingreso1);
        } catch (Exception ex) {
            Logger.getLogger(MovimientoStockJpaControllerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
        controller.close();

        ValorAlmacenamientoJpaController<ValorAgroquimico> vaController = new ValorAlmacenamientoJpaController<ValorAgroquimico>(ValorAgroquimico.class);
        ValorAgroquimico valorAgroquimico1 = vaController.find(d1, a1);
        assertEquals(new Double(10), valorAgroquimico1.getStockActual().getValor());

        ValorAgroquimico valorAgroquimico2 = vaController.find(d2, a1);
        assertEquals(new Double(20), valorAgroquimico2.getStockActual().getValor());
        vaController.getEntityManager().close();

        DepositoJpaController depoController = new DepositoJpaController();
        d1 = depoController.findByNombre("depoIngreso1");
        assertEquals(new Double(10), d1.getValorDeposito(a1).getStockActual().getValor());
        d2 = depoController.findByNombre("depoIngreso2");
        assertEquals(new Double(20), d2.getValorDeposito(a1).getStockActual().getValor());

        depoController.getEntityManager().close();
    }

    /**
     * Test of persist method, of class MovimientoStockJpaController.
     */
    @Test
    public void testPersist_MovimientoDeposito() {
    }

    /**
     * Test of persist method, of class MovimientoStockJpaController.
     */
    @Test
    public void testPersist_CancelacionIngresoStock() {
    }

    /**
     * Test of persist method, of class MovimientoStockJpaController.
     */
    @Test
    public void testPersist_AjusteInventario() {
    }
}
