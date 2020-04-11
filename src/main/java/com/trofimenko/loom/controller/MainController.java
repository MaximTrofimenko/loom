package com.trofimenko.loom.controller;

import com.trofimenko.loom.domain.Message;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessageRepository messageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model){
        Iterable<Message> messages = messageRepository.findAll();

        if (filter != null && !filter.isEmpty()){
            messages = messageRepository.findByTag(filter);
        } else
            messages = messageRepository.findAll();

        model.addAttribute("messages", messages);
        model.addAttribute("filter",filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Model model,
            @RequestParam("file")MultipartFile file
            ) throws IOException {

        Message message = new Message(text, tag, user);
        /*
        загрузка файла
         */
        if(file != null && !file.getOriginalFilename().isEmpty()){                           //проверка на пустоту файла и наличее дирректории
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();                      //генерируем случайный uuid
            String resultFilename = uuidFile + "." + file.getOriginalFilename(); //создаем название файла

            file.transferTo(new File(uploadPath + "/" + resultFilename));                           //загружаем файл

            message.setFilename(resultFilename);
        }

        messageRepository.save(message);
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }
}