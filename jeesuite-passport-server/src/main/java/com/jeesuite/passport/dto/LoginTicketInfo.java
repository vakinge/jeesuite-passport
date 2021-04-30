package com.jeesuite.passport.dto;

public class LoginTicketInfo {

	private String clientId;
	private String sessionId;
	private String returnUrl;
	
	public LoginTicketInfo() {}
	
	public LoginTicketInfo(String clientId,String sessionId, String returnUrl) {
		this.clientId = clientId;
		this.sessionId = sessionId;
		this.returnUrl = returnUrl;
	}
	
	

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
	
}
