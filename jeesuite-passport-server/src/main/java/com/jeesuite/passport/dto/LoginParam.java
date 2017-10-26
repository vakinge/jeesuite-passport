package com.jeesuite.passport.dto;

import io.swagger.annotations.ApiModelProperty;

public class LoginParam {

	@ApiModelProperty(value = "登录名(userName,mobile，email)",required = true)
	private String loginName;
	@ApiModelProperty(value = "密码",required = true)
	private String password;
	@ApiModelProperty(value = "验证码")
	private String code;
	
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
