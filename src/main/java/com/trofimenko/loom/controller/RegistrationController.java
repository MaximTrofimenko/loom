package com.trofimenko.loom.controller;

import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(){
        return "/registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){
        if (!userService.addUser(user)){
            model.addAttribute("message","User exist!");
            return "registration";
        }
        return "redirect:/login";
    }
}
