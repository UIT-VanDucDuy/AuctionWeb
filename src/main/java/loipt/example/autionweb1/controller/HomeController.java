package loipt.example.autionweb1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping(value = "/home")
    public String showHome(){
        return "home1";
    }

}