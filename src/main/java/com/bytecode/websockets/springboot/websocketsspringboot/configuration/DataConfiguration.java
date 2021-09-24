package com.bytecode.websockets.springboot.websocketsspringboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataConfiguration {
    @Bean(name = "ids")
    @Scope("singleton")
    public List<String> getIds(){
        return new ArrayList<>();
    }
}
