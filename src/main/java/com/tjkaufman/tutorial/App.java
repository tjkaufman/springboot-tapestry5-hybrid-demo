package com.tjkaufman.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class App extends SpringBootServletInitializer
{

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
//    {
//        return application.sources(AppConfiguration.class);
//    }

    public static void main(String[] args) throws Exception
    {
//        SpringApplication application = new SpringApplication(App.class);
//        application.setApplicationContextClass(AnnotationConfigWebApplicationContext.class);
        SpringApplication.run(App.class, args);
    }
}
