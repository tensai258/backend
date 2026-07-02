package com.zhixuebanxing.controller;

import com.zhixuebanxing.dto.KnowledgeDTO;
import com.zhixuebanxing.service.KnowledgeService;
import com.zhixuebanxing.vo.KnowledgeVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping("/upload")
    public Result<Void> uploadKnowledge(@Valid @RequestBody KnowledgeDTO dto) {
        return knowledgeService.uploadKnowledge(dto);
    }

    @GetMapping("/documents")
    public Result<PageResult<KnowledgeVO>> listKnowledge(
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return knowledgeService.listKnowledge(courseId, page, size);
    }

    @GetMapping("/search")
    public Result<PageResult<KnowledgeVO>> searchKnowledge(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return knowledgeService.searchKnowledge(keyword, page, size);
    }

    @DeleteMapping("/documents/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        return knowledgeService.deleteDocument(id);
    }
}
