package com.jeesuite.passport.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.dto.AuthUserDetails;
import com.jeesuite.passport.dto.LoginClientInfo;
import com.jeesuite.passport.dto.LoginParam;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.model.WrapperResponse;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * 账号密码登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年3月26日
 */
@Controller  
@RequestMapping(value = "/sso")
public class LoginController extends BaseLoginController{

	@Value("${front.login.url}?ticket=%s")
	private String frontLoginUrl;
	
	@RequestMapping(value = "login",method = RequestMethod.GET)
	public String toLoginpage(HttpServletRequest request,HttpServletResponse response){
		
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);
		if(StringUtils.isBlank(returnUrl))returnUrl = request.getHeader(HttpHeaders.REFERER);
		if(StringUtils.isBlank(returnUrl)){
			clientId = AppConstants.DEFAULT_CLIENT_ID;
			returnUrl = frontUcenterUrl;
		}
		
		String redirectUrl;
		if(StringUtils.isBlank(clientId)){
			redirectUrl = appendQueryParam(returnUrl, "error", "client_id is required");
		}else {
			LoginClientInfo ticketInfo = new LoginClientInfo(clientId, returnUrl);
			String ticket = SecurityDelegating.getSessionManager().setTemporaryObject(AppConstants.TICKET, ticketInfo, 60);
			redirectUrl = String.format(frontLoginUrl,ticket);
		}
		
		return redirectTo(redirectUrl);
	}
	
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public @ResponseBody String login(HttpServletRequest request,@RequestBody LoginParam pram){
		String ticket = request.getParameter(SecurityConstants.PARAM_TICKET);
		String returnUrl = request.getHeader(HttpHeaders.REFERER);
		String clientId = AppConstants.DEFAULT_CLIENT_ID;
		
		try {
			if(StringUtils.isNotBlank(ticket)) {			
				LoginClientInfo ticketInfo = SecurityDelegating.getSessionManager().getTemporaryObjectByEncodeKey(ticket);
				if(ticketInfo == null) {
					throw new JeesuiteBaseException(4001, "临时票据过期或不正确");
				}
				clientId = ticketInfo.getClientId();
				returnUrl = ticketInfo.getReturnUrl();
			}

			String username = request.getParameter(OAuth.OAUTH_USERNAME);
			String password = request.getParameter(OAuth.OAUTH_PASSWORD);
			
			if (StringUtils.isAnyBlank(username, password)) {
				throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
			}
			
			UserSession session = SecurityDelegating.doAuthentication(username, password);
			
			if(AppConstants.DEFAULT_CLIENT_ID.equals(clientId)){
				if(StringUtils.isBlank(returnUrl)) {
					returnUrl = frontUcenterUrl;
				}
				return redirectTo(returnUrl);
			}else{
				return loginSuccessRedirect(session,clientId,returnUrl);
			}
		} catch (Exception e) {
			String error = "系统繁忙";
			if(e instanceof JeesuiteBaseException) {
				error = e.getMessage();
			}else {
				logger.error("登录异常",e);
			}
			try {
				error = java.net.URLEncoder.encode(error, "UTF-8");
			} catch (UnsupportedEncodingException e1) {}
			String redirectUrl = String.format(frontLoginUrl, ticket,error);
			return redirectTo(redirectUrl);
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
	
	
	@RequestMapping(value = "current_user", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<AuthUserDetails> getMyInfo(){
		UserSession session = SecurityDelegating.getCurrentSession();
		AuthUserDetails account = accountService.findAcctountById(session.getUserId()).toAuthUser();
		return new WrapperResponse<>(account);
	}
	
	@RequestMapping(value = "status",method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<String> status(){
		UserSession session = SecurityDelegating.getCurrentSession();
		//未登录
		if(session == null || session.isAnonymous()){
			return new WrapperResponse<>(401, "Unauthorized");
		}else{
			return new WrapperResponse<>();
		}
	}

}
