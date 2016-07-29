package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.ValorUnidad;
import ar.com.init.agros.model.VariedadCultivo;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import org.hibernate.validator.NotNull;

/**
 * Clase ValorSemilla
 * Esta clase contiene los valores de inventario actual para cada semilla en cada almacenamiento.
 * Para evitar tener que recalcular los valores acutales cada vez que se necesiten.
 *
 * @author gmatheu
 * @version 08/12/2010
 */
@Entity
@DiscriminatorValue("S")
public class ValorSemilla<T extends Almacenamiento> extends ValorGrano<T> {

    private static final long serialVersionUID = -1L;
    public static String[] TABLE_HEADERS = {"Variedad", "Almacenamiento", "Stock Actual Almacenamiento"};
    private VariedadCultivo semilla;

    /** Constructor por defecto de ValorDeposito */
    public ValorSemilla() {
        super();
    }

    public ValorSemilla(VariedadCultivo semilla, T almacenamiento, UnidadMedida unidad) {
        super(almacenamiento, unidad);
        this.semilla = semilla;
    }

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    public VariedadCultivo getSemilla() {
        return semilla;
    }

    @Transient
    @Override
    public VariedadCultivo getDetalle() {
        return getSemilla();
    }

    @ManyToOne(targetEntity = Almacenamiento.class)
    @Override
    public T getAlmacenamiento() {
        return super.getAlmacenamiento();
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column =
        @Column(name = "valorStockActual"))
    })
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns =
        @JoinColumn(name = "unidadStockActual_id"))
    })
    @Override
    public ValorUnidad getStockActual() {
        return super.getStockActual();
    }

    public void setSemilla(VariedadCultivo semilla) {
        this.semilla = semilla;
        if (semilla != null) {
            this.setStockActual(new ValorUnidad(0D, MagnitudEnum.PESO.patron()));
        } else {
            setStockActual(null);
        }
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> r = new ArrayList<Object>();
        r.add(this.getSemilla().toString());
        r.add(this.getAlmacenamiento());
        r.add(this.getStockActual());


        return r;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Override
    public String toString() {
        return getSemilla() + " [" + MagnitudEnum.PESO.patron().getAbreviatura() + "] - " + getAlmacenamiento();
    }

    @Override
    @Transient
    public String getListLine() {
        return semilla.toString();
    }

    @Override
    public String entityName() {
        return "Valor Semilla";
    }
}
