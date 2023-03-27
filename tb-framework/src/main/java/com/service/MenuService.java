package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2023-03-27 10:40:20
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserKey(Long userId);
}

