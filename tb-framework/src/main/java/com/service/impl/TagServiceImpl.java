package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.ResponseResult;
import com.domain.dto.TagDto;
import com.domain.entity.Tag;
import com.domain.vo.PageVo;
import com.domain.vo.TagVo;
import com.mapper.TagMapper;
import com.qiniu.util.Json;
import com.service.TagService;
import com.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-03-26 17:11:12
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Resource
    private TagMapper tagMapper;
    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagDto tagListDto) {
        //若name属性存在且不为null 执行查询
        LambdaQueryWrapper<Tag> queryWrapper =
                new LambdaQueryWrapper<>();
       queryWrapper.eq(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName())
                   .eq(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());
        Page<Tag> page = new Page<>(pageNum,pageSize);
         page(page, queryWrapper);
        //封装数据返回
        List<TagVo> list = BeanCopyUtils.copyBeanList(page.getRecords(), TagVo.class);
        PageVo pageVo = new PageVo(list,page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(TagDto tagDto) {
        save(new Tag(tagDto));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteTag(Long id) {
        tagMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        TagDto tagDto = BeanCopyUtils.copyBean(tag, TagDto.class);
        return ResponseResult.okResult(tagDto);
    }

    @Override
    public ResponseResult<TagVo> updateTag(TagVo tagVo) {
        Tag tag = BeanCopyUtils.copyBean(tagVo, Tag.class);
        LambdaUpdateWrapper<Tag> queryWrapper =
                new LambdaUpdateWrapper<>();
        queryWrapper.eq(Tag::getId,tagVo.getId());
        tagMapper.updateById(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<TagVo> listAllTag() {
        LambdaQueryWrapper<Tag> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getName);
        List<Tag> list = list(queryWrapper);
        List<TagVo> tagVoList = BeanCopyUtils.copyBeanList(list, TagVo.class);
        return ResponseResult.okResult(tagVoList);
    }
}

