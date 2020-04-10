package com.trofimenko.loom.controller;

import com.trofimenko.loom.domain.Role;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users",userRepository.findAll());
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
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())  //переводим enum в set ролей
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        /*
        добавляем роли пользователю, тоесть сравниваем списов ВСЕХ ролей и ролей которые
        заполнил пользователь через форму
        Если роль из формы соответствует роли в базовом списке то добавляем эту роль юзеру
         */
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
        return "redirect:/user";
    }

}
