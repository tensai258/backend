package com.zhixuebanxing.service;

import com.zhixuebanxing.dto.ChatRequestDTO;
import com.zhixuebanxing.vo.ChatMessageVO;
import com.zhixuebanxing.vo.Result;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Flux<String> chatStream(Long userId, ChatRequestDTO dto);
    Result<String> chatSync(Long userId, ChatRequestDTO dto);
    Result<List<ChatMessageVO>> getHistory(String sessionId);
    Result<String> createSession(Long userId);
    Result<Void> deleteSession(String sessionId);
}
