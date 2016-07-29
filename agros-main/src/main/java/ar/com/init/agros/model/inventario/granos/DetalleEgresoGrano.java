package ar.com.init.agros.model.inventario.granos;

import ar.com.init.agros.model.almacenamiento.Almacenable;
import ar.com.init.agros.model.almacenamiento.ValorAlmacenamiento;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Clase DetalleIngresoStock
 *
 *
 * @author gmatheu
 * @version 29/06/2009 
 */
@MappedSuperclass
public abstract class DetalleEgresoGrano<T extends ValorAlmacenamiento, A extends Almacenable, D extends BaseEntity>
        extends DetalleOperacionGrano<T, A, D> {

    private static final long serialVersionUID = -1L;
    public static final String[] TABLE_HEADERS;
    protected static final int CARTA_PORTE_COLUMN = 8;

    static {

        List<String> aux = new ArrayList<String>(Arrays.asList(DetalleOperacionGrano.TABLE_HEADERS));
        aux.set(SERVICIO_COLUMN, "Destino");
        aux.add(CARTA_PORTE_COLUMN, "Carta de Porte");
        TABLE_HEADERS = aux.toArray(new String[0]);
    }
    private String cartaPorte;

    /** Constructor por defecto de DetalleIngresoStock */
    public DetalleEgresoGrano() {
        super();
        setTipo(TipoMovimientoStockEnum.EGRESO.tipo());
        setPositivo(false);
        setCancelado(false);
    }

    public String getCartaPorte() {
        return cartaPorte;
    }

    public void setCartaPorte(String cartaPorte) {
        this.cartaPorte = cartaPorte;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = super.getTableLine();

        r.add(SERVICIO_COLUMN, getCartaPorte());

        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
