package com.example.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.example.commonutils.R;
import com.example.eduservice.entity.ArticleCollect;
import com.example.eduservice.entity.EduArticle;
import com.example.eduservice.service.ArticleCollectService;
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
@RequestMapping("/eduservice/article-collect")
public class ArticleCollectController {

  @Autowired
  private ArticleCollectService articleCollectService;

  @Autowired
  private EduArticleService articleService;

  // 添加或者移除文章收藏
  @PostMapping("addOrUpdateArticleCollect/{articleId}/{memberId}")
  public R addOrUpdateArticleCollect(
          @PathVariable String articleId ,@PathVariable String memberId){
    QueryWrapper<ArticleCollect> wrapper = new QueryWrapper<>();
    wrapper.eq("article_id",articleId);
    wrapper.eq("member_id",memberId);
    ArticleCollect articleCollect = articleCollectService.getOne(wrapper);
    if(ObjectUtils.isNull(articleCollect)){
      articleCollect = new ArticleCollect();
      articleCollect.setArticleId(articleId);
      articleCollect.setMemberId(memberId);
      articleCollectService.save(articleCollect);

      EduArticle eduArticle = articleService.getById(articleId);
      eduArticle.setCollectCount(eduArticle.getCollectCount()+1);
      articleService.updateById(eduArticle);
    }else{
      articleCollectService.removeById(articleCollect.getId());

      EduArticle eduArticle = articleService.getById(articleId);
      eduArticle.setCollectCount(eduArticle.getCollectCount()-1);
      articleService.updateById(eduArticle);
    }
    return R.ok();
  }
}

