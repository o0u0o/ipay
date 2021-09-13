
<h1 align="center">
  <a href="http://personal.oasismoney.cn/">
  <img src="https://github.com/o0u0o/ipay/blob/main/doc/logo/ipay.jpg" width="250"/></a>
  <br>
  IPAY
</h1>

<h4 align="center">支付SDK | <a href="https://github.com/o0u0o/ipay" target="_blank">GITHUB</a></h4>

<p align="center">

  <a href="https://spring.io/" rel="nofollow">
    <img src="https://img.shields.io/badge/spring%20boot-2.0.8.RELEASE-green" alt="spring boot" data-canonical-src="https://img.shields.io/badge/spring%20boot-2.0.8.RELEASE-green" style="max-width:100%;">
  </a>

  <a href="https://mybatis.org/mybatis-3/zh/index.html" rel="nofollow">
    <img src="https://img.shields.io/badge/mybatis-3.5.0-yellow" alt="mybatis" data-canonical-src="https://img.shields.io/badge/mybatis-3.5.0-yellow" style="max-width:100%;">
  </a>

  <a href="https://www.lvzhoubao.cn/" rel="nofollow">
  <img src="https://img.shields.io/badge/copyright-o0u0o-lightgrey.svg" alt="copyright" data-canonical-src="https://img.shields.io/badge/copyright-共创绿洲(贵州)医疗系统技术有限公司-lightgrey.svg" style="max-width:100%;">
  </a>

</p>

<blockquote align="center">
  <em>ipay</em> 是<strong>支持微信支付宝的支付sdk</strong>。<br>
 支付宝、微信支付方式多样, 开发繁琐, 使用该sdk, 只需10行代码, 帮你搞定！
</blockquote>


## 版本更新
#### 1.3.3(2020.5.23)
    2. 修正支付沙盒

#### 1.3.1(2019.11.28)
    1. 修复：微信APP支付签名问题
    2. 新增支付宝WAP支付
    3. PayRequest增加参数returnUrl, 优先级高于PayConfig.returnUrl
    4. 修复：查询订单，微信订单未支付的情况下timeEnd会返回空
#### 1.3.0(2019.10.29）
    1. 正式版发布

更多更新记录 https://github.com/Pay-Group/best-pay-sdk/releases

## 文档
1. [使用文档](https://github.com/Pay-Group/best-pay-sdk/blob/develop/doc/use.md)

## 特别注意
1. 要求JDK8+
2. IDE 需安装 lombok 插件
3. 如果想贡献代码，请阅读[代码贡献指南](https://github.com/Pay-Group/best-pay-sdk/blob/master/doc/CONTRIBUTION.md)

## 在线体验
1. http://best-pay.springboot.cn
2. 微信公众号支付请扫码体验
    
    ![师兄干货](http://img.mukewang.com/5db958ec0001b67d02580258.jpg)

## 交流方式
1. qq群（590730230）加群暗号：best。目前项目处于刚刚起步开发阶段，欢迎有兴趣的朋友加群共同开发。


## Maven最新版本
```xml
<!-- https://mvnrepository.com/artifact/cn.springboot/best-pay-sdk -->
<dependency>
    <groupId>cn.springboot</groupId>
    <artifactId>best-pay-sdk</artifactId>
    <version>1.3.0</version>
</dependency>
```




