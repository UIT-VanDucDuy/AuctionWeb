package com.example.auctionweb.controller;

import com.example.auctionweb.service.IBidHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private IBidHistoryService bidHistoryService;

    @GetMapping(value = {"/", "/home"})
    public String showHome(Model model){
        model.addAttribute("bidHistoryList", bidHistoryService.findAll());
        return "home1";
    }

}