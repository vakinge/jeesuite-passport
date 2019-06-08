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
	private String returnUrl;
	private Integer lognUserId;
	
	public SnsLoginState() {}

	public SnsLoginState(String appId,String snsType, String returnUrl, Integer lognUserId) {
		this.state = TokenGenerator.generate();
		this.appId = appId;
		this.snsType = snsType;
		this.returnUrl = returnUrl;
		this.lognUserId = lognUserId;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
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
