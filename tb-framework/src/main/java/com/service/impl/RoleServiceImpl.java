package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.SystemConstants;
import com.domain.dto.RoleDto;
import com.domain.entity.Role;
import com.domain.entity.RoleMenu;
import com.domain.vo.PageVo;
import com.domain.vo.RoleVo;
import com.mapper.RoleMapper;
import com.service.RoleMenuService;
import com.service.RoleService;
import com.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-03-27 10:47:57
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;
    @Override
    public List<String> selectRoleKeyByUserId(Long userId) {
        //判断是否是管理员 是: 返回集合中只需要有admin
        if(userId == 1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }

        return getBaseMapper().selectRoleKeysByUserId(userId);
    }

    @Override
    public PageVo roleList(Integer pageNum, Integer pageSize, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper =
                new LambdaQueryWrapper<>();
        //需要有角色列表分页查询的功能
        //要求能够针对角色名称进行模糊查询
        queryWrapper.like(StringUtils.hasText(roleName),Role::getRoleName,roleName)
                        .eq(StringUtils.hasText(status),Role::getStatus,status)
                            .eq(!StringUtils.hasText(status),Role::getStatus, SystemConstants.STATUS_NORMAL);
        //要求按照role_sort进行升序排列
        queryWrapper.orderByAsc(Role::getRoleSort);
        //需要有角色列表分页查询的功能
        Page<Role> page = new Page<>(pageNum,pageSize);

        page(page,queryWrapper);
        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(page.getRecords(), RoleVo.class);
        return new PageVo(roleVos,page.getTotal());
    }

    @Override
    public void addRole(RoleDto dto) {
        Role role = BeanCopyUtils.copyBean(dto, Role.class);
        save(role);
        List<RoleMenu> roleMenus = dto.getMenuIds().stream()
                .map(ids -> new RoleMenu(role.getId(), Long.valueOf(ids)))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenus);
    }
}

