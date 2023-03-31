package com.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuVoSimple {
    @TableId
    private Long id;
    //菜单名称
    private String menuName;
    //父菜单ID
    private Long parentId;

  List<MenuVoSimple> children;

}
