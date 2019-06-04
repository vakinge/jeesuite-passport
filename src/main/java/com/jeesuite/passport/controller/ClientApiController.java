package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.component.jwt.JwtHelper;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.security.SecurityConstants.CacheType;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.model.WrapperResponse;
import com.jeesuite.springweb.utils.ParameterUtils;

/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2019年6月4日
 */
@Controller  
@RequestMapping(value = "/api")
public class ClientApiController {

	@Autowired
	private AppService appService;
	
	@RequestMapping(value = "get_auth_configs", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<Map<String,Map<String,String>>> getAuthConfigs(HttpServletRequest request){
		preCheck(request);
		
		Map<String, Map<String,String>> data = new HashMap<>();
		data.put("jwtConfig", JwtHelper.getJwtConfigs());
		
		if(SecurityDelegating.getSecurityDecision().cacheType() == CacheType.redis){
			Map<String,String> redisConfig = new HashMap<>();
			Properties properties = ResourceUtils.getAllProperties("auth.cache");
			properties.forEach((k,v)-> {
				redisConfig.put(k.toString(), v.toString());
			});
			data.put("redisConfig", redisConfig);
		}
		
 		return new WrapperResponse<>();
	}
	
	@RequestMapping(value = "check_access_token", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<Boolean> checkSessionId(HttpServletRequest request,String accessToken){
		preCheck(request);
		boolean validated = SecurityDelegating.validateSessionId(accessToken);
		return new WrapperResponse<>(validated);
	}
	
	@RequestMapping(value = "ticket_exchange_jwt", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<String> ticketExchangeJWT(HttpServletRequest request,String ticket){
		preCheck(request);
		String sessionId = SecurityDelegating.ticketToObject(ticket);
		if(StringUtils.isBlank(sessionId))throw new JeesuiteBaseException(500, "ticket不存在或已过期");
		UserSession session = SecurityDelegating.genUserSession(sessionId);
		String jwt = JwtHelper.createToken(session);
		
		return new WrapperResponse<>(jwt);
	}
	
	private void preCheck(HttpServletRequest request){
		Map<String, Object> params = ParameterUtils.queryParamsToMap(request);
		
		String appId = Objects.toString(params.get("appId"), null);
		String timestamp = Objects.toString(params.get("timestamp"), null); 
		String sign = Objects.toString(params.get("sign"), null); 
		if(StringUtils.isAnyBlank(appId,timestamp,sign)){
			throw new JeesuiteBaseException(500, "Parameter[appId,timestamp,sign] is required");
		}
		ClientConfigEntity entity = appService.findByClientId(params.get("appId").toString());
		if(entity == null)throw new JeesuiteBaseException(500, "appId不存在");
		
		String signBase = ParameterUtils.mapToQueryParams(params) + entity.getClientSecret();
		String expectSign = DigestUtils.md5(signBase);
		
		if(!StringUtils.equals(sign, expectSign))throw new JeesuiteBaseException(500, "签名错误");
	}
}
