package com.jeesuite.passport.component.openauth;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.passport.AppConstants.OpenSubType;
import com.jeesuite.passport.AppConstants.OpenType;

public class OauthUser implements Serializable {

	private static final String UNKOWN = "unkown";

	private static final long serialVersionUID = 1L;

	private String fromClientId;
	private String openId;
	private String nickname;
	private String avatar;
	private String gender; // male/female/unknow
	private String unionId;
	private OpenType openType;
	private OpenSubType openSubType;

	public String getFromClientId() {
		return fromClientId;
	}

	public void setFromClientId(String fromClientId) {
		this.fromClientId = fromClientId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getGender() {
		return UNKOWN.equals(gender) ? null : gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
	public void setOpenType(String openType) {
		String[] parts = StringUtils.split(openType, ":");
		this.openType = OpenType.valueOf(parts[0]);
		if(parts.length == 2) {
			this.openSubType = OpenSubType.valueOf(parts[1]);
		}else {
			this.openSubType = OpenSubType.oauth;
		}
	}

	public OpenType getOpenType() {
		return openType;
	}

	public OpenSubType getOpenSubType() {
		return openSubType;
	}

	public String userInfoToUrlQueryString(){
		return String.format("nickname=%s&gender=%s&avatar=%s", nickname,gender,avatar);
	}

}
