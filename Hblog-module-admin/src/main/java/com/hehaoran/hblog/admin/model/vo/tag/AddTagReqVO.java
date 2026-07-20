package com.hehaoran.hblog.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "添加标签 VO")
public class AddTagReqVO {

    @NotBlank(message = "标签名称不能为空")
    @Length(min = 1, max = 60, message = "标签名称字数限制 1 ~ 60 之间")
    private String name;
}
