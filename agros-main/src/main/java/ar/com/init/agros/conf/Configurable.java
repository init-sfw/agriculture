package ar.com.init.agros.conf;

/**
 * Interfaz Configurable
 * Deben implementarla aquellas clases que necesitan cargar datos de configuracion al inicio de la aplicacion.
 *
 * @author gmatheu
 * @version 23/07/2009 
 */

public interface Configurable 
{
    /**
     * Metodo que debe cargar los valores configuracion necesarios.
     * @param manager
     */
    public void loadConfiguration(ConfMgr manager);
}
