package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.dto.RoleDto;
import com.domain.entity.Role;
import com.domain.vo.PageVo;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-03-27 10:47:57
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long userId);

    PageVo roleList(Integer pageNum, Integer pageSize, String roleName, String status);

    void addRole(RoleDto dto);
}

