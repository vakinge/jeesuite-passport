/**
 * 
 */
package com.jeesuite.passport.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginUserInfo;
import com.jeesuite.springweb.client.SimpleRestTemplateBuilder;
import com.jeesuite.springweb.model.WrapperResponse;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/logged/*", description = "用户登录信息")
public class GetLoginInfoEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String getStatusUrl = ClientConfig.authServerBasePath() + "/user/login_status/";
	private static String getInfoUrl = ClientConfig.authServerBasePath() + "/user/info/";
	
	//@Autowired
	private RestTemplate restTemplate = new SimpleRestTemplateBuilder().build();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//String header = req.getHeader(BaseConstants.HEADER_AUTH_USER);
		String sessionId = AuthSessionHelper.getSessionId(req);
		
		String act = req.getPathInfo().substring(1);
        try {
        	if("status".equals(act)){
    			ResponseEntity<WrapperResponseEntity> entity = restTemplate.getForEntity(getStatusUrl+sessionId, WrapperResponseEntity.class);
    			WebUtils.responseOutJson(resp, JsonUtils.toJson(entity.getBody()));
    		}else if("info".equals(act)){
    			ParameterizedTypeReference<WrapperResponse<LoginUserInfo>> arearesponseType = new ParameterizedTypeReference<WrapperResponse<LoginUserInfo>>() {
    			};
    			
    			WrapperResponse<LoginUserInfo> response = restTemplate.exchange(getInfoUrl+sessionId,HttpMethod.GET, null, arearesponseType).getBody();
    			WebUtils.responseOutJson(resp, JsonUtils.toJson(response.getData()));
    		}
		} catch (JeesuiteBaseException e) {
			WebUtils.responseOutJson(resp, JsonUtils.toJson(new WrapperResponseEntity(e.getCode(), e.getMessage())));
		}
		
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
