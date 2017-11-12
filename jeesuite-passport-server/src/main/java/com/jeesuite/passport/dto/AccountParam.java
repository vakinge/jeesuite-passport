package com.jeesuite.passport.dto;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.util.DigestUtils;

import io.swagger.annotations.ApiModelProperty;

public class AccountParam extends RequestMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id,mobile必填一个")
	private Integer id;
	@ApiModelProperty(value = "登录用户名")
    private String username;
    @ApiModelProperty(value = "昵称")
    private String nickname;

    private String email;
    @ApiModelProperty(value = "id,mobile必填一个")
    private String mobile;

    private String password;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
    
	public Integer getId() {
		return id == null ? 0 : id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public static boolean validatePassword(String orignPassword,String cryptPassword){
		String orignToCrypt = DigestUtils.md5(orignPassword);
		return StringUtils.equalsIgnoreCase(orignToCrypt, cryptPassword);
	}
}