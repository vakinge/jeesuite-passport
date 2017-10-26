package com.jeesuite.passport.helper;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.model.LoginSession;

public class AuthSessionHelper {
	
	private static final int EXPIRE = 1000*60*3;
	private static final String NULL = "null";
	
	public static void main(String[] args) {
		String sessionId = generateSessionId(true);
		System.out.println(sessionId);
		
		validateSessionId("RTdFOTNDREU5QzMzOTBCOTA4RjAxQ0RGMTc4NTQ5RDEwMzE1NDBGNDBCNzlCMEIw",true);
	}
	
	public static String generateSessionId(boolean anonymous){
		String str = DigestUtils.md5Short(TokenGenerator.generate()).concat(String.valueOf(System.currentTimeMillis()));	
		return SecurityCryptUtils.encrypt(str);
	}
	
	public static void validateSessionId(String seesionId,boolean validateExpire){
		long timestamp = 0;
		try {
			timestamp = Long.parseLong(SecurityCryptUtils.decrypt(seesionId).substring(6));
		} catch (Exception e) {
			throw new JeesuiteBaseException(4005, "sessionId格式不正确");
		}
		if(validateExpire && System.currentTimeMillis() - timestamp > EXPIRE){
			throw new JeesuiteBaseException(4005, "sessionId过期");
		}
	}
	
	public static LoginSession getLoginSession(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return AuthRedisClient.getInstance().get(key);
	}
	
	public static LoginSession validateSessionIfNotCreateAnonymous(String sessionId){
		LoginSession session = null;
		if(StringUtils.isNotBlank(sessionId)){
			session = getLoginSession(sessionId);
		}
		
		if(session == null){			
			session = LoginSession.create(true);
			storgeLoginSession(session);
		}

		return session;
	}
	
	public static LoginSession getSessionIfNotCreateAnonymous(HttpServletRequest request){
		LoginSession session = null;
		String sessionId = getSessionId(request);
		if(StringUtils.isNotBlank(sessionId)){
			session = getLoginSession(sessionId);
		}
		
		if(session == null){			
			session = LoginSession.create(true);
			storgeLoginSession(session);
		}

		return session;
	}
	
	public static LoginSession getLoginSessionByUserId(long  userId){
		String key = String.format(PassportConstants.LOGIN_UID_CACHE_KEY, userId);
		String sessionId = AuthRedisClient.getInstance().getStr(key);
		if(StringUtils.isBlank(sessionId))return null;
		key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return  StringUtils.isBlank(sessionId) ? null : AuthRedisClient.getInstance().get(key);
	}
	
	public static void storgeLoginSession(LoginSession session){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, session.getSessionId());
		AuthRedisClient.getInstance().set(key,session, session.getExpiresIn());
		if(!session.isAnonymous()){			
			key = String.format(PassportConstants.LOGIN_UID_CACHE_KEY, session.getUserId());
			AuthRedisClient.getInstance().setStr(key, session.getSessionId(), session.getExpiresIn());
		}
	}
	
	public static boolean accesstokenExists(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return AuthRedisClient.getInstance().exists(key, false);
	}
	
	public static void removeLoginSession(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		LoginSession session = AuthRedisClient.getInstance().get(key);
		if(session != null){
			AuthRedisClient.getInstance().remove(key, false);
			key = String.format(PassportConstants.LOGIN_UID_CACHE_KEY, session.getUserId());
			AuthRedisClient.getInstance().remove(key, true);
		}
	}
	
	public static String createOauthState(String redirctUrl){
		String state = UUID.randomUUID().toString().replaceAll("-", "");
		AuthRedisClient.getInstance().setStr(state, redirctUrl, CacheExpires.IN_1MIN);
		return state;
	}
	
    public static String getOauth2RedirctUrl(String state){
		return AuthRedisClient.getInstance().getStr(state);
	}
    
    public static Cookie createSessionCookies(String sessionId,String domain,int expire){
		Cookie cookie = new Cookie(PassportConstants.AYG_SESSION_NAME,sessionId);  
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expire);
		return cookie;
	}
	
	
	/**
	 * 获取授权Id （accessToken or  sessionId）
	 * @param request
	 * @return
	 */
	public static String getSessionId(HttpServletRequest request) {
		String sessionId = request.getParameter(PassportConstants.ACCESSTOKEN);
		if(isBlank(sessionId)){
			sessionId = request.getHeader(PassportConstants.ACCESSTOKEN);
		}
		if(isBlank(sessionId)){
			Cookie[] cookies = request.getCookies();
			if(cookies == null)return null;
			for (Cookie cookie : cookies) {
				if(PassportConstants.AYG_SESSION_NAME.equals(cookie.getName())){
					sessionId = cookie.getValue();
					break;
				}
			}
		}
		return sessionId;
	}
	
	private static boolean isBlank(String str){
		return StringUtils.isBlank(str) || NULL.equals(str);
	}
	
	public static String destroySessionAndCookies(HttpServletRequest request,HttpServletResponse response) {
		
		String sessionId = AuthSessionHelper.getSessionId(request);
		
		
		if(StringUtils.isNotBlank(sessionId)){
			AuthSessionHelper.removeLoginSession(sessionId);
			
			//
			String domain = request.getServerName();
			response.addCookie(createSessionCookies(StringUtils.EMPTY, domain, 0));
		}
		return sessionId;
	}
}
