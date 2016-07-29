package ar.com.init.agros.controller;

import ar.com.init.agros.controller.base.BaseEntityJpaController;
import ar.com.init.agros.model.util.Configuration;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Clase ConfigurationJpaController
 *
 *
 * @author gmatheu
 * @version 13/07/2009 
 */
public class ConfigurationJpaController extends BaseEntityJpaController<Configuration>
{

    /** Constructor por defecto de ConfigurationJpaController */
    public ConfigurationJpaController()
    {
        super(Configuration.class);
    }

    public List<String> findValuesByKey(String... keys)
    {
        List<Configuration> confs = findByKey(keys);

        List<String> r = new ArrayList<String>();

        for (Configuration c : confs) {
            r.add(c.getConfValue());
        }

        return r;
    }

    public String findValueByKey(String... keys)
    {
        Configuration c = findUniqueByKey(keys[0]);
        return (c != null ? c.getConfValue() : null);
    }

    public Configuration findUniqueByKey(String key)
    {
        List<Configuration> confs = findByKey(key);

        if (confs.size() > 0) {
            return confs.get(0);
        }
        else {
            return new Configuration(key, null);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Configuration> findByKey(String... keys)
    {
        return createHibernateQuery("FROM " + Configuration.class.getName() + " WHERE confKey IN (:keys)").getHibernateQuery().setParameterList(
                "keys", keys).list();
    }

    public void removeConfiguration(String key)
    {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        removeConfiguration(em, key);
        em.getTransaction().commit();
    }

    private void removeConfiguration(EntityManager em, String key)
    {
        em.createQuery("DELETE FROM " + Configuration.class.getName() + " WHERE confKey = :key").setParameter(
                "key",
                key).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public void replaceConfiguration(List newValues, String key)
    {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        removeConfiguration(em, key);
        em.getTransaction().commit();

        persist(newValues);
    }

    @SuppressWarnings("unchecked")
    public void replaceConfiguration(Configuration conf)
    {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        removeConfiguration(em, conf.getConfKey());
        em.getTransaction().commit();

        persist(conf);
    }

    public Boolean findBooleanByKey(String key)
    {
        return findBooleanByKey(key, false);
    }

    public Boolean findBooleanByKey(String key, boolean defaultValue)
    {
        try {
            String val = findUniqueByKey(key).getConfValue();

            if (val.length() > 0) {
                return Boolean.parseBoolean(val);
            }
            else {
                return defaultValue;
            }
        }
        catch (Exception ex) {
            return defaultValue;
        }
    }

    public Long findLongByKey(String key)
    {
        try {
            return Long.parseLong(findUniqueByKey(key).getConfValue());
        }
        catch (Exception ex) {
            return 0L;
        }
    }
}
