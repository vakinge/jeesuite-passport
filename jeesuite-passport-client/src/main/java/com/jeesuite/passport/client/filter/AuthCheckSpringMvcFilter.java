package com.jeesuite.passport.client.filter;

import java.io.IOException;
import java.io.PrintWriter;

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
			if(isAjax(request)){
				responseOutWithJson(response, JsonUtils.toJson(new WrapperResponseEntity(e.getCode(), e.getMessage())));
			}else{
				String redirectUri = request.getRequestURL().toString();
				
				String loginUrl = ClientConfig.authServerBasePath() + "/auth/login?redirect_uri=" + redirectUri;
				response.sendRedirect(loginUrl);
			}
		}
	}
	
	
	private boolean isAjax(HttpServletRequest request){
	    return  (request.getHeader("X-Requested-With") != null  
	    && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString())) ;
	}
	
	private void responseOutWithJson(HttpServletResponse response,String json) {  
	    //将实体对象转换为JSON Object转换  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(json);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	}  

}
