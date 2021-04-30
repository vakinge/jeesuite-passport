package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.LoginParam;
import com.jeesuite.passport.dto.LoginTicketInfo;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * 账号密码登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年3月26日
 */
@Controller  
@RequestMapping(value = "/service")
public class LoginController extends BaseLoginController{

	@Value("${front.login.url}?return_url?=%s&ticket=%s")
	private String frontLoginUrl;
	@Value("${front.ucenter.url}")
	private String frontUcenterUrl;
	
	@RequestMapping(value = "login",method = RequestMethod.GET)
	public String toLoginpage(HttpServletRequest request,HttpServletResponse response){
		
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);
		if(StringUtils.isBlank(returnUrl))returnUrl = request.getHeader(HttpHeaders.REFERER);
		if(StringUtils.isBlank(returnUrl)){
			clientId = AppConstants.DEFAULT_CLIENT_ID;
			returnUrl = frontUcenterUrl;
		}else if(!returnUrl.startsWith("http")){
			returnUrl = WebUtils.getBaseUrl(request) + returnUrl;
		}
		
		String redirectUrl;
		if(StringUtils.isBlank(clientId)){
			redirectUrl = appendQueryParam(returnUrl, "errorMessage", "client_id is required");
		}else {
			String ticket = SecurityDelegating.objectToTicket(new LoginTicketInfo(clientId,null, returnUrl));
			redirectUrl = String.format(frontLoginUrl, returnUrl,ticket);
		}
		
		return redirectTo(redirectUrl);
	}
	
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public String login(HttpServletRequest request,@RequestBody LoginParam param){
		String ticket = request.getParameter(SecurityConstants.PARAM_TICKET);
		String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);

		String clientId = AppConstants.DEFAULT_CLIENT_ID;
		if(StringUtils.isNotBlank(ticket)) {			
			LoginTicketInfo ticketInfo = SecurityDelegating.ticketToObject(ticket);
			clientId = ticketInfo.getClientId();
		}
		
		if(StringUtils.isBlank(returnUrl)){
			
		}
		
		String username = request.getParameter(OAuth.OAUTH_USERNAME);
		String password = request.getParameter(OAuth.OAUTH_PASSWORD);
		
		if (StringUtils.isAnyBlank(username, password)) {
			throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
		}
		
		UserSession session = SecurityDelegating.doAuthentication(username, password);
		
		if(AppConstants.DEFAULT_CLIENT_ID.equals(clientId)){
			return redirectTo(returnUrl);
		}else{
			ClientConfigEntity clientConfig = getClientConfig(clientId);
			return loginSuccessRedirect(session, clientConfig.getCallbackUri(),returnUrl);
		}
	}
	
	@RequestMapping(value = "logout",method = {RequestMethod.POST,RequestMethod.GET})
	public String logout(HttpServletRequest request ,HttpServletResponse response){
		String redirctUrl = request.getHeader(HttpHeaders.REFERER);
		String baseUrl = WebUtils.getBaseUrl(request);
		if(redirctUrl.startsWith(baseUrl)){
			redirctUrl = baseUrl + "/login";
		}
		SecurityDelegating.doLogout();
		return "redirect:" + redirctUrl;
	}
	
	@RequestMapping(value = "redirect",method = RequestMethod.GET)
	public String redirect(Model model,HttpServletRequest request){
		
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);
		
		UserSession session = SecurityDelegating.getCurrentSession();
		//未登录跳转回登录页面
		if(session == null || session.isAnonymous()){
			model.addAttribute(SecurityConstants.PARAM_RETURN_URL, returnUrl);
			model.addAttribute(SecurityConstants.PARAM_CLIENT_ID, clientId);
			return "login";
		}else{
			//
			ClientConfigEntity clientConfig = getClientConfig(clientId);
			return loginSuccessRedirect(session, clientConfig.getCallbackUri(),returnUrl);
		}
	}

}
