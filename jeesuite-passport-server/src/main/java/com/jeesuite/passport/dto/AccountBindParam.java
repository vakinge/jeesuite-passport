package com.jeesuite.passport.dto;

public class AccountBindParam extends RequestMetadata {

	private static final long serialVersionUID = 1L;

	private String username;
	private String nickname;
	private String password;
	private String gender;
	private String avatar;
	private String authTicket;

	public String getAuthTicket() {
		return authTicket;
	}

	public void setAuthTicket(String authTicket) {
		this.authTicket = authTicket;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	

}
