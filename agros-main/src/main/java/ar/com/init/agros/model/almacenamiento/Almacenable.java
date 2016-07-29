package ar.com.init.agros.model.almacenamiento;

import ar.com.init.agros.model.terreno.Campo;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gmatheu
 */
public interface Almacenable {

    public Set<Campo> getCampos();

    void setCampos(Set<Campo> campos);

    List<ValorSemilla> getValoresSemilla();

    void setValoresSemilla(List<ValorSemilla> valoresSemilla);

    boolean addCampo(Campo campo);

    boolean removeCampo(Campo campo);

    void removeAllCampos();

    abstract String getTipoAlmacenamiento();

    List<Object> getTableLine();

    String entityName();
}
