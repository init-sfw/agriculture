/*
 * Application.java
 */
package ar.com.init.agros.view;

import ar.com.init.agros.conf.ConfMgr;
import ar.com.init.agros.conf.GuiaFitosanitariaMgr;
import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.email.EmailManager;
import ar.com.init.agros.preload.Preload;
import ar.com.init.agros.view.configuracion.DialogConfiguracion;
import ar.com.init.agros.view.license.DialogLicencia;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.jdesktop.application.SingleFrameApplication;
import org.xito.dialog.DialogManager;

/**
 * The main class of the application.
 */
public class Application extends SingleFrameApplication
{

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup()
    {
        DialogLicencia.validateLicense(null);
        show(new ApplicationView(this));
    }

    @Override
    protected void ready()
    {
        super.ready();

        if (!GuiaFitosanitariaMgr.checkCarpetaGuia())
        {
            DialogConfiguracion conf = new DialogConfiguracion(this.getMainFrame());
            if (conf.isShowAutomatically())
            {
                conf.setSelectedTab(DialogConfiguracion.TAB_GUIA_FITOSANITARIA);
                conf.setVisible(true);
            }
        }
    }

    @Override
    protected void end()
    {
        try {
            EmailManager.getInstance().setStop(true);
            EmailManager.getInstance().join();
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        EntityManagerUtil.closeEntityManager();
        super.end();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root)
    {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Application
     */
    public static Application getApplication()
    {
        return Application.getInstance(Application.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args)
    {
        try {
            try {
                InputStream is = ClassLoader.getSystemResourceAsStream("etc/logging.properties");
                LogManager.getLogManager().readConfiguration(is);     
            }
            catch (IOException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SecurityException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }

            Preload preload = new Preload();
            preload.loadEntities();

            ConfMgr.getInstance().loadConfiguration();

//            DetalleMovimientoStockJpaController<DetalleMovimientoStock> detalleController = new DetalleMovimientoStockJpaController<DetalleMovimientoStock>(
//                    DetalleMovimientoStock.class);
//            detalleController.ajustarAutomaticamente();

            EmailManager emailManager = EmailManager.getInstance();
            emailManager.start();

            launch(Application.class, args);

        }
        catch (Exception ex) {
            if (ex.getCause() instanceof GenericJDBCException) {
                DialogManager.showError(null, "No se pudo iniciar la aplicación",
                        "Hubo un error en la conexión con la base de datos. Probablemente haya otra instancia de la aplicación corriendo.",
                        ex);
            }
            else {
                DialogManager.showError(null, "No se pudo iniciar la aplicación",
                        "Hubo un error desconicido, por favor envie el contenido de la pestaña detalle a ...",
                        ex);
            }

            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            EntityManagerUtil.closeEntityManager();
        }
    }
}
