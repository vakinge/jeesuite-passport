package com.jeesuite.passport.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		
		chain.doFilter(req, res);
	}

}
