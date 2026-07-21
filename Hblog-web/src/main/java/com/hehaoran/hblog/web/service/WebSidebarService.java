package com.hehaoran.hblog.web.service;

import com.hehaoran.hblog.common.utils.Response;

public interface WebSidebarService {

    Response findCategoryList();

    Response findTagList();
}
