package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.ResponseResult;
import com.constants.SystemConstants;
import com.entity.Link;
import com.mapper.LinkMapper;
import com.service.LinkService;
import com.utils.BeanCopyUtils;
import com.vo.LinkVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-03-10 12:14:20
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的
        LambdaQueryWrapper<Link> linkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        linkLambdaQueryWrapper.eq(Link::getStatus,SystemConstants.LINK_STATUS_NORMAL);
        List<Link> linkList = list(linkLambdaQueryWrapper);

        //封装Vo
        List<LinkVo> lv = BeanCopyUtils.copyBeanList(linkList, LinkVo.class);
        //封装返回
        return ResponseResult.okResult(lv);
    }
}

