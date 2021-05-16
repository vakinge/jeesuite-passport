/**
 * 
 */
package com.jeesuite.passport;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.jeesuite.common.util.ResourceUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
public class PassportConfigHolder {
	
	private static String clientId;
	private static String clientSecret;
	private static String redirctUri;
	
	private static String serverBasePath;
	private static String loginUrlTemplate;
	private static String logoutUrlTemplate;
	private static String oauth2LoginUrlTemplate;
	private static String openLoginUrlTemplate;
	private static boolean jwtEnabled;
	private static String jwtSecret;
	private static int jwtExpiresIn;
	
	static {
		serverBasePath = ResourceUtils.getAndValidateProperty("jeesuite.passport.baseUrl");
		if(serverBasePath.endsWith("/")) {
			serverBasePath = serverBasePath.substring(0, serverBasePath.length() - 1);
		}
		loginUrlTemplate = serverBasePath + "/auth/login?client_id=%s&return_url=%s";
		logoutUrlTemplate = serverBasePath + "/auth/logout?client_id=%s&return_url=%s";
		oauth2LoginUrlTemplate = serverBasePath + "/auth/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s";
		openLoginUrlTemplate = serverBasePath + "/auth/open/login/%s?client_id=%s&return_url=%s&scope=%";
		//
		jwtEnabled = ResourceUtils.getBoolean("jeesuite.passport.jwtEnabled");
		if(jwtEnabled) {
			try {
				Class.forName("io.jsonwebtoken.Jwts");
			} catch (ClassNotFoundException e) {
				jwtEnabled = false;
				System.err.println(ExceptionUtils.getRootCauseMessage(e));
			}
		}
		jwtExpiresIn = ResourceUtils.getInt("jeesuite.passport.jwtExpiresIn", 0);
		try {
			loadPasswordServerConfigs();
		} catch (Exception e) {}
	}

	private static void loadPasswordServerConfigs() {
		Map<String, String> configs = PassportApiClient.getConfigs();
		jwtSecret = configs.remove("jwtconfig.secret");
	}
	
	private static SessionStorageProvider sessionStorageProvider;
	

	public static String clientId(){
		if(clientId == null){
			clientId = ResourceUtils.getAndValidateProperty("jeesuite.passport.clientId");
		}
		return clientId;
	}
	
	public static String clientSecret(){
		if(clientSecret == null){
			clientSecret = ResourceUtils.getAndValidateProperty("jeesuite.passport.clientSecret");
		}
		return clientSecret;
	}

	public static String getLoginUrl(String returnUrl) {
		return String.format(loginUrlTemplate, clientId(),returnUrl);
	}
	
	public static String getLogoutUrl(String returnUrl) {
		return String.format(logoutUrlTemplate, clientId(),returnUrl);
	}
	
	public static String getOauth2LoginUrl(String type,String returnUrl) {
		return String.format(oauth2LoginUrlTemplate, type,clientId(),returnUrl);
	}
	
	public static String getOpenLoginUrl(String type,String returnUrl) {
		return String.format(openLoginUrlTemplate, type,clientId(),returnUrl);
	}
	
	public static String buildServerUrl(String uri) {
		return serverBasePath + uri;
	}
	

	public static String redirctUri(){
		if(redirctUri == null){
			redirctUri = ResourceUtils.getProperty("sso.redirect.uri","/login_callback");
		}
		return redirctUri;
	}

	public static String defaultLoginSuccessRedirctUri() {
		return ResourceUtils.getProperty("sso.login-success.default.redirectUri","");
	}
	
	public static String snsLoginRegUri() {
		return ResourceUtils.getProperty("sso.sns.register.uri","");
	}

	public static SessionStorageProvider getSessionStorageProvider() {
		return sessionStorageProvider;
	}
	
	public static void setSessionStorageProvider(SessionStorageProvider sessionStorageProvider) {
		PassportConfigHolder.sessionStorageProvider = sessionStorageProvider;
	}

	public static boolean isJwtEnabled() {
		return jwtEnabled;
	}
	
	public static int jwtExpiresIn() {
		return jwtExpiresIn;
	}

	public static String jwtSecret() {
		if(jwtSecret == null) {
			loadPasswordServerConfigs();
		}
		return jwtSecret;
	}
	
}
