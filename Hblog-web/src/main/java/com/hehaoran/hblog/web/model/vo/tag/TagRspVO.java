package com.hehaoran.hblog.web.model.vo.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagRspVO {

    private Long id;

    private String name;
}
