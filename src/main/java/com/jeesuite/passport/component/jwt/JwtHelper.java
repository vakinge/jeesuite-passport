package com.jeesuite.passport.component.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.security.model.UserSession;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2019年6月4日
 */
@Component
public class JwtHelper {

    public static final String TOKEN_HEADER = "Authorization";

    private static final String SECRET = ResourceUtils.getProperty("auth.jwt.secret");
    private static final String ISS = "jeesuite-passport";
    private static SignatureAlgorithm signatureAlgorithm;
    private static Map<String, String> jwtConfigs = new HashMap<>(2);
    static{
    	String algorithmName = ResourceUtils.getProperty("auth.jwt.signatureAlgorithm",SignatureAlgorithm.HS256.getJcaName());
        if(algorithmName.equals(SignatureAlgorithm.HS256.getJcaName())){
        	signatureAlgorithm = SignatureAlgorithm.HS256;
        }else if(algorithmName.equals(SignatureAlgorithm.HS512.getJcaName())){
        	signatureAlgorithm = SignatureAlgorithm.HS512;
        }else if(algorithmName.equals(SignatureAlgorithm.RS256.getJcaName())){
        	signatureAlgorithm = SignatureAlgorithm.RS256;
        }else if(algorithmName.equals(SignatureAlgorithm.RS512.getJcaName())){
        	signatureAlgorithm = SignatureAlgorithm.RS512;
        }
        
        jwtConfigs.put("secret", SECRET);
        jwtConfigs.put("algorithm", algorithmName);
    }

    private static final String[] USER_CLAIMS = new String[]{"userName"};
    
    public static Map<String, String> getJwtConfigs() {
		return jwtConfigs;
	}

	// 创建token
    public static String createToken(UserSession session) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(USER_CLAIMS[0], session.getUserName());
        return Jwts.builder()
                .signWith(signatureAlgorithm, SECRET)
                .setClaims(map)
                .setIssuer(ISS)
                .setSubject(String.valueOf(session.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + session.getExpiresIn() * 1000))
                .compact();
    }

    public static String getUserId(String token){
        return getTokenBody(token).getSubject();
    }

    public static String getUserName(String token){
        return (String) getTokenBody(token).get(USER_CLAIMS[0]);
    }

    // 是否已过期
    public static boolean isExpiration(String token){
        return getTokenBody(token).getExpiration().before(new Date());
    }

    private static Claims getTokenBody(String token){
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
    
    
}
