package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.domain.ResponseResult;
import com.domain.entity.LoginUser;
import com.domain.entity.Menu;
import com.domain.entity.User;
import com.domain.vo.AdminUserInfoVo;
import com.domain.vo.MenuVo;
import com.domain.vo.RoutersVo;
import com.domain.vo.UserInfoVo;
import com.service.LoginService;
import com.service.MenuService;
import com.service.RoleService;
import com.utils.BeanCopyUtils;
import com.utils.JwtUtil;
import com.utils.RedisCache;
import com.utils.SecurityUtils;
import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;
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
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        System.out.println("LoginUser: "+loginUser);
        //如果获取不到
        if(Objects.isNull(loginUser)){
            //提示重新登录
//            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
//            WebUtils.renderString(response, JSON.toJSONString(result));
//            return null;
            System.out.println("loginUser: null");
        }
        String id = loginUser.getUser().getId().toString();
        String token = JwtUtil.createJWT(id);
        //把用户信息存入redis
        redisCache.setCacheObject("Login:"+id,loginUser);
        //把user转换成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);

        //把token和userinfo封装 返回
        //BlogUserLoginVo Vo = new BlogUserLoginVo(token,userInfoVo);
        //SecurityContextHolder.getContext().setAuthentication();
        Map<String,String> map =new HashMap<>();
        map.put("token",token);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {
        //获取userId
        String id = SecurityUtils.getUserId().toString();
        //删除redis中的用户信息
        redisCache.deleteObject("Login:"+id);

        return ResponseResult.okResult(200,"退出成功！");
    }

    @Override
    public ResponseResult<AdminUserInfoVo> getInfo() {
        //获取当前登录的用id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询权限信息
        List<String> permsList = menuService.selectPermsByUserKey(userId);
        //根据用户id查询角色信息
        List<String>  roleKeyList = roleService.selectRoleKeyByUserId(userId);
        //获取用户信息User
        User user = SecurityUtils.getLoginUser().getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //封装数据返回
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(permsList,roleKeyList,userInfoVo);

        return ResponseResult.okResult(adminUserInfoVo);
    }

    @Override
    public ResponseResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
         List<MenuVo> menuVos = menuService.selectRouterMenuTreeByUserId(userId);

        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menuVos));
    }
}
