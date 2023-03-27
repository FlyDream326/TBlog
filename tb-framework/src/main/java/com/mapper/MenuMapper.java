package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-27 10:40:20
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsByUserKey(Long userId);
}

