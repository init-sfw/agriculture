package ar.com.init.agros.model.inventario;

import ar.com.init.agros.model.base.TablizableEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Clase MovimientoStock
 *
 *
 * @author gmatheu
 * @version 08/12/2010
 */
@MappedSuperclass
public class MovimientoStockAlmacenamiento<T extends DetalleMovimientoStockAlmacenamiento> extends TablizableEntity {

    private static final long serialVersionUID = -1L;
    private Date fecha;
    private Date fechaCreacion;
    private String observaciones;
    protected List<T> detalles;
    public static final String[] TABLE_HEADERS = {"Fecha de ingreso", "Fecha de registro", "Observaciones", "Cantidad de detalles"};

    /** Constructor por defecto de MovimientoStock */
    public MovimientoStockAlmacenamiento() {
        super();
        setFechaCreacion(new Date());
    }

    @Transient
    public List<T> getDetalles() {
        if (detalles == null) {
            detalles = new ArrayList<T>();
        }
        return detalles;
    }

    /**
     * Devuelve la MISMA lista que getDetalles, pero casteados al tipo especifico de la clase T de la
     * especificado en la clase que la implementa
     * @return
     */
    @Transient
    @SuppressWarnings("unchecked")
    public List<T> getCastedDetalles() {
        return (List<T>) getDetalles();

    }

    public void addDetalle(T detalle) {
        getDetalles().add(detalle);
    }

    @SuppressWarnings("unchecked")
    public void setCastedDetalles(List<T> detalles) {
        this.detalles = (List<T>) detalles;
    }

    public void setDetalles(List<T> detalles) {
        this.detalles = detalles;
    }

    public int contarDetalles() {
        if (detalles == null) {
            return 0;
        } else {
            int c = detalles.size();

            for (DetalleMovimientoStockAlmacenamiento detalle : detalles) {
                if (detalle.isCancelado()) {
                    c--;
                }
            }

            return c;
        }
    }

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    protected void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Length(min = 0, max = 1024)
    @Column(length = 1024)
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String entityName() {
        return "Movimiento de stock";
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> aux = new ArrayList<Object>(4);
        aux.add(fecha);
        aux.add(fechaCreacion);
        aux.add(observaciones);
        aux.add(contarDetalles());
        return aux;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }
}
