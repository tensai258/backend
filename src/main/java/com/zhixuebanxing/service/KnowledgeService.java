package com.zhixuebanxing.service;

import com.zhixuebanxing.dto.KnowledgeDTO;
import com.zhixuebanxing.vo.KnowledgeVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;

public interface KnowledgeService {
    Result<Void> uploadKnowledge(KnowledgeDTO dto);
    Result<PageResult<KnowledgeVO>> listKnowledge(Long courseId, Integer page, Integer size);
    Result<PageResult<KnowledgeVO>> searchKnowledge(String keyword, Integer page, Integer size);
}
