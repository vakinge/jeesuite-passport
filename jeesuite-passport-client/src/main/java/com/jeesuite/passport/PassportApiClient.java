package com.jeesuite.passport;

import java.util.HashMap;
import java.util.Map;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.http.HttpMethod;
import com.jeesuite.common.http.HttpRequestEntity;
import com.jeesuite.common.http.HttpResponseEntity;
import com.jeesuite.common.http.HttpUtils;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.util.DigestUtils;
import com.jeesuite.passport.response.AuthnResponse;
import com.jeesuite.springweb.utils.ParameterUtils;

/**
 * 
 * 
 * <br>
 * Class Name   : PassportApiClient
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @version 1.0.0
 * @date May 9, 2021
 */
public class PassportApiClient {

	public static Map<String, String> getConfigs(){
		
		String url = PassportConfigHolder.buildServerUrl("/auth/configs");
		String body = executeRequest(url, null);
		return JsonUtils.toHashMap(body, String.class);
	}

	public static AuthnResponse ticketExchangeUser(String ticket) {
		String url = PassportConfigHolder.buildServerUrl("/auth/ticket_exchange");
		String body = executeRequest(url, ticket);
		return JsonUtils.toObject(body, AuthnResponse.class);
	}

	@SuppressWarnings("rawtypes")
	private static String executeRequest(String url,String ticket) {
		Map<String, Object> params = new HashMap<>();
		params.put("client_id", PassportConfigHolder.clientId());
		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		if(ticket != null)params.put("ticket", ticket);
		String baseSign = ParameterUtils.mapToQueryParams(params) + PassportConfigHolder.clientSecret();
		params.put("sign", DigestUtils.md5(baseSign));
		
		HttpRequestEntity requestEntity = HttpRequestEntity.create(HttpMethod.GET).queryParams(params);
		HttpResponseEntity responseEntity = HttpUtils.execute(url, requestEntity);
		
		if(!responseEntity.isSuccessed()) {
			throw new JeesuiteBaseException(responseEntity.getMessage());
		}
		
		Map map = JsonUtils.toObject(responseEntity.getBody(), Map.class);
		if(Integer.parseInt(map.get("code").toString()) == 200) {
			return JsonUtils.toJson(map.get("data"));
		}else {
			
			throw new JeesuiteBaseException(map.get("msg").toString());
		}
	}
}
