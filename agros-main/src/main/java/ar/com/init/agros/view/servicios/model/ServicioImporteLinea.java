package ar.com.init.agros.view.servicios.model;

import ar.com.init.agros.model.ValorMoneda;
import ar.com.init.agros.model.servicio.Servicio;

/**
 * Clase ServicioImporteLinea
 *
 *
 * @author fbobbio
 * @version 22-dic-2010 
 */
public class ServicioImporteLinea 
{

    private Servicio servicio;
    private ValorMoneda importe;

    /** Constructor por defecto de ServicioImporteLinea */
    public ServicioImporteLinea()
    {
    }

    public ServicioImporteLinea(Servicio servicio, ValorMoneda importe)
    {
        this.servicio = servicio;
        this.importe = importe;
    }

    public ValorMoneda getImporte()
    {
        return importe;
    }

    public void setImporte(ValorMoneda importe)
    {
        this.importe = importe;
    }

    public Servicio getServicio()
    {
        return servicio;
    }

    public void setServicio(Servicio servicio)
    {
        this.servicio = servicio;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    public String toString()
    {
        return super.toString();
    }
}
