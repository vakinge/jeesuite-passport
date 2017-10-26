package com.jeesuite.passport.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.annotation.RequireSecurityOption;
import com.jeesuite.passport.dto.TicketCheckParam;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.annotation.CorsEnabled;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

@Controller  
@RequestMapping(value = "/")
public class AuthCommonController{

	private String[] setSsoCookieUris = org.springframework.util.StringUtils.tokenizeToStringArray(ResourceUtils.getProperty("auth.set-sso-cookie.uris"), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
	
	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request ,HttpServletResponse response){
		String redirctUrl = request.getHeader(HttpHeaders.REFERER);
		
		AuthSessionHelper.destroySessionAndCookies(request, response);
		return "redirect:" + redirctUrl;
	}
	
	@RequestMapping(value = "sso/setcookie")
	public String ssoSetLoginCookies(HttpServletResponse response,@RequestParam("ticket") String ticket,@RequestParam("session_id") String sessionId){
		String url = new RedisString(ticket).get();
		if(StringUtils.isBlank(url)){
			WrapperResponseEntity entity = new WrapperResponseEntity(403, "未授权访问01");
			WebUtils.responseOutJsonp(response, PassportConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
		
		AuthSessionHelper.validateSessionId(sessionId, true);
		LoginSession session = AuthSessionHelper.getLoginSession(sessionId);
		
		if(session == null){
			WrapperResponseEntity entity = new WrapperResponseEntity(403, "未授权访问02");
			WebUtils.responseOutJsonp(response, PassportConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
		
		StringBuilder urlBuiler = new StringBuilder(url);
		urlBuiler.append("?session_id=").append(sessionId);
		urlBuiler.append("&expires_in=").append(session.getExpiresIn());
		
		return "redirect:"+urlBuiler.toString();
	}
	
	@CorsEnabled
	@RequireSecurityOption(innerInvokeOnly = true,requireLogin = false)
	@RequestMapping(value = "ticket_apply/{action}", method = RequestMethod.GET)
	public @ResponseBody WrapperResponseEntity applyAuthTicket(@PathVariable("action") String action){
		String ticket = TokenGenerator.generate();
		new RedisString(ticket).set(action, CacheExpires.IN_1MIN);
		return new WrapperResponseEntity(ticket);
	}
	
	@CorsEnabled
	@RequireSecurityOption(innerInvokeOnly = true,requireLogin = false)
	@RequestMapping(value = "ticket_check", method = RequestMethod.POST)
	public @ResponseBody WrapperResponseEntity validateTicket(@RequestBody TicketCheckParam param){
		if(StringUtils.isAnyBlank(param.getTicket(),param.getContent())){
			return new WrapperResponseEntity(4001,"输入参数错误");
		}
		String cont = new RedisString(param.getTicket()).get();
		if(StringUtils.equals(param.getContent(), cont)) {
			return new WrapperResponseEntity(true);
		}
		return new WrapperResponseEntity(4001,"ticket验证错误");
	}
	
	
	@CorsEnabled
	@RequestMapping(value = "sso/get_setcookie_list", method = RequestMethod.GET)
	public @ResponseBody List<String> getSsoSetCookieTickets(HttpServletRequest request){
		
		String domain = WebUtils.getDomain(request.getHeader(HttpHeaders.REFERER));
		
		List<String> tickets = new ArrayList<String>();
		for (String uri : setSsoCookieUris) {
			if(uri.contains(domain))continue;
			String ticket = TokenGenerator.generate();
			new RedisString(ticket).set(uri, CacheExpires.IN_3MINS);
			tickets.add(ticket);
		}
		
		return tickets;
	}

}
