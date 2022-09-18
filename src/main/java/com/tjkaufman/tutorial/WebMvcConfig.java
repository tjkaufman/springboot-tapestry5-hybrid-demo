/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tjkaufman.tutorial;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This configuration class defines the base API path for the application's exposed API utilizing
 * Spring's @RestController annotation
 * @author tjk46
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer  {

    private static final String API_BASE_PATH = "api";
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_BASE_PATH, HandlerTypePredicate.forAnnotation(RestController.class));
    }
}