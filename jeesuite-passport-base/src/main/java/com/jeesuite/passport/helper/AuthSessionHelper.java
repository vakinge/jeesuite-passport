package com.jeesuite.passport.helper;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.model.LoginSession;

public class AuthSessionHelper {
	
	public static final String AUTH_CACHE_GROUP = "auth_cache";

	public static LoginSession getLoginSession(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return  new RedisObject(key,AUTH_CACHE_GROUP).get();
	}
	
	public static LoginSession getLoginSessionByUserId(long  userId){
		String key = String.format(PassportConstants.LOGIN_UID_CACHE_KEY, userId);
		String sessionId = new RedisString(key,AUTH_CACHE_GROUP).get();
		
		key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return  StringUtils.isBlank(sessionId) ? null : new RedisObject(key,AUTH_CACHE_GROUP).get();
	}
	
	public static void storgeLoginSession(LoginSession session){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, session.getSessionId());
		new RedisObject(key,AUTH_CACHE_GROUP).set(session, session.getExpiresIn());
		new RedisString(String.format(PassportConstants.LOGIN_UID_CACHE_KEY, session.getUserId())).set(key, session.getExpiresIn());
	}
	
	public static boolean accesstokenExists(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		return new  RedisString(key,AUTH_CACHE_GROUP).exists();
	}
	
	public static void removeLoginSession(String sessionId){
		String key = String.format(PassportConstants.SESSION_CACHE_KEY, sessionId);
		RedisObject redisObject = new RedisObject(key,AUTH_CACHE_GROUP);
		LoginSession session = redisObject.get();
		if(session != null){
			redisObject.remove();
			new RedisString(String.format(PassportConstants.LOGIN_UID_CACHE_KEY, session.getUserId())).remove();
		}
	}
	
	public static void refreshIfWouldExpire(LoginSession session){
		long currentSecond = System.currentTimeMillis()/1000;
		if(session.getExpiresAt() - currentSecond > 1800){
			return;
		}
		session.setExpiresIn(3600);
		storgeLoginSession(session);
	
	}
	
	public static String createOauthState(String clientOrigUrl){
		String state = UUID.randomUUID().toString().replaceAll("-", "");
		new  RedisString(state,AUTH_CACHE_GROUP).set(clientOrigUrl, CacheExpires.IN_1MIN);
		return state;
	}
	
    public static String getOauthState(String state){
		return new  RedisString(state,AUTH_CACHE_GROUP).get();
	}
    
    public static Cookie createSessionCookies(String sessionId,String ssoDomain,int expire){
		Cookie cookie = new Cookie(PassportConstants.AYG_SESSION_NAME,sessionId);  
		cookie.setDomain(ssoDomain);
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
	public static String getAuthId(HttpServletRequest request) {
		String authId = request.getParameter(PassportConstants.ACCESSTOKEN);
		if(StringUtils.isBlank(authId)){
			authId = request.getHeader(PassportConstants.ACCESSTOKEN);
		}
		if(StringUtils.isBlank(authId)){
			Cookie[] cookies = request.getCookies();
			if(cookies == null)return null;
			for (Cookie cookie : cookies) {
				if(PassportConstants.AYG_SESSION_NAME.equals(cookie.getName())){
					authId = cookie.getValue();
					break;
				}
			}
		}
		return authId;
	}
	
	public static String destroySession(HttpServletRequest request,HttpServletResponse response,String ssoDomain) {
		String authId = request.getParameter(PassportConstants.ACCESSTOKEN);
		if(StringUtils.isBlank(authId)){
			authId = request.getHeader(PassportConstants.ACCESSTOKEN);
		}
		
		boolean isCookies = false;
		if(StringUtils.isBlank(authId)){
			Cookie[] cookies = request.getCookies();
			if(cookies == null)return null;
			for (Cookie cookie : cookies) {
				if(PassportConstants.AYG_SESSION_NAME.equals(cookie.getName())){
					authId = cookie.getValue();
					isCookies = true;
					break;
				}
			}
		}
		
		if(StringUtils.isNotBlank(authId)){
			AuthSessionHelper.removeLoginSession(authId);
			if(isCookies){
				response.addCookie(createSessionCookies(StringUtils.EMPTY, ssoDomain, 0));
			}
		}
		return authId;
	}
}
