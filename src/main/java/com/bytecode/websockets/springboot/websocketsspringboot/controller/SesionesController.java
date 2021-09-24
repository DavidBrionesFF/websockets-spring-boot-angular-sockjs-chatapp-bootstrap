package com.bytecode.websockets.springboot.websocketsspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sesions")
public class SesionesController {
    @Autowired
    @Qualifier("ids")
    public List<String> ids;

    @GetMapping("/findAll")
    public ResponseEntity<List<Map<String, String>>> findAll(){
        List<Map<String, String>> data = new ArrayList<>();

        for (String id: ids){
            HashMap<String, String> obj = new HashMap<>();
            obj.put("id", id);
            data.add(obj);
        }

        return ResponseEntity.ok(data);
    }
}
