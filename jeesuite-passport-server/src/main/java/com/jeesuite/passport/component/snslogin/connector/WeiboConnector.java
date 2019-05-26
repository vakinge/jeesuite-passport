package com.jeesuite.passport.component.snslogin.connector;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jeesuite.passport.component.snslogin.OauthConnector;
import com.jeesuite.passport.component.snslogin.OauthUser;


public class WeiboConnector extends OauthConnector {

	public static final String SNS_TYPE = "weibo";
	
	public WeiboConnector(String appkey, String appSecret) {
		super(appkey, appSecret);
	}

	public String createAuthorizeUrl(String state) {

		StringBuilder urlBuilder = new StringBuilder("https://api.weibo.com/oauth2/authorize?");
		urlBuilder.append("scope=email");
		urlBuilder.append("&client_id=" + getClientId());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&state=" + state);

		return urlBuilder.toString();
	}

	protected OauthUser getOauthUser(String code) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "authorization_code");
		params.put("client_id", getClientId());
		params.put("client_secret", getClientSecret());
		params.put("redirect_uri", getRedirectUri());
		params.put("code", code);

		String url = "https://api.weibo.com/oauth2/access_token";
		String httpString = httpPost(url, params);

		if (StringUtils.isBlank(httpString)) {
			return null;
		}

		JSONObject json = JSONObject.parseObject(httpString);
		String accessToken = json.getString("access_token");
		String uid = json.getString("uid");

		url = "https://api.weibo.com/2/users/show.json?" + "access_token=" + accessToken + "&uid=" + uid;

		httpString = httpGet(url);
		json = JSONObject.parseObject(httpString);

		OauthUser user = new OauthUser();
		user.setAvatar(json.getString("avatar_large"));
		user.setNickname(json.getString("screen_name"));
		user.setOpenId(json.getString("id"));
		user.setGender(genders.get(json.getString("gender")));

		return user;
	}
	
	@Override
	public String snsType() {
		return SNS_TYPE;
	}

	static Map<String, String> genders = new HashMap<String, String>();

	static {
		genders.put("m", "male");
		genders.put("f", "female");
		genders.put("n", "unkown");
	}

}
