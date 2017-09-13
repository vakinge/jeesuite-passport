/**
 * 
 */
package com.jeesuite.passport.client;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.cache.redis.JedisProvider;
import com.jeesuite.cache.redis.JedisProviderFactory;
import com.jeesuite.cache.redis.sentinel.JedisSentinelProvider;
import com.jeesuite.cache.redis.standalone.JedisStandaloneProvider;
import com.jeesuite.passport.exception.UnauthorizedException;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月20日
 */
public class AuthChecker {

	private List<String> ignoreUris = new ArrayList<>();
	private List<String> ignoreUrisWithWildcard = new ArrayList<>();

	public AuthChecker(String[] ignoreCheckUris) {
		ignoreUrisWithWildcard.add("/oauth2");
		for (String uri : ignoreCheckUris) {
			if (StringUtils.isEmpty(uri))
				continue;
			if (uri.endsWith("*")) {
				ignoreUrisWithWildcard.add(uri.replaceAll("\\*", ""));
			} else {
				ignoreUris.add(uri);
			}
		}
		//
		initAuthCacheRedis();
	}
	
	public AuthChecker() {
		this(StringUtils.trimToEmpty(ClientConfig.get(ClientConstants.AUTH_IGNORE_URIS)).split(",|;"));
	}
	
	

	public LoginSession process(HttpServletRequest request) {
		// 是否需要鉴权
		boolean requered = ignoreUris.contains(request.getRequestURI());
		if (!requered) {
			for (String uri : ignoreUrisWithWildcard) {
				if (requered = uri.startsWith(uri))
					break;
			}
		}

		String authId = AuthSessionHelper.getAuthId(request);
		if (requered && StringUtils.isBlank(authId)) {
			throw new UnauthorizedException();
		}
		
		LoginSession loginSession = null;

		if(StringUtils.isNotBlank(authId)){
			loginSession = AuthSessionHelper.getLoginSession(authId);
		}
		
		if (requered && loginSession == null) {
			throw new UnauthorizedException();
		}

		return loginSession;
	}

	private void initAuthCacheRedis() {
		// 从服务器同步配置
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(1);
		poolConfig.setMinEvictableIdleTimeMillis(60 * 1000);
		poolConfig.setMaxTotal(ClientConfig.getInt("auth.redis.pool.max", 15));
		poolConfig.setMaxWaitMillis(30 * 1000);
		
		String mode = ClientConfig.get("auth.redis.mode");
		String[] servers = ClientConfig.get("auth.redis.servers").split(",");
		int timeout = ClientConfig.getInt("auth.redis.conn.timeout", 3000);
		String password = ClientConfig.get("auth.redis.password");
		int database = ClientConfig.getInt("auth.redis.database", 0);
		String masterName = ClientConfig.get("auth.redis.masterName");
				
		JedisProvider<Jedis, BinaryJedis> provider = null;
		
		String clientName = AuthSessionHelper.AUTH_CACHE_GROUP + "_" + ClientConfig.clientId();
		if(JedisSentinelProvider.MODE.equals(mode)){
			provider = new JedisSentinelProvider(AuthSessionHelper.AUTH_CACHE_GROUP, poolConfig, servers, timeout, password, database, clientName, masterName);
		}else if(JedisStandaloneProvider.MODE.equals(mode)){
			provider = new JedisStandaloneProvider(AuthSessionHelper.AUTH_CACHE_GROUP, poolConfig, servers,timeout, password, database, clientName);
		}
		JedisProviderFactory.addProvider(provider);
	}

}
