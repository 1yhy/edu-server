package com.example.eduorder.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 31913
 */
@ApiModel(value = "后台order查询对象", description = "订单查询对象封装")
@Data
public class OrderQuery implements Serializable {

  @ApiModelProperty(value = "订单号")
  private String orderNo;

  @ApiModelProperty(value = "课程Id")
  private String courseId;

  @ApiModelProperty(value = "用户Id")
  private String memberId;

  @ApiModelProperty(value = "教师Id")
  private String teacherId;

  @ApiModelProperty(value = "订单状态：1已支付，0未支付")
  private Integer status;

  @ApiModelProperty(value = "支付方式：1微信，2支付宝")
  private Integer payType;

  @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
  private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

  @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
  private String end;
}
