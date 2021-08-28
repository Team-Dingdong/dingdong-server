package dingdong.dingdong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() throws Exception {
        return "index"; //note that this says .html
    }
}