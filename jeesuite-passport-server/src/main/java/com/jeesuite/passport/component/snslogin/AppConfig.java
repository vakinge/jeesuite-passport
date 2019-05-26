package com.jeesuite.passport.component.snslogin;

public class AppConfig {

	String appkey;
	String appSecret;
	
	public AppConfig(String appkey, String appSecret) {
		super();
		this.appkey = appkey;
		this.appSecret = appSecret;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	
}
