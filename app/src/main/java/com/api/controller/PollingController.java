package com.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PollingController {
    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
