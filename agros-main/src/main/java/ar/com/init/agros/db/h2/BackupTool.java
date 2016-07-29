package ar.com.init.agros.db.h2;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase BackupTool
 * MUY RUSTICA Clase para realizar respaldos y restauraciones de una base de datos H2.
 *
 * @author gmatheu
 * @version 08/07/2009 
 */
public class BackupTool
{

    public static void backup(String path) throws SQLException
    {
        Logger.getLogger(BackupTool.class.getName()).info("Starting Database Backup");

        executeSQLCommand("SCRIPT DROP BLOCKSIZE 40960 TO '" + path + "' COMPRESSION ZIP");

        Logger.getLogger(BackupTool.class.getName()).info("Database Backup Completed");
    }

    public static String getBackupFile()
    {
        String backupFile = EntityManagerUtil.PROPERTIES.getProperty(EntityManagerUtil.URL);
        backupFile = backupFile.substring(backupFile.lastIndexOf(":") + 1) + "DB.zip";
        return backupFile;
    }

    public static void restore(String path) throws SQLException
    {
        Logger.getLogger(BackupTool.class.getName()).info("Starting Database Restore");

        executeSQLCommand("RUNSCRIPT FROM '" + path + "' COMPRESSION ZIP");

        Logger.getLogger(BackupTool.class.getName()).info("Database Restored");
    }

    public static void dropAllObjects() throws SQLException
    {
//        Logger.getLogger(BackupTool.class.getName()).info("DROPING ALL OBJECTS");
//
//        executeSQLCommand("DROP ALL OBJECTS");
//
//        Logger.getLogger(BackupTool.class.getName()).info("ALL OBJECTS DROPPED");
    }

    private static void executeSQLCommand(String sqlCommand) throws SQLException
    {
        Connection conn = null;
        try {
            org.h2.Driver.load();
            String url = EntityManagerUtil.PROPERTIES.getProperty(EntityManagerUtil.URL);
            String user = EntityManagerUtil.PROPERTIES.getProperty(EntityManagerUtil.USERNAME);
            String pass = EntityManagerUtil.PROPERTIES.getProperty(EntityManagerUtil.PASSWORD);
            conn = DriverManager.getConnection(url, user, pass);
            Statement stat = conn.createStatement();
            stat.execute(sqlCommand);

        }
        catch (SQLException ex) {
            Logger.getLogger(BackupTool.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex) {
                    Logger.getLogger(BackupTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
