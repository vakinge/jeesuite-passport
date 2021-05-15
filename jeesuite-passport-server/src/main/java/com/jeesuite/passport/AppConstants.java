package com.jeesuite.passport;

public class AppConstants {
	public static final String SUPER_ADMIN_NAME = "sa";
    public static final String RESOURCE_SERVER_NAME = "oauth-server";
    public static final String INVALID_CLIENT_ID = "客户端验证失败，如错误的client_id/client_secret。";
    public static final String INVALID_ACCESS_TOKEN = "accessToken无效或已过期。";
    public static final String INVALID_REDIRECT_URI = "缺少授权成功后的回调地址。";
    public static final String INVALID_AUTH_CODE = "错误的授权码。";
    
    public static final String ERROR = "error";
    
    
    public static final String CAPTCHA = "captcha";
    
    public static final String TICKET = "ticket";
    
    
    public static enum OpenType{
		weixin,weibo,qq,taobao,alipay,osc
	}
    
    public static enum OpenSubType{
    	gzh,xcx,oauth
	}
    
    public static enum Gender{
    	male,female,unknow
	}
}
