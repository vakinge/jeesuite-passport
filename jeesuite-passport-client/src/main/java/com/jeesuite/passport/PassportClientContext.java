/**
 * 
 */
package com.jeesuite.passport;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
public class PassportClientContext {
	
	private static String clientId;
	private static String clientSecret;
	private static String redirctUri;
	
	private static String loginUrlTemplate;
	private static String oauth2LoginUrlTemplate;
	private static String openLoginUrlTemplate;
	
	static {
		String serverBasePath = ResourceUtils.getAndValidateProperty("jeesuite.passport.baseUrl");
		if(serverBasePath.endsWith("/")) {
			serverBasePath = serverBasePath.substring(0, serverBasePath.length() - 1);
		}
		loginUrlTemplate = serverBasePath + "/sso/login?client_id=%s&return_url=%s";
		oauth2LoginUrlTemplate = serverBasePath + "/sso/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s";
		openLoginUrlTemplate = serverBasePath + "/sso/open/login/%s?client_id=%s&return_url=%s&scope=%";
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
	
	public static String getOauth2LoginUrl(String type,String returnUrl) {
		return String.format(oauth2LoginUrlTemplate, type,clientId(),returnUrl);
	}
	
	public static String getOpenLoginUrl(String type,String returnUrl) {
		return String.format(openLoginUrlTemplate, type,clientId(),returnUrl);
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
	
	
	
}
