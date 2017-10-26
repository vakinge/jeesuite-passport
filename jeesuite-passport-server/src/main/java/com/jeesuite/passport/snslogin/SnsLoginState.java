package com.jeesuite.passport.snslogin;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Transient;

public class SnsLoginState implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String appId;
	private String domain;
	private String snsType;
	@Transient
	private String state;
	private String regPageUri;
	private String successDirectUri;
	private String orignUrl;
	
	public SnsLoginState() {}

	public SnsLoginState(String appId,String domain, String snsType, String regPageUri, String successDirectUri,String orignUrl) {
		this.state = UUID.randomUUID().toString().replaceAll("-", "");
		this.appId = appId;
		this.domain = domain;
		this.snsType = snsType;
		this.regPageUri = regPageUri;
		this.successDirectUri = successDirectUri;
		this.orignUrl = orignUrl;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSnsType() {
		return snsType;
	}

	public void setSnsType(String snsType) {
		this.snsType = snsType;
	}
	

	public String getRegPageUri() {
		return regPageUri;
	}

	public void setRegPageUri(String regPageUri) {
		this.regPageUri = regPageUri;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSuccessDirectUri() {
		return successDirectUri;
	}

	public void setSuccessDirectUri(String successDirectUri) {
		this.successDirectUri = successDirectUri;
	}

	public String getOrignUrl() {
		return orignUrl;
	}

	public void setOrignUrl(String orignUrl) {
		this.orignUrl = orignUrl;
	}
	
}
