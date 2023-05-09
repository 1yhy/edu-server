package com.example.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.example.commonutils.R;
import com.example.eduservice.entity.ArticleLike;
import com.example.eduservice.entity.EduArticle;
import com.example.eduservice.service.ArticleLikeService;
import com.example.eduservice.service.EduArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/eduservice/article-like")
public class ArticleLikeController {

  @Autowired
  private ArticleLikeService articleLikeService;

  @Autowired
  private EduArticleService articleService;

  // 添加或者移除文章收藏
  @PostMapping("addOrUpdateArticleLike/{articleId}/{memberId}")
  public R addOrUpdateArticleLike(
          @PathVariable String articleId , @PathVariable String memberId){
    QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
    wrapper.eq("article_id",articleId);
    wrapper.eq("member_id",memberId);
    ArticleLike articleLike = articleLikeService.getOne(wrapper);
    if(ObjectUtils.isNull(articleLike)){
      articleLike = new ArticleLike();
      articleLike.setArticleId(articleId);
      articleLike.setMemberId(memberId);
      articleLikeService.save(articleLike);

      EduArticle eduArticle = articleService.getById(articleId);
      eduArticle.setLikeCount(eduArticle.getLikeCount()+1);
      articleService.updateById(eduArticle);
    }else{
      articleLikeService.removeById(articleLike.getId());

      EduArticle eduArticle = articleService.getById(articleId);
      eduArticle.setLikeCount(eduArticle.getLikeCount()-1);
      articleService.updateById(eduArticle);
    }
    return R.ok();
  }
}

