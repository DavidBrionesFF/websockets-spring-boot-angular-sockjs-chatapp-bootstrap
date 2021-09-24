package com.bytecode.websockets.springboot.websocketsspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    @Qualifier("ids")
    public List<String> ids;

    @GetMapping
    public ModelAndView findChat(){
        return new ModelAndView("index");
    }
}
