package ar.com.init.agros.preload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.apache.commons.digester.Digester;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.xml.sax.SAXException;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.Divisa;
import ar.com.init.agros.model.FormaFumigacionEnum;
import ar.com.init.agros.model.MagnitudEnum;
import ar.com.init.agros.model.UnidadMedida;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.inventario.TipoMovimientoStockEnum;
import ar.com.init.agros.model.util.Configuration;

/**
 * Clase Preload
 *
 * Sirve para cargar en la base de datos los objetos por defecto que usa la aplicacion.
 *
 *
 * @author gmatheu
 * @version 02/06/2009
 */
public class Preload {

    private static final Logger log = Logger.getLogger(Preload.class.getName());
    private List<BaseEntity> applicationEntities;
    private List<BaseEntity> otherEntities;
    //CLAVES PARA CONFIGURACION
    public static final String LOADED_FILES = "LOADED_FILES";

    /** Constructor por defecto de Preload */
    public Preload() {
        applicationEntities = new ArrayList<BaseEntity>();
        otherEntities = new ArrayList<BaseEntity>();

        //TODO: Versionar los preloads, para diferente actualizaciones. Usar anotaciones.
        //XXX: Para empezar, agregamos las instancias directamente al vector.

        //INSTANCIAS QUE SON PARTE DE LA APLICACION       

        for (MagnitudEnum magnitudEnum : MagnitudEnum.values()) {
             applicationEntities.add(magnitudEnum.patron());
        }

        applicationEntities.add(Divisa.getPatron());
        applicationEntities.add(UnidadMedida.getDolarPorHa());
        applicationEntities.add(UnidadMedida.getDolarPorQuintal());
        applicationEntities.add(UnidadMedida.getDolarPorTonelada());

        for (TipoMovimientoStockEnum val : TipoMovimientoStockEnum.values()) {
            applicationEntities.add(val.tipo());
        }

        applicationEntities.add(UnidadMedida.getQuintal());

        for (FormaFumigacionEnum forma : FormaFumigacionEnum.values()) {
            applicationEntities.add(forma.formaFumigacion());
        }

    }

    /**
     * Estas entidades se actualizan cada vez que se inicia la aplicacion.
     *
     * @return
     */
    public List<BaseEntity> getRefreshEntities() {
        return otherEntities;
    }

    public void setOtherEntities(List<BaseEntity> otherEntities) {
        this.otherEntities = otherEntities;
    }

    public List<BaseEntity> getApplicationEntities() {
        return applicationEntities;
    }

    /**
     * Guarda solo las entidades correspondientes a la aplicacion.
     *
     * @param em
     */
    public void loadApplicationEntities(EntityManager em) {
        log.info("================LOADING APPLICATION ENTITIES============");
        persist(em, applicationEntities, true);
    }

    /**
     *  Guarda en la base de datos todos los objetos en entities.
     *
     */
    @SuppressWarnings("unchecked")
    public void loadEntities() {
        log.info("===========STARTING PRELOAD============");

        EntityManager em = EntityManagerUtil.createEntityManager();

        loadApplicationEntities(em);

        log.info("===========LOADING OTHER ENTITIES==============");
        loadXmlData(em);

        log.info("===========LOADING OTHER ENTITIES==============");
        persist(em, otherEntities, false);

        em.close();

        log.info("===========FINISHED PRELOAD==============");
    }

    /** M�todo que retorna una descripci�n de la clase
     *  @return descripci�n de la clase
     */
    @Override
    public String toString() {
        return super.toString();
    }

    @SuppressWarnings("unchecked")
    private void persist(EntityManager em, List<BaseEntity> entities, boolean transaction) {
        if (transaction) {
            em.getTransaction().begin();
        }
        try {
            for (BaseEntity entity : entities) {
                BaseEntity found = em.find(entity.getClass(), entity.getId());

                ClassValidator validator = new ClassValidator(entity.getClass());
                List<InvalidValue> invalidValues = Arrays.asList(validator.getInvalidValues(entity));
                if (found == null) {
                    log.log(Level.FINE, "{0} - id: {1}", new Object[]{entity.getClass().getName(), entity.getId()});
                    if (invalidValues.isEmpty()) {
                        em.persist(entity);
                    } else {
                        String msg = "No se pudo insertar por errores de validacion: ";
                        for (InvalidValue invalidValue : invalidValues) {
                            msg += invalidValue.getPropertyName() + " (" + invalidValue.getMessage() + ")";
                        }
                        log.info(msg);
                    }
                } else {
                    em.merge(found);
                }
            }
            if (transaction) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadXmlData(EntityManager em) {
        log.info("================Loading xml data============");

        try {
            Digester digester = new Digester();

            OsirisData.createDigesterRules(digester);

            String file = "OsirisData_v1.xml"; //TODO Cargar multiples archivos

            if (!ConfMgr.getInstance().getController().findValuesByKey(LOADED_FILES).contains(file)) {

                log.log(Level.INFO, "Processing file: {0}", file);
                InputStream is = getClass().getClassLoader().getResourceAsStream("preload/" + file);

                OsirisData osirisData = (OsirisData) digester.parse(is);

                List<BaseEntity> entities = osirisData.getEntities(1L);

                entities.add(new Configuration(LOADED_FILES, file));

                persist(em, entities, true);
            } else {
                log.log(Level.INFO, "Already processed file : {0}", file);
            }

        } catch (SAXException ex) {
            Logger.getLogger(Preload.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Preload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
