package com.jeesuite.passport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jeesuite.security.SecurityDelegatingFilter;
import com.jeesuite.springboot.starter.cache.EnableJeesuiteCache;
import com.jeesuite.springboot.starter.mybatis.EnableJeesuiteMybatis;


@SpringBootApplication
@MapperScan(basePackages = "com.jeesuite.passport.dao.mapper")
@EnableJeesuiteCache
@EnableJeesuiteMybatis
@ComponentScan(value = {"com.jeesuite.passport","com.jeesuite.springweb"})
@EnableTransactionManagement
//@EnableCircuitBreaker
public class PassportApplicationStarter {
	
	
    public static void main(String[] args) {
    	
    	long starTime = System.currentTimeMillis();
    	new SpringApplicationBuilder(PassportApplicationStarter.class).web(WebApplicationType.SERVLET).run(args);
    	//
        long endTime = System.currentTimeMillis();
        long time = endTime - starTime;
        System.out.println("\nStart Time: " + time / 1000 + " s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
       
    }

    @Bean
	public FilterRegistrationBean<SecurityDelegatingFilter> securityFilterRegistration() {
	    FilterRegistrationBean<SecurityDelegatingFilter> registration = new FilterRegistrationBean<>();
	    registration.setFilter(new SecurityDelegatingFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("securityFilter");
	    registration.setOrder(0);
	    return registration;
	} 
}
