package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-27 10:47:57
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<String> selectRoleKeysByUserId(Long userId);
}

