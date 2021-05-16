package com.jeesuite.passport.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.jeesuite.common.http.HttpMethod;
import com.jeesuite.common.util.PathMatcher;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.log.Log;
import com.jeesuite.log.LogFactory;
import com.jeesuite.passport.ClientConstants;
import com.jeesuite.passport.PassportConfigHolder;
import com.jeesuite.passport.SessionUtils;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * 
 * 统一认证全局filter
 * <br>
 * Class Name   : PassportGlobalFilter
 *
 * @author jiangwei
 * @version 1.0.0
 * @date Apr 11, 2021
 */
public class SSOGlobalFilter implements Filter {
	
	private static Log log = LogFactory.getLog("com.jeesuite.passport");

	private static String apiUriSuffix = ResourceUtils.getProperty("api.uri.suffix");
	private static final String DOT = ".";
	private static final String MSG_401_UNAUTHORIZED = "{\"code\": 401,\"msg\":\"401 Unauthorized\"}";
	
	private static PathMatcher anonymousUrlMatcher;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		//忽略静态资源
		if(request.getRequestURI().contains(DOT) && (apiUriSuffix == null || !request.getRequestURI().endsWith(apiUriSuffix))) {
			chain.doFilter(req, res);
			return;
		}
		
		if(request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			chain.doFilter(req, res);
			return;
		}
		
		if(anonymousUrlMatcher == null) {
			List<String> uris = ResourceUtils.getList("jeesuite.passport.anonymousUris");
			anonymousUrlMatcher = new PathMatcher(request.getContextPath(), uris);
		}
		
		String returnUrl = request.getHeader(HttpHeaders.REFERER);
		if(returnUrl == null) {
			returnUrl = WebUtils.getBaseUrl(request) + PassportConfigHolder.defaultLoginSuccessRedirctUri();
		}
		if(request.getRequestURI().endsWith(ClientConstants.SSO_LOGIN_URI)) {
			String loginUrl = PassportConfigHolder.getLoginUrl(returnUrl);
			response.sendRedirect(loginUrl);
			return;
		}else if(request.getRequestURI().endsWith(ClientConstants.SSO_LOGOUT_URI)) {
			SessionUtils.destroySession(request, response);
			String loginUrl = PassportConfigHolder.getLogoutUrl(returnUrl);
			response.sendRedirect(loginUrl);
			return;
		}
		
		//
		SessionUtils.init(request);
		
		String handleName = request.getParameter(ClientConstants.AUTHN_HANDLE);
		if(StringUtils.isNotBlank(handleName)) {
			if(log.isDebugEnabled()) {
				
			}
			if(handleName.equals(ClientConstants.LOGIN_HANDLE)) {
				SessionUtils.createSession(request, response);
				//避免循环，移除监听的参数
				returnUrl = removeAuthSpecParams(request.getRequestURL().toString());
				response.sendRedirect(returnUrl);
			}else if(handleName.equals(ClientConstants.LOGOUT_HANDLE)) {
				SessionUtils.destroySession(request, response);
			}
			return;
		}
		
		//
		if(SessionUtils.getCurrentUser() == null && !anonymousUrlMatcher.match(request.getRequestURI())) {
			if(WebUtils.isAjax(request)){				
				WebUtils.responseOutJson(response, MSG_401_UNAUTHORIZED);
			}else{
				String loginUrl = PassportConfigHolder.getLoginUrl(returnUrl);
				response.sendRedirect(loginUrl);
			}
			return;
		}
		
		
		chain.doFilter(req, res);
	}


	private static String removeAuthSpecParams(String url) {
		if(!url.contains(ClientConstants.AUTHN_HANDLE))return url;
		String[] parts = StringUtils.split(url, "?&");
	    StringBuilder builder = new StringBuilder(parts[0]);
	    boolean first = true;
	    for (int i = 1; i < parts.length; i++) {
			if(parts[i].startsWith(ClientConstants.AUTHN_HANDLE) || parts[i].startsWith(ClientConstants.PARAM_TICKET)) {
				continue;
			}
			if(first) {
				builder.append("?");
				first = false;
			}else {
				builder.append("&");
			}
			builder.append(parts[i]);
		}
	    
	    String string = builder.toString();
		return string;
	}
}
