package com.jeesuite.passport.client;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.jeesuite.passport.LoginContext;

public class RestTemplateAutoHeaderInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		try {
			Map<String, String> customHeaders = LoginContext.getCustomHeaders();
			request.getHeaders().setAll(customHeaders);
		} catch (Exception e) {}
		return execution.execute(request, body);
	}

}
