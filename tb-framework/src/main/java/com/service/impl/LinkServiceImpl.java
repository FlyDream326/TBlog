package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.ResponseResult;
import com.constants.SystemConstants;
import com.domain.entity.Link;
import com.domain.vo.PageVo;
import com.mapper.LinkMapper;
import com.service.LinkService;
import com.utils.BeanCopyUtils;
import com.domain.vo.LinkVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public PageVo linkList(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Link::getName,name)
                .eq(StringUtils.hasText(status),Link::getStatus,status);
        Page<Link> linkPage = new Page<>();
        page(linkPage,queryWrapper);
        return new PageVo(linkPage.getRecords(),linkPage.getTotal());
    }
}

