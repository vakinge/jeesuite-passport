package com.jeesuite.passport.helper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.common.JeesuiteBaseException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build();
	
	public static String httpGet(String url) {
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				return response.body().string();
			}
			throw new JeesuiteBaseException(1001, response.message());
		} catch (Exception e) {
			LOGGER.error("httpGet error，url:"+url, e);
			throw new JeesuiteBaseException(400, "请求第三方错误");
		}
	}
	
	public static  String httpPost(String url,Map<String, String> params) {
		try {
			FormBody.Builder builder = new FormBody.Builder();
	    	for (String key : params.keySet()) {
	    		builder.add(key, params.get(key));
			}

	        RequestBody body = builder.build();
	    	Request request = new Request.Builder().url(url).post(body).build();
	    	Response response = client.newCall(request).execute();
	    	
	    	if(response.isSuccessful()){
				return response.body().string();
			}
			throw new JeesuiteBaseException(1001, response.message());
		} catch (Exception e) {
			LOGGER.error("httpGet error", e);
			throw new JeesuiteBaseException(400, "请求第三方错误");
		}
	}
}
