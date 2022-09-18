/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tjkaufman.tutorial.spring.controller;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tapestry5.http.services.ApplicationGlobals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tjk46
 */
@Slf4j
@RestController
public class TestSpringController {

    @Autowired
    ApplicationGlobals applicationGlobals;
    
    @GetMapping(value= "/springcontroller")
    public @ResponseBody List<String> findAll() {
        if(applicationGlobals.getContext()!= null){
            log.debug("ApplicationGlobals is defined! That's Crazy!!!");
        }
//		return userAccountRepository.findAll();
        return Arrays.asList("gimble", "gamble", "gumble", "grundle");
    }
}
