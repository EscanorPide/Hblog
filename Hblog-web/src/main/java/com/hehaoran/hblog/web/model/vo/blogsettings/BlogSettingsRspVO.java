package com.hehaoran.hblog.web.model.vo.blogsettings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogSettingsRspVO {

    private String logo;

    private String name;

    private String author;

    private String introduction;

    private String avatar;

    private String githubHomepage;

    private String giteeHomepage;

    private String csdnHomepage;

    private String zhihuHomepage;
}
