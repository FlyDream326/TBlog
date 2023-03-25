package com.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.ResponseResult;
import com.entity.User;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.mapper.UserMapper;
import com.service.UserService;
import com.utils.BeanCopyUtils;
import com.utils.SecurityUtils;
import com.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
//        BCryptPasswordEncoder bCryptPasswordEncoder =
//                new BCryptPasswordEncoder();
//        String encode = bCryptPasswordEncoder.encode(user.getPassword());
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
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

