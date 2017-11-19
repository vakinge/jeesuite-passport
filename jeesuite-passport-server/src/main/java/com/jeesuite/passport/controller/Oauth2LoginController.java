/**
 * 
 */
package com.jeesuite.passport.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.passport.Constants;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.passport.service.AppService;
import com.jeesuite.passport.service.OAuthService;


/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月18日
 */
@Controller  
@RequestMapping(value = "/oauth2")
public class Oauth2LoginController extends BaseLoginController{ 
	
	@Autowired
    private OAuthService oAuthService;
	
	@Autowired
    private AppService appService;

	@RequestMapping(value = "authorize")
	public Object authorize( Model model, HttpServletRequest request ) throws URISyntaxException, OAuthSystemException {

		try {
			//构建OAuth 授权请求
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			//检查提交的客户端id是否正确
			ClientConfigEntity app = appService.findByClientId(oauthRequest.getClientId());
            if (app == null) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_ID)
                                .buildJSONMessage();
                return new ResponseEntity<Object>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
			
			//如果用户没有登录，跳转到登陆页面
			if (org.apache.commons.lang3.StringUtils.equals(request.getMethod(), HttpMethod.GET) 
					|| validateUser(request,model) == null ) {
				//登录失败时跳转到登陆页面
				model.addAttribute(OAuth.OAUTH_CLIENT_ID, oauthRequest.getClientId());
				model.addAttribute(OAuth.OAUTH_RESPONSE_TYPE, oauthRequest.getResponseType());
				model.addAttribute(OAuth.OAUTH_REDIRECT_URI, oauthRequest.getRedirectURI());
				model.addAttribute(OAuth.OAUTH_STATE, oauthRequest.getState());
				return "login";
			}

			//生成授权码
			String authCode = null;
			if ( oauthRequest.getResponseType().equals(ResponseType.CODE.toString()) ) {
				String username = request.getParameter(OAuth.OAUTH_USERNAME); //获取用户名
				authCode = TokenGenerator.generate();
				oAuthService.storeAuthCode(authCode, username);
				
			}

			//进行OAuth响应构建
			OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, 302);
			//设置授权码
			builder.setCode(authCode);

			//构建响应
			final OAuthResponse response = builder.location(oauthRequest.getRedirectURI()).buildQueryMessage();

			//根据OAuthResponse返回ResponseEntity响应
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(new URI(response.getLocationUri()));
			return new ResponseEntity<Object>(headers, HttpStatus.valueOf(response.getResponseStatus()));
		} catch ( OAuthProblemException e ) {

			//出错处理
			String redirectUri = e.getRedirectUri();
			if ( OAuthUtils.isEmpty(redirectUri) ) {
				//告诉客户端没有传入redirectUri直接报错
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.add("Content-Type", "application/json; charset=utf-8");
				return new ResponseEntity<Object>(responseHeaders, HttpStatus.NOT_FOUND);
			}
			//返回错误消息（如?error=）
			final OAuthResponse response = OAuthASResponse.errorResponse(302).error(e).location(redirectUri)
					.buildQueryMessage();
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(new URI(response.getLocationUri()));
			return new ResponseEntity<Object>(headers, HttpStatus.valueOf(response.getResponseStatus()));
		}
	}


	 @RequestMapping("/access_token")
	    public HttpEntity<Object> token(HttpServletRequest request)
	            throws URISyntaxException, OAuthSystemException {

	        try {
	            //构建OAuth请求
	            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

	            ClientConfigEntity app = appService.findByClientId(oauthRequest.getClientId());
	            //检查提交的客户端id是否正确
	            if (app == null) {
	                OAuthResponse response =
	                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
	                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
	                                .setErrorDescription(Constants.INVALID_CLIENT_ID)
	                                .buildJSONMessage();
	                return new ResponseEntity<Object>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
	            }

	            // 检查客户端安全KEY是否正确
	            if (StringUtils.isBlank(oauthRequest.getClientSecret())
	            		|| !oauthRequest.getClientSecret().equals(app.getClientSecret())) {
	                OAuthResponse response =
	                        OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
	                                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
	                                .setErrorDescription(Constants.INVALID_CLIENT_ID)
	                                .buildJSONMessage();
	                return new ResponseEntity<Object>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
	            }

	            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
	            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
	            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
	                if (!oAuthService.checkAuthCode(authCode)) {
	                    OAuthResponse response = OAuthASResponse
	                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
	                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
	                            .setErrorDescription(Constants.INVALID_AUTH_CODE)
	                            .buildJSONMessage();
	                    return new ResponseEntity<Object>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
	                }
	            }

	            //生成Access Token
	            UserInfo account = oAuthService.findAccountByAuthCode(authCode);
	            
	            LoginSession loginSession = createLoginSesion(request,account);

	            //生成OAuth响应
	            OAuthResponse response = OAuthASResponse
	                    .tokenResponse(HttpServletResponse.SC_OK)
	                    .setAccessToken(loginSession.getSessionId())
	                    .setExpiresIn(String.valueOf(oAuthService.createExpireIn()))
	                    .buildJSONMessage();

	            //根据OAuthResponse生成ResponseEntity
	            return new ResponseEntity<Object>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));

	        } catch (OAuthProblemException e) {
	            //构建错误响应
	            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
	                    .buildJSONMessage();
	            return new ResponseEntity<Object>(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
	        }
	    }

	    /**
	     * 验证accessToken
	     *
	     * @param accessToken
	     * @return
	     */
	    @RequestMapping(value = "/token_check", method = RequestMethod.POST)
	    public ResponseEntity<Object> checkAccessToken(@RequestParam("actoken") String accessToken) {
	        boolean b = oAuthService.checkAccessToken(accessToken);
	        return b ? new ResponseEntity<Object>(HttpStatus.valueOf(HttpServletResponse.SC_OK)) : new ResponseEntity<Object>(HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
	    }
} 
