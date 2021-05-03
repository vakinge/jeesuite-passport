package com.jeesuite.passport.dto;

public class LoginClientInfo {

	private String clientId;
	private String returnUrl;
	
	public LoginClientInfo() {}
	
	public LoginClientInfo(String clientId, String returnUrl) {
		this.clientId = clientId;
		this.returnUrl = returnUrl;
	}
	
	

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
	
}
