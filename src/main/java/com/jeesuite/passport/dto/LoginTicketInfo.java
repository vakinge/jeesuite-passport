package com.jeesuite.passport.dto;

public class LoginTicketInfo {

	private String sessionId;
	private String returnUrl;
	
	public LoginTicketInfo() {}
	
	public LoginTicketInfo(String sessionId, String returnUrl) {
		super();
		this.sessionId = sessionId;
		this.returnUrl = returnUrl;
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
