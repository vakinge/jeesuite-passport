package com.jeesuite.passport;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jeesuite.passport.exception.UnauthorizedException;
import com.jeesuite.passport.model.LoginSession;

public class LoginContext {
	
	private final static ThreadLocal<LoginSession> holder = new ThreadLocal<>();
	

	public static LoginSession getLoginSession(){
		//由于线程复用导致部分没有响应的请求没有清掉线程变量，故去掉
		LoginSession loginSession = null;//holder.get();
		if(loginSession == null){
			String headerString = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(PassportConstants.HEADER_AUTH_USER);
			loginSession = LoginSession.decode(headerString);
//			if(loginSession != null){
//				holder.set(loginSession);
//			}
		}
		return loginSession;
	}
	
	public static void resetLoginSessionHolder(){
		holder.remove();
	}
	
	/**
	 * 获取登录信息，未登录抛出异常
	 * @return
	 */
	public static LoginSession getRequireLoginSession(){
		LoginSession loginInfo = getLoginSession();
		if(loginInfo == null)throw new UnauthorizedException();
		return loginInfo;
	}
	
	public static Map<String, String> getCustomHeaders(){
		Map<String, String> headers = new HashMap<>();
		 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		 while(headerNames.hasMoreElements()){
			 String headerName = headerNames.nextElement();
			 if(headerName.equalsIgnoreCase(PassportConstants.HEADER_AUTH_USER)){				 
				 String headerValue = request.getHeader(headerName);
				 if(headerValue != null)headers.put(headerName, headerValue);
			 }
		 }
		 return headers;
	}
}
