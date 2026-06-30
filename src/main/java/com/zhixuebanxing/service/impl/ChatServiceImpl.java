package com.zhixuebanxing.service.impl;

import com.zhixuebanxing.ai.ChatClientService;
import com.zhixuebanxing.dto.ChatRequestDTO;
import com.zhixuebanxing.entity.Dialogue;
import com.zhixuebanxing.mapper.DialogueMapper;
import com.zhixuebanxing.service.ChatService;
import com.zhixuebanxing.vo.ChatMessageVO;
import com.zhixuebanxing.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClientService chatClientService;
    private final DialogueMapper dialogueMapper;

    @Override
    public Flux<String> chatStream(Long userId, ChatRequestDTO dto) {
        String sessionId = dto.getSessionId() != null ? dto.getSessionId() : UUID.randomUUID().toString();
        String model = dto.getModel() != null ? dto.getModel() : "deepseek-chat";

        // Save user message
        Dialogue userDialogue = new Dialogue();
        userDialogue.setUserId(userId);
        userDialogue.setSessionId(sessionId);
        userDialogue.setRole("user");
        userDialogue.setContent(dto.getMessage());
        userDialogue.setModel(model);
        dialogueMapper.insert(userDialogue);

        // Build message history
        List<Message> messages = buildMessageHistory(sessionId, dto.getMessage());

        return chatClientService.chatStream(messages, dto.getUseRag())
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(chunk -> {})
            .doOnComplete(() -> {
                // Note: In real implementation, we'd collect the full response and save it
                // This is simplified for the stream demo
            });
    }

    @Override
    public Result<String> chatSync(Long userId, ChatRequestDTO dto) {
        String sessionId = dto.getSessionId() != null ? dto.getSessionId() : UUID.randomUUID().toString();
        String model = dto.getModel() != null ? dto.getModel() : "deepseek-chat";

        // Save user message
        Dialogue userDialogue = new Dialogue();
        userDialogue.setUserId(userId);
        userDialogue.setSessionId(sessionId);
        userDialogue.setRole("user");
        userDialogue.setContent(dto.getMessage());
        userDialogue.setModel(model);
        dialogueMapper.insert(userDialogue);

        List<Message> messages = buildMessageHistory(sessionId, dto.getMessage());
        String response = chatClientService.chatSync(messages, dto.getUseRag());

        // Save assistant message
        Dialogue assistantDialogue = new Dialogue();
        assistantDialogue.setUserId(userId);
        assistantDialogue.setSessionId(sessionId);
        assistantDialogue.setRole("assistant");
        assistantDialogue.setContent(response);
        assistantDialogue.setModel(model);
        assistantDialogue.setTokens(response.length());
        dialogueMapper.insert(assistantDialogue);

        return Result.success(response);
    }

    @Override
    public Result<List<ChatMessageVO>> getHistory(String sessionId) {
        List<Dialogue> dialogues = dialogueMapper.selectBySessionId(sessionId);
        List<ChatMessageVO> vos = dialogues.stream().map(d -> {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(d.getId());
            vo.setSessionId(d.getSessionId());
            vo.setRole(d.getRole());
            vo.setContent(d.getContent());
            vo.setModel(d.getModel());
            vo.setTokens(d.getTokens());
            vo.setCreateTime(d.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<String> createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        return Result.success(sessionId);
    }

    private List<Message> buildMessageHistory(String sessionId, String currentMessage) {
        List<Dialogue> history = dialogueMapper.selectBySessionId(sessionId);
        List<Message> messages = new ArrayList<>();
        for (Dialogue d : history) {
            if ("user".equals(d.getRole())) {
                messages.add(new UserMessage(d.getContent()));
            } else if ("assistant".equals(d.getRole())) {
                messages.add(new AssistantMessage(d.getContent()));
            }
        }
        messages.add(new UserMessage(currentMessage));
        return messages;
    }
}
