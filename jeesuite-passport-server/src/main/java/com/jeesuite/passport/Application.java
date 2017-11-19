package com.jeesuite.passport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeesuite.passport.filter.AuthCheckFilter;
import com.jeesuite.springboot.starter.cache.EnableJeesuiteCache;
import com.jeesuite.springboot.starter.mybatis.EnableJeesuiteMybatis;


@Controller
@SpringBootApplication
@MapperScan(basePackages = "com.jeesuite.passport.dao.mapper")
@EnableJeesuiteCache
@EnableJeesuiteMybatis
@ComponentScan(value = {"com.jeesuite.passport","com.jeesuite.springweb"})
@EnableDiscoveryClient
@EnableTransactionManagement
//@EnableCircuitBreaker
public class Application {
	
	@Bean 
	public AuthCheckFilter authSessionFilter(){
		return new AuthCheckFilter();
	}
	
	@RequestMapping("/")
    String home() {
        return "redirect:/ucenter/index";
    }
	
    public static void main(String[] args) {
    	
    	long starTime = System.currentTimeMillis();
    	new SpringApplicationBuilder(Application.class).web(true).run(args);
    	//
        long endTime = System.currentTimeMillis();
        long time = endTime - starTime;
        System.out.println("\nStart Time: " + time / 1000 + " s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
       
    }
}
