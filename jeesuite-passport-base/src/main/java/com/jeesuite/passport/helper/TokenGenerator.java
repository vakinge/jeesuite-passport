package com.jeesuite.passport.helper;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.util.DigestUtils;

public class TokenGenerator {

	public static String generate(){
		String str = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY) + DigestUtils.md5(System.currentTimeMillis());
		return str;
	}
	
	public static void main(String[] args) {
		System.out.println(TokenGenerator.generate());
	}
}
