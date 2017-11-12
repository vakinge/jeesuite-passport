package com.jeesuite.passport.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.helper.SecurityCryptUtils;
import com.jeesuite.passport.helper.TokenGenerator;

public class LoginSession {

	public static final int SESSION_EXPIRE_SECONDS = ResourceUtils.getInt("auth.session.expire.seconds", 86400);
	public static final int ANON_SESSION_EXPIRE_SECONDS = ResourceUtils.getInt("auth.session.expire.seconds", 3600);
	private static final String CLIENT_ID = ResourceUtils.getProperty("auth.client.id");
	private static final String CONTACT_CHAR = "#";
	
	private Integer userId;
	private String userName;

	private String clientId;
	private String sessionId;
	private Integer expiresIn;
	private long expiresAt;
	
	private LoginUserInfo userInfo;
	
	public LoginSession() {}
	
	public static LoginSession create(boolean anonymous){
		LoginSession session = new LoginSession();
		session.clientId = CLIENT_ID;
		session.sessionId = AuthSessionHelper.generateSessionId(anonymous);
		session.expiresIn = anonymous ? ANON_SESSION_EXPIRE_SECONDS : SESSION_EXPIRE_SECONDS;
		session.expiresAt = System.currentTimeMillis()/1000 + session.expiresIn;
		return session;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Integer getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
		this.expiresAt = System.currentTimeMillis()/1000 + this.expiresIn;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isAnonymous(){
		return userId == null || userId == 0;
	}


	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	
	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	public LoginUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(LoginUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String toEncodeString(){
		
		StringBuilder builder = new StringBuilder();
		builder.append(CLIENT_ID).append(CONTACT_CHAR);
		builder.append(sessionId);
		if(isAnonymous() == false){
			builder.append(CONTACT_CHAR);
			builder.append(userId).append(CONTACT_CHAR);
			if(StringUtils.isNotBlank(userName)){
				builder.append(userName);
			}
		}
		
		return SecurityCryptUtils.encrypt(builder.toString());
	}
	

	public static LoginSession decode(String encodeString){
		if(StringUtils.isBlank(encodeString))return null;
		encodeString = SecurityCryptUtils.decrypt(encodeString);
		String[] splits = encodeString.split(CONTACT_CHAR); //StringUtils.split(encodeString, CONTACT_CHAR);
		
		LoginSession session = new LoginSession();
		session.setClientId(splits[0]);
		session.setSessionId(splits[1]);
		
		if(splits.length > 2){
			session.setUserId(Integer.parseInt(splits[2]));
			session.setUserName(splits[3]);
		}
		
		return session;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	 public static void main(String[] args) {
		LoginSession session = LoginSession.create(false);
		session.setUserId(1000);
		session.setUserName("周大福");
		String encodeString = session.toEncodeString();
		System.out.println(encodeString);
		LoginSession session2 = decode(encodeString);
		System.out.println(session2);
		System.out.println("---------------------");
		session = LoginSession.create(true);
		encodeString = session.toEncodeString();
		session2 = decode(encodeString);
		System.out.println(session2);
		
	}
	
	
}
