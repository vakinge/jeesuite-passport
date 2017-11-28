/**
 * 
 */
package com.jeesuite.passport.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.exception.UnauthorizedException;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月20日
 */
public class AuthChecker {
	
	private static Logger log = LoggerFactory.getLogger(AuthChecker.class);
	
	private List<String> protectedUris = new ArrayList<>();
	private List<String> protectedUriPrefixs = new ArrayList<>();
	private List<Pattern> protectedUriPatterns = new ArrayList<>();

	public AuthChecker(String ignoreCheckUris) {
		if(StringUtils.isNotBlank(ignoreCheckUris)){
			String[] segs = ignoreCheckUris.split(";|,");
			String contextPath = ResourceUtils.getProperty("context-path", "");
			for (String seg : segs) {
				seg = contextPath + seg;
				if(seg.contains("*")){
					if(seg.endsWith("*")){
						protectedUriPrefixs.add(seg.replaceAll("\\*+", ""));
					}else{
						protectedUriPatterns.add(Pattern.compile(seg));
					}
				}else{
					protectedUris.add(seg);
				}
			}
			log.info("protectedUris:{} \nprotectedUriPrefixs:{} \n protectedUriPatterns:{}",protectedUris,protectedUriPrefixs,protectedUriPatterns);
		}
	}
	
	public AuthChecker() {
		this(StringUtils.trimToEmpty(ClientConfig.get(ClientConstants.AUTH_PROTECTED_URIS)));
	}
	
	

	public LoginSession process(HttpServletRequest request,HttpServletResponse response) {
		
		// 是否需要鉴权
		boolean requered = protectedUris.contains(request.getRequestURI());
		if(!requered){
			for (String prefix : protectedUriPrefixs) {
				if(requered = request.getRequestURI().startsWith(prefix)){
					break;
				}
			}
		}
		
		if(!requered){
			for (Pattern pattern : protectedUriPatterns) {
				if(requered = pattern.matcher(request.getRequestURI()).matches()){
					break;
				}
			}
		}

		String sessionId = AuthSessionHelper.getSessionId(request);
		LoginSession session = AuthSessionHelper.validateSessionIfNotCreateAnonymous(sessionId);
		
		//如果是首次生成session
		if(StringUtils.isBlank(sessionId) && session.isAnonymous()){
			String domain = WebUtils.getRootDomain(request);
			AuthSessionHelper.createSessionCookies(session.getSessionId(), domain, session.getExpiresIn());
		}
				
		if (requered && session.isAnonymous()) {
			throw new UnauthorizedException();
		}

		LoginContext.setLoginSession(session);
		return session;
	}

}
