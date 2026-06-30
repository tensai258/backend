package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhixuebanxing.dto.KnowledgeDTO;
import com.zhixuebanxing.entity.Knowledge;
import com.zhixuebanxing.mapper.KnowledgeMapper;
import com.zhixuebanxing.service.KnowledgeService;
import com.zhixuebanxing.vo.KnowledgeVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeMapper knowledgeMapper;

    @Override
    public Result<Void> uploadKnowledge(KnowledgeDTO dto) {
        Knowledge knowledge = new Knowledge();
        BeanUtils.copyProperties(dto, knowledge);
        knowledgeMapper.insert(knowledge);
        return Result.success("知识点上传成功", null);
    }

    @Override
    public Result<PageResult<KnowledgeVO>> listKnowledge(Long courseId, Integer page, Integer size) {
        Page<Knowledge> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(Knowledge::getCourseId, courseId);
        }
        wrapper.eq(Knowledge::getDeleted, 0).orderByDesc(Knowledge::getCreateTime);

        Page<Knowledge> result = knowledgeMapper.selectPage(pageParam, wrapper);
        List<KnowledgeVO> vos = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        PageResult<KnowledgeVO> pageResult = new PageResult<>(vos, result.getTotal(), result.getCurrent(), result.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<KnowledgeVO>> searchKnowledge(String keyword, Integer page, Integer size) {
        Page<Knowledge> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Knowledge::getName, keyword)
                .or()
                .like(Knowledge::getDescription, keyword)
                .or()
                .like(Knowledge::getTags, keyword);
        }
        wrapper.eq(Knowledge::getDeleted, 0).orderByDesc(Knowledge::getCreateTime);

        Page<Knowledge> result = knowledgeMapper.selectPage(pageParam, wrapper);
        List<KnowledgeVO> vos = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        PageResult<KnowledgeVO> pageResult = new PageResult<>(vos, result.getTotal(), result.getCurrent(), result.getSize());
        return Result.success(pageResult);
    }

    private KnowledgeVO convertToVO(Knowledge knowledge) {
        KnowledgeVO vo = new KnowledgeVO();
        BeanUtils.copyProperties(knowledge, vo);
        return vo;
    }
}
