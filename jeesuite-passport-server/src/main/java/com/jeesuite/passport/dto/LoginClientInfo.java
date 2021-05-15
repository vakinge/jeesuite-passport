package com.jeesuite.passport.dto;

public class LoginClientInfo {

	private String clientId;
	private String returnUrl;
	private String openType;
	
	public LoginClientInfo() {}
	
	public LoginClientInfo(String clientId, String returnUrl) {
		this.clientId = clientId;
		this.returnUrl = returnUrl;
	}
	

	public LoginClientInfo(String clientId, String openType, String returnUrl) {
		super();
		this.clientId = clientId;
		this.openType = openType;
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

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}
	
	
}
