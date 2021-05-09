package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.AppConstants.OpenSubType;
import com.jeesuite.passport.component.openauth.OauthConnector;
import com.jeesuite.passport.component.openauth.OauthUser;
import com.jeesuite.passport.component.openauth.SnsLoginState;
import com.jeesuite.passport.component.openauth.connector.QQConnector;
import com.jeesuite.passport.component.openauth.connector.WeiboConnector;
import com.jeesuite.passport.component.openauth.connector.WeixinMpConnector;
import com.jeesuite.passport.component.openauth.connector.WinxinConnector;
import com.jeesuite.passport.dao.entity.OpenOauth2ConfigEntity;
import com.jeesuite.passport.dao.entity.UserPrincipalEntity;
import com.jeesuite.passport.dao.mapper.OpenOauth2ConfigEntityMapper;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.utils.WebUtils;


/**
 * 第三方开放平台登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年5月23日
 */
@Controller
@RequestMapping("/auth/open")
public class OpenOAuthConnectController extends BaseLoginController implements CommandLineRunner{
	

	@Autowired
	private OpenOauth2ConfigEntityMapper openOauth2ConfigMapper;
	private Map<String, OauthConnector> oauthConnectors = new HashMap<>();
	private WeixinMpConnector weixinGzhConnector = new WeixinMpConnector();
	
	@RequestMapping(value = "login/{type}", method = RequestMethod.GET)
	public String loginRedirect(HttpServletRequest request,@PathVariable("type") String type
			,@RequestParam(value="client_id",required=false) String clientId
			,@RequestParam(value="return_url",required=false) String returnUrl){
		
		boolean isWxGzh = WeixinMpConnector.SNS_TYPE.equals(type);

		OauthConnector connector = null;
		if(!isWxGzh){
			connector = oauthConnectors.get(type);
			if(connector == null)throw new JeesuiteBaseException(1001,"未找到登录类型["+type+"]配置");
		}
		
		if(StringUtils.isBlank(clientId)){
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
		SnsLoginState snsState = new SnsLoginState(null, type, null, session.getUserId());
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
			model.addAttribute(AppConstants.ERROR, "访问失效，state expire");
			return AppConstants.ERROR; 
		}

		OauthUser oauthUser;
		if(WeixinMpConnector.SNS_TYPE.equals(loginState.getOpenType())){
			oauthUser = weixinGzhConnector.getUser(loginState.getAppId(), code);
		}else{
			oauthUser = oauthConnectors.get(loginState.getOpenType()).getUser(code);
		}
		if(StringUtils.isBlank(oauthUser.getOpenId())){
			model.addAttribute(AppConstants.ERROR, "callback error");
			return AppConstants.ERROR; 
		}
		
		oauthUser.setOpenType(loginState.getOpenType());
		oauthUser.setFromClientId(loginState.getAppId());

		//根据openid 找用户
		UserPrincipalEntity userPrincipal = userService.findUserByOpenId(oauthUser.getOpenType(), oauthUser.getOpenId());
		if(userPrincipal == null){
			userPrincipal = userService.createUserIfAbent(oauthUser);
		}
		
		UserSession session = SecurityDelegating.updateSession(userPrincipal.toAuthUser());
		if(loginState.getAppId() == null){
			return redirectTo(loginState.getReturnUrl());
		}else{
			return loginSuccessRedirect(session,loginState.getAppId(),loginState.getReturnUrl());
		}
	}


	@Override
	public void run(String... args) throws Exception {
		List<OpenOauth2ConfigEntity> list = openOauth2ConfigMapper.selectAll();
		for (OpenOauth2ConfigEntity entity : list) {
			if(!entity.getEnabled())continue;
			if(QQConnector.TYPE.equals(entity.getOpenType())){
				oauthConnectors.put(QQConnector.TYPE, new QQConnector(entity.getAppId(), entity.getAppSecret()));
			}else if(WeiboConnector.TYPE.equals(entity.getOpenType())){
				oauthConnectors.put(WeiboConnector.TYPE, new WeiboConnector(entity.getAppId(), entity.getAppSecret()));
			}else if(WinxinConnector.TYPE.equals(entity.getOpenType())){
				if(OpenSubType.gzh.name().equals(entity.getSubType())){
					weixinGzhConnector.addConfig(entity.getBindClientIds(), entity.getAppId(), entity.getAppSecret());
				}else if(OpenSubType.xcx.name().equals(entity.getSubType())){
					
				}else{
					oauthConnectors.put(WinxinConnector.TYPE, new WinxinConnector(entity.getAppId(), entity.getAppSecret()));
				}
			}
		}
	}

	
}
