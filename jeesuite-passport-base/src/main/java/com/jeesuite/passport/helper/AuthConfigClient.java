package com.jeesuite.passport.helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.common.util.ResourceUtils;

public class AuthConfigClient {
	
	private static final String DEFAULT_SECRET_KEY = DigestUtils.md5("jeesuite");

	private static Logger log = LoggerFactory.getLogger(AuthConfigClient.class);

	private static AuthConfigClient instance = new AuthConfigClient();
	
	private boolean initialized = false;
	private AuthConfigClient() {
		try {
			Class.forName("com.jeesuite.passport.controller.ClientSideController");
			initialized = true;
		} catch (Exception e) {}
		
		fetchFromServer();
	}
	
	public static AuthConfigClient getInstance() {
		if(!instance.initialized){
			instance.fetchFromServer();
		}
		return instance;
	}

	@SuppressWarnings("rawtypes")
	private synchronized void fetchFromServer(){
		if(initialized)return;
		try {
			log.info("fetchFromServer -> begin");
			String clientId = ResourceUtils.getProperty("auth.client.id");
			String clientSecret = ResourceUtils.getProperty("auth.client.secret");
			
			long timestamp = System.currentTimeMillis();
			String url = ResourceUtils.getAndValidateProperty("auth.server.baseurl") + "/clientside/sync_config?client_id=%s&sign=%s&timestamp=%s";
			Map<String, String> map = new HashMap<>();
			map.put("timestamp", String.valueOf(timestamp));
			map.put("client_id", clientId);
			String sign = SecurityCryptUtils.generateSign(clientSecret, map);
			url = String.format(url, clientId,sign,timestamp);
			String json = null;
			try {
				json = HttpUtils.get(url).getBody();
			} catch (Exception e) {
				//retry
				try {Thread.sleep(2000);} catch (Exception e2) {}
				json = HttpUtils.get(url).getBody();
			}
			Map remoteConfigs = JsonUtils.toObject(json, Map.class);
			if(remoteConfigs.containsKey("error")){
				throw new RuntimeException(remoteConfigs.get("error") + "");
			}
			
			if(remoteConfigs.containsKey("code")){
				if(!"200".equals(remoteConfigs.get("code").toString())){
					throw new JeesuiteBaseException(501, "auth service error:" + remoteConfigs.get("msg"));
				}
				remoteConfigs = (Map) remoteConfigs.get("data");
			}
			
			for (Object key : remoteConfigs.keySet()) {
				ResourceUtils.add(key.toString(), remoteConfigs.get(key).toString());
			}
			
			initialized = true;
			log.info("fetchFromServer -> finish");
		} catch (Exception e) {
			log.warn("fetchFromServer -> error", e);
		}
		
	}
	

	public boolean isInitialized() {
		return initialized;
	}

	public String getAuthRedisMode(){
		return ResourceUtils.getAndValidateProperty("auth.redis.mode");
	}
	
	public String[] getAuthRedisServer(){
		return ResourceUtils.getAndValidateProperty("auth.redis.servers").split(",");
	}
	
	public String getAuthRedisPassword(){
		return ResourceUtils.getProperty("auth.redis.password");
	}
	
	public int getAuthRedisTimeout(){
		return Integer.parseInt(ResourceUtils.getProperty("auth.redis.conn.timeout","3000"));
	}
	
	public int getAuthRedisDatabase(){
		return Integer.parseInt(ResourceUtils.getProperty("auth.redis.database","0"));
	}
	
	public String getAuthRedisMasterName(){
		return ResourceUtils.getAndValidateProperty("auth.redis.masterName");
	}
	
	public String getJwtSecret(){
		return ResourceUtils.getAndValidateProperty("auth.jwt.secret");
	}
	
	public String getCryptType(){
		return ResourceUtils.getProperty("auth.crypt.type", "DES");
	}
	
	public String getCryptSecret(){
		String property = ResourceUtils.getProperty("auth.crypt.key",DEFAULT_SECRET_KEY);
		return DigestUtils.md5(property).substring(0, 2) + DigestUtils.md5Short(property);
	}
}
