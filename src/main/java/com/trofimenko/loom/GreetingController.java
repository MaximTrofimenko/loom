package com.trofimenko.loom;

import com.trofimenko.loom.domain.Message;
import com.trofimenko.loom.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
@Slf4j
@Controller
public class GreetingController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);

        int x = 4;
        int y = 0;

        try {
            System.out.println(x / y);
            log.debug("Дебаг");
        } catch (Exception e) {
            log.error("ХУЙЙЙЙЙЙ");
            log.debug("Дебаг");
        }
        log.info("ПИЗДА!!!!!");
        log.warn("ЭТО ВАРН");
        log.debug("Дебаг");




        return "greeting";
    }

    @GetMapping
    public String main(Model model){
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    @PostMapping
    public String add(@RequestParam String text, @RequestParam String tag, Model model){

        Message message = new Message(text, tag);
        messageRepository.save(message);

        return "redirect:/";
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