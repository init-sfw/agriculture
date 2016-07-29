package ar.com.init.agros.conf;

import ar.com.init.agros.controller.ConfigurationJpaController;
import ar.com.init.agros.email.EmailManager;
import java.util.logging.Logger;

/**
 * Clase ConfMgr
 * Clase que maneja configuraciones generales de la aplicacion
 *
 * @author gmatheu
 * @version 13/07/2009 
 */
public class ConfMgr
{

    private static ConfMgr instance;
    private static Logger log = Logger.getLogger(ConfMgr.class.getName());
    private ConfigurationJpaController configurationController;

    /////////////////////////////////////////////////////
    /** Constructor por defecto de ConfMgr */
    private ConfMgr()
    {
        super();
        configurationController = new ConfigurationJpaController();
    }

    public static ConfMgr getInstance()
    {
        if (instance == null) {
            instance = new ConfMgr();
        }
        return instance;
    }

    public ConfigurationJpaController getController()
    {
        return configurationController;
    }

    /**
     * Carga todos los valores por defecto de las variables configurables de la aplicacion
     */
    public void loadConfiguration()
    {
        log.info("Loading Configuration");

        EmailManager.getInstance().loadConfiguration(this);

        GuiaFitosanitariaMgr.loadConfiguration(this);

        log.info("Configuration Loaded");
    }
}

