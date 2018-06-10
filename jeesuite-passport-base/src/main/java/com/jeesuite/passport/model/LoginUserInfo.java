package com.jeesuite.passport.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LoginUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

    private String username;
    
    private String nickname;

    private String email;

    private String mobile;

    private String avatar;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return StringUtils.isBlank(username) ? mobile : username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return StringUtils.isBlank(nickname) ? getUsername() : nickname;
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

}