package ar.com.init.agros.model.servicio;

import ar.com.init.agros.model.Telefono;
import ar.com.init.agros.model.base.TablizableEntity;
import ar.com.init.agros.model.base.type.EnumUserType;
import ar.com.init.agros.util.gui.Listable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.swing.table.TableModel;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

/**
 * Clase Proveedor
 *
 *
 * @author fbobbio
 * @version 31-may-2009 
 */
@TypeDefs({
    @TypeDef(name = "tipoServicio", typeClass = EnumUserType.class,
    parameters = {
        @Parameter(name = "enumClassName", value = "ar.com.init.agros.model.servicio.TipoServicio")})})
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"razonSocial", "tipo"})})
public class Servicio extends TablizableEntity implements Listable {

    private static final long serialVersionUID = -1L;
    public static String[] TABLE_HEADERS = {"Razón Social", "Dirección", "Teléfonos", "Tipo"};
    private String razonSocial;
    private String domicilio;
    private List<Telefono> telefonos;
    private TipoServicio tipo;

    /** Constructor por defecto de Proveedor */
    public Servicio() {
        super();
    }

    public Servicio(String razonSocial, String domicilio, List<Telefono> telefonos) {
        this.razonSocial = razonSocial;
        this.domicilio = domicilio;
        this.telefonos = telefonos;
    }

    public Servicio(String razonSocial, String domicilio, List<Telefono> telefonos, TipoServicio tipo) {
        this.razonSocial = razonSocial;
        this.domicilio = domicilio;
        this.telefonos = telefonos;
        this.tipo = tipo;
    }

    public Servicio(UUID id, String razonSocial, String domicilio, List<Telefono> telefonos) {
        this(razonSocial, domicilio, telefonos);
        setId(id.toString());
    }

    @NotEmpty
    @Length(max = 255)
    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    @Length(max = 255)
    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }

    @Type(type = "tipoServicio")
    @NotNull
    public TipoServicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicio tipo) {
        this.tipo = tipo;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString() {
        return razonSocial + " " + domicilio;
    }

    @Override
    @Transient
    public List<Object> getTableLine() {
        List<Object> ret = new ArrayList<Object>();
        ret.add(razonSocial);
        ret.add(domicilio);
        if (telefonos != null) {
            if (telefonos.size() == 1) {
                ret.add(telefonos.get(0).toString());
            } else {
                ret.add(telefonos.size());
            }
        }
        ret.add(getTipo().nombre());
        return ret;
    }

    @Override
    @Transient
    public String[] getTableHeaders() {
        return TABLE_HEADERS;
    }

    @Transient
    @Override
    public TableModel getTableModel() {
        TableModel ret = new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {null, null, null}
                },
                getTableHeaders()) {

            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        return ret;
    }

    @Override
    @Transient
    public String getListLine() {
        return razonSocial;
    }

    @Override
    public String entityName() {
        return "Servicio";
    }
}
