package com.jeesuite.passport.snslogin;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Transient;

public class SnsLoginState implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String appId;
	private String snsType;
	private boolean regAction;
	@Transient
	private String state;
	private String regPageUrl;
	private String successDirectUrl;
	

	public SnsLoginState(String appId, String snsType, boolean regAction, String regPageUrl, String successDirectUrl) {
		this.state = UUID.randomUUID().toString().replaceAll("-", "");
		this.appId = appId;
		this.snsType = snsType;
		this.regAction = regAction;
		this.regPageUrl = regPageUrl;
		this.successDirectUrl = successDirectUrl;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getSnsType() {
		return snsType;
	}

	public void setSnsType(String snsType) {
		this.snsType = snsType;
	}
	

	public boolean isRegAction() {
		return regAction;
	}

	public void setRegAction(boolean regAction) {
		this.regAction = regAction;
	}

	public String getRegPageUrl() {
		return regPageUrl;
	}

	public void setRegPageUrl(String regPageUrl) {
		this.regPageUrl = regPageUrl;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSuccessDirectUrl() {
		return successDirectUrl;
	}

	public void setSuccessDirectUrl(String successDirectUrl) {
		this.successDirectUrl = successDirectUrl;
	}
	
	
}
