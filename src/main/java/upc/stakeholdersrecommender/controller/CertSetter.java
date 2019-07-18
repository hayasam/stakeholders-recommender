package upc.stakeholdersrecommender.controller;

    import java.io.*;
    //VERY IMPORTANT.  SOME OF THESE EXIST IN MORE THAN ONE PACKAGE!
import java.security.GeneralSecurityException;
import java.security.KeyStore;
    import java.security.KeyStoreException;
    import java.security.NoSuchAlgorithmException;
    import java.security.cert.Certificate;
    import java.security.cert.CertificateException;
    import java.security.cert.CertificateFactory;

public class CertSetter {

    public void loadCert() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        //Put everything after here in your function.
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);//Make an empty store
        InputStream fis = new FileInputStream(new File("GloVe_model/openreq_eu.crt"));
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (bis.available() > 0) {
            Certificate cert = cf.generateCertificate(bis);
            trustStore.setCertificateEntry("fiddler" + bis.available(), cert);
        }

    }
}
