package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.ResponseResult;
import com.constants.SystemConstants;
import com.domain.entity.Comment;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.mapper.CommentMapper;
import com.service.CommentService;
import com.service.UserService;
import com.utils.BeanCopyUtils;
import com.domain.vo.CommentVo;
import com.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-03-19 10:22:13
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private UserService userService;
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {

        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断 为0进行文章查询    为1进行友链查询(articleId=null)
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根评论 RootId 为 -1
        queryWrapper.eq(Comment::getRootId, SystemConstants.COMMENT_STATUS_DRAFT1);
        //评论类型
        queryWrapper.eq(Comment::getType,commentType);

        //分页 pageNum pageSize
        Page page = new Page(pageNum,pageSize);
        page(page,queryWrapper);
        List<CommentVo> list =toCommentVoList(page.getRecords());
        //查询对应根评论的子评论集合并赋值
        for (CommentVo commentVo:list){
            //查询对应的子评论
          List<CommentVo> children =getChildren(commentVo.getId());
            //赋值
            commentVo.setChildren(children);

        }
        return ResponseResult.okResult(new PageVo(list,page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if(!StringUtils.hasText(comment.getContent())){
            throw  new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //存入MySQL
        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 根据根评论的id 查询对应的子评论的集合
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByAsc(Comment::getCreateBy);
        List<Comment> list = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(list);
        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVo = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历vo集合
        for(CommentVo vo:commentVo){
            //通过creatBy查询用户的昵称并赋值
            String nickName = userService.getById(vo.getCreateBy()).getNickName();
            vo.setNikeName(nickName);
            //通过toCommentUserID查询用户的昵称并赋值
            //如果通过toCommentUserID不为-1才查询
            if(vo.getToCommentUserId()!=-1){
                String nickName1 = userService.getById(vo.getToCommentUserId()).getNickName();
                vo.setToCommentUserName(nickName1);
            }
        }

        return commentVo;
    }
}

