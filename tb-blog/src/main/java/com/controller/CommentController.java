package com.controller;

import com.domain.ResponseResult;
import com.constants.SystemConstants;
import com.domain.dto.AddCommentDto;
import com.domain.entity.Comment;
import com.service.CommentService;
import com.utils.BeanCopyUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论",description = "评论相关接口")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId,Integer pageNum,Integer pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult addComment(@RequestBody AddCommentDto addCommentDto){
        Comment comment = BeanCopyUtils.copyBean(addCommentDto, Comment.class);
        return commentService.addComment(comment);
    }

    @ApiOperation(value = "友链评论列表",notes = "获取一页友链")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pageNum",value = "页号"),
                    @ApiImplicitParam(name = "pageSize",value = "页面大小")
            }
    )
    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }
}
