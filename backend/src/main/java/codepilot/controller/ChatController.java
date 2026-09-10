package codepilot.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codepilot.dto.chat.ChatRequest;
import codepilot.dto.chat.ChatResponse;
import codepilot.service.ChatService;

@RestController
@RequestMapping("/api/projects/{projectId}/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> askQuestion(
            @PathVariable Long projectId,
            @Valid @RequestBody ChatRequest request) {

        ChatResponse response = chatService.askQuestion(projectId, request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
