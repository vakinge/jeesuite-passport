package com.jeesuite.passport.client;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.annotation.RequiresPermissions;
import com.jeesuite.passport.exception.ForbiddenAccessException;
import com.jeesuite.passport.model.LoginSession;

public class SecurityCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if(handler instanceof HandlerMethod){
			HandlerMethod method = (HandlerMethod)handler;
			RequiresPermissions  config = method.getMethod().getAnnotation(RequiresPermissions.class);
			if(config != null){
				LoginSession loginSession = LoginContext.getRequireLoginSession();
				if(config.userType() >= 0){
					if(loginSession.getUserType() != config.userType()){
						throw new ForbiddenAccessException();
					}
				}
				
				List<String> permissions = loginSession.getPermissions();
				if(permissions == null || config.value() == null || config.value().length == 0){
					throw new ForbiddenAccessException();
				}
				
				for (String p : config.value()) {
					if(permissions.contains(p))return true;
				}
				
				throw new ForbiddenAccessException();
			}
		}
	
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {}
	
	boolean isAjax(HttpServletRequest request){
	    return  (request.getHeader("X-Requested-With") != null  
	    && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString())) ;
	}

}
