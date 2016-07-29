package ar.com.init.agros.controller.superficie;

import ar.com.init.agros.controller.BaseEntityJpaControllerTest;
import ar.com.init.agros.controller.CampoJpaController;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.almacenamiento.Silo;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gmatheu
 */
public class CampoJpaControllerTest extends BaseEntityJpaControllerTest {

    public CampoJpaControllerTest() {
    }

    @Test
    public void persist() {
        Silo silo = persistSilo("silo");
        Deposito deposito = persistDeposito("depo");

        CampoJpaController controller = new CampoJpaController();
        Campo campo = new Campo();
        campo.setNombre("campo");
        campo.setSuperficie(new ValorUnidad(10D, MagnitudEnum.SUPERFICIE.patron()));
        Lote lote = new Lote(campo);
        lote.setNombre("lote");
        lote.setSuperficie(new ValorUnidad(10D, MagnitudEnum.SUPERFICIE.patron()));
        campo.addLote(lote);

        campo.addAlmacenamiento(silo);
        campo.addAlmacenamiento(deposito);

        controller.persist(campo);

        List<Campo> campos = controller.findAllEntities();

        for (Campo c : campos) {
            if (c.getNombre().equals("campo")) {
                assertEquals(2, c.getAlmacenamientos().size());
                assertEquals(1, c.getDepositos().size());
                assertEquals(1, c.getSilos().size());
            }
        }
    }
}
