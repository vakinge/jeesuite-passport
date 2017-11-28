package com.jeesuite.passport.client.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.client.AuthChecker;
import com.jeesuite.passport.client.ClientConfig;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

public class AuthCheckSpringMvcFilter extends OncePerRequestFilter{
	
	private  AuthChecker checker;
	
	public AuthCheckSpringMvcFilter() {
		checker = new AuthChecker();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {			
			checker.process(request,response);
			
			filterChain.doFilter(request, response);
		} catch (JeesuiteBaseException e) {
			if(WebUtils.isAjax(request)){
				WebUtils.responseOutJson(response, JsonUtils.toJson(new WrapperResponseEntity(e.getCode(), e.getMessage())));
			}else{
				String redirectUri = request.getRequestURL().toString();
				
				String loginUrl = ClientConfig.authServerBasePath() + "/login?redirect_uri=" + redirectUri;
				response.sendRedirect(loginUrl);
			}
		}
	}
	
}
