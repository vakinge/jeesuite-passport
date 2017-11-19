package com.jeesuite.passport.helper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jeesuite.common.crypt.Base64;
import com.jeesuite.common.crypt.DES;
import com.jeesuite.common.util.DigestUtils;

public class SecurityCryptUtils {

	private static String passwordSalt = "q@#tr5~d2&P6#_9G";
	
	public static String cryptPassword(String password,String salt) {
		return DigestUtils.md5(password.concat(passwordSalt).concat(salt));
	}
	
	public static String encrypt(String data) {
		 String cryptKey = AuthConfigClient.getInstance().getCryptSecret();
		 String encode = DES.encrypt(cryptKey, data);
		 byte[] bytes = Base64.encodeToByte(encode.getBytes(StandardCharsets.UTF_8), false);
		 return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static String decrypt(String data) {
		String cryptKey = AuthConfigClient.getInstance().getCryptSecret();
		 byte[] bytes = Base64.decode(data);
		 data = new String(bytes, StandardCharsets.UTF_8);
		 return DES.decrypt(cryptKey, data);
	}
	
	
	public static String generateSign(Map<String, String> sPara) {
		String cryptKey = AuthConfigClient.getInstance().getCryptSecret();
		return generateSign(cryptKey, sPara);
	}
	
	public static String generateSign(String signKey,Map<String, String> sPara) {
    	String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    	prestr = prestr + signKey;
    	String mysign = DigestUtils.md5(prestr);
        return mysign;
    }

	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
	private static String createLinkString(Map<String, String> params) {
    	
    	if(params == null || params.isEmpty())return "";

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

}
