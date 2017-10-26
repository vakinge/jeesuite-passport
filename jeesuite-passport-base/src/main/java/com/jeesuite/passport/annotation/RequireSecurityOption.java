package com.jeesuite.passport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSecurityOption {

	String[] permissons() default {};
	
	boolean innerInvokeOnly() default false;
	
	boolean requireLogin() default false;
}
