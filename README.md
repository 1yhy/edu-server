# Java课程资源网站---后端代码

<p align="center">
  <img width="320" src="https://edu-2022-11-20.oss-cn-guangzhou.aliyuncs.com/edu.png">
</p>
<p align="center">
  <a href="https://github.com/vuejs/vue">
    <img src="https://img.shields.io/badge/springboot-2.2.1-brightgreen.svg" alt="springboot">
  </a>
  <a href="https://vuex.vuejs.org/zh/guide/">
    <img src="https://img.shields.io/badge/mysql-5.7-brightgreen.svg" alt="mysql">
  </a>
  <a href="https://vuex.vuejs.org/zh/guide/">
    <img src="https://img.shields.io/badge/jdk-1.8-blue.svg" alt="jdk">
  </a>
</p>


## 项目介绍

`edu_server`是Java课程资源网站的后端项目，是基于尚硅谷的 [【尚硅谷_谷粒学苑-微服务+全栈在线教育实战项目】](https://b23.tv/U982mZF) 进行二次开发的项目。

## 前言

- 开源不易，希望大家star支持一下

## 前后端代码地址

- gitee码云代码地址：
- gitee前台代码链接：[https://gitee.com/yanghongyu2001/edu_front.git](https://gitee.com/yanghongyu2001/edu_front.git)
- gitee后台代码链接：[https://gitee.com/yanghongyu2001/edu_admin.git](https://gitee.com/yanghongyu2001/edu_admin.git)
- github前台代码链接：
- github后台代码链接：

## 相关技术

### 前端：

- 基础框架：Nuxt2(前台) vue2(后台)
- 状态管理：vuex(前台) vuex(后台)
- 路由组件：vue-router(后台)
- 网络请求：axios
- 其他技术：详见前端项目的package.json

### 后端：

- 基础框架：springboot
- ORM框架：mybatisplus
- 权限框架：springsecurity
- 缓存中间件：redis
- 消息中间件：rabbitmq
- 其他技术：详见后端项目的pom.xml

## 组织结构

```
edu_server
├── common -- 通用代码
├────├── common_utils -- 通用工具类
├────├── service_base -- 全局异常处理
├────├── service_security -- 服务安全框架
├────├── pom.xml -- 公共依赖
├── gateway -- 网关代码
├── service -- 核心服务模块
├────├── service_acl -- 后台权限模块
├────├── service_cms -- 首页轮播图模块
├────├── service_edu -- 网站课程核心模块
├────├── service_msm -- 阿里云短信验证模块
├────├── service_order -- 订单模块
├────├── service_oss -- 阿里云OSS模块
├────├── service_statistic -- 系统统计模块
├────├── service_ucenter -- 用户模块
├────├── service_vod -- 阿里云视频点播模块
└── pom.xml -- 父依赖
```



## 运行

#### 前提条件(系统需要以下配置)

- Java环境配置(jdk1.8)
- Maven配置
- 安装Redis
- 安装Nacos
- Mysql5.7

#### 修改项目配置

- 修改service下每个模块的`application.yml`文件
  - 修改数据库地址、用户名、密码
  - 修改redis地址、密码
  - 修改service_oss模块的application.yml配置为自己阿里云的配置
  - 修改service_vod模块的application.yml配置为自己阿里云的配置
  - 修改service_order模块（因为自己开通了微信支付功能，修改了尚硅谷教程的参数，具体完整内容可以自己观看尚硅谷视频修改）
    - 修改`Controller`下的`PayLogController`文件，把`wxNotify`方法里的`key`补充为尚硅谷视频中的key值
    - 修改`Service`下的`PayLogServiceImpl`文件，把`createNative`方法里的缺少的一些支付参数补充为尚硅谷视频中的值
- 修改`service`下每个模块和`gatewaty`下的`bootstrap.yml`文件
  - 修改Nacos地址为自己的Nacos地址
  - 修改运行服务的命名空间为自己添加的命名空间Id
- 把`service`下每个模块和`gatewaty`下的`application.yml`文件配置添加到Nacos中

#### 添加阿里云vod依赖（maven仓库下载不了这个jar包）

- [阿里云视频点播JavaSdk下载地址](https://help.aliyun.com/document_detail/106648.htm?spm=a2c4g.53406.0.0.49bd77baqbocsS#multiTask2011)

- 找到1.4.11版本进行下载

![image-20230510000320928](https://yhyblog-2023-2-8.oss-cn-hangzhou.aliyuncs.com/md/2023/05/202305100003012.png)

- 把1.4.11的`jar`包放到自己Maven仓库地址的地址中

![image-20230510000026624](https://yhyblog-2023-2-8.oss-cn-hangzhou.aliyuncs.com/md/2023/05/202305100000675.png)

![image-20230509235610411](https://yhyblog-2023-2-8.oss-cn-hangzhou.aliyuncs.com/md/2023/05/202305100007970.png)



#### 运行项目所有模块，没有报错则启动成功
