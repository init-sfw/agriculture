/**
 * Clase Telefono que maneja a una línea particular de teléfono
 *
 * @author FedericoSebastián Bobbio - init() software
 * @version Agosto de 2007
 */
package ar.com.init.agros.model;

import ar.com.init.agros.model.base.BaseEntity;
import javax.persistence.Entity;

@Entity
public class Telefono extends BaseEntity
{

    private static final long serialVersionUID = -1L;
    private String tipo;
    private String caracteristica;
    private String numero;

    /** Constructor de Telefono sin parámetros */
    public Telefono()
    {
        super();
    }

    /** Constructor de Telefono completo
     * @param pTipo el tipo de teléfono
     *  @param pCaracteristica la caracteristica zonal para el teléfono
     *  @param pNumero el número de la línea de teléfono
     */
    public Telefono(String pTipo, String pCaracteristica, String pNumero)
    {
        super();
        tipo = pTipo;
        caracteristica = pCaracteristica;
        numero = pNumero;
    }

    /** Método que devuelve el tipo de teléfono
     *  @return el tipo de teléfono
     */
    public String getTipo()
    {
        return tipo;
    }

    /** Método que setea el tipo de teléfono
     *  @param tipo el tipo de teléfono
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    /** Método que devuelve la caracteristica zonal para el teléfono
     *  @return la caracteristica zonal para el teléfono
     */
    public String getCaracteristica()
    {
        return caracteristica;
    }

    /** Método que setea la caracteristica zonal para el teléfono
     *  @param caracteristica la caracteristica zonal para el teléfono
     */
    public void setCaracteristica(String caracteristica)
    {
        this.caracteristica = caracteristica;
    }

    /** Método que devuelve el número de la línea de teléfono
     *  @return el número de la línea de teléfono
     */
    public String getNumero()
    {
        return numero;
    }

    /** Método que setea el número de la línea de teléfono
     *  @param numero el número de la línea de teléfono
     */
    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    @Override
    public String toString()
    {
        return "(" + caracteristica + ") " + numero;
    }

    @Override
    public String entityName()
    {
        return "Teléfono";
    }
}
