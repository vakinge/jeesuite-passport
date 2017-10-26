package com.jeesuite.passport.snslogin.connector;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.helper.HttpUtils;
import com.jeesuite.passport.snslogin.AppConfig;
import com.jeesuite.passport.snslogin.OauthUser;

public class WeixinGzhConnector  {
	
	private static Logger logger = LoggerFactory.getLogger(WeixinGzhConnector.class);

	public static final String SNS_TYPE = "wxgzh";
	
	public static final String SNSAPI_BASE = "snsapi_base";
	
	public static final String SNSAPI_USERINFO = "snsapi_userinfo";
	
	private Map<String, AppConfig> appConfigs = new HashMap<>();
	
	public WeixinGzhConnector() {}

	public void addConfig(String groupName, String appkey, String appSecret){
		appConfigs.put(groupName, new AppConfig(appkey, appSecret));
	}
	

	public String getClientId(String groupName) {
		AppConfig appConfig = appConfigs.get(groupName);
		if(appConfig == null)throw new JeesuiteBaseException(1001, "no wxgzh config for["+groupName+"]");
		return appConfig.getAppkey();
	}

	public String getClientSecret(String groupName) {
		AppConfig appConfig = appConfigs.get(groupName);
		return appConfig.getAppSecret();
	}

	public String getAuthorizeUrl(String groupName, String scope,String redirectUrl,String state) {

		StringBuilder urlBuilder = new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?");
		urlBuilder.append("appid=").append(getClientId(groupName))
                .append("&redirect_uri=").append(redirectUrl)
                .append("&response_type=code&scope=").append(scope)
                .append("&state=")
                .append(state)
                .append("#wechat_redirect");

		return urlBuilder.toString();
	}

	protected JSONObject getAccessToken(String groupName,String code) {

		StringBuilder urlBuilder = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code");
		urlBuilder.append("&appid=" + getClientId(groupName));
		urlBuilder.append("&secret=" + getClientSecret(groupName));
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = HttpUtils.httpGet(url);

		logger.debug("getAccessToken response:{}",httpString);
		return JSONObject.parseObject(httpString);
	}

	public OauthUser getUser(String groupName,String code) {

		JSONObject tokenJson = getAccessToken(groupName,code);
		String accessToken = tokenJson.getString("access_token");
		String openId = tokenJson.getString("openid");
		
		if(StringUtils.isAnyBlank(accessToken,openId)){
			//:{"errcode":40125,"errmsg":"invalid appsecret, view more at http:\/\/t.cn\/RAEkdVq, hints: [ req_id: CIa870106th27 ]"}
			String errorMsg = tokenJson.containsKey("errmsg") ? tokenJson.getString("errmsg") : "获取accesstoken错误";
			throw new JeesuiteBaseException(4001, errorMsg);
		}

		String url = "https://api.weixin.qq.com/sns/userinfo?" + "access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";

		String httpString = HttpUtils.httpGet(url);

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
		return SNS_TYPE;
	}

}
