package com.jeesuite.passport.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.Constants;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.passport.service.AccountService;
import com.jeesuite.springweb.utils.WebUtils;

public abstract class BaseAuthController {

	@Autowired
	protected AccountService accountService;
	
	private static String rootDomain;//根域名
	
	@Value("${auth.cookies.domian}")
	protected String authCookiesDomain;
	
	/**
	 * 验证来源域名合法性
	 * @param domain
	 */
	protected boolean validateOrignDomain(String domain){
		return true;
	}
	
	protected Account validateUser( HttpServletRequest request ,Model model) {
		String username = request.getParameter(OAuth.OAUTH_USERNAME);
		String password = request.getParameter(OAuth.OAUTH_PASSWORD);
		if ( StringUtils.isAnyBlank(username,password)) {
			model.addAttribute(Constants.ERROR, "登录失败:用户名或密码不能为空");
			return null;
		}
		
		Account account = accountService.checkAndGetAccount(username,password);
		if(account == null){
			model.addAttribute(Constants.ERROR, "登录失败:用户名或密码错误");
			return null;
		}
		
	   return account;
	}
	
	protected LoginSession createLoginSesion(HttpServletRequest request,Account account){
		String sessionId = null;
		LoginSession session = AuthSessionHelper.getLoginSessionByUserId(account.getId());
		if(session == null && StringUtils.isNotBlank(sessionId = AuthSessionHelper.getSessionId(request))){
			session = AuthSessionHelper.getLoginSession(sessionId);
		}
		if(session == null)session = LoginSession.create(false);
		
		session.setUserId(account.getId());
		session.setUserName(account.getUsername());
		//
		AuthSessionHelper.storgeLoginSession(session);
		
		return session;
	}
	
	
	protected String createSessionAndSetResponse(HttpServletRequest request,HttpServletResponse response,Account account,String redirectUri,String orignUrl){
		
		String orignDomain = WebUtils.getDomain(redirectUri);
		
		LoginSession session = null;
		//同域
		if(StringUtils.contains(orignDomain, authCookiesDomain)){
			session = createLoginSesion(request,account);
			//
			Cookie cookie = AuthSessionHelper.createSessionCookies(session.getSessionId(), authCookiesDomain, session.getExpiresIn());
			response.addCookie(cookie);
			redirectUri = String.format("%s?orign_url=%s", redirectUri,orignUrl);
		}else{
			session = createLoginSesion(request,account);
			orignUrl = StringUtils.trimToEmpty(orignUrl);
			StringBuilder urlBuiler = new StringBuilder(redirectUri);
			urlBuiler.append("?session_id=").append(session.getSessionId());
			urlBuiler.append("&expires_in=").append(session.getExpiresIn());
			urlBuiler.append("&orign_url=").append(orignUrl);
			urlBuiler.append("&auth_code=").append(TokenGenerator.generateWithSign());
			redirectUri = urlBuiler.toString();
		}
		
		return redirectTo(redirectUri);
	}

    
	protected String getRootDomain(HttpServletRequest request) {
		if(rootDomain == null){
			String routeBaseUri = ResourceUtils.getProperty("route.base.url");
			if(routeBaseUri == null){				
				rootDomain = WebUtils.getRootDomain(request);
			}else{
				routeBaseUri = StringUtils.split(routeBaseUri,"/")[1];
				String[] segs = StringUtils.split(routeBaseUri, ".");
				int len = segs.length;
				rootDomain = segs[len - 2] + "."+ segs[len - 1];
			}
		}
		return rootDomain;
	}
	
	protected String redirectTo(String url) {
		return "redirect:" + url;
	}

}
