/**
 * 
 */
package com.jeesuite.passport.client.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;

import com.jeesuite.passport.client.AuthChecker;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月21日
 */
@Priority(1)
public class AuthCheckJerseyFilter implements ContainerRequestFilter,ContainerResponseFilter {

	private  AuthChecker checker;
    
    @Context
    private HttpServletRequest request;
    
    
	public AuthCheckJerseyFilter() {
		checker = new AuthChecker();
	}


	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		checker.process(request);
	}

	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
	}

}
