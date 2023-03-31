package com.controller;

import com.annotation.SystemLog;
import com.domain.ResponseResult;
import com.domain.entity.Menu;
import com.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @GetMapping("/list")
    public ResponseResult menuList(String status,String menuName){
      List<Menu> menuList = menuService.menuList(status,menuName);
        return ResponseResult.okResult(menuList);
    }
    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu  menu){
        return menuService.addMenu(menu);
    }
    @GetMapping("/{id}")
    public ResponseResult getMenuById(@PathVariable("id")Long id){
       return ResponseResult.okResult(menuService.getById(id));
    }
    @PutMapping
    @SystemLog(businessName = "更新菜单")
    public ResponseResult updateMenu(@RequestBody Menu menu){
        if (menu.getId().equals(menu.getParentId())){
            return ResponseResult.errorResult(500,"修改菜单'写博文'失败，上级菜单不能选择自己");
        }
        menuService.updateById(menu);
        return ResponseResult.okResult();
    }
    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable("menuId")Long id){
        return menuService.deleteMenu(id);
    }

}
