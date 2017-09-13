package com.jeesuite.passport.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.passport.service.AccountService;

public class BaseAuthController {

	@Value("${auth.safe.domain}")
	protected String safeDomain;
	@Autowired
	protected AccountService accountService;
	
	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request ,HttpServletResponse response){
		String redirctUrl = request.getHeader("Referer");
		
		AuthSessionHelper.destroySession(request, response,safeDomain);
		
		redirctUrl = request.getRequestURL().toString().replace("/logout", "/login") + "?redirect_uri=" + redirctUrl;
		return "redirect:" + redirctUrl;
	}
	
	
	protected Account validateUser( HttpServletRequest request ,Model model) {
		String username = request.getParameter(OAuth.OAUTH_USERNAME);
		String password = request.getParameter(OAuth.OAUTH_PASSWORD);
		if ( StringUtils.isEmpty(username) || StringUtils.isEmpty(password) ) {
			model.addAttribute("error", "登录失败:用户名或密码不能为空");
			return null;
		}
		
		Account account = accountService.findAcctountByLoginName(username);
		//TODO 验证用户密码
		if(account == null){
			model.addAttribute("error", "登录失败:用户名或密码错误");
			return null;
		}
		
	   return account;
	}
	
	protected LoginSession createLoginSesion(HttpServletResponse response,Account account){
		LoginSession session = new LoginSession(true);
		session.setUserId(account.getId());
		session.setUserName(account.getUsername());
		//TODO 
		AuthSessionHelper.storgeLoginSession(session);
		
		if(response != null){
			Cookie cookie = AuthSessionHelper.createSessionCookies(session.getSessionId(), safeDomain, session.getExpiresIn());
			response.addCookie(cookie);
			//
		}
		return session;
	}
}
