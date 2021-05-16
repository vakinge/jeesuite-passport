package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.passport.component.jwt.JwtHelper;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.AuthnResponse;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.security.SecurityConstants;
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
@RequestMapping(value = "/auth")
public class ClientApiController {

	private static final int SIGN_EXPIRE_MILLIS = 5 * 60 * 1000;
	
	@Autowired
	private AppService appService;
	
	@RequestMapping(value = "configs", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<Map<String,String>> getMetadaatas(HttpServletRequest request){
		preCheck(request);
		
		Map<String,String> data = new HashMap<>();
		Map<String, String> jwtConfigs = JwtHelper.getJwtConfigs();
		jwtConfigs.forEach( (k,v) -> {
			data.put("jwtconfig." + k, v);
		} );
		
 		return new WrapperResponse<>(data);
	}
	
	@RequestMapping(value = "check_access_token", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<Boolean> checkSessionId(HttpServletRequest request,String accessToken){
		preCheck(request);
		boolean validated = SecurityDelegating.validateSessionId(accessToken);
		return new WrapperResponse<>(validated);
	}
	
	@RequestMapping(value = "ticket_exchange", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<AuthnResponse> ticketExchangeJWT(HttpServletRequest request,String ticket){
		preCheck(request);
		
		String sessionId = SecurityDelegating.getSessionAttributeByKey(ticket);
		if(sessionId == null)throw new JeesuiteBaseException(500, "ticket不存在或已过期");
		UserSession session = SecurityDelegating.genUserSession(sessionId);
		
		AuthnResponse result = new AuthnResponse();
		result.setAccessToken(session.getSessionId());
		result.setExpiresIn(session.getExpiresIn());
		result.setAuthUser(session.getUserInfo());
		
		return new WrapperResponse<>(result);
	}
	
	private void preCheck(HttpServletRequest request){
		Map<String, Object> params = ParameterUtils.queryParamsToMap(request);
		
		String clientId = Objects.toString(params.get(SecurityConstants.PARAM_CLIENT_ID), null);
		String timestamp = Objects.toString(params.get(SecurityConstants.PARAM_TIMESTAMP), null); 
		String sign = Objects.toString(params.get("sign"), null); 
		if(StringUtils.isAnyBlank(clientId,timestamp,sign)){
			throw new JeesuiteBaseException(500, "Parameter[client_id,timestamp,sign] is required");
		}
		ClientConfigEntity entity = appService.findByClientId(clientId);
		if(entity == null)throw new JeesuiteBaseException(500, "appId不存在");
		
		if(System.currentTimeMillis() - Long.parseLong(timestamp) > SIGN_EXPIRE_MILLIS) {
			throw new JeesuiteBaseException(500, "请求签名过期");
		}
		String signBase = ParameterUtils.mapToQueryParams(params) + entity.getClientSecret();
		String expectSign = DigestUtils.md5(signBase);
		
		if(!StringUtils.equals(sign, expectSign))throw new JeesuiteBaseException(500, "签名错误");
	}
}
