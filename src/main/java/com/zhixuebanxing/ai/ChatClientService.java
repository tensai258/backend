package com.zhixuebanxing.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientService {

    private final OpenAiChatModel chatModel;
    private final RAGService ragService;

    public String chatSync(List<Message> messages, boolean useRag) {
        List<Message> promptMessages = new ArrayList<>();
        if (useRag && !messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            if (lastMessage instanceof UserMessage userMessage) {
                String systemPrompt = ragService.buildSystemPromptWithContext(userMessage.getContent());
                promptMessages.add(new SystemMessage(systemPrompt));
            }
        } else {
            promptMessages.add(new SystemMessage("你是一个专业的教育AI助手，请根据学生的问题提供详细、准确的解答。"));
        }
        promptMessages.addAll(messages);

        Prompt prompt = new Prompt(promptMessages);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    public Flux<String> chatStream(List<Message> messages, boolean useRag) {
        List<Message> promptMessages = new ArrayList<>();
        if (useRag && !messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            if (lastMessage instanceof UserMessage userMessage) {
                String systemPrompt = ragService.buildSystemPromptWithContext(userMessage.getContent());
                promptMessages.add(new SystemMessage(systemPrompt));
            }
        } else {
            promptMessages.add(new SystemMessage("你是一个专业的教育AI助手，请根据学生的问题提供详细、准确的解答。"));
        }
        promptMessages.addAll(messages);

        Prompt prompt = new Prompt(promptMessages);
        return chatModel.stream(prompt)
            .map(chunk -> {
                String text = chunk.getResult().getOutput().getContent();
                return text != null ? text : "";
            });
    }
}
