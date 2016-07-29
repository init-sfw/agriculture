/**
 * Clase Telefono que maneja a una l�nea particular de tel�fono
 *
 * @author FedericoSebasti�n Bobbio - init() software
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

    /** Constructor de Telefono sin par�metros */
    public Telefono()
    {
        super();
    }

    /** Constructor de Telefono completo
     * @param pTipo el tipo de tel�fono
     *  @param pCaracteristica la caracteristica zonal para el tel�fono
     *  @param pNumero el n�mero de la l�nea de tel�fono
     */
    public Telefono(String pTipo, String pCaracteristica, String pNumero)
    {
        super();
        tipo = pTipo;
        caracteristica = pCaracteristica;
        numero = pNumero;
    }

    /** M�todo que devuelve el tipo de tel�fono
     *  @return el tipo de tel�fono
     */
    public String getTipo()
    {
        return tipo;
    }

    /** M�todo que setea el tipo de tel�fono
     *  @param tipo el tipo de tel�fono
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    /** M�todo que devuelve la caracteristica zonal para el tel�fono
     *  @return la caracteristica zonal para el tel�fono
     */
    public String getCaracteristica()
    {
        return caracteristica;
    }

    /** M�todo que setea la caracteristica zonal para el tel�fono
     *  @param caracteristica la caracteristica zonal para el tel�fono
     */
    public void setCaracteristica(String caracteristica)
    {
        this.caracteristica = caracteristica;
    }

    /** M�todo que devuelve el n�mero de la l�nea de tel�fono
     *  @return el n�mero de la l�nea de tel�fono
     */
    public String getNumero()
    {
        return numero;
    }

    /** M�todo que setea el n�mero de la l�nea de tel�fono
     *  @param numero el n�mero de la l�nea de tel�fono
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
        return "Tel�fono";
    }
}
