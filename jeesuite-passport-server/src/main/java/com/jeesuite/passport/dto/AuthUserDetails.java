package com.jeesuite.passport.dto;

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
public class AuthUserDetails extends AuthUser {

	private String nickname;

    private String email;

    private String mobile;

    private String avatar;
    
	public String getNickname() {
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

    
}
