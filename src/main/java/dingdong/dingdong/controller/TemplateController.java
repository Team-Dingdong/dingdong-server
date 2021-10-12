package dingdong.dingdong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/chat")
public class TemplateController {

    @GetMapping("")
    public String chat(Model model) {
        return "chat";
    }
}
