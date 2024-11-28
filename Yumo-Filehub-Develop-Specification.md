# Yumo-Filehub (羽沫文件中转站) 开发指南

## 项目概述
Yumo-Filehub是一个基于Spring Boot 3的文件中转站，允许用户上传文件并生成可下载的链接。该项目使用Kotlin语言开发，前端页面简单，存放在静态目录中。文件通过RESTful接口进行上传，后台使用Redis存储文件信息和状态。

## 开发环境与技术栈
- **语言**: Kotlin
- **框架**: Spring Boot 3
- **构建工具**: Gradle
- **数据库**: Redis
- **前端**: HTML/CSS/JavaScript (静态文件)

## 项目目录结构
```plaintext
yumo-filehub/
├── build.gradle.kts          // Gradle构建文件
├── settings.gradle           // Gradle设置文件
├── src/
│   ├── main/
│   │   ├── kotlin/           // Kotlin代码
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── controller/   // 控制器
│   │   │           ├── service/      // 服务层
│   │   │           ├── model/        // 数据模型
│   │   │           ├── config/       // 配置类
│   │   │           └── exception/    // 异常处理
│   │   └── resources/
│   │       ├── application.yml       // 配置文件
│   │       └── static/                // 前端静态文件
│   └── test/                        // 测试代码
└── README.md                       // 项目说明
```

## 主要类与包的命名与组织
1. **Controller**: 处理HTTP请求的类，负责接收请求并返回响应。
   - `FileController`: 处理文件上传和下载的请求。
   - `AdminController`: 处理管理员相关的请求，比如用户管理和AccessKey管理。

2. **Service**: 业务逻辑处理类。
   - `FileService`: 处理文件的上传、下载和过期文件的删除逻辑。
   - `AuthService`: 处理用户鉴权逻辑。

3. **Model**: 数据模型类。
   - `FileInfo`: 文件信息模型，包括源文件名、上传时间、文件有效期、AccessKey等。

4. **Config**: 配置类，包含Redis和Spring Boot的配置。

5. **Exception**: 自定义异常处理类。

## 数据结构设计
### Redis存储结构
使用Redis的Hash数据结构存储文件信息，键为文件的UUID，值为文件的属性（JSON格式）。
```plaintext
HSET <file_id> "sourceName" "<original_filename>"
HSET <file_id> "uploadTime" "<timestamp>"
HSET <file_id> "expiryTime" "<expiry_timestamp>"
HSET <file_id> "accessKey" "<access_key>"
```

## 接口设计
### 1. 文件上传接口
- **URL**: `/file/upload`
- **方法**: `POST`
- **请求体**: `multipart/form-data`
- **请求参数**:
  - `file`: 上传的文件
  - `accessKey`: 用户提供的AccessKey

- **成功响应**:
  - **状态码**: `200 OK`
  - **内容**:
    ```json
    {
      "url": "<download_url>",
      "message": "File uploaded successfully"
    }
    ```

- **失败响应**:
  - **状态码**: `403 Forbidden` (当AccessKey无效)
  - **内容**:
    ```json
    {
      "message": "Invalid AccessKey"
    }
    ```

### 2. 文件下载接口
- **URL**: `/file/download/{fileId}`
- **方法**: `GET`
- **请求参数**:
  - `accessKey`: 用户提供的AccessKey
- **成功响应**: 返回文件内容
- **失败响应**: 状态码及对应消息

### 3. 管理员用户管理接口
- **URL**: `/admin/userManagement`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "action": "add|delete|get",
    "accessKey": "<access_key>"
  }
  ```

## 主要功能实现
### 1. 文件上传实现
#### FileController.kt
```kotlin
@RestController
@RequestMapping("/file")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("accessKey") accessKey: String
    ): ResponseEntity<Map<String, String>> {
        val result = fileService.uploadFile(file, accessKey)
        return ResponseEntity.ok(result)
    }
}
```

#### FileService.kt
```kotlin
@Service
class FileService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun uploadFile(file: MultipartFile, accessKey: String): Map<String, String> {
        validateAccessKey(accessKey) // 验证AccessKey

        val uuid = UUID.randomUUID().toString()
        val tempFile = File("resources/tempfile/userfile/$uuid")
        file.transferTo(tempFile)

        val expiryTime = System.currentTimeMillis() + 12 * 60 * 60 * 1000 // 12小时过期
        storeFileInfo(uuid, file.originalFilename, expiryTime, accessKey)

        return mapOf("url" to "/file/download/$uuid")
    }

    private fun storeFileInfo(fileId: String, sourceName: String, expiryTime: Long, accessKey: String) {
        val fileInfo = mapOf(
            "sourceName" to sourceName,
            "uploadTime" to System.currentTimeMillis(),
            "expiryTime" to expiryTime,
            "accessKey" to accessKey
        )
        redisTemplate.opsForHash<String, Any>().putAll(fileId, fileInfo)
    }

    private fun validateAccessKey(accessKey: String) {
        // 检查accessKey的有效性
        if (!isValidKey(accessKey)) {
            throw IllegalAccessException("Invalid AccessKey")
        }
    }
}
```

### 2. 文件有效期控制与删除
#### 定时任务
使用Spring的Scheduled注解定期检查Redis中的文件信息并删除过期文件。

```kotlin
@EnableScheduling
@Service
class FileCleanupService(private val redisTemplate: RedisTemplate<String, Any>) {

    @Scheduled(fixedRate = 60 * 1000) // 每分钟检查
    fun cleanUpExpiredFiles() {
        val keys = redisTemplate.keys("*")
        keys?.forEach { key ->
            val expiryTime = redisTemplate.opsForHash<String, Long>().get(key, "expiryTime")
            if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
                redisTemplate.delete(key) // 删除Redis中的记录
                val file = File("resources/tempfile/userfile/$key")
                if (file.exists()) {
                    file.delete() // 删除服务器中的文件
                }
            }
        }
    }
}
```

## 用户鉴权
在文件上传和下载接口中嵌入AccessKey的检查逻辑，确保只有获得授权的用户可以使用。

### AccessKey管理
在`AdminController`中实现AccessKey的增删查功能。
```kotlin
@RestController
@RequestMapping("/admin")
class AdminController(private val authService: AuthService) {

    @PostMapping("/userManagement")
    fun manageAccessKey(@RequestBody request: AccessKeyRequest): ResponseEntity<String> {
        authService.manageAccessKey(request)
        return ResponseEntity.ok("Operation successful")
    }
}
```

## 注意事项
1. **文件大小**: 上传的文件大小应控制在500MB以内，可以在前端进行限制和后端验证。
2. **异常处理**: 所有接口应有统一的异常处理机制，返回标准的错误响应。
3. **安全性**: 通过HTTPS部署，保护数据传输安全。AccessKey应随机生成且难以猜测。

## 总结
以上是Yumo-Filehub项目的开发指南，包括项目结构、接口设计、主要功能实现、Redis数据结构和用户鉴权等。开发者可以根据这些指导进行具体实现，确保符合开发规范并能在真实应用场景中有效运行。



---



# Yumo-Filehub 开发指南

## 1. 项目概述

Yumo-Filehub（羽沫文件中转站）是一个基于Spring Boot 3框架的全栈Web应用，旨在为用户提供一个安全且高效的文件上传与分享平台。本应用使用Kotlin语言开发，前端页面直接存放在static目录下，后端采用RESTful API风格设计，数据库选用Redis来存储用户信息和文件元数据。

### 1.1 技术栈

- **后端**: Spring Boot 3, Kotlin, Gradle, Redis
- **前端**: HTML, CSS, JavaScript (静态资源)
- **构建工具**: Gradle
- **其他**: UUID生成器用于文件名处理

## 2. 项目结构

遵循标准的Maven或Gradle项目布局：

```
1├── src
2│   ├── main
3│   │   ├── kotlin
4│   │   │   └── com
5│   │   │       └── yumo
6│   │   │           ├── config
7│   │   │           ├── controller
8│   │   │           ├── model
9│   │   │           ├── repository
10│   │   │           ├── service
11│   │   │           └── util
12│   │   ├── resources
13│   │   │   ├── static
14│   │   │   │   ├── css
15│   │   │   │   ├── js
16│   │   │   │   └── index.html
17│   │   │   └── templates
18│   │   └── tempfile
19│   │       └── userfile
20└── build.gradle
```

## 3. 功能实现细节

### 3.1 文件上传功能

#### 3.1.1 前端实现

- 用户通过`index.html`页面中的表单选择文件并提交。
- 表单应支持多种格式文件，限制大小至500MB。
- 提交时附带accessKey验证字段。

```
1<!-- 简化版示例 -->
2<form action="/file/upload" method="post" enctype="multipart/form-data">
3    <input type="file" name="file" />
4    <input type="text" name="accessKey" placeholder="请输入Access Key" required/>
5    <button type="submit">上传</button>
6</form>
```

#### 3.1.2 后端逻辑

- `FileController`接收POST请求。
- 验证accessKey有效性。
- 保存文件到指定路径，并重命名以UUID。
- 将相关信息保存到Redis。
- 返回包含下载链接的响应给客户端。

```
1// FileController.kt
2@RestController
3@RequestMapping("/file")
4class FileController(private val fileService: FileService) {
5
6    @PostMapping("/upload")
7    fun uploadFile(@RequestParam("file") file: MultipartFile,
8                   @RequestParam("accessKey") accessKey: String): ResponseEntity<String> {
9        if (!fileService.validateAccessKey(accessKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Access Key")
10        
11        val fileId = fileService.saveFile(file)
12        val downloadUrl = "http://yourdomain.com/file/download/$fileId"
13        // 注意这里需要设置过期时间等参数
14        fileService.saveFileInfoToRedis(fileId, accessKey, downloadUrl, file.originalFilename!!)
15        
16        return ResponseEntity.ok(downloadUrl)
17    }
18}
```

### 3.2 Redis数据模型

- 使用哈希表存储每个文件的信息：`file:<uuid>` -> {originalName, uploadedTime, expiryTime, accessKey, downloadUrl}。

```
1// 示例代码片段
2fun saveFileInfoToRedis(fileId: String, accessKey: String, url: String, originalName: String) {
3    redisTemplate.opsForHash<String, Any>().putAll("file:$fileId", mapOf(
4        "originalName" to originalName,
5        "uploadedTime" to System.currentTimeMillis(),
6        "expiryTime" to (System.currentTimeMillis() + 43200000), // 12小时后
7        "accessKey" to accessKey,
8        "downloadUrl" to url
9    ))
10}
```

### 3.3 用户鉴权管理

- `/admin/userManagement`接口允许管理员添加、删除或查询accessKeys。
- 访问该接口需进行身份认证。

```
1// AdminController.kt
2@RestController
3@RequestMapping("/admin")
4class AdminController(private val adminService: AdminService) {
5    
6    @PostMapping("/userManagement")
7    fun manageUser(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
8        val action = request["action"]
9        val accessKey = request["accessKey"]
10
11        when (action) {
12            "add" -> adminService.addAccessKey(accessKey!!)
13            "remove" -> adminService.removeAccessKey(accessKey!!)
14            else -> return ResponseEntity.badRequest().body("Unknown action")
15        }
16
17        return ResponseEntity.ok().build()
18    }
19}
```

## 4. 其他注意事项

- 定期清理过期文件及Redis记录。
- 确保所有外部输入都经过适当的验证。
- 考虑增加异常处理机制以提高系统的健壮性。
- 对敏感操作如修改权限等应有日志记录。

以上即为Yumo-Filehub项目的初步开发指南。希望这份文档能够帮助您顺利完成项目！如果有任何疑问或者需要进一步的帮助，请随时联系我。