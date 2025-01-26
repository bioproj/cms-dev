[![Static Badge](https://img.shields.io/badge/gitee-bioproj%2Fcms--dev-brightgreen?logo=gitee)](https://gitee.com/bioproj/cms-dev)


### 一、内容（文章）管理系统介绍
+ 本项目使用SpringBoot作为后端
+ 使用thymeleaf作为模板引擎，实现全部页面静态化
+ 使用vue开发管理员界面

### 二、项目部署
#### 2.1 前提条件
1. 需要将模板文件<https://github.com/wangyang1749/cms-template.git>拷贝在用户目录的cms下
2. 根据配置文件，配置数据库

#### 2.2 其他模块
1. 使用vue实现的后界面，源码参考如下
<https://github.com/wangyang1749/cms-admin.git> <br> 

2. 原生android调用api，项目地址如下（正在开发中）
<https://github.com/wangyang1749/cms-android.git>
+ 前端静态化使用的模板文件

#### 2.3 快速远程部署
```shell
ssh roor@XXX.XXX "cd XXX && git pull && ./mvnw clean && ./mvnw install && ./init.sh"
```

### 三、系统开发
```
git clone https://github.com/BioinfoFungi/cms-dev.git
```
```
./mvnw spring-boot:run
```
+ 后台:<http://localhost:8080/admin>
+ 前台:<http://localhost:8080>



### 四、更新记录
20218.14
1. 更改项目架构

2020.3.23
1. 更新文章和分类的关系变换为一对多（一篇文章只能在一个分类中）
2. 新增栏目功能

2020.3.17
1. 添加一键更新所有Category HTML功能
2. 添加设置文章默认模板的功能

2020.3.9
1. 实现导出文章PDF
2. 删除本程序采用socket对`nodejs`渲染`Katex`和`mermaid`的远程调用, 
改为java调用本地命令的方式实现
3. 将授权模块分离出来

 2020.3.7 
1. 实现markdown添加图片包裹一层div
2. 修复文章更新不能删除旧分类文章列表中的文章标题
3. 增加重新生成所有文章Html功能

### 五、项目展示
+ 目前我已将本项目部署在自己的小型服务器里
<http://bioinfofungi.com>


+ 后台界面
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/5406acbf-de2f-418b-866f-3d113fad41a9.png)
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/593ceb55-19c9-4b9b-9612-c8f8fc771a66.png)
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/c18cfb8d-023a-4881-a82a-d4291f7695ef.png)
+ 静态化的前台界面
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/d68e9599-496a-470f-86ac-7926374ea56e.png)
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/6a358f19-2db8-4afc-a2bb-d0afff8e676a.png)
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/94535ba9-4d75-4792-b6b2-53eac38a1ee9.png)
![](https://wangyang-bucket.oss-cn-beijing.aliyuncs.com/image-bed/9fac9998-c498-4742-b9a8-f42bcc29d82f.png)
