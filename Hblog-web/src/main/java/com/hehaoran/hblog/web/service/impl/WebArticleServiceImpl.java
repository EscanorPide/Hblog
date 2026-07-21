package com.hehaoran.hblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.admin.event.ReadArticleEvent;
import com.hehaoran.hblog.common.domain.dos.ArticleCategoryRelDO;
import com.hehaoran.hblog.common.domain.dos.ArticleContentDO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import com.hehaoran.hblog.common.domain.dos.ArticleTagRelDO;
import com.hehaoran.hblog.common.domain.dos.CategoryDO;
import com.hehaoran.hblog.common.domain.dos.TagDO;
import com.hehaoran.hblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleContentMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleTagRelMapper;
import com.hehaoran.hblog.common.domain.mapper.CategoryMapper;
import com.hehaoran.hblog.common.domain.mapper.TagMapper;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.exception.BizException;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.article.ArticleDetailReqVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleDetailRspVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleListReqVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleListRspVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleTagRspVO;
import com.hehaoran.hblog.web.model.vo.article.CategoryArticleListReqVO;
import com.hehaoran.hblog.web.model.vo.article.TagArticleListReqVO;
import com.hehaoran.hblog.web.service.WebArticleService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebArticleServiceImpl implements WebArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleContentMapper articleContentMapper;
    @Resource
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Resource
    private ArticleTagRelMapper articleTagRelMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Response findArticleList(ArticleListReqVO reqVO) {
        return findArticlePage(reqVO.getCurrent(), reqVO.getSize(), reqVO.getTitle(), null);
    }

    @Override
    public Response findArticleDetail(ArticleDetailReqVO reqVO) {
        ArticleDO articleDO = articleMapper.selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getId, reqVO.getId())
                .eq(ArticleDO::getIsDeleted, false));
        if (articleDO == null) {
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContentDO contentDO = articleContentMapper.selectByArticleId(articleDO.getId());
        ArticleCategoryRelDO categoryRelDO = articleCategoryRelMapper.selectByArticleId(articleDO.getId());
        List<ArticleTagRelDO> tagRelDOS = articleTagRelMapper.selectByArticleId(articleDO.getId());

        CategoryDO categoryDO = categoryRelDO == null ? null : categoryMapper.selectOne(Wrappers.<CategoryDO>lambdaQuery()
                .eq(CategoryDO::getId, categoryRelDO.getCategoryId())
                .eq(CategoryDO::getIsDeleted, false));
        Map<Long, TagDO> tagMap = findActiveTags(tagRelDOS);

        // 发布文章阅读事件（异步增加阅读量）
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleDO.getId()));

        return Response.success(ArticleDetailRspVO.builder()
                .id(articleDO.getId())
                .title(articleDO.getTitle())
                .cover(articleDO.getCover())
                .summary(articleDO.getSummary())
                .content(contentDO == null ? null : contentDO.getContent())
                .createTime(articleDO.getCreateTime())
                .readNum(articleDO.getReadNum())
                .categoryId(categoryRelDO == null ? null : categoryRelDO.getCategoryId())
                .categoryName(categoryDO == null ? null : categoryDO.getName())
                .tags(toTagVos(tagRelDOS, tagMap))
                .build());
    }

    @Override
    public Response findArticleListByCategory(CategoryArticleListReqVO reqVO) {
        CategoryDO categoryDO = categoryMapper.selectOne(Wrappers.<CategoryDO>lambdaQuery()
                .eq(CategoryDO::getId, reqVO.getCategoryId())
                .eq(CategoryDO::getIsDeleted, false));
        if (categoryDO == null) {
            return findArticlePage(reqVO.getCurrent(), reqVO.getSize(), null, Collections.emptyList());
        }
        List<Long> articleIds = articleCategoryRelMapper.selectList(Wrappers.<ArticleCategoryRelDO>lambdaQuery()
                        .eq(ArticleCategoryRelDO::getCategoryId, reqVO.getCategoryId()))
                .stream().map(ArticleCategoryRelDO::getArticleId).collect(Collectors.toList());
        return findArticlePage(reqVO.getCurrent(), reqVO.getSize(), null, articleIds);
    }

    @Override
    public Response findArticleListByTag(TagArticleListReqVO reqVO) {
        TagDO tagDO = tagMapper.selectOne(Wrappers.<TagDO>lambdaQuery()
                .eq(TagDO::getId, reqVO.getTagId())
                .eq(TagDO::getIsDeleted, false));
        if (tagDO == null) {
            return findArticlePage(reqVO.getCurrent(), reqVO.getSize(), null, Collections.emptyList());
        }
        List<Long> articleIds = articleTagRelMapper.selectByTagId(reqVO.getTagId())
                .stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toList());
        return findArticlePage(reqVO.getCurrent(), reqVO.getSize(), null, articleIds);
    }

    private Response findArticlePage(Long current, Long size, String title, List<Long> limitedArticleIds) {
        Page<ArticleDO> page = new Page<>(current == null ? 1L : current, size == null ? 10L : size);
        if (limitedArticleIds != null && limitedArticleIds.isEmpty()) {
            return PageResponse.success(page, Collections.emptyList());
        }

        String titleCondition = StringUtils.isNotBlank(title) ? title.trim() : null;
        Page<ArticleDO> articlePage = articleMapper.selectPage(page, Wrappers.<ArticleDO>lambdaQuery()
                .in(limitedArticleIds != null, ArticleDO::getId, limitedArticleIds)
                .eq(ArticleDO::getIsDeleted, false)
                .like(StringUtils.isNotBlank(titleCondition), ArticleDO::getTitle, titleCondition)
                .orderByDesc(ArticleDO::getCreateTime));
        List<ArticleDO> articleDOS = articlePage.getRecords();
        if (CollectionUtils.isEmpty(articleDOS)) {
            return PageResponse.success(articlePage, Collections.emptyList());
        }

        List<Long> articleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());
        Map<Long, ArticleCategoryRelDO> categoryRelMap = articleCategoryRelMapper.selectByArticleIds(articleIds).stream()
                .collect(Collectors.toMap(ArticleCategoryRelDO::getArticleId, Function.identity(), (first, ignored) -> first));
        List<ArticleTagRelDO> tagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);
        Map<Long, List<ArticleTagRelDO>> tagRelsByArticleId = tagRelDOS.stream()
                .collect(Collectors.groupingBy(ArticleTagRelDO::getArticleId));

        Set<Long> categoryIds = categoryRelMap.values().stream()
                .map(ArticleCategoryRelDO::getCategoryId).collect(Collectors.toSet());
        Map<Long, CategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap() : categoryMapper.selectBatchIds(categoryIds)
                .stream().filter(category -> Boolean.FALSE.equals(category.getIsDeleted()))
                .collect(Collectors.toMap(CategoryDO::getId, Function.identity()));
        Map<Long, TagDO> tagMap = findActiveTags(tagRelDOS);

        List<ArticleListRspVO> vos = new ArrayList<>();
        for (ArticleDO articleDO : articleDOS) {
            ArticleCategoryRelDO categoryRelDO = categoryRelMap.get(articleDO.getId());
            CategoryDO categoryDO = categoryRelDO == null ? null : categoryMap.get(categoryRelDO.getCategoryId());
            vos.add(ArticleListRspVO.builder()
                    .id(articleDO.getId())
                    .title(articleDO.getTitle())
                    .cover(articleDO.getCover())
                    .summary(articleDO.getSummary())
                    .createTime(articleDO.getCreateTime())
                    .readNum(articleDO.getReadNum() == null ? 0L : articleDO.getReadNum())
                    .categoryId(categoryRelDO == null ? null : categoryRelDO.getCategoryId())
                    .categoryName(categoryDO == null ? null : categoryDO.getName())
                    .tags(toTagVos(tagRelsByArticleId.get(articleDO.getId()), tagMap))
                    .build());
        }
        return PageResponse.success(articlePage, vos);
    }

    private Map<Long, TagDO> findActiveTags(List<ArticleTagRelDO> tagRelDOS) {
        if (CollectionUtils.isEmpty(tagRelDOS)) {
            return Collections.emptyMap();
        }
        Set<Long> tagIds = tagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toSet());
        return tagMapper.selectBatchIds(tagIds).stream()
                .filter(tag -> Boolean.FALSE.equals(tag.getIsDeleted()))
                .collect(Collectors.toMap(TagDO::getId, Function.identity()));
    }

    private List<ArticleTagRspVO> toTagVos(List<ArticleTagRelDO> tagRelDOS, Map<Long, TagDO> tagMap) {
        if (CollectionUtils.isEmpty(tagRelDOS)) {
            return Collections.emptyList();
        }
        return tagRelDOS.stream().map(rel -> tagMap.get(rel.getTagId()))
                .filter(tag -> tag != null)
                .map(tag -> ArticleTagRspVO.builder().id(tag.getId()).name(tag.getName()).build())
                .collect(Collectors.toList());
    }
}
