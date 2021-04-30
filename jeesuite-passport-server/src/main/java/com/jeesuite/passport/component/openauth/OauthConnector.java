package com.jeesuite.passport.component.openauth;

import java.util.Map;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.http.HttpResponseEntity;
import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.common.json.JsonUtils;


public abstract class OauthConnector {

	// 第一步，构建跳转的URL，跳转后用户登录成功，返回到callback url，并带上code
	// 第二步，通过code，获取access token
	// 第三步，通过 access token 获取用户的open_id
	// 第四步，通过 open_id 获取用户信息
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
		HttpResponseEntity responseEntity = HttpUtils.get(url);
		if(!responseEntity.isSuccessed()) {
			throw new JeesuiteBaseException(400, responseEntity.getMessage());
		}
		return responseEntity.getBody();
	}
	
	protected String httpPost(String url,Map<String, String> params) {
		HttpResponseEntity responseEntity = HttpUtils.postJson(url, JsonUtils.toJson(params));
		if(!responseEntity.isSuccessed()) {
			throw new JeesuiteBaseException(400, responseEntity.getMessage());
		}
		return responseEntity.getBody();
	}

	public abstract String createAuthorizeUrl(String state);

	protected abstract OauthUser getOauthUser(String code);

	public abstract String snsType();
	
	public OauthUser getUser(String code) {
		return getOauthUser(code);
	}

}
