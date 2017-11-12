package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.Constants;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.snslogin.OauthConnector;
import com.jeesuite.passport.snslogin.OauthUser;
import com.jeesuite.passport.snslogin.SnsLoginState;
import com.jeesuite.passport.snslogin.connector.OSChinaConnector;
import com.jeesuite.passport.snslogin.connector.QQConnector;
import com.jeesuite.passport.snslogin.connector.WeiboConnector;
import com.jeesuite.passport.snslogin.connector.WeixinGzhConnector;
import com.jeesuite.passport.snslogin.connector.WinxinConnector;
import com.jeesuite.springweb.utils.IpUtils;
import com.jeesuite.springweb.utils.WebUtils;



@Controller
@RequestMapping("/snslogin")
public class ThreePartAuthController extends BaseAuthController implements EnvironmentAware{
	
	@Value("${sns.login.bind}")
	private boolean snsLoginBind;
	private Map<String, OauthConnector> oauthConnectors = new HashMap<>();
	private WeixinGzhConnector weixinGzhConnector = new WeixinGzhConnector();
	private Map<String, String> callbackUris = new HashMap<>();
	
	@RequestMapping(value = "{type}", method = RequestMethod.GET)
	public String redirect(HttpServletRequest request,@PathVariable("type") String type
			,@RequestParam(value="client_id",required=false) String clientId
			,@RequestParam(value="reg_uri",required=false) String regPageUri
			,@RequestParam("redirect_uri") String redirectUri
			,@RequestParam(value="origin_url",required=false) String orignUrl){
		
		boolean isWxGzh = WeixinGzhConnector.SNS_TYPE.equals(type);

		OauthConnector connector = null;
		if(!isWxGzh){
			connector = oauthConnectors.get(type);
			if(connector == null)throw new JeesuiteBaseException(1001,"不支持授权类型:"+type);
		}
		
		String orignDomain = WebUtils.getDomain(redirectUri);
		//
		validateOrignDomain(clientId,orignDomain);
		
		SnsLoginState loginState = new SnsLoginState(clientId,orignDomain, type, regPageUri, redirectUri,orignUrl);
		new RedisObject(loginState.getState()).set(loginState, CacheExpires.IN_1MIN);
		
		String callBackUri = getCallbackUri(request,type);
		
		String url;
		if(isWxGzh){
			String scope = request.getParameter("scope");
			scope = WeixinGzhConnector.SNSAPI_USERINFO.equalsIgnoreCase(scope) ? WeixinGzhConnector.SNSAPI_USERINFO : WeixinGzhConnector.SNSAPI_BASE;
			url = weixinGzhConnector.getAuthorizeUrl(orignDomain, scope, callBackUri, loginState.getState());
		}else{
			url = connector.getAuthorizeUrl(loginState.getState(), callBackUri);			
		}

		return redirectTo(url); 
	}
	

	@RequestMapping(value = "callback", method = {RequestMethod.GET,RequestMethod.POST})
	public String callback(HttpServletRequest request,HttpServletResponse response,Model model) {
		String code = request.getParameter(PassportConstants.PARAM_CODE);
		String state = request.getParameter("state");
		
		SnsLoginState loginState = null;
		if(StringUtils.isBlank(state) || (loginState = new RedisObject(state).get()) == null){
			model.addAttribute(Constants.ERROR, "访问失效，state expire");
			return Constants.ERROR; 
		}

		OauthUser oauthUser;
		if(WeixinGzhConnector.SNS_TYPE.equals(loginState.getSnsType())){
			oauthUser = weixinGzhConnector.getUser(loginState.getDomain(), code);
		}else{
			oauthUser = oauthConnectors.get(loginState.getSnsType()).getUser(code);
		}
		if(StringUtils.isBlank(oauthUser.getOpenId())){
			model.addAttribute(Constants.ERROR, "callback error");
			return Constants.ERROR; 
		}
		oauthUser.setSnsType(loginState.getSnsType());
		oauthUser.setFromClientId(loginState.getAppId());
		//根据openid 找用户
		Account account = accountService.findAcctountBySnsOpenId(loginState.getSnsType(), oauthUser.getOpenId());
		
		if(account != null){
			return createSessionAndSetResponse(request, response, account, loginState.getSuccessDirectUri(),loginState.getOrignUrl());
		}
		
		//跳转去绑定页面
		if(StringUtils.isNotBlank(loginState.getRegPageUri())){//业务系统自定义绑定页面
			String ticket = TokenGenerator.generate();
			new RedisObject(ticket).set(oauthUser, CacheExpires.IN_HALF_HOUR);
			return "redirect:" + loginState.getRegPageUri() + "?auth_ticket=" + ticket + "&" + oauthUser.userInfoToUrlQueryString();
		}else{
			if(snsLoginBind){
				model.addAttribute("oauthUser", oauthUser);
				model.addAttribute("redirect_uri", loginState.getSuccessDirectUri());
				return "/user/bind";
			}else{
				//创建用户并登陆
				AccountBindParam bindParam = new AccountBindParam();
				bindParam.setAppId(loginState.getAppId());
				bindParam.setIpAddr(IpUtils.getIpAddr(request));
				account = accountService.createAccountByOauthInfo(oauthUser,bindParam);
				//
				return createSessionAndSetResponse(request, response, account, loginState.getSuccessDirectUri(),loginState.getOrignUrl());
			}
			
		}
		 
	}

	@Override
	public void setEnvironment(Environment environment) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment, "threepart.oauth.");
		Map<String, Object> subProperties = resolver.getSubProperties("");
		String type;String appKey;String appSecret;
		for (String key : subProperties.keySet()) {
			type = key.substring(0, key.lastIndexOf("."));
			
			if(oauthConnectors.containsKey(type))continue;
			appKey = environment.getProperty("threepart.oauth."+type+".appId");
			appSecret = environment.getProperty("threepart.oauth."+type+".appSecret");
			
			if(QQConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new QQConnector(appKey, appSecret));
			}else if(WinxinConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WinxinConnector(appKey, appSecret));
			}else if(WeiboConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WeiboConnector(appKey, appSecret));
			}else if(OSChinaConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new OSChinaConnector(appKey, appSecret));
			}else if(type.startsWith(WeixinGzhConnector.SNS_TYPE)){
				String domain = type.split("\\[|\\]")[1];
				weixinGzhConnector.addConfig(domain, appKey, appSecret);
			}
			
		}
		
	}
	
	private String getCallbackUri(HttpServletRequest request, String type) {
		String callbackUri = callbackUris.get(type);
		if(callbackUri != null)return callbackUri;
		synchronized (callbackUris) {			
			String routeBaseUri = ResourceUtils.getProperty("route.base.url");
			if(routeBaseUri != null){
				routeBaseUri = routeBaseUri.endsWith("/") ? routeBaseUri : routeBaseUri.concat("/");
				callbackUri = routeBaseUri + "snslogin/callback";
			}else{
				String authServerBasePath = ResourceUtils.getProperty("auth.server.baseurl");
				if(StringUtils.isNotBlank(authServerBasePath)){					
					if(authServerBasePath.endsWith("/"))authServerBasePath = authServerBasePath.substring(0, authServerBasePath.length() - 1);
					callbackUri = authServerBasePath + "/snslogin/callback";
				}else{					
					callbackUri = request.getRequestURL().toString().split("/" + type)[0] + "/callback";
				}
				
			}
			callbackUris.put(type, callbackUri);
		}
		
		return callbackUri;
	}

}
