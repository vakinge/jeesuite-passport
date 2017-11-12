package com.jeesuite.passport.dto;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.springweb.utils.IpUtils;

public class RequestMetadata {

	@JsonIgnore
	private String ipAddr;
	@JsonIgnore
	private String appId;
	@JsonIgnore
	private Date time;
	
	public RequestMetadata() {}
	
	public static RequestMetadata build(HttpServletRequest request){
		RequestMetadata metadata = new RequestMetadata();
		metadata.setIpAddr(IpUtils.getIpAddr(request));
		metadata.setAppId(request.getParameter(request.getParameter(PassportConstants.PARAM_CLIENT_ID)));
		return metadata;
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
