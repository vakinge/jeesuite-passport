package com.jeesuite.passport.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.cache.redis.JedisProvider;
import com.jeesuite.cache.redis.JedisProviderFactory;
import com.jeesuite.cache.redis.sentinel.JedisSentinelProvider;
import com.jeesuite.cache.redis.standalone.JedisStandaloneProvider;
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
		
		if(!AuthConfigClient.getInstance().isInitialized()){
			log.warn("AuthConfigClient can't initialize,because AuthConfig don't initialized");
			return;
		}
		
		log.info("JedisProvider initialized -> begin");
		String mode = AuthConfigClient.getInstance().getAuthRedisMode();
		String[] servers = AuthConfigClient.getInstance().getAuthRedisServer();
		int timeout = AuthConfigClient.getInstance().getAuthRedisTimeout();
		String password = AuthConfigClient.getInstance().getAuthRedisPassword();
		int database = AuthConfigClient.getInstance().getAuthRedisDatabase();
		String masterName = AuthConfigClient.getInstance().getAuthRedisMasterName();
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
	

}
