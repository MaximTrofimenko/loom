package com.trofimenko.loom.controller;

import com.trofimenko.loom.domain.Role;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.servise.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users",userService.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(
            @PathVariable User user, //тут мы получаем сразу user по его id
            Model model
    ){
        model.addAttribute("user",user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }
    @PostMapping
    public String userSave(
            @RequestParam String username,         //получаем username из формы
            @RequestParam Map<String,String> form, //получаем список ролей, их может быть разное количество, поэтому Map
            @RequestParam("userId") User user      //получем сразу user по его id который дергается из form
    ){
        userService.saveUser(user,username,form);
        return "redirect:/user";
    }

}
