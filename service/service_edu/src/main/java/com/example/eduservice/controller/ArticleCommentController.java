package com.example.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.R;
import com.example.eduservice.entity.ArticleComment;
import com.example.eduservice.entity.EduArticle;
import com.example.eduservice.service.ArticleCommentService;
import com.example.eduservice.service.EduArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/eduservice/article-comment")
public class ArticleCommentController {

  @Autowired
  private ArticleCommentService articleCommentService;

  @Autowired
  private EduArticleService articleService;


  // 筛选获取评论
  @PostMapping("pageCommentCondition/{current}/{limit}")
  public R pageArticleCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) ArticleComment articleComment) {
    Page<ArticleComment> pageComment = new Page<>(current, limit);
    QueryWrapper<ArticleComment> wrapper = new QueryWrapper<>();
    //多条件组合查询
    String articleId = articleComment.getArticleId();
    String memberId = articleComment.getMemberId();
    //判断条件值是否为空，如果不为空拼接条件
    if (!StringUtils.isEmpty(articleId)) {
      wrapper.like("article_Id", articleId);
    }

    if (!StringUtils.isEmpty(memberId)) {
      wrapper.like("member_id", memberId);
    }

    wrapper.orderByDesc("gmt_create");
    articleCommentService.page(pageComment, wrapper);
    long total = pageComment.getTotal();
    List<ArticleComment> records = pageComment.getRecords();
    return R.ok().data("total", total).data("rows", records);
  }

  // 添加评论
  @PostMapping("addArticleComment")
  public R addArticleComment(
          @RequestBody ArticleComment articleComment){
    Boolean success = articleCommentService.save(articleComment);
    if (success){
      EduArticle eduArticle = articleService.getById(articleComment.getArticleId());
      eduArticle.setCommentCount(eduArticle.getCommentCount()+1);
      articleService.updateById(eduArticle);
    }
    return success?R.ok():R.error();
  }

  // 删除评论
  @DeleteMapping("deleteArticleComment/{commentId}")
  public R deleteArticleComment(
          @PathVariable String commentId){
    ArticleComment articleComment = articleCommentService.getById(commentId);
    if(ObjectUtils.isNull(articleComment)){
      return R.error().message("评论不存在");
    }
    boolean b = articleCommentService.removeById(commentId);
    return b ? R.ok():R.error();
  }


  // 获取用户所有评论
  @GetMapping("getUserAllComment/{memberId}")
  public R getUserAllComment(
          @PathVariable String memberId){
    // 根据用户id获取所有评论
    QueryWrapper<ArticleComment> wrapper = new QueryWrapper<>();
    wrapper.eq("member_id",memberId);
    List<ArticleComment> articleComment = articleCommentService.list(wrapper);

    // 根据文章id获取文章
    List<String> collect = articleComment.stream().map(ArticleComment::getArticleId).collect(Collectors.toList());

    // 评论为零
    if(collect.size()==0){
      return R.ok().data("articleComment",articleComment);
    }
    List<EduArticle> eduArticles = (List<EduArticle>) articleService.listByIds(collect);

    // 将文章添加到评论中
    articleComment.forEach(item->{
      item.setArticle(eduArticles.stream()
              .filter(article->
                      article.getId().equals(item.getArticleId())).findFirst().orElse(null));
    });
    return R.ok().data("articleComment",articleComment);
  }

}

