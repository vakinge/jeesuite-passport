package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.component.jwt.JwtHelper;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.LoginResult;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.security.SecurityConstants;
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

	protected final static Logger logger = LoggerFactory.getLogger("com.jeesuite.passport.controller");
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected AppService appService;
	
	@Value("${security.jwt.enabled:false}")
	protected boolean jwtEnabled;
	
	@Value("${front.ucenter.url}")
	protected String frontUcenterUrl;
	
	@Value("${front.errorpage.url}?returnUrl=%s&error=%s")
	protected String frontErrorPageUrl;

	/**
	 * 验证来源域名合法性
	 * @param domain
	 */
	protected ClientConfigEntity getClientConfig(String clientId){
		ClientConfigEntity appEntity = appService.findByClientId(clientId);
		if(appEntity == null)throw new JeesuiteBaseException(4001,"App不存在，clientId["+clientId+"]");
		return appEntity;
	}
	
	protected ClientConfigEntity getClientConfig(HttpServletRequest request){
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		if(StringUtils.isBlank(clientId))return null;
		ClientConfigEntity appEntity = appService.findByClientId(clientId);
		if(appEntity == null)throw new JeesuiteBaseException(4001,"App不存在，clientId["+clientId+"]");
		return appEntity;
	}
	
	protected LoginResult buildLoginResult(UserSession session,String clientId,String returnUrl) {
		//应用内登录，前端自行跳转
		if(StringUtils.isBlank(clientId)){
			returnUrl = null;
		}else {
			ClientConfigEntity clientConfig = getClientConfig(clientId);
			if(StringUtils.isNotBlank(clientConfig.getCallbackUri())) {
				returnUrl = clientConfig.getCallbackUri();
			}
			StringBuilder urlBuiler = new StringBuilder(returnUrl);
			//获取用户信息的ticket
			String ticket = SecurityDelegating.getSessionManager().setTemporaryObject(SecurityConstants.AUTHN_HANDLE, session.getSessionId(), 60);
			urlBuiler.append("?").append(SecurityConstants.AUTHN_HANDLE).append("=login");
			urlBuiler.append("&").append(SecurityConstants.PARAM_TICKET).append("=").append(ticket);
			if(jwtEnabled) {
				String payload = JwtHelper.createToken(session);
				urlBuiler.append("&payload=").append(payload);
			}
			returnUrl = urlBuiler.toString();
		}
		LoginResult result = new LoginResult();
		result.setUid(session.getUserId());
		result.setToken(session.getSessionId());
		result.setRedirect(returnUrl);
		
		return result;
	}

	protected String loginSuccessRedirect(UserSession session,String clientId,String returnUrl){
		LoginResult result = buildLoginResult(session, clientId, returnUrl);
		return redirectTo(result.getRedirect());
	}

	
	protected String redirectTo(String url) {
		if(!url.startsWith("http")) {
			url = WebUtils.getBaseUrl(CurrentRuntimeContext.getRequest(), true) + url;
		}
		return "redirect:" + url;
	}
	
	protected String appendQueryParam(String url,String name,String value) {
		String spliter = url.contains("?") ? "&" : "?";
		return new StringBuilder(url).append(spliter).append(name).append("=").append(value).toString();
	}
	
	
	protected String getCookiesDomain(HttpServletRequest request) {
		String cookieDomain = SecurityDelegating.getConfigurerProvider().cookieDomain();
		if(cookieDomain == null){
			//未指定则为当前根域名
			cookieDomain = request.getServerName();
		}
		return cookieDomain;
	}

}
