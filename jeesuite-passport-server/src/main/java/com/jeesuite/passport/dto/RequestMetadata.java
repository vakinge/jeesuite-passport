package com.jeesuite.passport.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RequestMetadata {

	@JsonIgnore
	private String ipAddr;
	@JsonIgnore
	private String appId;
	@JsonIgnore
	private Date time;
	
	public RequestMetadata() {}
	public RequestMetadata(String ipAddr, String appId, Date time) {
		super();
		this.ipAddr = ipAddr;
		this.appId = appId;
		this.time = time;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	
	
	
}
