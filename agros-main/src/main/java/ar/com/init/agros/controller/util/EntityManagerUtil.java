package ar.com.init.agros.controller.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * Clase de ayuda para obtener conexiones a la base de datos.
 *
 *
 * @author gmatheu
 */
public class EntityManagerUtil {

    private static final Logger logger = Logger.getLogger(EntityManagerUtil.class.getName());
    public static final String PU_NAME = "agrosPersistenceUnit";
    private static EntityManagerFactory emf = null;
    private static EntityManager em;
    public static final String URL = "hibernate.connection.url";
    public static final String USERNAME = "hibernate.connection.username";
    public static final String PASSWORD = "hibernate.connection.password";
    public static final String HBM2DDL = "hibernate.hbm2ddl.auto";
    public static final String SHOW_SQL = "hibernate.show_sql";
    public static final Properties PROPERTIES = new Properties();

    private static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {

            InputStream is = null;
            try {
                is = ClassLoader.getSystemResourceAsStream("etc/db.properties");
                if (is != null) {
                    PROPERTIES.load(is);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            }

            logger.info(String.format("Starting database: %s", PROPERTIES.get(URL)));
            emf = Persistence.createEntityManagerFactory(PU_NAME, PROPERTIES);
        }
        return emf;
    }

    /**
     * Crea un EntityManager basado en el Persistene Unit predeterminado.
     *
     * @return
     */
    public static EntityManager createEntityManager() {

        em = getEntityManagerFactory().createEntityManager();

        return em;
    }

    public static CriteriaBuilder getCriteriaBuilder() {
        return getEntityManagerFactory().getCriteriaBuilder();
    }

    public static void closeEntityManager() {
        if (em != null) {
            try {
                em.flush();
            } catch (TransactionRequiredException tre) {
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
