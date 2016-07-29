package ar.com.init.agros.util.gui.updateui;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase UpdatableSubject
 *
 *
 * @author fbobbio
 * @version 12-jul-2009 
 */
public class UpdatableSubject
{
    private static List<UpdatableListener> listenersList;

    /** Método que se encarga de añadir un listener de modificación en base de datos
     *  @param listener el objeto que depende del sujeto
     */
    public static void addUpdatableListener(UpdatableListener listener)
    {
        if (listener == null)
            return;
        if (listenersList == null || listenersList.size() == 0)
        {
            listenersList = new ArrayList<UpdatableListener>();
        }
        listenersList.add(listener);
    }

    public static boolean removeUpdatableListener(UpdatableListener listener)
    {
        if (listenersList == null || listenersList.size() == 0)
            return false;
        else
            return listenersList.remove(listener);
    }

    public static void notifyListeners()
    {
        for (int i = 0; listenersList != null && i < listenersList.size(); i++)
        {
            if (listenersList.get(i) != null && listenersList.get(i).isNowVisible())
            {
                listenersList.get(i).refreshUI();
            }
        }
    }
}
