package com.vsftam.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SimpleController {

    @RequestMapping("/")
    String welcome() {
        return "Welcome to the Electronic Store\n";
    }
}
