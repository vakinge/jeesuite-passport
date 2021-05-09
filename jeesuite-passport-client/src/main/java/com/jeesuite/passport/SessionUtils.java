package com.jeesuite.passport;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.ThreadLocalContext;
import com.jeesuite.common.model.AuthUser;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.response.AuthnResponse;

public class SessionUtils {
	
	private static final String REQUEST_KEY = "_ctx_request_";
	private static final String CURRENT_USER_KEY = "_ctx_current_user_";
	
	public static void init(HttpServletRequest request) {
		ThreadLocalContext.set(REQUEST_KEY, request);
		ThreadLocalContext.remove(CURRENT_USER_KEY);
		String sessionId = getSessionId(request);
		if(StringUtils.isNotBlank(sessionId)) {
			SessionStorageProvider sessionStorageProvider = PassportConfigHolder.getSessionStorageProvider();
			AuthUser authUser;
			if(sessionStorageProvider != null) {
				authUser = sessionStorageProvider.get(sessionId);
			}else {
				authUser = (AuthUser) request.getSession(true).getAttribute(sessionId);
			}
			if(authUser != null) {
				ThreadLocalContext.set(CURRENT_USER_KEY, authUser);
			}
		}
		
		
	}
	
	private static String getSessionId(HttpServletRequest request) {
		String sessionId = null;
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;
		for (Cookie cookie : cookies) {
			if (ClientConstants.SSO_SESSION_NAME.equals(cookie.getName())) {
				sessionId = cookie.getValue();
				break;
			}
		}

		return sessionId;
	}
	
	public static void createSession(HttpServletRequest request, HttpServletResponse response) {
		
		String sessionId;
		int expiresIn;
		if(PassportConfigHolder.isJwtEnabled()) {
			sessionId = TokenGenerator.generate();
			expiresIn = PassportConfigHolder.jwtExpiresIn();
			//
			String payload = request.getParameter(ClientConstants.PARAM_PAYLOAD);
			
		}else {
			String ticket = request.getParameter(ClientConstants.PARAM_TICKET);
			AuthnResponse authnResponse = PassportApiClient.ticketExchangeUser(ticket);
			//
			sessionId = authnResponse.getAccessToken();
			expiresIn = authnResponse.getExpiresIn();
			//
			SessionStorageProvider sessionStorageProvider = PassportConfigHolder.getSessionStorageProvider();
			if(sessionStorageProvider != null) {
				sessionStorageProvider.set(sessionId,authnResponse.getAuthUser());
			}else {
				request.getSession().setAttribute(sessionId, authnResponse.getAuthUser());
			}
			response.addCookie(createSessionCookies(request, sessionId, expiresIn));
		}
		
	}

	public static String destroySession(HttpServletRequest request, HttpServletResponse response) {

		String sessionId = getSessionId(request);
		if (StringUtils.isNotBlank(sessionId)) {
			SessionStorageProvider sessionStorageProvider = PassportConfigHolder.getSessionStorageProvider();
			if(sessionStorageProvider != null) {				
				PassportConfigHolder.getSessionStorageProvider().remove(sessionId);
			}else {
				request.getSession().removeAttribute(sessionId);
			}
			response.addCookie(createSessionCookies(request, StringUtils.EMPTY, 0));
		}
		
		return sessionId;
	}
	
	public static AuthUser getCurrentUser() {
		AuthUser authUser = ThreadLocalContext.get(CURRENT_USER_KEY);
		if(authUser != null)return authUser;
		
		SessionStorageProvider sessionStorageProvider = PassportConfigHolder.getSessionStorageProvider();
		HttpServletRequest request = ThreadLocalContext.get(REQUEST_KEY);
		
		String sessionId = getSessionId(request);
		if(sessionId == null)return null;
		if(sessionStorageProvider != null) {
			authUser = sessionStorageProvider.get(sessionId);
		}else {
			authUser = (AuthUser) request.getSession(true).getAttribute(sessionId);
		}
		if(authUser != null) {
			ThreadLocalContext.set(CURRENT_USER_KEY, authUser);
		}
		return authUser;
	}
	
	private static Cookie createSessionCookies(HttpServletRequest request,String sessionId,int expire){
		String domain = request.getServerName();
		if(domain == null){
			domain = request.getServerName();
		}
		Cookie cookie = new Cookie(ClientConstants.SSO_SESSION_NAME,sessionId);  
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expire);
		
		return cookie;
	}
}
