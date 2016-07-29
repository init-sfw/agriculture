package ar.com.init.agros.util.db;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Clase para agregar y verificar archivos de version en los backups de base de datos.
 * @author gmatheu
 */
public class VersionChecker {

    private static final Logger logger = Logger.getLogger(VersionChecker.class.getName());
    public static final String VERSION_FILE = "osiris.ver";
    public static final String SCRIPT_FILE = "script.sql";

    public static String checkVersion(String path) {
        return checkVersion(new File(path));
    }

    public static String checkVersion(File file) {
        String r = null;

        try {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(VERSION_FILE);

            if (entry != null) {
                InputStream is = zipFile.getInputStream(entry);

                int c = -1;

                String existingHash = "";
                while ((c = is.read()) != -1) {
                    existingHash += (char) c;
                }
                is.close();

                for (Versions version : Versions.values()) {
                    String hash = calculateHash(version);

                    if (existingHash.equals(hash)) {
                        r = version.id();
                        break;
                    }
                }


            } else {
                ZipEntry scriptEntry = zipFile.getEntry(SCRIPT_FILE);

                if (scriptEntry != null) {
                    r = Versions.V1_0.id();
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (r == null) {
            throw new IllegalArgumentException("Version desconocida");
        }

        return r;
    }

    public static void addVersionEntry(String path, Versions version) {

        String hash = calculateHash(version);

        try {
            File file = new File(path);
            ZipFile zipFile = new ZipFile(file);

            File tempFile = new File(path + ".tmp");
            FileOutputStream fos = new FileOutputStream(tempFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ZipOutputStream zos = new ZipOutputStream(bos);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                zos.putNextEntry(e);
                if (!e.isDirectory()) {
                    copy(zipFile.getInputStream(e), zos);
                }
                zos.closeEntry();
            }


            zos.putNextEntry(new ZipEntry(VERSION_FILE));
            for (byte b : hash.getBytes()) {
                zos.write((char) b);
            }
            zos.closeEntry();

            zos.close();
            zipFile.close();


            file.delete();
            tempFile.renameTo(file);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    private static final byte[] BUFFER = new byte[4096 * 1024];

    /**
     * copy input to output stream - available in several StreamUtils or Streams classes
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(BUFFER)) != -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }

    private static String calculateHash(Versions version) {
        byte[] bytesOfMessage;
        try {
            bytesOfMessage = version.id().getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] digest = md.digest(bytesOfMessage);
            String hash = "";
            for (byte b : digest) {
                hash += b;
            }
            return hash;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
