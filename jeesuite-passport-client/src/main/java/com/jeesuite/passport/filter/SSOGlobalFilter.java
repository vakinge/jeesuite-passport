package com.jeesuite.passport.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.jeesuite.passport.PassportClientContext;
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
	
	public static final String AUTHN_SUC_TICKET = "authn_suc_ticket";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		String returnUrl = request.getHeader(HttpHeaders.REFERER);
		if(returnUrl == null) {
			returnUrl = WebUtils.getBaseUrl(request) + PassportClientContext.defaultLoginSuccessRedirctUri();
		}
		if("/sso/login".equals(request.getRequestURI())) {
			String loginUrl = PassportClientContext.getLoginUrl(returnUrl);
			response.sendRedirect(loginUrl);
			return;
		}
		
		String ticket = request.getParameter(AUTHN_SUC_TICKET);
		if(StringUtils.isNotBlank(ticket)) {
			//
			System.out.println("ticket:" + ticket);
		}
		
		String logoutEvent = request.getParameter("logout_event");
		if(StringUtils.isNotBlank(logoutEvent)) {
			PassportClientContext.getSessionStorageProvider().remove(sessionId);
		}
		
		chain.doFilter(req, res);
	}

}
