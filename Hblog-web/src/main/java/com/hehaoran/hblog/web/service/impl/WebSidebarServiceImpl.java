package com.hehaoran.hblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hehaoran.hblog.common.domain.dos.ArticleCategoryRelDO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import com.hehaoran.hblog.common.domain.dos.ArticleTagRelDO;
import com.hehaoran.hblog.common.domain.dos.CategoryDO;
import com.hehaoran.hblog.common.domain.dos.TagDO;
import com.hehaoran.hblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleTagRelMapper;
import com.hehaoran.hblog.common.domain.mapper.CategoryMapper;
import com.hehaoran.hblog.common.domain.mapper.TagMapper;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.category.CategoryRspVO;
import com.hehaoran.hblog.web.model.vo.tag.TagRspVO;
import com.hehaoran.hblog.web.service.WebSidebarService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebSidebarServiceImpl implements WebSidebarService {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Resource
    private ArticleTagRelMapper articleTagRelMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Response findCategoryList() {
        List<CategoryDO> categories = categoryMapper.selectList(Wrappers.<CategoryDO>lambdaQuery()
                .eq(CategoryDO::getIsDeleted, false)
                .orderByAsc(CategoryDO::getCreateTime));
        if (CollectionUtils.isEmpty(categories)) {
            return Response.success(Collections.emptyList());
        }

        Set<Long> categoryIds = categories.stream().map(CategoryDO::getId).collect(Collectors.toSet());
        List<ArticleCategoryRelDO> categoryRels = articleCategoryRelMapper.selectList(Wrappers.<ArticleCategoryRelDO>lambdaQuery()
                .in(ArticleCategoryRelDO::getCategoryId, categoryIds));
        Set<Long> articleIds = categoryRels.stream().map(ArticleCategoryRelDO::getArticleId).collect(Collectors.toSet());
        Set<Long> activeArticleIds = articleIds.isEmpty() ? Collections.emptySet() : articleMapper.selectList(
                        Wrappers.<ArticleDO>lambdaQuery().in(ArticleDO::getId, articleIds)
                                .eq(ArticleDO::getIsDeleted, false))
                .stream().map(ArticleDO::getId).collect(Collectors.toSet());
        Map<Long, Long> totals = categoryRels.stream()
                .filter(rel -> activeArticleIds.contains(rel.getArticleId()))
                .collect(Collectors.groupingBy(ArticleCategoryRelDO::getCategoryId, Collectors.counting()));

        List<CategoryRspVO> vos = categories.stream()
                .map(category -> CategoryRspVO.builder().id(category.getId()).name(category.getName())
                        .articlesTotal(totals.getOrDefault(category.getId(), 0L)).build())
                .collect(Collectors.toList());
        return Response.success(vos);
    }

    @Override
    public Response findTagList() {
        List<TagDO> tags = tagMapper.selectList(Wrappers.<TagDO>lambdaQuery()
                .eq(TagDO::getIsDeleted, false)
                .orderByAsc(TagDO::getCreateTime));
        if (CollectionUtils.isEmpty(tags)) {
            return Response.success(Collections.emptyList());
        }

        Set<Long> tagIds = tags.stream().map(TagDO::getId).collect(Collectors.toSet());
        List<ArticleTagRelDO> tagRels = articleTagRelMapper.selectList(Wrappers.<ArticleTagRelDO>lambdaQuery()
                .in(ArticleTagRelDO::getTagId, tagIds));
        Set<Long> articleIds = tagRels.stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toSet());
        Set<Long> activeArticleIds = articleIds.isEmpty() ? Collections.emptySet() : articleMapper.selectList(
                        Wrappers.<ArticleDO>lambdaQuery().in(ArticleDO::getId, articleIds)
                                .eq(ArticleDO::getIsDeleted, false))
                .stream().map(ArticleDO::getId).collect(Collectors.toSet());
        Map<Long, Long> totals = tagRels.stream()
                .filter(rel -> activeArticleIds.contains(rel.getArticleId()))
                .collect(Collectors.groupingBy(ArticleTagRelDO::getTagId, Collectors.counting()));

        List<TagRspVO> vos = tags.stream()
                .map(tag -> TagRspVO.builder().id(tag.getId()).name(tag.getName())
                        .articlesTotal(totals.getOrDefault(tag.getId(), 0L)).build())
                .collect(Collectors.toList());
        return Response.success(vos);
    }
}
