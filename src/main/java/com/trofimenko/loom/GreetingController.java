package com.trofimenko.loom;

import com.trofimenko.loom.domain.Message;
import com.trofimenko.loom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GreetingController {
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Model model){
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    @PostMapping("/main")
    public String add(@RequestParam String text, @RequestParam String tag, Model model){

        Message message = new Message(text, tag);
        messageRepository.save(message);

        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter,Model model){
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()){
            messages = messageRepository.findByTag(filter);
        } else
            messages = messageRepository.findAll();

        model.addAttribute("messages", messages);
        return "main";
    }

}