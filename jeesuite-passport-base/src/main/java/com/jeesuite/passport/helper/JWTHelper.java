package com.jeesuite.passport.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.passport.model.LoginUserInfo;

public class JWTHelper {

    private static final String SECRET = AuthConfigClient.getInstance().getJwtSecret();

    private static final String PAYLOAD = "payload";

    private static final String USERID = "userId";

    private static final String SUB = "sub";

    private static final String ISS = "iss";


    public static String sign(LoginUserInfo user) {
        try {
            final JWTSigner signer = new JWTSigner(SECRET);
            final Map<String, Object> claims = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(user);
            claims.put(PAYLOAD, jsonString);
            claims.put(USERID,user.getId());
            return signer.sign(claims);
        } catch(Exception e) {
            return null;
        }
    }


    public static LoginUserInfo unsign(String jwt) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        try {
            final Map<String,Object> claims= verifier.verify(jwt);
            if (claims.containsKey(PAYLOAD)&&claims.containsKey(USERID)) {
                String json = (String)claims.get(PAYLOAD);
                String userId = claims.get(USERID).toString();
                LoginUserInfo user = JsonUtils.getMapper().readValue(json,LoginUserInfo.class);
                if (userId.equals(user.getId().toString())){
                    return user;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) {
    	LoginUserInfo userDTO = new LoginUserInfo();
        userDTO.setId(100);
        userDTO.setUsername("tom");
        String sign = JWTHelper.sign(userDTO);
        System.out.println(sign);
        LoginUserInfo unsign = JWTHelper.unsign(sign);
        System.out.println(ToStringBuilder.reflectionToString(unsign));

    }

}
