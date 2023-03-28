package com.service.impl;

import com.domain.ResponseResult;
import com.domain.entity.LoginUser;
import com.domain.entity.User;
import com.service.BlogLoginService;
import com.utils.BeanCopyUtils;
import com.utils.JwtUtil;
import com.utils.RedisCache;
import com.domain.vo.BlogUserLoginVo;
import com.domain.vo.UserInfoVo;
import com.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = manager.authenticate(authenticationToken);
        //判断认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或者密码错误");
        }
        //获取userId 生成token
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String id = loginUser.getUser().getId().toString();
        String token = JwtUtil.createJWT(id);
        //把用户信息存入redis

        redisCache.setCacheObject("blogLogin:"+id,loginUser);
        //如果获取不到
        if(Objects.isNull(loginUser)){
            //提示重新登录
//            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
//            WebUtils.renderString(response, JSON.toJSONString(result));
//            return null;
            System.out.println("loginUser: null");
        }
        //把user转换成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);

        //把token和userinfo封装 返回
        BlogUserLoginVo Vo = new BlogUserLoginVo(token,userInfoVo);
        //SecurityContextHolder.getContext().setAuthentication();
        return ResponseResult.okResult(Vo);
    }

    @Override
    public ResponseResult logout() {
        //获取token，解析userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取userId
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String id = loginUser.getUser().getId().toString();
        //删除redis中的用户信息
        redisCache.deleteObject("blogLogin:"+id);

        return ResponseResult.okResult(200,"退出成功！");
    }
}
