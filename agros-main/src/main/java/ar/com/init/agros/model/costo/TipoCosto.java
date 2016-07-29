package ar.com.init.agros.model.costo;

import ar.com.init.agros.model.Divisa;
import ar.com.init.agros.model.MonedaMedida;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.base.NamedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.NotNull;

/**
 * Clase TipoCosto
 *
 *
 * @author gmatheu
 * @version 11/07/2009 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "tipo"}))
public class TipoCosto extends NamedEntity
{

    private static final long serialVersionUID = -1L;
    public static final String ENTITY_NAME = "Tipo de Costo";
    public static final String[] TABLE_HEADERS_TIPO_COSTO = {"Nombre", "Descripción", "Tipo", "Unidad Medida"};
    public static final TipoCosto TIPO_COSTO_AGROQUIMICO = new TipoCosto(new UUID(9999, 100),
            "Agroquímicos",
            "Costos de Agroquímicos");
    private MonedaMedida unidadMedida;
    private TipoTipoCosto tipo;

    /** Constructor por defecto de TipoCosto */
    public TipoCosto()
    {
        super();
    }

    public TipoCosto(UUID uuid, String nombre, String desc)
    {
        super(uuid, nombre, desc);
    }

    @NotNull
    public TipoTipoCosto getTipo()
    {
        return tipo;
    }

    public void setTipo(TipoTipoCosto tipo)
    {
        this.tipo = tipo;
    }

    @Embedded
    @AssociationOverrides({
        @AssociationOverride(name = "unidad", joinColumns = @JoinColumn(name = "unidadMed_id")),
        @AssociationOverride(name = "divisa", joinColumns = @JoinColumn(name = "divisaMed_id"))
    })
    @NotNull
    public MonedaMedida getUnidadMedida()
    {
        return unidadMedida;
    }

    public void setUnidadMedida(MonedaMedida unidadMedida)
    {
        this.unidadMedida = unidadMedida;
    }

    public void setUnidad(String id)
    {
        if (id != null && id.length() > 0) {
            if (unidadMedida == null) {
                UUID uuid = new UUID(0, Integer.parseInt(id));

                if (UnidadMedida.getDolarPorHa().getUUID().equals(uuid)) {
                    unidadMedida = new MonedaMedida(UnidadMedida.getDolarPorHa());
                }
                else if (UnidadMedida.getDolarPorQuintal().getUUID().equals(uuid)) {
                    unidadMedida = new MonedaMedida(UnidadMedida.getDolarPorQuintal());
                }
                else if (UnidadMedida.getDolarPorTonelada().getUUID().equals(uuid)) {
                    unidadMedida = new MonedaMedida(UnidadMedida.getDolarPorTonelada());
                }
            }
        }
    }

    public void setDivisa(String id)
    {
        if (id != null && id.length() > 0) {
            if (unidadMedida == null) {
                UUID uuid = new UUID(0, Integer.parseInt(id));
                if (Divisa.getPatron().getUUID().equals(uuid)) {
                    unidadMedida = new MonedaMedida(Divisa.getPatron());
                }
            }
        }

    }

    public void setTipoId(int tipo)
    {
        this.tipo = TipoTipoCosto.values()[tipo];
    }

    @Override
    public String entityName()
    {
        return ENTITY_NAME;
    }

    @Override
    @Transient
    public String[] getTableHeaders()
    {
        return TABLE_HEADERS_TIPO_COSTO;
    }

    @Override
    @Transient
    public List<Object> getTableLine()
    {
        List<Object> r = new ArrayList<Object>();
        r.add(getNombre());
        r.add(getDescripcion());
        r.add(getTipo());
        r.add(getUnidadMedida());
        return r;
    }

    @Override
    @Transient
    public String getListLine()
    {
        return getNombre();
    }

    public enum TipoTipoCosto
    {

        PULVERIZACION("Pulverización"),
        SIEMBRA("Siembra"),
        POST_COSECHA("Post Cosecha"),
        CAMPANIA("Campaña");
        private String nombre;

        private TipoTipoCosto(String nom)
        {
            this.nombre = nom;
        }

        @Override
        public String toString()
        {
            return nombre;
        }
    }
}

