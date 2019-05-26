package com.jeesuite.passport.component.snslogin;

import java.io.Serializable;

import javax.persistence.Transient;

import com.jeesuite.common.util.TokenGenerator;

public class SnsLoginState implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String appId;
	private String snsType;
	@Transient
	private String state;
	private String regPageUri;
	private String successDirectUri;
	private String orignUrl;
	private Integer lognUserId;
	
	public SnsLoginState() {}
	
	public SnsLoginState(String appId, String snsType, Integer lognUserId) {
		super();
		this.appId = appId;
		this.snsType = snsType;
		this.lognUserId = lognUserId;
	}

	public SnsLoginState(String appId,String snsType, String regPageUri, String successDirectUri,String orignUrl) {
		this.state = TokenGenerator.generate();
		this.appId = appId;
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

	public Integer getLognUserId() {
		return lognUserId;
	}

	public void setLognUserId(Integer lognUserId) {
		this.lognUserId = lognUserId;
	}
	
	public boolean loginAction(){
		return lognUserId == null || lognUserId == 0;
	}
}
