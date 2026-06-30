package com.zhixuebanxing.controller;

import com.zhixuebanxing.dto.ChatRequestDTO;
import com.zhixuebanxing.service.ChatService;
import com.zhixuebanxing.util.JwtUtil;
import com.zhixuebanxing.vo.ChatMessageVO;
import com.zhixuebanxing.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody ChatRequestDTO dto,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(authHeader);
        return chatService.chatStream(userId, dto)
            .onErrorResume(e -> {
                log.error("Stream error: ", e);
                return Flux.just("data: [ERROR] " + e.getMessage() + "\n\n");
            });
    }

    @PostMapping("/sync")
    public Result<String> chatSync(@Valid @RequestBody ChatRequestDTO dto,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(authHeader);
        return chatService.chatSync(userId, dto);
    }

    @GetMapping("/history/{sessionId}")
    public Result<List<ChatMessageVO>> getHistory(@PathVariable String sessionId) {
        return chatService.getHistory(sessionId);
    }

    @PostMapping("/session")
    public Result<String> createSession(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserId(authHeader);
        return chatService.createSession(userId);
    }

    private Long extractUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserId(token);
            }
        }
        return null;
    }
}
