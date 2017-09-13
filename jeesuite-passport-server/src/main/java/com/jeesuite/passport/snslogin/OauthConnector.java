package com.jeesuite.passport.snslogin;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.common.http.HttpRequestEntity;
import com.jeesuite.common.http.HttpUtils;

public abstract class OauthConnector {

	// 第一步，构建跳转的URL，跳转后用户登录成功，返回到callback url，并带上code
	// 第二步，通过code，获取access token
	// 第三步，通过 access token 获取用户的open_id
	// 第四步，通过 open_id 获取用户信息
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthConnector.class);

	private String clientId;
	private String clientSecret;
	private String redirectUri;

	public OauthConnector(String appkey, String appSecret) {
		this.clientId = appkey;
		this.clientSecret = appSecret;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getAuthorizeUrl(String state, String redirectUri) {
		this.redirectUri = redirectUri;
		return createAuthorizeUrl(state);
	}

	protected String httpGet(String url) {
		try {
			return HttpUtils.get(url).getBody();
		} catch (Exception e) {
			LOGGER.error("httpGet error", e);
		}
		return null;
	}
	protected String httpPost(String url,Map<String, String> params) {
		try {
			return HttpUtils.post(url, HttpRequestEntity.create().textParams(params)).getBody();
		} catch (Exception e) {
			LOGGER.error("httpGet error", e);
		}
		return null;
	}

	public abstract String createAuthorizeUrl(String state);

	protected abstract OauthUser getOauthUser(String code);

	public abstract String snsType();
	
	public OauthUser getUser(String code) {
		return getOauthUser(code);
	}

}
