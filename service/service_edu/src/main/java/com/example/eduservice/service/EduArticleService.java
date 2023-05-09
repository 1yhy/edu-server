package com.example.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eduservice.entity.EduArticle;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
public interface EduArticleService extends IService<EduArticle> {

  Map<String, Object> searchArticle(Page<EduArticle> pageCourse, String keyword);

  Boolean addArticle(EduArticle eduArticle);

  List<EduArticle> collectList(String memberId);
  List<EduArticle> likeList(String memberId);

  EduArticle getArticleById(String articleId);
}
