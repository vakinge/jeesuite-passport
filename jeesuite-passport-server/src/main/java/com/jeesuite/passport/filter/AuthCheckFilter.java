package com.jeesuite.passport.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

public class AuthCheckFilter extends OncePerRequestFilter{
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String sessionId = AuthSessionHelper.getSessionId(request);
		LoginSession session = AuthSessionHelper.getLoginSession(sessionId);
		
		if(session != null && !session.isAnonymous()){
			LoginContext.setLoginSession(session);
		}else{
			if(request.getRequestURI().startsWith("/ucenter")){
				if(WebUtils.isAjax(request)){
					WebUtils.responseOutJson(response, JsonUtils.toJson(new WrapperResponseEntity(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name())));
				}else{
					String loginUrl = WebUtils.getBaseUrl(request) + "/login";
					response.sendRedirect(loginUrl);
				}
				return;
			}
		}
		
		filterChain.doFilter(request, response);
	}


}
