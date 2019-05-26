package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.utils.WebUtils;


/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2016年4月18日
 */
public abstract class BaseLoginController {

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected AppService appService;
	
	private static String rootDomain;//根域名
	
	@Value("${auth.cookies.domain}")
	protected String authCookiesDomain;
	
	/**
	 * 验证来源域名合法性
	 * @param domain
	 */
	protected void validateOrignDomain(String clientId,String domain){
		ClientConfigEntity appEntity = appService.findByClientId(clientId);
		if(appEntity == null)throw new JeesuiteBaseException(4001,"App不存在，clientId["+clientId+"]");
		if(StringUtils.isBlank(appEntity.getAllowDomains()) 
				|| !appEntity.getAllowDomains().contains(domain))throw new JeesuiteBaseException(4001,"未授权域名["+domain + "]");
	}
	

	protected String doLogin(String username,String password,String redirectUri){
		if (StringUtils.isAnyBlank(username,password)) {
			throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
		}
		boolean setCookies = false;
		if(StringUtils.isNotBlank(redirectUri)){			
			String orignDomain = WebUtils.getDomain(redirectUri);
			//同域写cookies
			setCookies = StringUtils.contains(orignDomain, authCookiesDomain);
		}
		
		UserSession session = SecurityDelegating.doAuthentication(username, password, setCookies);
		if(!setCookies && StringUtils.isNotBlank(redirectUri)){
			StringBuilder urlBuiler = new StringBuilder(redirectUri);
			urlBuiler.append("?session_id=").append(session.getSessionId());
			urlBuiler.append("&expires_in=").append(session.getExpiresIn());
			urlBuiler.append("&ticket=").append(TokenGenerator.generateWithSign());
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
