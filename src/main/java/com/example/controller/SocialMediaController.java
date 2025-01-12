package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@Controller
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<?> register(@RequestBody Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return ResponseEntity.status(400).body("Username cannot be blank");
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return ResponseEntity.status(400).body("Password must be at least 4 characters long");
        }

        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount != null) {
            return ResponseEntity.status(409).body("Username already exits.");
        }

        Account registeredAccount = accountService.registerAccount(account);
        return ResponseEntity.status(200).body(registeredAccount);
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> login(@RequestBody Account account) {
        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount == null || !account.getPassword().equals(existingAccount.getPassword())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.status(200).body(existingAccount);
    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<?> createMessage(@RequestBody Message message) {
        String messageText = message.getMessageText();
        if (messageText == null || messageText.isBlank() || messageText.length() >= 255) {
            return ResponseEntity.status(400).body("Message not created");
        }

        Account existingAccount = accountService.findByAccountId(message.getPostedBy());
        if (existingAccount == null) {
            return ResponseEntity.status(400).body("Message not created");
        }

        Message createdMessage = messageService.createMessage(message);
        return ResponseEntity.status(200).body(createdMessage);
    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.status(200).body(messages);
    }

    @GetMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable String messageId) {
        Message message = messageService.getMessageById(messageId);
        return ResponseEntity.status(200).body(message);
    }

    @DeleteMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<Integer> deleteMessageById(@PathVariable String messageId) {
        int deleted = messageService.deleteMessageById(messageId);

        if (deleted > 0) {
            return ResponseEntity.status(200).body(deleted);
        }
        return ResponseEntity.status(200).build();
    }

    @PatchMapping("/messages/{messageId}")
    public @ResponseBody ResponseEntity<?> updateMessage(@PathVariable String messageId, @RequestBody Map<String, String> payload) {

        String messageText = payload.get("messageText");
        if (messageText == null || messageText.isBlank() || messageText.length() > 255) {
            return ResponseEntity.status(400).body("Invalid message text.");
        }

        Message messageExists = messageService.getMessageById(messageId);
        if (messageExists == null) {
            return ResponseEntity.status(400).body("Message does not exist.");
        }

        boolean isUpdated = messageService.updateMessageByMessageId(messageId, messageText);

        if (isUpdated) {
            return ResponseEntity.status(200).body(1);
        }

        return ResponseEntity.status(400).build();
    }
}
