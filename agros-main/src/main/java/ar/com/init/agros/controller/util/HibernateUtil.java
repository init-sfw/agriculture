/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.controller.util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author gmatheu
 */
public class HibernateUtil
{

    /**
     * Devuelve una nueva Session abierta de Hibernate.
     * Para ser utilizado SOLO cuando se necesite una Session de Hibernate.
     * Por ej para crear un reporte con HQL en DynamicJasper
     *
     * @return la Session de hibernate
     */
    public static Session getHibernateSession()
    {
        return ((HibernateEntityManager) EntityManagerUtil.createEntityManager()).getSession();
    }

    /**
     *
     * Metodo que devuelve una consulta en base a la cadena pasada por parametro para ser ejecutada directamente.
     *
     * //TODO: Verificar que el em no quede abierto.
     *
     * @param query Consulta HQL
     * @return Hibernate Query para ser ejecutada directamente
     */
    public Query createQuery(String query)
    {
        return getHibernateSession().createQuery(query);
    }
}
