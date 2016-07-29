package ar.com.init.agros.model.inventario;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import java.util.UUID;

/**
 *
 * @author gmatheu
 */
public enum TipoMovimientoStockEnum
{
    INGRESO(TipoMovimientoStock.INGRESO_VALUE, new TipoMovimientoStock(new UUID(0, 1), "Ingreso", "Ingreso al stock")),
    CANCELACION(TipoMovimientoStock.CANCELACION_VALUE, new TipoMovimientoStock(new UUID(0, 2), "Cancelación", "Cancelar ingreso al stock")),
    AJUSTE(TipoMovimientoStock.AJUSTE_VALUE, new TipoMovimientoStock(new UUID(0, 3), "Ajuste", "Ajuste por control de inventario")),
    MOVIMIENTO_DEPOSITO(TipoMovimientoStock.MOVIMIENTO_DEPOSITO_VALUE, new TipoMovimientoStock(new UUID(0, 4), "Movimiento entre Depositos", "Movimiento entre Depositos")),
    OTRO(TipoMovimientoStock.OTRO_VALUE, new TipoMovimientoStock(new UUID(0, 5), "Otro", "Otro movimiento")),
    EGRESO(TipoMovimientoStock.EGRESO_VALUE, new TipoMovimientoStock(new UUID(0, 6), "Egreso", "Egreso"));
    
    private final TipoMovimientoStock tipo;
    private final String value;

    TipoMovimientoStockEnum(String value, TipoMovimientoStock tipo)
    {
        this.tipo = tipo;
        this.value = value;
    }

    public final String value()
    {
        return value;
    }

    public final TipoMovimientoStock tipo()
    {
        return EntityManagerUtil.createEntityManager().merge(tipo);
    }
}
