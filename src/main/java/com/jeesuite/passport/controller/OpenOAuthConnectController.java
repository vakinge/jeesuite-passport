package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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
import com.jeesuite.passport.component.snslogin.OauthConnector;
import com.jeesuite.passport.component.snslogin.OauthUser;
import com.jeesuite.passport.component.snslogin.SnsLoginState;
import com.jeesuite.passport.component.snslogin.connector.OSChinaConnector;
import com.jeesuite.passport.component.snslogin.connector.QQConnector;
import com.jeesuite.passport.component.snslogin.connector.WeiboConnector;
import com.jeesuite.passport.component.snslogin.connector.WeixinMpConnector;
import com.jeesuite.passport.component.snslogin.connector.WinxinConnector;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.utils.IpUtils;
import com.jeesuite.springweb.utils.WebUtils;


/**
 * 第三方开放平台登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年5月23日
 */
@Controller
@RequestMapping("/open")
public class OpenOAuthConnectController extends BaseLoginController implements InitializingBean{
	
	@Value("${sns.login.next.bind:false}")
	private boolean snsLoginBind;
	private Map<String, OauthConnector> oauthConnectors = new HashMap<>();
	private WeixinMpConnector weixinGzhConnector = new WeixinMpConnector();
	
	@RequestMapping(value = "login/{type}", method = RequestMethod.GET)
	public String loginRedirect(HttpServletRequest request,@PathVariable("type") String type
			,@RequestParam(value="client_id",required=false,defaultValue = Constants.DEFAULT_CLIENT_ID) String clientId
			,@RequestParam(value="return_url",required=false) String returnUrl){
		
		boolean isWxGzh = WeixinMpConnector.SNS_TYPE.equals(type);

		OauthConnector connector = null;
		if(!isWxGzh){
			connector = oauthConnectors.get(type);
			if(connector == null)throw new JeesuiteBaseException(1001,"不支持授权类型:"+type);
		}
		
		if(Constants.DEFAULT_CLIENT_ID.equals(clientId)){
			returnUrl = WebUtils.getBaseUrl(request) + "/ucenter/index";
		}else{
			getClientConfig(clientId);
		}
		//
		SnsLoginState snsState = new SnsLoginState(clientId, type, returnUrl,null);
		new RedisObject(snsState.getState()).set(snsState, CacheExpires.IN_3MINS);
		
		String callBackUri = request.getRequestURL().toString().split("/" + type)[0] + "/callback";
		String redirectUrl;
		if(isWxGzh){
			String scope = request.getParameter("scope");
			scope = WeixinMpConnector.SNSAPI_USERINFO.equalsIgnoreCase(scope) ? WeixinMpConnector.SNSAPI_USERINFO : WeixinMpConnector.SNSAPI_BASE;
			redirectUrl = weixinGzhConnector.getAuthorizeUrl(clientId, scope, callBackUri, snsState.getState());
		}else{
			redirectUrl = connector.getAuthorizeUrl(snsState.getState(), callBackUri);			
		}

		return redirectTo(redirectUrl); 
	}
	

	@RequestMapping(value = "bind/{type}", method = RequestMethod.GET)
	public String bindRedirect(HttpServletRequest request,@PathVariable("type") String type){
		UserSession session = SecurityDelegating.getAndValidateCurrentSession();
		SnsLoginState snsState = new SnsLoginState(null, type, null, Integer.parseInt(session.getUserId().toString()));
		new RedisObject(snsState.getState()).set(snsState, CacheExpires.IN_3MINS);
		
		OauthConnector connector = oauthConnectors.get(type);
		if(connector == null)throw new JeesuiteBaseException(1001,"不支持授权类型:"+type);
		String callBackUri = request.getRequestURL().toString().split("/" + type)[0] + "/callback";
		String authorizeUrl = connector.getAuthorizeUrl(snsState.getState(), callBackUri);	
		
		return redirectTo(authorizeUrl); 
	}
	
	@RequestMapping(value = "callback", method = {RequestMethod.GET,RequestMethod.POST})
	public String callback(HttpServletRequest request,HttpServletResponse response,Model model) {
		String code = request.getParameter(SecurityConstants.PARAM_CODE);
		String state = request.getParameter("state");
		
		SnsLoginState loginState = null;
		if(StringUtils.isBlank(state) || (loginState = new RedisObject(state).get()) == null){
			model.addAttribute(Constants.ERROR, "访问失效，state expire");
			return Constants.ERROR; 
		}

		OauthUser oauthUser;
		if(WeixinMpConnector.SNS_TYPE.equals(loginState.getSnsType())){
			oauthUser = weixinGzhConnector.getUser(loginState.getAppId(), code);
		}else{
			oauthUser = oauthConnectors.get(loginState.getSnsType()).getUser(code);
		}
		if(StringUtils.isBlank(oauthUser.getOpenId())){
			model.addAttribute(Constants.ERROR, "callback error");
			return Constants.ERROR; 
		}
		
		getClientConfig(loginState.getAppId());
		oauthUser.setOpenType(loginState.getSnsType());
		oauthUser.setFromClientId(loginState.getAppId());
		//绑定
		if(!loginState.loginAction()){
			userService.addSnsAccountBind(loginState.getLognUserId(), oauthUser);
			return redirectTo(WebUtils.getBaseUrl(request) + "/ucenter/snsbinding");
		}
		
		//根据openid 找用户
		UserInfo userInfo = userService.findAcctountBySnsOpenId(loginState.getSnsType(), oauthUser.getOpenId());
		if(userInfo == null){
			//跳转去绑定页面
			if(snsLoginBind){
				model.addAttribute("oauthUser", oauthUser);
				model.addAttribute(SecurityConstants.PARAM_RETURN_URL, loginState.getReturnUrl());
				return "bind";
			}else{
				//创建用户
				AccountBindParam bindParam = new AccountBindParam();
				bindParam.setAppId(loginState.getAppId());
				bindParam.setIpAddr(IpUtils.getIpAddr(request));
				userInfo = userService.createUserByOauthInfo(oauthUser,bindParam);
			}
		}
		
		UserSession session = SecurityDelegating.updateSession(userInfo);
		if(Constants.DEFAULT_CLIENT_ID.equals(loginState.getAppId())){
			return redirectTo(loginState.getReturnUrl());
		}else{
			ClientConfigEntity clientConfig = getClientConfig(loginState.getAppId());
			return loginSuccessRedirect(session, clientConfig.getCallbackUri(),loginState.getReturnUrl());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Properties properties = ResourceUtils.getAllProperties("threepart.oauth.");
		String type;String appKey;String appSecret;
		for (Object key : properties.keySet()) {
			type = StringUtils.splitByWholeSeparator(key.toString(), ".")[2];
			
			if(oauthConnectors.containsKey(type))continue;
			appKey = properties.getProperty("threepart.oauth."+type+".appId");
			appSecret = properties.getProperty("threepart.oauth."+type+".appSecret");
			
			if(QQConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new QQConnector(appKey, appSecret));
			}else if(WinxinConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WinxinConnector(appKey, appSecret));
			}else if(WeiboConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WeiboConnector(appKey, appSecret));
			}else if(OSChinaConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new OSChinaConnector(appKey, appSecret));
			}else if(type.startsWith(WeixinMpConnector.SNS_TYPE)){
				String appName = type.split("\\[|\\]")[1];
				if(!weixinGzhConnector.contains(appName)){					
					weixinGzhConnector.addConfig(appName, appKey, appSecret);
				}
			}
			
		}
	}
}
