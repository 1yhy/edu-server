package com.example.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.R;
import com.example.eduservice.entity.EduArticle;
import com.example.eduservice.entity.vo.ArticleQuery;
import com.example.eduservice.service.EduArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/eduservice/article")
public class EduArticleController {

  @Autowired
  private EduArticleService articleService;

  /**
   * Gets front course list.
   *
   * @param page          the page
   * @param limit         the limit
   * @param keyword       the search keyword
   * @return the front course list
   */
  @PostMapping(value = {"searchArticle/{page}/{limit}","searchArticle/{page}/{limit}/{keyword}"})
  public R searchArticle(@PathVariable long page, @PathVariable long limit,
                        @PathVariable(required = false) String keyword){
    Page<EduArticle> pageArticle = new Page<>(page,limit);
    Map<String,Object> map =  articleService.searchArticle(pageArticle,keyword);
    return R.ok().data(map);
  }


  // 添加文章
  @PostMapping("addArticle")
  public R addArticle(
                        @RequestBody EduArticle eduArticle){
    Boolean success = articleService.addArticle(eduArticle);
    return success?R.ok():R.error();
  }


  // 删除文章
  @DeleteMapping ("deleteArticle/{articleId}")
  public R deleteArticle(
          @PathVariable String articleId){
    EduArticle eduArticle = articleService.getById(articleId);
    if(ObjectUtils.isNull(eduArticle)){
      return R.error().message("文章不存在");
    }
    boolean b = articleService.removeById(articleId);
    return b ? R.ok():R.error();
  }

  // 更新文章
  @PostMapping ("updateArticle")
  public R updateArticle(
          @RequestBody EduArticle article){
    EduArticle eduArticle = articleService.getById(article.getId());
    if(ObjectUtils.isNull(eduArticle)){
      return R.error().message("文章不存在");
    }
    boolean b = articleService.updateById(article);
    return b ? R.ok():R.error();
  }

  // 筛选获取帖子
  @PostMapping("pageArticleCondition/{current}/{limit}")
  public R pageArticleCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) ArticleQuery articleQuery) {
    Page<EduArticle> pageArticle = new Page<>(current, limit);
    QueryWrapper<EduArticle> wrapper = new QueryWrapper<>();
    //多条件组合查询
    String title = articleQuery.getArticleTitle();
    Integer status = articleQuery.getArticleStatus();
    String memberId = articleQuery.getMemberId();
    //判断条件值是否为空，如果不为空拼接条件
    if (!StringUtils.isEmpty(title)) {
      wrapper.like("article_title", title);
    }

    if (!StringUtils.isEmpty(memberId)) {
      wrapper.like("member_id", memberId);
    }
    if (!StringUtils.isEmpty(status)) {
      wrapper.eq("article_status", status);
    }

    wrapper.orderByDesc("gmt_create");
    articleService.page(pageArticle, wrapper);
    long total = pageArticle.getTotal();
    List<EduArticle> records = pageArticle.getRecords();
    return R.ok().data("total", total).data("rows", records);
  }


  // 获取收藏帖子
  @GetMapping("articleCollectList/{memberId}")
  public R articleCollectList(@PathVariable String memberId) {
    List<EduArticle> eduArticles = articleService.collectList(memberId);
    if (ObjectUtils.isNull(eduArticles)) {
      return R.ok().message("暂无收藏帖子");
    }
    return R.ok().data("articleCollectList", eduArticles);
  }

  // 获取点赞帖子
  @GetMapping("articleLikeList/{memberId}")
  public R articleLikeList(@PathVariable String memberId) {
    List<EduArticle> eduArticles = articleService.likeList(memberId);
    if (ObjectUtils.isNull(eduArticles)) {
      return R.ok().message("暂无点赞帖子");
    }
    return R.ok().data("articleLikeList", eduArticles);
  }

  // 获取帖子详细信息
  @GetMapping("articleDetail/{articleId}")
  public R articleDetail(@PathVariable String articleId) {
    EduArticle eduArticle = articleService.getArticleById(articleId);
    if (ObjectUtils.isNull(eduArticle)) {
      return R.error().message("帖子不存在");
    }
    return R.ok().data("articleDetail", eduArticle);
  }

}

