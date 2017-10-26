package com.jeesuite.passport.helper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.cache.redis.JedisProvider;
import com.jeesuite.cache.redis.JedisProviderFactory;
import com.jeesuite.cache.redis.sentinel.JedisSentinelProvider;
import com.jeesuite.cache.redis.standalone.JedisStandaloneProvider;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.common.util.ResourceUtils;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 权限验证redis客户端
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年9月26日
 */
public class AuthRedisClient {

	
	private static Logger log = LoggerFactory.getLogger(AuthRedisClient.class);
	private static final String AUTH_CACHE_GROUP = "auth_cache";
	private boolean initialized = false;
	private String authServerBasePath;
	
	private static AuthRedisClient instance = new AuthRedisClient();
	
	private AuthRedisClient() {}
	

	public static AuthRedisClient getInstance() {
		if(!instance.initialized){
			instance.initAuthCacheRedis();
		}
		return instance;
	}
	
	public <T> T get(String key) {
		return new RedisObject(key,AUTH_CACHE_GROUP).get();
	}

	public String getStr(String key){
		return new RedisString(key,AUTH_CACHE_GROUP).get();
	}

	public boolean set(String key, Object value, long expireSeconds) {
		if(value == null)return false;
		return new RedisObject(key,AUTH_CACHE_GROUP).set(value, expireSeconds);
	}
	
	public boolean setStr(String key, Object value, long expireSeconds) {
		if(value == null)return false;
		return new RedisString(key,AUTH_CACHE_GROUP).set(value.toString(),expireSeconds);
	}


	public boolean remove(String key,boolean isString) {
		if(isString)return new RedisString(key,AUTH_CACHE_GROUP).remove();
		return new RedisObject(key,AUTH_CACHE_GROUP).remove();
	}
	
	public boolean exists(String key,boolean isString) {
		if(isString)return new RedisString(key,AUTH_CACHE_GROUP).exists();
		return new RedisObject(key,AUTH_CACHE_GROUP).exists();
	}

	private synchronized void initAuthCacheRedis() {
		if(initialized){
			return;
		}
		
		if(JedisProviderFactory.containsGroup(AUTH_CACHE_GROUP)){
			initialized = true;
			return;
		}
		
		//如果配置中心没配置则从认证服务器拉取
		if(!ResourceUtils.containsProperty("auth.redis.servers")){			
			fetchFromServer();
		}
		
		log.info("JedisProvider initialized -> begin");
		// 从服务器同步配置
		String mode = ResourceUtils.getProperty("auth.redis.mode");
		String[] servers = ResourceUtils.getProperty("auth.redis.servers").split(",");
		int timeout = ResourceUtils.getInt("auth.redis.conn.timeout", 3000);
		String password = ResourceUtils.getProperty("auth.redis.password");
		int database = ResourceUtils.getInt("auth.redis.database", 0);
		String masterName = ResourceUtils.getProperty("auth.redis.masterName");
		String clientName = AUTH_CACHE_GROUP + "_" + ResourceUtils.getProperty("auth.client.id");
				
		JedisProvider<Jedis, BinaryJedis> provider = null;
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(1);
		poolConfig.setMinEvictableIdleTimeMillis(60 * 1000);
		poolConfig.setMaxTotal(ResourceUtils.getInt("auth.redis.pool.max", 15));
		poolConfig.setMaxWaitMillis(30 * 1000);
		
		if(JedisSentinelProvider.MODE.equals(mode)){
			provider = new JedisSentinelProvider(AUTH_CACHE_GROUP, poolConfig, servers, timeout, password, database, clientName, masterName);
		}else if(JedisStandaloneProvider.MODE.equals(mode)){
			provider = new JedisStandaloneProvider(AUTH_CACHE_GROUP, poolConfig, servers,timeout, password, database, clientName);
		}
		JedisProviderFactory.addProvider(provider);
		
		initialized = true;
		
		log.info("JedisProvider initialized -> finish");
	}
	
	private String authServerBasePath(){
		if(authServerBasePath == null){
			authServerBasePath = ResourceUtils.getProperty("auth.server.baseurl");
			if(authServerBasePath.endsWith("/"))authServerBasePath = authServerBasePath.substring(0, authServerBasePath.length() - 1);
		}
		return authServerBasePath;
	}
	
	private void fetchFromServer(){
		try {
			log.info("fetchFromServer -> begin");
			String clientId = ResourceUtils.getProperty("auth.client.id");
			String clientSecret = ResourceUtils.getProperty("auth.client.secret");
			
			String url = authServerBasePath() + "/clientside/sync_config?client_id=%s&sign=%s";
			String sign = DigestUtils.md5WithSalt(clientId, clientSecret);
			url = String.format(url, clientId,sign);
			String json = null;
			try {
				json = HttpUtils.get(url).getBody();
			} catch (Exception e) {
				//retry
				try {Thread.sleep(2000);} catch (Exception e2) {}
				json = HttpUtils.get(url).getBody();
			}
			@SuppressWarnings("rawtypes")
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
			
			log.info("fetchFromServer -> finish");
		} catch (Exception e) {
			log.warn("fetchFromServer -> error", e);
			throw new JeesuiteBaseException(501, "auth service unavailable");
		}
		
	}

}
