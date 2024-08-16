package org.inmobiliarity.chatboot.application.controller;

import org.apache.hc.core5.http.ParseException;
import org.inmobiliarity.chatboot.domain.model.Property;
import org.inmobiliarity.chatboot.domain.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        try {
            String response = chatService.processUserMessage(message);
            return ResponseEntity.ok(response);
        } catch (IOException | ParseException e) {
            return ResponseEntity.status(500).body("Error processing message");
        }
    }

    @PostMapping("/properties")
    public ResponseEntity<String> saveProperty(@RequestBody Property property) {
        try {
            chatService.saveProperty(property);
            return ResponseEntity.ok("Property saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving property");
        }
    }
}
