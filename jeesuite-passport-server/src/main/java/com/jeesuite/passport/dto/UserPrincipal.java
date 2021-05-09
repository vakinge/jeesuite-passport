package com.jeesuite.passport.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jeesuite.common.json.deserializer.DateConvertDeserializer;
import com.jeesuite.common.json.serializer.DateConvertSerializer;

/**
 * 用户主体基本信息
 * 
 * <br>
 * Class Name   : UserPrincipal
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @version 1.0.0
 * @date May 6, 2021
 */
public class UserPrincipal {

	private String id;

    private String email;

    private String mobile;

    private String realname;

    private String avatar;

    private Integer age;

    private String gender;
    @JsonSerialize(using = DateConvertSerializer.class) 
    @JsonDeserialize(using = DateConvertDeserializer.class)
    private Date birthday;
    
    private Integer idType;

    private String idNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public Integer getIdType() {
		return idType;
	}

	public void setIdType(Integer idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
    
    
}
