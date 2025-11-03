package com.example.auctionweb.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping ("/")
public class LoginController {
    @GetMapping("/login")
    public String login(Model model) {
        return "auth/login";
    }
    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage() {
        return "redirect:/home";
    }
}
