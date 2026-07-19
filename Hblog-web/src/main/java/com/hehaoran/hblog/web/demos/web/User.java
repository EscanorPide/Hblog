/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hehaoran.hblog.web.demos.web;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class User {
    @NotBlank(message = "用户名不许为空")
    private String username;
    @Min(0)
    @Max(100)
    @NotNull(message = "年龄不许为空")
    private Integer age;
    // 创建时间
    private LocalDateTime createTime;
    // 更新日期
    private LocalDate updateDate;
    // 时间
    private LocalTime time;
    @Override
    public String toString() {
        return "User{username='" + username + "', age=" + age + "}";
    }
}
