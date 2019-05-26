/**
 * 
 */
package com.jeesuite.passport.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.model.AccessToken;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/login_callback", description = "登录成功回调登录信息")
public class LoginSuccessCallback extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		boolean setCookies = false;
		int expiresIn = LoginSession.SESSION_EXPIRE_SECONDS;
		String sessionId = req.getParameter(SecurityConstants.PARAM_SESSION_ID);
		String code = req.getParameter(SecurityConstants.PARAM_CODE);
		String redirectUri = req.getParameter(SecurityConstants.PARAM_ORIGIN_URL);
		String loginType = req.getParameter(SecurityConstants.PARAM_LOGIN_TYPE);
		String ticket = req.getParameter(SecurityConstants.PARAM_TICKET);
		
		if(StringUtils.isNotBlank(sessionId)){
			//验证session合法性
			try {
				TokenGenerator.validate(ticket, true);
			} catch (JeesuiteBaseException e) {
				WebUtils.responseOutHtml(resp, e.getMessage());
				return;
			}
			setCookies = true;
			expiresIn = Integer.parseInt(req.getParameter(SecurityConstants.PARAM_EXPIRE_IN));
			if("jsonp".equals(loginType)){
				String domain = WebUtils.getDomain(req.getRequestURL().toString());
				Cookie loginCookie = AuthSessionHelper.createSessionCookies(sessionId, domain, expiresIn);
				resp.addCookie(loginCookie);
				
				WrapperResponseEntity responseEntity = new WrapperResponseEntity();
				WebUtils.responseOutJsonp(resp, SecurityConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, responseEntity);
				return;
			}else{				
				if(StringUtils.isBlank(redirectUri)){
					WebUtils.responseOutHtml(resp, "非法请求");
					return;
				}
			}
		}else if(StringUtils.isNotBlank(code)){
			setCookies = true;
			String state = req.getParameter("state");
			if(StringUtils.isAnyBlank(code,state)){
				WebUtils.responseOutHtml(resp, "非法请求");
				return;
			}
			redirectUri = AuthSessionHelper.getOauth2RedirctUrl(state);
			
	    	FormBody.Builder builder = new FormBody.Builder();
	    	builder.add("client_id", ClientConfig.clientId());
	    	builder.add("client_secret", ClientConfig.clientSecret());
	    	builder.add("grant_type", "authorization_code");
	    	builder.add("code", code);
	    	
	    	String redirctUri = req.getRequestURL().toString().split("\\?")[0];
	    	builder.add("redirect_uri", redirctUri);

	        RequestBody body = builder.build();
	    	Request request = new Request.Builder().url(ClientConfig.authServerBasePath() + "/oauth2/access_token").post(body).build();
	    	Response response = client.newCall(request).execute();
	    	
	    	String jsonString = response.body().string();
	    	if(jsonString.contains(ClientConstants.ACCESS_TOKEN)){
	    		AccessToken accessToken = JsonUtils.toObject(jsonString, AccessToken.class);
	    		sessionId = accessToken.getAccessToken();
				expiresIn = accessToken.getExpiresIn();
	    	}else{
	    		WebUtils.responseOutHtml(resp, "登录失败");
	    		return;
	    	}
	    	
		}
		
		if(StringUtils.isBlank(redirectUri)){
			WebUtils.responseOutHtml(resp, "非法请求");
			return;
		}

		// set cookies
		if(setCookies){
			String domain = WebUtils.getRootDomain(req);		
			Cookie loginCookie = AuthSessionHelper.createSessionCookies(sessionId, domain, expiresIn);
			resp.addCookie(loginCookie);
		}
		
		resp.sendRedirect(redirectUri);
	}
	

	@Override
	public void destroy() {
		super.destroy();
	}

}
