package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduservice.entity.ArticleCollect;
import com.example.eduservice.entity.ArticleComment;
import com.example.eduservice.entity.ArticleLike;
import com.example.eduservice.entity.EduArticle;
import com.example.eduservice.mapper.EduArticleMapper;
import com.example.eduservice.service.ArticleCollectService;
import com.example.eduservice.service.ArticleCommentService;
import com.example.eduservice.service.ArticleLikeService;
import com.example.eduservice.service.EduArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
@Service
public class EduArticleServiceImpl extends ServiceImpl<EduArticleMapper, EduArticle> implements EduArticleService {


  @Autowired
  private ArticleCollectService articleCollectService;

  @Autowired
  private ArticleLikeService articleLikeService;

  @Autowired
  private ArticleCommentService articleCommentService;
  // 搜索帖子
  @Override
  public Map<String, Object> searchArticle(Page<EduArticle> pageArticle, String keyword) {

    if(StringUtils.isEmpty(keyword)) {
      QueryWrapper<EduArticle> wrapper = new QueryWrapper<>();
      wrapper.eq("article_status",1);
      baseMapper.selectPage(pageArticle,wrapper);
      Map<String, Object> map = getStringObjectMap(pageArticle);
      return map;
    }

      QueryWrapper<EduArticle> wrapper = new QueryWrapper<>();
      wrapper.likeRight("article_title",keyword);
      wrapper.or().like("article_content",keyword);
      wrapper.eq("article_status",1);
      baseMapper.selectPage(pageArticle,wrapper);

    Map<String, Object> map = getStringObjectMap(pageArticle);
    return map;
  }

  private static Map<String, Object> getStringObjectMap(Page<EduArticle> pageArticle) {
    List<EduArticle> records = pageArticle.getRecords();
    long current = pageArticle.getCurrent();
    long pages = pageArticle.getPages();
    long size = pageArticle.getSize();
    long total = pageArticle.getTotal();
    boolean hasNext = pageArticle.hasNext();
    boolean hasPrevious = pageArticle.hasPrevious();

    Map<String, Object> map = new HashMap<>();
    map.put("items", records);
    map.put("current", current);
    map.put("pages", pages);
    map.put("size", size);
    map.put("total", total);
    map.put("hasNext", hasNext);
    map.put("hasPrevious", hasPrevious);
    return map;
  }

  @Override
  public Boolean addArticle(EduArticle eduArticle) {
          return baseMapper.insert(eduArticle) > 0;
  }

  // 获取收藏列表
  @Override
  public List<EduArticle> collectList(String memberId) {
    QueryWrapper<ArticleCollect> wrapper = new QueryWrapper<>();
    wrapper.eq("member_id",memberId);
    List<String> collect = articleCollectService.list(wrapper).stream().map(ArticleCollect::getArticleId).collect(Collectors.toList());
    if (collect.size() > 0){
      QueryWrapper<EduArticle> articleWrapper = new QueryWrapper<>();
      articleWrapper.in("id",collect);
      articleWrapper.eq("article_status",1);
      return baseMapper.selectList(articleWrapper);
    }
    return null;
  }


  // 获取点赞列表
  @Override
  public List<EduArticle> likeList(String memberId) {
    QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
    wrapper.eq("member_id",memberId);
    List<String> collect = articleLikeService.list(wrapper).stream().map(ArticleLike::getArticleId).collect(Collectors.toList());
    if (collect.size() > 0){
      QueryWrapper<EduArticle> articleWrapper = new QueryWrapper<>();
      articleWrapper.in("id",collect);
      articleWrapper.eq("article_status",1);
      return baseMapper.selectList(articleWrapper);
    }
    return null;
  }

  @Override
  public EduArticle getArticleById(String articleId) {
    QueryWrapper<EduArticle> wrapper = new QueryWrapper<>();
    wrapper.eq("id",articleId);
    EduArticle eduArticle = baseMapper.selectOne(wrapper);

    if(eduArticle.getIsDeleted()){
      throw new RuntimeException("该帖子已被删除");
    }

    if(eduArticle.getArticleStatus() == 2){
      throw new RuntimeException("该帖子已被禁用");
    }

    if(eduArticle != null){
      eduArticle.setViewCount(eduArticle.getViewCount() + 1);
      baseMapper.updateById(eduArticle);

      QueryWrapper<ArticleComment> commentWrapper = new QueryWrapper<>();
      commentWrapper.eq("article_id",articleId);
      List<ArticleComment> commentList = articleCommentService.list(commentWrapper);
      eduArticle.setCommentList(commentList);
      return eduArticle;
    }
    return null;
  }
}
