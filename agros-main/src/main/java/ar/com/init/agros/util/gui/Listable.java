package ar.com.init.agros.util.gui;

/**
 * Clase de interfaz Listable la cual otorga el comportamiento a una clase para poder ser 
 * listada en un componente swing JList.
 *
 * @author FedericoSebastián Bobbio - init() software
 * @version Agosto de 2007
 */
public interface Listable
{
    /** Método que debe redefinirse en la clase que implemente la interfaz Listable y debería devolver
     *  una cadena con los datos que se desean mostrar en una línea del JList para representar a una 
     *  instancia particular de dicha clase.
     *  @return la cadena con los datos que se desean mostrar en una línea del JList
     */
    public String getListLine();
}
