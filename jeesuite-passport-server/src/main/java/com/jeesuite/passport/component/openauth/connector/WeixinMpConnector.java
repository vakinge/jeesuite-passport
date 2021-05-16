package com.jeesuite.passport.component.openauth.connector;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.passport.component.openauth.AppConfig;
import com.jeesuite.passport.component.openauth.OauthUser;

public class WeixinMpConnector  {
	
	private static Logger logger = LoggerFactory.getLogger(WeixinMpConnector.class);

	public static final String TYPE = "wechat:gzh";
	
	public static final String SNSAPI_BASE = "snsapi_base";
	
	public static final String SNSAPI_USERINFO = "snsapi_userinfo";
	
	private Map<String, AppConfig> appConfigs = new HashMap<>();
	
	public WeixinMpConnector() {}
	
	public boolean contains(String appName){
		return appConfigs.containsKey(appName);
	}

	public void addConfig(String appName, String appkey, String appSecret){
		appConfigs.put(appName, new AppConfig(appkey, appSecret));
	}
	

	public String getClientId(String appName) {
		AppConfig appConfig = appConfigs.get(appName);
		if(appConfig == null)throw new JeesuiteBaseException(1001, "no wxgzh config for["+appName+"]");
		return appConfig.getAppkey();
	}

	public String getClientSecret(String appName) {
		AppConfig appConfig = appConfigs.get(appName);
		return appConfig.getAppSecret();
	}

	public String getAuthorizeUrl(String appName, String scope,String redirectUrl,String state) {

		StringBuilder urlBuilder = new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?");
		urlBuilder.append("appid=").append(getClientId(appName))
                .append("&redirect_uri=").append(redirectUrl)
                .append("&response_type=code&scope=").append(scope)
                .append("&state=")
                .append(state)
                .append("#wechat_redirect");

		return urlBuilder.toString();
	}

	protected JSONObject getAccessToken(String appName,String code) {

		StringBuilder urlBuilder = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code");
		urlBuilder.append("&appid=" + getClientId(appName));
		urlBuilder.append("&secret=" + getClientSecret(appName));
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = HttpUtils.get(url).getBody();

		logger.debug("getAccessToken response:{}",httpString);
		return JSONObject.parseObject(httpString);
	}

	public OauthUser getUser(String appName,String code) {

		JSONObject tokenJson = getAccessToken(appName,code);
		String accessToken = tokenJson.getString("access_token");
		String openId = tokenJson.getString("openid");
		
		if(StringUtils.isAnyBlank(accessToken,openId)){
			//:{"errcode":40125,"errmsg":"invalid appsecret, view more at http:\/\/t.cn\/RAEkdVq, hints: [ req_id: CIa870106th27 ]"}
			String errorMsg = tokenJson.containsKey("errmsg") ? tokenJson.getString("errmsg") : "获取accesstoken错误";
			throw new JeesuiteBaseException(4001, errorMsg);
		}

		String url = "https://api.weixin.qq.com/sns/userinfo?" + "access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";

		String httpString = HttpUtils.get(url).getBody();

		OauthUser user = new OauthUser();
		JSONObject json = JSONObject.parseObject(httpString);
		user.setAvatar(json.getString("headimgurl"));
		user.setNickname(json.getString("nickname"));
		user.setOpenId(openId);
		int sex = json.getIntValue("sex");
		user.setGender(sex == 1 ? "male" : "female");
		user.setUnionId(json.getString("unionid"));

		return user;
	}
	
	public String snsType() {
		return TYPE;
	}

}
