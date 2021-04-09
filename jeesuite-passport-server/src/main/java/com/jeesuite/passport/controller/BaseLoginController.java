package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.component.jwt.JwtHelper;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.LoginTicketInfo;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.passport.service.AccountService;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.CurrentRuntimeContext;
import com.jeesuite.springweb.utils.WebUtils;


/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2016年4月18日
 */
public abstract class BaseLoginController {

	@Autowired
	protected AccountService accountService;
	
	@Autowired
	protected AppService appService;

	/**
	 * 验证来源域名合法性
	 * @param domain
	 */
	protected ClientConfigEntity getClientConfig(String clientId){
		ClientConfigEntity appEntity = appService.findByClientId(clientId);
		if(appEntity == null)throw new JeesuiteBaseException(4001,"App不存在，clientId["+clientId+"]");
		return appEntity;
	}
	

	protected String loginSuccessRedirect(UserSession session,String redirectUri,String returnUrl){

		String orignDomain = WebUtils.getDomain(redirectUri);
		String cookieDomain = getCookiesDomain(CurrentRuntimeContext.getRequest());
        //		
		if(!StringUtils.contains(orignDomain, cookieDomain)){
			StringBuilder urlBuiler = new StringBuilder(redirectUri);
			urlBuiler.append("?access_token=").append(session.getSessionId());
			urlBuiler.append("&expires_in=").append(session.getExpiresIn());
			String ticket = SecurityDelegating.objectToTicket(new LoginTicketInfo(session.getSessionId(), returnUrl));
			urlBuiler.append("&ticket=").append(ticket);
			redirectUri = urlBuiler.toString();
		}else if(!StringUtils.equals(orignDomain, cookieDomain)){
			String jwt = JwtHelper.createToken(session);
			CurrentRuntimeContext.getResponse().addHeader(JwtHelper.TOKEN_HEADER, jwt);
		}
		
		return redirectTo(redirectUri);
	}

	
	protected String redirectTo(String url) {
		return "redirect:" + url;
	}
	
	protected String getCookiesDomain(HttpServletRequest request) {
		String cookieDomain = SecurityDelegating.getSecurityDecision().cookieDomain();
		if(cookieDomain == null){
			//未指定则为当前根域名
			cookieDomain = WebUtils.getRootDomain(request);
		}
		return cookieDomain;
	}

}
