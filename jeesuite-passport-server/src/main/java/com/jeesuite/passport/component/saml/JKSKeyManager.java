/*
 * Copyright 2016-2018 www.jeesuite.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeesuite.passport.component.saml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.jeesuite.common.util.ResourceUtils;


/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2018年5月22日
 */
public class JKSKeyManager {

	private final static Logger logger = LoggerFactory.getLogger(JKSKeyManager.class);

	private static Credential signingCredential = null;
	final static Signature assertionSignature = null;

	static {
		intializeCredentials();
	}

	private static void intializeCredentials() {
		KeyStore ks = null;
		InputStream fis = null;
		char[] password = ResourceUtils.getAndValidateProperty("saml.idp.keystore.password").toCharArray();

		// Get Default Instance of KeyStore
		try {
			ks = KeyStore.getInstance("jks");
		} catch (KeyStoreException e) {
			logger.error("Error while Intializing Keystore", e);
		}

		// Load KeyStore
		try {
			String location = ResourceUtils.getAndValidateProperty("saml.idp.keystore.location");
			if(location.startsWith("file://")){
				fis = new FileInputStream(new File(location.replace("file://", "")));
			}else{
				Thread.currentThread().getContextClassLoader().getResource(location);
				ClassPathResource resource = new ClassPathResource(location);
				fis = resource.getInputStream();
			}
			ks.load(fis, password);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Failed to Load the KeyStore:: ", e);
		} catch (CertificateException e) {
			logger.error("Failed to Load the KeyStore:: ", e);
		} catch (IOException e) {
			logger.error("Failed to Load the KeyStor7e:: ", e);
		}

		// Close InputFileStream
		try {
			fis.close();
		} catch (IOException e) {
			logger.error("Failed to close file stream:: ", e);
		}

		// Get Private Key Entry From Certificate
		KeyStore.PrivateKeyEntry pkEntry = null;
		try {
			char[] entryPassword = ResourceUtils.getAndValidateProperty("saml.idp.keystore.entry.password").toCharArray();
			String alias = ResourceUtils.getAndValidateProperty("saml.idp.certificate.alias");
			pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias,new KeyStore.PasswordProtection(entryPassword));
		} catch (NoSuchAlgorithmException e) {
			logger.error("Failed to Get Private Entry From the keystore ", e);
		} catch (UnrecoverableEntryException e) {
			logger.error("Failed to Get Private Entry From the keystore", e);
		} catch (KeyStoreException e) {
			logger.error("Failed to Get Private Entry From the keystore", e);
		}
		PrivateKey pk = pkEntry.getPrivateKey();

		X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		credential.setPrivateKey(pk);
		signingCredential = credential;

		logger.info("Private Key" + pk.toString());

	}

	public static Credential getSigningCredential() {
		return signingCredential;
	}

}
