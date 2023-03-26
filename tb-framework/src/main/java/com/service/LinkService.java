package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.ResponseResult;
import com.domain.entity.Link;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-03-10 12:14:19
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();
}

