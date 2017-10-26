package com.jeesuite.passport.dto;

public class AccountBindParam extends AccountParam {

	private static final long serialVersionUID = 1L;
	
	private String authTicket;

	public String getAuthTicket() {
		return authTicket;
	}

	public void setAuthTicket(String authTicket) {
		this.authTicket = authTicket;
	}

	
	
}
