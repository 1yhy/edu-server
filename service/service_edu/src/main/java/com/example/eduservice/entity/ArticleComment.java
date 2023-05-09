package com.example.eduservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author yhy
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleComment对象", description="")
public class ArticleComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "评论内容")
    private String commentContent;

    @ApiModelProperty(value = "文章id")
    private String articleId;

    @ApiModelProperty(value = "用户id")
    private String memberId;

    @ApiModelProperty(value = "用户名称")
    private String memberName;

    @ApiModelProperty(value = "用户头像")
    private String memberAvatar;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "结束时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @ApiModelProperty(value = "是否删除：0：未删除 1：已删除 ")
    @TableLogic(value = "0",delval = "1")
    private Boolean isDeleted;

    @TableField(exist = false)
    private EduArticle article;
}
