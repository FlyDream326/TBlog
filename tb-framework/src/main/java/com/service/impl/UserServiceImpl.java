package com.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.SystemConstants;
import com.domain.ResponseResult;
import com.domain.dto.AddUserDetailsDto;
import com.domain.dto.AddUserDto;
import com.domain.entity.Role;
import com.domain.entity.User;
import com.domain.entity.UserRole;
import com.domain.vo.PageVo;
import com.domain.vo.UserRoleDetailVo;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.mapper.UserMapper;
import com.mapper.UserRoleMapper;
import com.service.RoleService;
import com.service.UserRoleService;
import com.service.UserService;
import com.utils.BeanCopyUtils;
import com.utils.SecurityUtils;
import com.domain.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-03-19 10:48:22
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Override
    public ResponseResult userInfo() {
        //获取userId 通过userId来查询user
        User user = getById(SecurityUtils.getUserId());
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        //特定更新
//        LambdaQueryWrapper<User> queryWrapper =
//                new LambdaQueryWrapper<>();
//        queryWrapper.eq(User::getAvatar,user.getAvatar())
//                    .eq(User::getEmail,user.getEmail())
//                    .eq(User::getId,user.getId())
//                    .eq(User::getNickName,user.getNickName())
//                    .eq(User::getSex,user.getSex());
//        update(queryWrapper);
        //直接更新
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if (!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        //对数据(数据库)是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        //对密码进行加密
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public PageVo userList(Integer pageNum, Integer pageSize, String userName, String phonenumber, String status) {
        //如果userName phonenumber  值不为空则进行模糊查询
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(userName),User::getUserName,userName)
                .like(StringUtils.hasText(phonenumber),User::getPhonenumber,phonenumber)
                .eq(StringUtils.hasText(status),User::getStatus,status);
        Page<User> userPage = new Page<>(pageNum,pageSize);
        page(userPage,queryWrapper);
        return new PageVo(userPage.getRecords(),userPage.getTotal());
    }

    @Override
    public void addUser(AddUserDto dto) {
        User user = BeanCopyUtils.copyBean(dto, User.class);
        //判断用户名是否为空   空：抛出异常
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        //判断用户名之前是否已存在
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        //判断邮箱是否已存在
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        //判断手机号是否已存在
        if (phonenumberExist(user.getPhonenumber())){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        //密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        List<UserRole> list = dto.getRoleIds().stream()
                .map(id -> new UserRole(user.getId(), Long.valueOf(id)))
                .collect(Collectors.toList());
        userRoleService.saveBatch(list);
        System.out.println("新增用户成功！");
    }

    @Override
    public UserRoleDetailVo getUserDetailsId(Long id) {
        User user = getById(id);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        LambdaQueryWrapper<UserRole> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,userInfoVo.getId());
        List<String> roles = userRoleService.list(queryWrapper).stream()
                .map(userRole -> userRole.getRoleId().toString())
                .collect(Collectors.toList());
        LambdaQueryWrapper<Role> queryWrapper1 =
                new LambdaQueryWrapper<>();
        queryWrapper1.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> list = roleService.list(queryWrapper1);
        return new UserRoleDetailVo(roles,list,userInfoVo);
    }

    @Override
    public void updateUserDetails(AddUserDetailsDto dto) {
        User user = BeanCopyUtils.copyBean(dto, User.class);
        LambdaUpdateWrapper<User> updateWrapper =
                new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,user.getId());
        update(user,updateWrapper);
        List<UserRole> userRoles = dto.getRoleIds().stream()
                .map(rids -> new UserRole(user.getId(), Long.valueOf(rids)))
                .collect(Collectors.toList());
        LambdaQueryWrapper<UserRole> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.getBaseMapper().delete(queryWrapper);
        userRoleService.saveBatch(userRoles);
    }

    private boolean phonenumberExist(String phonenumber) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber,phonenumber);
        return  count(queryWrapper)>0 ;
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return  count(queryWrapper)>0 ;
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        return  count(queryWrapper)>0 ;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return  count(queryWrapper)>0 ;
    }
}

