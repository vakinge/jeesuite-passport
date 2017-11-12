package com.jeesuite.passport;

public class PassportConstants {
	
	public static final String AYG_SESSION_NAME = "AYG_SESSIONID";
	public static final String ACCESSTOKEN = "accessToken";
	
	//auth cache keys
	public static final String AUTHCODE_CACHE_KEY = "auth:code:%s";
    public static final String SESSION_CACHE_KEY = "auth:session:%s";
    public final static String LOGIN_UID_CACHE_KEY = "auth:uid:%s";
    
 // parameter
    public static final String PARAM_CLIENT_ID = "client_id";
 	public static final String PARAM_RETURN_URL = "return_url";
 	public static final String PARAM_origin_url = "origin_url";
 	public static final String PARAM_SESSION_ID = "session_id";
 	public static final String PARAM_LOGIN_TYPE = "login_type";
 	public static final String PARAM_CODE = "code";
 	public static final String PARAM_EXPIRE_IN = "expires_in";
 	public static final String PARAM_TICKET = "ticket";
 	public static final String PARAM_ACT = "act";
 	
 	 // header
    public static final String HEADER_AUTH_USER = "x-auth-user";
    
    public static final String JSONP_LOGIN_CALLBACK_FUN_NAME = "jsonpLoginCallback";
	
	public static final String JSONP_SETCOOKIE_CALLBACK_FUN_NAME = "jsonpSetCookieCallback";
	
	
	
}
