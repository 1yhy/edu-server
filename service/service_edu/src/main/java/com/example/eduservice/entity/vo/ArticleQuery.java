package com.example.eduservice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "Article查询对象", description = "文章查询对象封装")
@Data
public class ArticleQuery implements Serializable {

    @ApiModelProperty(value = "文章标题名称,模糊查询")
    private String articleTitle;

    @ApiModelProperty(value = "文章状态 0：审核中 1已审核 2审核失败")
    private Integer articleStatus;

    @ApiModelProperty(value = "作者id")
    private String memberId;
}