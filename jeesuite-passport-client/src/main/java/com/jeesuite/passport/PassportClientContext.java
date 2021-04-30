/**
 * 
 */
package com.jeesuite.passport;

import com.jeesuite.common.util.ResourceUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
public class PassportClientContext {
	
	private static String clientId;
	private static String clientSecret;
	private static String redirctUri;
	private static String authServerBasePath;
	
	private static SessionStorageProvider sessionStorageProvider;
	

	public static String clientId(){
		if(clientId == null){
			clientId = ResourceUtils.getProperty("auth.client.id");
		}
		return clientId;
	}
	
	public static String clientSecret(){
		if(clientSecret == null){
			clientSecret = ResourceUtils.getProperty("auth.client.secret");
		}
		return clientSecret;
	}

	public static String redirctUri(){
		if(redirctUri == null){
			redirctUri = ResourceUtils.getProperty("auth.redirect.uri","/login_callback");
		}
		return redirctUri;
	}
	
	public static String authServerBasePath(){
		if(authServerBasePath == null){
			authServerBasePath = ResourceUtils.getProperty("auth.server.baseurl");
			if(authServerBasePath.endsWith("/"))authServerBasePath = authServerBasePath.substring(0, authServerBasePath.length() - 1);
		}
		return authServerBasePath;
	}

	public static String defaultLoginSuccessRedirctUri() {
		return ResourceUtils.getProperty("auth.login-success.default.redirect.uri","");
	}
	
	public static String snsLoginRegUri() {
		return ResourceUtils.getProperty("auth.sns.register.uri","");
	}

	public static SessionStorageProvider getSessionStorageProvider() {
		return sessionStorageProvider;
	}
	
	
}
