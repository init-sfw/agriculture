package ar.com.init.agros.license;

import ar.com.init.agros.view.Application;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.license.NoLicenseInstalledException;
import de.schlichtherle.util.ObfuscatedString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Class LicenseVerifier
 *
 *
 * @author gmatheu
 * @version 08/08/2009 
 */
public class LicenseVerifier {

    private static final Logger logger =  Logger.getLogger(LicenseVerifier.class.getName());
    private static final LicenseVerifier instance = new LicenseVerifier();
    private LicenseManager licenseManager;
    private static final String INSTALLED_LICENSE_FILE =
            "." + File.separator + "osiris.lic";
    public static final String PUBLIC_KEYS_FILE = "etc/publicCerts.osi";
    public static final String PRODUCT_SUBJECT = "Osiris v1.0";
    private static final String MASTER_LICENSE_HASH = "b102cfbab69be6124d4041d2322b24cf";

    /** Constructor por defecto de LicenseVerifier */
    private LicenseVerifier() {
    }

    public static LicenseVerifier getInstance() {
        return instance;
    }

    public void installLicense() throws Exception {
        installLicense(INSTALLED_LICENSE_FILE);
    }

    public void installLicense(String file) throws Exception {
        
        File licenseFile = new File(file);
        if(!isMasterLicense(file))
        {       
            licenseManager = new LicenseManager(createLicenseParam());
            licenseManager.install(licenseFile);
        }else
        {
            logger.fine("Using master license");
        }
        File installedFile = new File(INSTALLED_LICENSE_FILE);

        if (!installedFile.exists()) {
            FileChannel in = (new FileInputStream(licenseFile)).getChannel();
            FileChannel out = (new FileOutputStream(installedFile)).getChannel();
            in.transferTo(0, licenseFile.length(), out);
            in.close();
            out.close();
        }
    }

    public void uninstallLicense(String file) throws Exception {
        licenseManager.uninstall();
    }

    public void verifyLicense() throws Exception, NoLicenseInstalledException {
        boolean isMaster = isMasterLicense(INSTALLED_LICENSE_FILE);

        if (!isMaster) {
           LicenseContent lc = licenseManager.verify();
           logger.log(Level.SEVERE, "{0} licensed for use on up to {1} {2}", new Object[]{lc.getSubject(), lc.getConsumerAmount(), lc.getConsumerType()});
        }else
        {
            logger.fine("Using master license");
        }
    }

    private boolean isMasterLicense(String file) throws IOException, FileNotFoundException {
        File licenseFile = new File(file);
        FileChannel fc = (new FileInputStream(licenseFile)).getChannel();
        byte[] bytes = new byte[(int) fc.size()];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        fc.read(bb);
        String hash = DigestUtils.md5Hex(bytes);       
        fc.close();
        boolean isMaster = hash.equals(MASTER_LICENSE_HASH);
        return isMaster;
    }

    public String generateUniqueKey() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs =
                    "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + "C" + "\")\n" + "Wscript.Echo objDrive.SerialNumber";  // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            Logger.getLogger(LicenseVerifier.class.getName()).log(Level.SEVERE, e.getMessage(), e);

        }

        result = DigestUtils.md5Hex(result);
        result = result.toUpperCase();

        StringBuilder buff = new StringBuilder(result);

        for (int i = buff.length() - 4; i > 0; i = i - 4) {
            buff.insert(i, "-");
        }

        return buff.toString();
    }

    public LicenseParam createLicenseParam() {
        final KeyStoreParam publicKeyStoreParam = new KeyStoreParam() {

            @Override
            public InputStream getStream() throws IOException {
                final String resourceName = PUBLIC_KEYS_FILE;
                final InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
                if (in == null) {
                    throw new FileNotFoundException(resourceName);
                }
                return in;
            }

            @Override
            public String getAlias() {
                return new ObfuscatedString(
                        new long[]{0x7EA563909082584BL, 0xED94506C09E16A28L, 0x80F968E843E57655L}).toString(); /* => "osirisPublicCert" */
            }

            @Override
            public String getStorePwd() {
                return new ObfuscatedString(
                        new long[]{0xC579552F883B2754L, 0x4CA65537938F47EAL, 0x487BCB3B0F3ACECBL}).toString(); /* => "rnh7659wS3AK8Gz" */
            }

            @Override
            public String getKeyPwd() {
                // These parameters are not used to create any licenses.
                // Therefore there should never be a private key in the keystore
                // entry. To enforce this policy, we return null here.
                return null; // causes failure if private key is found in this entry
            }
        };

        final CipherParam cipherParam = new CipherParam() {

            @Override
            public String getKeyPwd() {
                return getInstance().generateUniqueKey();
            }
        };

        return new LicenseParam() {

            @Override
            public String getSubject() {
                return PRODUCT_SUBJECT;
            }

            @Override
            public Preferences getPreferences() {
                return Preferences.systemNodeForPackage(Application.class);
            }

            @Override
            public KeyStoreParam getKeyStoreParam() {
                return publicKeyStoreParam;
            }

            @Override
            public CipherParam getCipherParam() {
                return cipherParam;
            }
        };
    }
}
