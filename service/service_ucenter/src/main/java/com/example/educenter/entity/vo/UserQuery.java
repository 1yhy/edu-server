package com.example.educenter.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@ApiModel(value = "user查询对象", description = "用户查询对象封装")
@Data
public class UserQuery implements Serializable {

  @ApiModelProperty(value = "名称,模糊查询")
  private String nickname;
  @ApiModelProperty(value = "性别：1男，0女")
  private Integer sex;

  @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
  private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

  @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
  private String end;
}

