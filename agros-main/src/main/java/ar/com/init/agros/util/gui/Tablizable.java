package ar.com.init.agros.util.gui;

import java.util.List;
import javax.swing.table.TableModel;

/**
 * Clase de interfaz Tablizablela cual otorga el comportamiento a una clase para poder ser 
 * mapeada en un componente swing JTable.
 *
 * @author Federico Sebasti�n Bobbio - init() software
 * @version 6 de septiembre de 2007
 */
public interface Tablizable
{
    /** M�todo que debe redefinirse en la clase que implemente la interfaz Tablizable y deber�a devolver
     *  un vector con los datos que se desean mostrar en una JTable para representar a una 
     *  instancia particular de dicha clase.
     *  @return un vector de vectores con los datos que se desean mostrar en un JTable
     */
    List<Object> getTableLine();
    
    /** M�todo que debe redefinirse en la clase que implemente la interfaz Tablizable y deber�a devolver 
     *  el vector conteniendo los nombres de las cabeceras de las columnas
     * @return el vector conteniendo los nombres de las cabeceras
     */
    String[] getTableHeaders();

    /** M�todo en el cual debe definirse el TableModel para las columnas de la tabla
     *  correspondientes a los datos a mostrar de una entidad
     * @return el TableModel creado para una entidad
     */
    TableModel getTableModel();

}
