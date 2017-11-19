package com.jeesuite.passport.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisString;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.annotation.CorsEnabled;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

@Controller  
@RequestMapping(value = "/")
public class CommonController{

	private String[] setSsoCookieUris = org.springframework.util.StringUtils.tokenizeToStringArray(ResourceUtils.getProperty("auth.set-sso-cookie.uris"), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
	
	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request ,HttpServletResponse response){
		String redirctUrl = request.getHeader(HttpHeaders.REFERER);
		String baseUrl = WebUtils.getBaseUrl(request);
		if(redirctUrl.startsWith(baseUrl)){
			redirctUrl = baseUrl + "/login";
		}
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
		
		LoginSession session = AuthSessionHelper.getLoginSession(sessionId);
		
		if(session == null){
			WrapperResponseEntity entity = new WrapperResponseEntity(403, "未授权访问02");
			WebUtils.responseOutJsonp(response, PassportConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
		
		StringBuilder urlBuiler = new StringBuilder(url);
		urlBuiler.append("?session_id=").append(sessionId);
		urlBuiler.append("&expires_in=").append(session.getExpiresIn());
		urlBuiler.append("&ticket=").append(TokenGenerator.generateWithSign());
		
		return "redirect:"+urlBuiler.toString();
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
