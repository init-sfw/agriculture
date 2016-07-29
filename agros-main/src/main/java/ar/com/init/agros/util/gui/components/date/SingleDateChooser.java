package ar.com.init.agros.util.gui.components.date;

import com.toedter.calendar.JDateChooser;
import java.util.Date;

/**
 * Clase SingleDateChooser
 *
 *
 * @author gmatheu
 * @version 13/06/2009 
 */
public class SingleDateChooser extends JDateChooser
{

    private static final long serialVersionUID = -1L;

    /** Constructor por defecto de SingleDateChooser */
    public SingleDateChooser()
    { 
        super("dd/MM/yyyy", "##/##/####", '_');
        this.setDate(new Date());
        this.setSize(140, this.getHeight());
    }
}
