package com.jeesuite.passport;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2019年6月4日
 */
public class JwtHelper {

    private static final String[] USER_CLAIMS = new String[]{"userName"};

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
                .setSigningKey(PassportConfigHolder.jwtSecret())
                .parseClaimsJws(token)
                .getBody();
    }
    
    
}
