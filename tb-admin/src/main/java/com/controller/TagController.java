package com.controller;


import com.domain.ResponseResult;
import com.domain.dto.TagDto;
import com.domain.vo.PageVo;
import com.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;
    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagDto tagDto) {

        return tagService.pageTagList(pageNum,pageSize, tagDto);
    }
    @PostMapping
    public ResponseResult addTag(@RequestBody TagDto tagDto){
        return tagService.addTag(tagDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable Long id){
        return tagService.deleteTag(id);
    }
}

