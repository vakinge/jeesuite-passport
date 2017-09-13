/**
 * 
 */
package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.passport.dao.entity.AppEntity;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.springboot.starter.cache.CacheProperties;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Controller  
@RequestMapping(value = "/clientside")
public class ClientSideController {

	@Autowired
	private AppService appService;
	
	@Autowired
	private CacheProperties cacheProperties;
	
	@GetMapping(value = "sync_config")
	public @ResponseBody Map<String, String> syncConfig(HttpServletRequest request){
		Map<String, String> result = new HashMap<>();
		String clientId = request.getParameter(OAuth.OAUTH_CLIENT_ID);
		String sign = request.getParameter("sign");
		
		AppEntity app = appService.findByClientId(clientId);
		if(app == null){
			result.put("error", "未找到clientId注册APP信息");
			return result;
		}
		String expectSign = DigestUtils.md5WithSalt(clientId, app.getClientSecret());
		if(!StringUtils.equals(sign, expectSign)){
			result.put("error", "签名错误");
			return result;
		}
		
		result.put("auth.redis.mode", cacheProperties.getMode());
		result.put("auth.redis.servers", cacheProperties.getServers());
		result.put("auth.redis.password", cacheProperties.getPassword());
		result.put("auth.redis.database", String.valueOf(cacheProperties.getDatabase()));
		result.put("auth.redis.masterName", cacheProperties.getMasterName());
		
		result.put("auth.safe.domain", ResourceUtils.getProperty("auth.safe.domain"));
		
		return result;
	}
	
}
