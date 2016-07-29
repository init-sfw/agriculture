package ar.com.init.agros.reporting.components;

/**
 * Interface que implementaran los componentes que componen los criterios de un reporte.
 * Incluye metodos para validar su contenido y limipiarlo.
 *
 * @author gmatheu
 * @version 07/07/2009 
 */
public interface ReportCriteria
{
    /**
     * Verifica que la seleccion correspondiente sea valida
     * @return
     */
    public boolean validateSelection();

    /**
     * Vuelve al estado inicial de valores del componente
     *
     */
    public void clear();

    /**
     * El valor que puede ser usado para el titulo de la pestaña
     *
     * @return
     */
    public String getTabTitle();

    /**
     * Mensaje que se mostrara si la validacion falla.
     *
     * @return
     */
    public String getErrorMessage();
}
