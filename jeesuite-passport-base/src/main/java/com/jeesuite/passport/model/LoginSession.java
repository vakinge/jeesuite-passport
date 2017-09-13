package com.jeesuite.passport.model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.common.crypt.Base64;
import com.jeesuite.common.crypt.DES;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.helper.TokenGenerator;

public class LoginSession {

	private static Logger log = LoggerFactory.getLogger("com.aygframework.support");
	
	public static final int SESSION_EXPIRE_SECONDS = ResourceUtils.getInt("auth.session.expire.seconds", 86400);
	private static final String CONTACT_CHAR = "#";
	private static final String PER_CONTACT_STR = ",";
	public static final String TOKEN_PREFIX = "actk";
	public static final String ANON_TOKEN_PREFIX = "anon";
	private static final String CRYPT_KEY = "a9H0k4w0";
	
	private Integer userId;
	private String userName;
	//用户类型（1:普通用户，2:企业用户，3:后台管理员）
	private int userType = 1;
	private String sourceAppId;
	private String sessionId;
	private Integer expiresIn;
	private Integer companyId = 0;
	private long expiresAt;
	//权限列表
	private List<String> permissions;
	
	public LoginSession() {}
	
	public LoginSession(boolean initId){
		if(initId){
			this.sessionId = TokenGenerator.generate();
			setExpiresIn(SESSION_EXPIRE_SECONDS);
		}
	}
	
	public LoginSession(String sessionId) {
		this.sessionId = sessionId;
		this.expiresIn = SESSION_EXPIRE_SECONDS;
		this.expiresAt = System.currentTimeMillis()/1000 + expiresIn;
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
	public String getSourceAppId() {
		return sourceAppId;
	}
	public void setSourceAppId(String sourceAppId) {
		this.sourceAppId = sourceAppId;
		if("provider".equals(sourceAppId) || "ehr".equals(sourceAppId)){
			userType = 2;
		}else if("admin".equals(sourceAppId)){
			userType = 3;
		}
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
		this.expiresAt = System.currentTimeMillis()/1000 + expiresIn;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
	
	public boolean isPersonalUser(){
		return userType == 1;
	}
	
	public boolean isEnterpriseUser(){
		return userType == 2;
	}
	
	public boolean isAdmin(){
		return userType == 3;
	}
	
	public int getUserType() {
		return userType;
	}

	public Integer getCompanyId() {
		return (companyId != null && companyId == 0) ? null : companyId;
	}

	public void setCompanyId(Integer companyId) {
		if(companyId == null)return;
		this.companyId = companyId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	
	public boolean hasPermissions(String permission){
		return this.permissions != null && this.permissions.contains(permission);
	}
	
	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String toEncodeString(String...permissonPrefixs){
	    
		StringBuilder builder = new StringBuilder();
		builder.append(userId).append(CONTACT_CHAR);
		if(StringUtils.isNotBlank(userName)){
			builder.append(userName);
		}
		builder.append(CONTACT_CHAR);
		builder.append(sourceAppId).append(CONTACT_CHAR);
		builder.append(userType).append(CONTACT_CHAR);
		builder.append(companyId);
		
		if(permissions != null && !permissions.isEmpty()){
			
			builder.append(CONTACT_CHAR);
			for (String s : permissions) {
				if(matchPermissonPrefix(permissonPrefixs,s)){					
					builder.append(s).append(PER_CONTACT_STR);
				}
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		
		return encrypt(builder.toString());
	}
	
	private boolean matchPermissonPrefix(String[] permissonPrefixs, String s) {
		if(permissonPrefixs == null || permissonPrefixs.length == 0 || StringUtils.isBlank(permissonPrefixs[0]))return false;
		for (String p : permissonPrefixs) {
			if(s.startsWith(p))return true;
		}
		return false;
	}

	public static LoginSession decode(String encodeString){
		if(StringUtils.isBlank(encodeString))return null;
		if(log.isDebugEnabled()){
			log.debug("LoginSession.decode ->",encodeString);
		}
		encodeString = decrypt(encodeString);
		String[] splits = encodeString.split(CONTACT_CHAR);
		if(splits.length < 5)return null;
		LoginSession session = new LoginSession();
		session.setUserId(Integer.parseInt(splits[0]));
		session.setUserName(splits[1]);
		session.setSourceAppId(splits[2]);
		session.setUserType(Integer.parseInt(splits[3]));
		session.setCompanyId(Integer.parseInt(splits[4]));
		if(splits.length == 6){
			session.setPermissions(new ArrayList<>(Arrays.asList(splits[5].split(PER_CONTACT_STR))));
		}
		return session;
	}
	
	public static String getAnonymousToken(){
		return ANON_TOKEN_PREFIX + UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	private static String encrypt(String data) {
		 String encode = DES.encrypt(CRYPT_KEY, data);
		 byte[] bytes = Base64.encodeToByte(encode.getBytes(StandardCharsets.UTF_8), false);
		 return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private static String decrypt(String data) {
		 byte[] bytes = Base64.decode(data);
		 data = new String(bytes, StandardCharsets.UTF_8);
		 return DES.decrypt(CRYPT_KEY, data);
	 }
	 

	 public static void main(String[] args) {
		LoginSession session = new LoginSession();
		session.setUserId(1000);
		session.setCompanyId(10000);
		session.setUserName("周大福");
		session.setUserType(1);
		session.setSourceAppId("cszj_mobile");
		session.setPermissions(new ArrayList<>(Arrays.asList("tax_add,update,delete".split(PER_CONTACT_STR))));
		String encodeString = session.toEncodeString("tax");
		System.out.println(encodeString);
		LoginSession session2 = decode(encodeString);
		System.out.println(session2);
	}
	
	
}
