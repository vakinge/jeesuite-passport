package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.AuthUserDetails;
import com.jeesuite.passport.dto.LoginClientInfo;
import com.jeesuite.passport.dto.LoginParam;
import com.jeesuite.passport.dto.LoginResult;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.AccessToken;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.exception.UnauthorizedException;
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
@RequestMapping(value = "/auth")
public class LoginController extends BaseLoginController{

	@Value("${front.login.url}?ticket=%s")
	private String frontLoginUrl;
	
	@RequestMapping(value = "login",method = RequestMethod.GET)
	public String toLoginPage(HttpServletRequest request,HttpServletResponse response){
		
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);
		if(StringUtils.isBlank(returnUrl))returnUrl = request.getHeader(HttpHeaders.REFERER);
		if(StringUtils.isBlank(returnUrl)){
			returnUrl = frontUcenterUrl;
		}
		
		UserSession session = SecurityDelegating.getCurrentSession();
		//已登录
		if(session != null && !session.isAnonymous()) {
			String redirect = buildLoginResult(session, clientId, returnUrl).getRedirect();
			if(redirect == null) {
				redirect = frontUcenterUrl;
			}
			return redirectTo(redirect);
		}
		
		String ticket;
		if(StringUtils.isBlank(clientId)){
			ticket = StringUtils.EMPTY;
		}else {
			LoginClientInfo ticketInfo = new LoginClientInfo(clientId, returnUrl);
			ticket = SecurityDelegating.getSessionManager().setTemporaryObject(AppConstants.TICKET, ticketInfo, 60);
		}
		String redirectUrl = String.format(frontLoginUrl,ticket);
		
		return redirectTo(redirectUrl);
	}
	
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public @ResponseBody WrapperResponse<LoginResult> login(HttpServletRequest request,@RequestBody LoginParam param){
		String ticket = request.getParameter(SecurityConstants.PARAM_TICKET);
		String returnUrl = null;
		String clientId = null;
		
		if(StringUtils.isNotBlank(ticket)) {			
			LoginClientInfo ticketInfo = SecurityDelegating.getSessionManager().getTemporaryObjectByEncodeKey(ticket);
			if(ticketInfo == null) {
				throw new JeesuiteBaseException(4001, "临时票据过期或不正确");
			}
			clientId = ticketInfo.getClientId();
			returnUrl = ticketInfo.getReturnUrl();
		}

		String username = param.getAccount();
		String password = param.getPassword();
		
		if (StringUtils.isAnyBlank(username, password)) {
			throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
		}
		
		UserSession session = SecurityDelegating.doAuthentication(username, password);
		
		return new WrapperResponse<>(buildLoginResult(session, clientId, returnUrl));
	}
	
	@RequestMapping(value = "logout",method = {RequestMethod.POST,RequestMethod.GET})
	public String logout(HttpServletRequest request ,HttpServletResponse response){
		
		ClientConfigEntity clientConfig = validateAndGetClientConfig(request);
		
		SecurityDelegating.doLogout();
		//TODO 异步发送登出
		
		if(clientConfig == null) {
			Map<String, String> data = new HashMap<>(1);
			data.put("redirect", String.format(frontLoginUrl, StringUtils.EMPTY));
			WebUtils.responseOutJson(response, JsonUtils.toJson(new WrapperResponse<>(data)));
			return null;
		}else {
			String returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);	
			return redirectTo(returnUrl);
		}
	}
	
	
	@RequestMapping(value = "current_user", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<AuthUserDetails> getMyInfo(){
		UserSession session = SecurityDelegating.getCurrentSession();
		AuthUserDetails account = userService.findUserById(session.getUserId()).toAuthUser();
		return new WrapperResponse<>(account);
	}
	
	@RequestMapping(value = "status",method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<AccessToken> getLonginStatus(){
		UserSession session = SecurityDelegating.getCurrentSession();
		//未登录
		if(session == null || session.isAnonymous()){
			throw new UnauthorizedException();
		}else{
			return new WrapperResponse<>(session.asAccessToken());
		}
	}

}
