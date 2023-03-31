package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.entity.ArticleTag;
import org.apache.ibatis.annotations.Select;


/**
 * 文章标签关联表(ArticleTag)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-29 22:50:03
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
    @Select("DELETE FROM sg_article_tag WHERE article_id=#{id}")
    void deleteByArticleId(Long id);
}

