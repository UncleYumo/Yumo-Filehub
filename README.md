# Yumo Filehub

[^轻量级的在线文件中转系统]: 隐私、高效、简约

> @Author  **Uncle Yumo**
>
> @Email [邮箱](uncleyumo@163.com)
>
> @Github  [UncleYumo](https://github.com/UncleYumo)
>
> @Repository  [项目仓库](https://github.com/UncleYumo/Yumo-Filehub))
>
> @Update Time  2024年12月9日凌晨
>
> ---

### 项目简介

本项目是作者为应付大三上学期的JavaWeb课设而设计开发的文件系统

前端演示地址：[http://filehub.uncleyumo.cn](http://filehub.uncleyumo.cn)

安卓后台在作者手机里，想看的[邮件](uncleyumo@163.com)联系

#### 后端（经典SSM）

SpringBoot 3 + Gradle + Kotlin + Quartz（定时任务）+ OAuth2 + Redis

> 在这里给我的新开源项目打个广告：
>
> [基于Kotlin的个性化控制台字体与日志打印工具 - print-plus-kotlin](https://github.com/UncleYumo/print-plus-kotlin)

#### 前端（响应式布局）

> 前端为网页端，组件库主要基于Element Plus并针对移动端、大屏移动端和PC端浏览器绘制了三套UI

Vue3 + Vite + Pinia + Element Plus + Naive UI

#### 后台 （安卓与微信小程序端）

> 主要用于用户 Access-Key 的分发和用户状态的更新与账号删除

UNIAPP + Vue3 + UNI-UI

---

### 使用须知

- 由于作者最近需要向授业老师展示课设成果，为保该项目于公网运行稳定（第一天测试被朋友打崩溃三次），目前仅支持10MB以下的文件上传和下载，且由于项目的文件存储方式为服务器储存，因此当后端崩溃后会导致用户文件的缓存命令变更，则有可能导致用户数据的丢失，请做好心理准备，因此不要将重要文件上传本项目！！！如果因为某些重要的原因需要找回文件，（仅限于服务器崩溃导致的，过期而被后端自动删除的文件概不负责）请 [邮件](uncleyumo@163.com) 或  [Gitub](https://github.com/UncleYumo) 联系我！
- 前端服务器部署在香港，部分用户的网络可能会卡顿或延迟
- 如果前端能用但是请求失败，请立即联系作者把**崩溃的后端重启**（我早晚会用k8s的！！！！！！）
- 迫于现实情况，目前的 AccessKey 仅小范围发放，如有需要请联系作者获取临时密钥
- 待补充 ... （2024年12月9日凌晨）

