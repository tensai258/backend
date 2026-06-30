package com.zhixuebanxing.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RAGService {

    @Autowired(required = false)
    private VectorStore vectorStore;

    public String retrieveRelevantContext(String query, int topK) {
        if (vectorStore == null) {
            log.warn("VectorStore not available, skipping RAG retrieval");
            return "";
        }
        try {
            SearchRequest request = SearchRequest.query(query).withTopK(topK);
            List<Document> documents = vectorStore.similaritySearch(request);
            if (documents == null || documents.isEmpty()) {
                return "";
            }
            return documents.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n---\n\n"));
        } catch (Exception e) {
            log.warn("RAG retrieval failed: {}", e.getMessage());
            return "";
        }
    }

    public String buildSystemPromptWithContext(String userQuery) {
        String context = retrieveRelevantContext(userQuery, 5);
        if (context.isBlank()) {
            return "你是一个专业的教育AI助手，请根据学生的问题提供详细、准确的解答。";
        }
        return String.format(
            "你是一个专业的教育AI助手。请基于以下参考资料回答学生的问题，如果参考资料中没有相关信息，请基于你的知识回答。\n\n参考资料：\n%s\n\n请根据以上资料回答学生的问题。",
            context
        );
    }
}
