package com.jeesuite.passport.component.openauth;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.util.TokenGenerator;

public class SnsLoginState implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String appId;
	private String openType;
	@Transient
	private String state;
	private String returnUrl;
	private String lognUserId;
	
	public SnsLoginState() {}

	public SnsLoginState(String appId,String openType, String returnUrl, String lognUserId) {
		this.state = TokenGenerator.generate();
		this.appId = appId;
		this.openType = openType;
		this.returnUrl = returnUrl;
		this.lognUserId = lognUserId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
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

	public String getLognUserId() {
		return lognUserId;
	}

	public void setLognUserId(String lognUserId) {
		this.lognUserId = lognUserId;
	}
	
	public boolean loginAction(){
		return StringUtils.isBlank(lognUserId);
	}
}
