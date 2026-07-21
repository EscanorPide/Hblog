package com.hehaoran.hblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hehaoran.hblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.hehaoran.hblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.hehaoran.hblog.admin.service.AdminBlogSettingsService;
import com.hehaoran.hblog.common.domain.dos.BlogSettingsDO;
import com.hehaoran.hblog.common.domain.mapper.BlogSettingsMapper;
import com.hehaoran.hblog.common.utils.Response;
import org.springframework.stereotype.Service;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {

    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        // VO 转 DO
        BlogSettingsDO blogSettingsDO = BlogSettingsDO.builder()
                .id(1L)
                .logo(updateBlogSettingsReqVO.getLogo())
                .name(updateBlogSettingsReqVO.getName())
                .author(updateBlogSettingsReqVO.getAuthor())
                .introduction(updateBlogSettingsReqVO.getIntroduction())
                .avatar(updateBlogSettingsReqVO.getAvatar())
                .githubHomepage(updateBlogSettingsReqVO.getGithubHomepage())
                .giteeHomepage(updateBlogSettingsReqVO.getGiteeHomepage())
                .csdnHomepage(updateBlogSettingsReqVO.getCsdnHomepage())
                .zhihuHomepage(updateBlogSettingsReqVO.getZhihuHomepage())
                .build();

        // 保存或更新（当数据库中存在 ID 为 1 的记录时，则执行更新操作，否则执行插入操作）
        saveOrUpdate(blogSettingsDO);
        return Response.success();
    }

    @Override
    public Response findDetail() {
        // 查询 ID 为 1 的记录
        BlogSettingsDO blogSettingsDO = getById(1L);

        // 未初始化时返回空对象，前端按空表单处理
        if (blogSettingsDO == null) {
            return Response.success(FindBlogSettingsRspVO.builder().build());
        }

        return Response.success(FindBlogSettingsRspVO.builder()
                .logo(blogSettingsDO.getLogo())
                .name(blogSettingsDO.getName())
                .author(blogSettingsDO.getAuthor())
                .introduction(blogSettingsDO.getIntroduction())
                .avatar(blogSettingsDO.getAvatar())
                .githubHomepage(blogSettingsDO.getGithubHomepage())
                .giteeHomepage(blogSettingsDO.getGiteeHomepage())
                .csdnHomepage(blogSettingsDO.getCsdnHomepage())
                .zhihuHomepage(blogSettingsDO.getZhihuHomepage())
                .build());
    }
}
