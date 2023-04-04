package com.controller;

import com.domain.ResponseResult;
import com.domain.dto.LinkDto;
import com.domain.dto.LinkStatusDto;
import com.domain.entity.Link;
import com.domain.vo.LinkVo;
import com.domain.vo.PageVo;
import com.service.LinkService;
import com.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;
    @GetMapping("/list")
    public ResponseResult linkList(@RequestParam
                                   Integer pageNum,
                                   Integer pageSize,
                                   String name,
                                   String status){
        PageVo list =linkService.linkList(pageNum,pageSize,name,status);

        return ResponseResult.okResult(list);
    }
    @PostMapping
    public ResponseResult addLink(@RequestBody LinkDto dto){
        Link link = BeanCopyUtils.copyBean(dto, Link.class);
        linkService.save(link);
        return ResponseResult.okResult();
    }
    @GetMapping("{id}")
    public ResponseResult findLinkById(@PathVariable("id")Long id){
        Link link = linkService.getById(id);
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);
        return ResponseResult.okResult(linkVo);
    }
    @PutMapping
    public ResponseResult updateLink(@RequestBody LinkVo dto){
        Link link = BeanCopyUtils.copyBean(dto, Link.class);
        linkService.saveOrUpdate(link);
        return ResponseResult.okResult();
    }
    @DeleteMapping("{id}")
    public ResponseResult deleteLink(@PathVariable("id")Long id){
        linkService.getBaseMapper().deleteById(id);
        return ResponseResult.okResult();
    }
    @PutMapping("/changeLinkStatus")
    public ResponseResult changeLinkStatus(@RequestBody LinkStatusDto dto){
        Link link = BeanCopyUtils.copyBean(dto,Link.class);
        linkService.updateById(link);
        return ResponseResult.okResult();
    }
}
