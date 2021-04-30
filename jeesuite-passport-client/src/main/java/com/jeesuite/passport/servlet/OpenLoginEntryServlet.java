/**
 * 
 */
package com.jeesuite.passport.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.jeesuite.passport.PassportClientContext;
import com.jeesuite.springweb.WebConstants;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/snslogin/*", description = "snslogin登录入口")
public class OpenLoginEntryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String snsType = req.getPathInfo().substring(1);
		String orignUrl = req.getParameter(WebConstants.PARAM_RETURN_URL);
		if(StringUtils.isBlank(orignUrl)){
			orignUrl = req.getHeader(HttpHeaders.REFERER);
		}		
		
		if(StringUtils.isBlank(orignUrl)){
			orignUrl = WebUtils.getBaseUrl(req) + PassportClientContext.defaultLoginSuccessRedirctUri();
		}
		
		String redirectUri = PassportClientContext.redirctUri();
		String scope = req.getParameter("scope");
		String url = String.format(PassportClientContext.authServerBasePath() + "/snslogin/%s?app_id=%s&reg_uri=%s&redirect_uri=%s&scope=%s&orign_url=%s", 
				                  snsType,
				                  PassportClientContext.clientId(),
				                  PassportClientContext.snsLoginRegUri(),
				                  redirectUri,
				                  scope,
				                  orignUrl);
		resp.sendRedirect(url);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
