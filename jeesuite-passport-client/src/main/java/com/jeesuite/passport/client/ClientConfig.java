/**
 * 
 */
package com.jeesuite.passport.client;

import java.util.HashMap;
import java.util.Map;

import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.common.util.ResourceUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
public class ClientConfig {
	
	private static Map<String, String> configs = new HashMap<String, String>();
	
	static{
		fetchFromServer();
	}
	
	private static String clientId;
	private static String clientSecret;
	private static String safeDomain;
	private static String redirctUri;
	private static String authServerBasePath;
	
	@SuppressWarnings("unchecked")
	private static synchronized void fetchFromServer(){
		if(!configs.isEmpty())return;
		try {
			configs.put("auth.client.id", ResourceUtils.getProperty("auth.client.id"));
			configs.put("auth.client.secret", ResourceUtils.getProperty("auth.client.secret"));
			configs.put("auth.redirect.uri", ResourceUtils.getProperty("auth.redirect.uri"));
			configs.put("auth.server.baseurl", ResourceUtils.getProperty("auth.server.baseurl"));
			clientId = configs.get("auth.client.id");
			clientSecret = configs.get("auth.client.secret");
			
			String url = ClientConfig.authServerBasePath() + "/clientside/sync_config?client_id=%s&sign=%s";
			String sign = DigestUtils.md5WithSalt(clientId, clientSecret);
			url = String.format(url, clientId,sign);
			String json = HttpUtils.get(url).getBody();
			@SuppressWarnings("rawtypes")
			Map remoteConfigs = JsonUtils.toObject(json, Map.class);
			if(remoteConfigs.containsKey("error")){
				throw new RuntimeException(configs.get("error"));
			}
			configs.putAll(remoteConfigs);
		} catch (Exception e) {
			configs.clear();
			throw new RuntimeException(e);
		}
		
	}
	
	public static String get(String key) {
		if(configs.isEmpty()){
			fetchFromServer();
		}
		if (configs.containsKey(key)) {
			return configs.get(key);
		}
        return null;
	}
	
	public static int getInt(String key,int defaultVal){
		String v = get(key);
		if(v != null)return Integer.parseInt(v);
		return defaultVal;
	}
	
	public static long getLong(String key,long defaultVal){
		String v = get(key);
		if(v != null)return Long.parseLong(v);
		return defaultVal;
	}

	public static String clientId(){
		if(clientId == null){
			clientId = configs.get("auth.client.id");
		}
		return clientId;
	}
	
	public static String clientSecret(){
		if(clientSecret == null){
			clientSecret = configs.get("auth.client.secret");
		}
		return clientSecret;
	}
	
	public static String safeDomain() {
		if(safeDomain == null){
			safeDomain = configs.get("auth.safe.domain");
		}
		return safeDomain;
	}

	public static String redirctUri(){
		if(redirctUri == null){
			redirctUri = configs.get("auth.redirect.uri");
		}
		return redirctUri;
	}
	
	public static String authServerBasePath(){
		if(authServerBasePath == null){
			authServerBasePath = configs.get("auth.server.baseurl");
			if(authServerBasePath.endsWith("/"))authServerBasePath = authServerBasePath.substring(0, authServerBasePath.length() - 1);
		}
		return authServerBasePath;
	}
}
