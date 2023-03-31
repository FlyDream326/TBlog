package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.SystemConstants;
import com.domain.ResponseResult;
import com.domain.entity.Menu;
import com.domain.vo.MenuVo;
import com.enums.AppHttpCodeEnum;
import com.mapper.MenuMapper;
import com.service.MenuService;
import com.utils.BeanCopyUtils;
import com.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-03-27 10:40:20
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserKey(Long userId) {
        //根据用户id 查询权限 如果是管理员返回全部权限 type: 1 管理员 0 普通用户
        if (userId==1L){
            LambdaQueryWrapper<Menu> queryWrapper =
                    new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType,SystemConstants.MENU,SystemConstants.BUTTON)
                        .eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = list(queryWrapper);
            List<String> perms = menuList.stream()
                                            .map(Menu::getPerms)
                                            .collect(Collectors.toList());
            return perms;
        }
        //否则返回所具有的权限
        return getBaseMapper().selectPermsByUserKey(userId);
    }

    @Override
    public List<MenuVo> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper baseMapper = getBaseMapper();
        List<Menu> menuList = null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //管理员：返回符合要求的Menu
          menuList = baseMapper.selectAllRouterMenu();


        }else {
            //否则 获取当前用户的所具有的Menu
            menuList = baseMapper.selectRouterMenuByUserId(userId);

        }
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);
        //构建Tree
        List<MenuVo> menuTree = buildMenuTree(menuVos,0L);

        return menuTree;
    }

    @Override
    public List<Menu> menuList(String status, String menuName) {
        /*需要展示菜单列表，不需要分页。
        可以针对菜单名进行模糊查询
        也可以针对菜单的状态进行查询。
        菜单要按照父菜单id和orderNum进行排序
         */
        LambdaQueryWrapper<Menu> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(status), Menu::getStatus, status)
                    .like(StringUtils.hasText(menuName), Menu::getMenuName, menuName);
        queryWrapper.orderByAsc(true, Menu::getOrderNum,Menu::getParentId);
        return list(queryWrapper);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        //查询新增menu是否已存在
        LambdaQueryWrapper<Menu> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuName,menu.getMenuName());
        //存在 则返回已存在错误代码
        if(count(queryWrapper)>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.MENU_NAME_EXIST);
        }
        //不存在 则新增
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long id) {
        LambdaQueryWrapper<Menu> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,id);
        if(count(queryWrapper)>0){
            return ResponseResult.errorResult(500,"存在子菜单不允许删除");
        }
        return ResponseResult.okResult();
    }


    private List<MenuVo> buildMenuTree(List<MenuVo> menuVos, long parentId) {
        //先找出第一层的菜单 然后去找他们的子菜单设置到children属性中
        List<MenuVo> menuTree = menuVos.stream()
                //寻找目录
                .filter(menuVo -> menuVo.getParentId().equals(parentId))
                //存放子菜单
                .map(menuVo -> menuVo.setChildren(getChildren(menuVo, menuVos)))
                .collect(Collectors.toList());
        return menuTree;
    }

    private List<MenuVo> getChildren(MenuVo menuVo, List<MenuVo> menuVos) {
        List<MenuVo> collect = menuVos.stream()
                //保留menuVo的children
                .filter(menuVo1 -> menuVo1.getParentId().equals(menuVo.getId()))
                //继续存放children，若有则说明具有三级菜单
                .map(m->m.setChildren(getChildren(m,menuVos)))
                .collect(Collectors.toList());
        return collect;
    }
}

