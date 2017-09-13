/**
 * 
 */
package com.jeesuite.passport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.dao.entity.AppEntity;
import com.jeesuite.passport.dao.mapper.AppEntityMapper;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.helper.AuthSessionHelper;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月18日
 */
@Service
public class OAuthService {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AppEntityMapper appMapper;
	
	public void storeAuthCode(String authCode, String username) {
		new  RedisString(String.format(PassportConstants.AUTHCODE_CACHE_KEY, authCode)).set(username, CacheExpires.IN_5MINS);
	}

	public Account findAccountByAuthCode(String authCode) {
		String loginName = new RedisString(String.format(PassportConstants.AUTHCODE_CACHE_KEY, authCode)).get();
		return accountService.findAcctountByLoginName(loginName);
	}

	public boolean checkAuthCode(String authCode) {
		return new  RedisString(String.format(PassportConstants.AUTHCODE_CACHE_KEY, authCode)).exists();
	}


	public boolean checkAccessToken(String accessToken) {
		return AuthSessionHelper.accesstokenExists(accessToken);
	}

	public String getUsernameByAuthCode(String authCode) {
		return new  RedisString(String.format(PassportConstants.AUTHCODE_CACHE_KEY, authCode)).get();
	}

	public long createExpireIn() {
		return 3600L;
	}

	public boolean checkClientId(String clientId) {
		AppEntity entity = appMapper.findByClientId(clientId);
		return entity != null;
	}

	public boolean checkClientSecret(String clientId,String clientSecret) {
		AppEntity entity = appMapper.findByClientId(clientId);
		return entity != null && entity.getClientSecret().equals(clientSecret);
	}


}
