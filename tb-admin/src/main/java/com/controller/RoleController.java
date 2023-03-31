package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constants.SystemConstants;
import com.domain.ResponseResult;
import com.domain.dto.RoleDto;
import com.domain.entity.Role;
import com.domain.vo.PageVo;
import com.domain.vo.RoleVo;
import com.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @GetMapping("/list")
    public ResponseResult roleList(Integer pageNum,Integer pageSize,String roleName,String status){
      PageVo pageVo = roleService.roleList(pageNum,pageSize,roleName,status);
        return ResponseResult.okResult(pageVo);
    }
    @PutMapping("/changeStatus")
    public ResponseResult roleStatus(@RequestBody RoleVo roleVo){
        //改变角色状态
        roleService.getBaseMapper().updateById(new Role(roleVo.getId(),roleVo.getStatus()));
        return ResponseResult.okResult();
    }
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        LambdaQueryWrapper<Role> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        return ResponseResult.okResult(roleService.list(queryWrapper));
    }
    @PostMapping
    public ResponseResult addRole(@RequestBody RoleDto dto){
        roleService.addRole(dto);
    return ResponseResult.okResult();
    }
}
