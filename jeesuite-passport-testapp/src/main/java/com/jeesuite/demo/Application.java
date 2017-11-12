package com.jeesuite.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(value = {"com.jeesuite.demo","com.jeesuite.springweb"})
@ServletComponentScan({ "com.jeesuite.passport.client" })
public class Application {
	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).web(true).run(args);
	}
	
	@RequestMapping("/")
    String home() {
        return "redirect:/index.html";
    }
    
}
