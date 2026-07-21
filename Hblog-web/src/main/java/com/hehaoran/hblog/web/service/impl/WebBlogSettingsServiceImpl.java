package com.hehaoran.hblog.web.service.impl;

import com.hehaoran.hblog.common.domain.dos.BlogSettingsDO;
import com.hehaoran.hblog.common.domain.mapper.BlogSettingsMapper;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.blogsettings.BlogSettingsRspVO;
import com.hehaoran.hblog.web.service.WebBlogSettingsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WebBlogSettingsServiceImpl implements WebBlogSettingsService {

    @Resource
    private BlogSettingsMapper blogSettingsMapper;

    @Override
    public Response findDetail() {
        BlogSettingsDO settingsDO = blogSettingsMapper.selectById(1L);
        if (settingsDO == null) {
            return Response.success(BlogSettingsRspVO.builder().build());
        }
        return Response.success(BlogSettingsRspVO.builder()
                .logo(settingsDO.getLogo())
                .name(settingsDO.getName())
                .author(settingsDO.getAuthor())
                .introduction(settingsDO.getIntroduction())
                .avatar(settingsDO.getAvatar())
                .githubHomepage(settingsDO.getGithubHomepage())
                .giteeHomepage(settingsDO.getGiteeHomepage())
                .csdnHomepage(settingsDO.getCsdnHomepage())
                .zhihuHomepage(settingsDO.getZhihuHomepage())
                .build());
    }
}
