package com.jeesuite.passport.component.saml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;


/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2018年5月22日
 */
public class KeyStoreLocator {

  private static CertificateFactory certificateFactory;

  static {
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException e) {
      throw new RuntimeException(e);
    }
  }

  public static KeyStore createKeyStore(String pemPassPhrase) {
    try {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, pemPassPhrase.toCharArray());
      return keyStore;
    } catch (Exception e) {
      //too many exceptions we can't handle, so brute force catch
      throw new RuntimeException(e);
    }
  }

  //privateKey must be in the DER unencrypted PKCS#8 format. See README.md
  public static void addPrivateKey(KeyStore keyStore, String alias, String privateKey, String certificate, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException {
    String wrappedCert = wrapCert(certificate);
    byte[] decodedKey = Base64.getDecoder().decode(privateKey.getBytes());

    char[] passwordChars = password.toCharArray();
    Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(wrappedCert.getBytes()));
    ArrayList<Certificate> certs = new ArrayList<>();
    certs.add(cert);

   byte[] privKeyBytes = toByteArray(decodedKey);
    

    KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
    RSAPrivateKey privKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(ks);
    keyStore.setKeyEntry(alias, privKey, passwordChars, certs.toArray(new Certificate[certs.size()]));
  }

  private static String wrapCert(String certificate) {
    return "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
  }
  
  private static byte[] toByteArray(byte[] decodedKey) throws IOException {
	  
	  ByteArrayInputStream input = new ByteArrayInputStream(decodedKey);
	  final ByteArrayOutputStream output = new ByteArrayOutputStream();
	  
	  final byte[] buffer = new byte[4096];
	  int n;
      while (-1 != (n = input.read(buffer))) {
          output.write(buffer, 0, n);
      }
      
      return output.toByteArray();
  }

}
