package com.jeesuite.passport.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeesuite.common.util.BeanUtils;
import com.jeesuite.passport.dao.entity.UserEntity;
import com.jeesuite.security.model.BaseUserInfo;

public class UserInfo extends BaseUserInfo {

    private String nickname;

    private String email;

    private String mobile;

    @JsonIgnore
    private String password;

    private String avatar;
    
    private String realname;

    private Integer age;

    private String gender;

    private Date birthday;
    
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

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Override
	public Integer getId() {
		return (Integer)super.getId();
	}

    public static void main(String[] args) {
    	UserEntity entity = new UserEntity();
    	entity.setId(1);
    	entity.setUsername("jj");
    	UserInfo userInfo = BeanUtils.copy(entity, UserInfo.class);
    	System.out.println(userInfo);
	}
}