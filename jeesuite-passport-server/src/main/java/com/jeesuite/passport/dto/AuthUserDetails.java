package com.jeesuite.passport.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jeesuite.common.model.AuthUser;

/**
 * 
 * 
 * <br>
 * Class Name   : AuthUserDetails
 *
 * @author jiangwei
 * @version 1.0.0
 * @date 2018年12月4日
 */
@JsonInclude(Include.NON_NULL)
public class AuthUserDetails extends AuthUser {

	private String realname;
	private String nickname;

    private String email;

    private String mobile;

    private String avatar;
    
    private String employeeId;
    private String departmentName;
    private String postName;
    
	public String getNickname() {
		if(StringUtils.isBlank(nickname)) {
			if(StringUtils.isNotBlank(realname)) {
				return realname;
			}
			return getUsername();
		}
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	
    
}
