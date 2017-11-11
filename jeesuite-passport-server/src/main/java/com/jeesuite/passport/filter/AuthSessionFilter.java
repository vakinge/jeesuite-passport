package com.jeesuite.passport.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;

public class AuthSessionFilter extends OncePerRequestFilter{
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String sessionId = AuthSessionHelper.getSessionId(request);
		LoginSession session = AuthSessionHelper.validateSessionIfNotCreateAnonymous(sessionId);
		
		if(session != null){
			LoginContext.setLoginSession(session);
		}
		
		filterChain.doFilter(request, response);
	}


}
