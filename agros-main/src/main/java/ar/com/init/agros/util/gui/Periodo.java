package ar.com.init.agros.util.gui;

import ar.com.init.agros.util.gui.exceptions.InvalidDateRangeException;
import java.util.Date;

/**
 * Clase Periodo
 *
 *
 * @author fbobbio
 * @version 26-ago-2009 
 */
public class Periodo implements Listable, Comparable<Periodo>
{
    private Date fromDate;
    private Date toDate;

    /** Constructor por defecto de Periodo */
    public Periodo(Date from, Date to) throws InvalidDateRangeException
    {
        if (from == null || to == null || to.before(from))
        {
            throw new InvalidDateRangeException();
        }
        this.fromDate = from;
        this.toDate = to;
    }

    public Date getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(Date fromDate)
    {
        this.fromDate = fromDate;
    }

    public Date getToDate()
    {
        return toDate;
    }

    public void setToDate(Date toDate)
    {
        this.toDate = toDate;
    }

    /** Método que retorna una descripción de la clase
     *  @return descripción de la clase
     */
    @Override
    public String toString()
    {
        return "Desde: " + GUIUtility.toMediumDate(fromDate) + " - Hasta: " + GUIUtility.toMediumDate(toDate);
    }
    public String toShortString()
    {
        return GUIUtility.toMediumDate(fromDate) + " - " + GUIUtility.toMediumDate(toDate);
    }

    @Override
    public String getListLine()
    {
        return toString();
    }

    @Override
    public int compareTo(Periodo o)
    {
        if (this.fromDate.equals(o.fromDate))
        {
            return 0;
        }
        if (this.fromDate.before(o.fromDate))
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
}
