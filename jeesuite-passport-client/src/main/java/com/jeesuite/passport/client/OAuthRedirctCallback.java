/**
 * 
 */
package com.jeesuite.passport.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.helper.AuthSessionHelper;

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
@WebServlet(urlPatterns="/oauth2/redirct_callback", description="oauth2.0 回调处理接口") 
public class OAuthRedirctCallback extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                                         .readTimeout(10, TimeUnit.SECONDS).build();
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
    	String code = req.getParameter("code");
    	String state = req.getParameter("state");
        
    	String clientOrignUrl = AuthSessionHelper.getOauthState(state);
    	
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
    	
    	//{"access_token":"a014d4771c64e67c06b3ce7fbaf7914d","expires_in":3600}
    	String jsonString = response.body().string();
    	if(jsonString.contains(ClientConstants.ACCESS_TOKEN)){
    		Map map = JsonUtils.toObject(jsonString, Map.class);
    		String accessToken = map.get(ClientConstants.ACCESS_TOKEN).toString();
    		int expire = Integer.parseInt(map.get("expires_in").toString());
    		Cookie loginCookie = AuthSessionHelper.createSessionCookies(accessToken, ClientConfig.safeDomain(), expire);
    		resp.addCookie(loginCookie);
    		//
    		resp.sendRedirect(clientOrignUrl);
    	}else{
    		resp.setContentType("text/html;charset=UTF-8");  
            PrintWriter out = null;
            try {
                out = resp.getWriter();
                out.write("登录失败");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
    	}
    }

	@Override
	public void destroy() {
		super.destroy();
	}

    
}
