package com.hehaoran.hblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
public interface ArticleMapper extends BaseMapper<ArticleDO> {
    /**
     * 分页查询
     * @param current 当前页码
     * @param size 每页展示的数据量
     * @param title 文章标题
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 先去首尾空格；title 为 null / 全空格时结果为空，下面 like 条件不会生效
        String titleCondition = StringUtils.isNotBlank(title) ? title.trim() : null;

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(titleCondition), ArticleDO::getTitle, titleCondition) // like 模糊查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒序

        return selectPage(page, wrapper);
    }
    /**
     * 阅读量+1
     * @param articleId
     * @return
     */
    default int increaseReadNum(Long articleId) {
        // 执行 SQL : UPDATE t_article SET read_num = read_num + 1 WHERE (id = XX)
        return update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(ArticleDO::getId, articleId));
    }
}
