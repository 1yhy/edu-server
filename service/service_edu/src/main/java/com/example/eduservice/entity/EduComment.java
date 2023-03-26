package com.example.eduservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * ClassName: EduComment
 * Package: com.example.eduservice.entity
 * Description:
 *
 * @Author: yhy
 * @Create: 2022/12/2 - 16:30
 * @Version: v1.0
 */
@NoArgsConstructor
@Data
public class EduComment {

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @NotEmpty(message = "课程Id不能为空")
    private String courseId;

    @NotEmpty(message = "评论内容不能为空")
    private String content;

    private String teacherId;

    private String nickname;

    @NotEmpty(message = "请先进行登录")
    private String memberId;

    private String avatar;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
