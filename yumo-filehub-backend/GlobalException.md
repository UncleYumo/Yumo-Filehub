Spring Boot 3项目中使用Kotlin和Gradle进行全局异常处理，并返回`ResultInfo`对象

### 1. 创建Spring Boot项目

首先，确保你已经安装了JDK 1.8或更高版本以及Gradle。你可以使用Spring Initializr（https://start.spring.io/）来快速生成一个Spring Boot项目模板。选择Kotlin语言，添加Web依赖，然后下载并解压项目。

### 2. 修改`build.gradle.kts`

在你的`build.gradle.kts`文件中，确保有如下配置：

```kotlin
plugins {
    id("org.springframework.boot") version "3.0.0" // 使用最新版的Spring Boot
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

### 3. 定义`ResultInfo`类

在`src/main/kotlin/com/example/demo/model/`目录下创建`ResultInfo.kt`文件，内容如下：

```kotlin
package com.example.demo.model

class ResultInfo(
    val code: Int,
    val message: String,
    var data: Any? = null
) {
    companion object {
        const val SUCCESS_CODE = 200
        const val ERROR_CODE = 400
        const val UNKNOWN_CODE = 500
        const val UNAUTHORIZED_CODE = 401
    }
}
```

### 4. 创建全局异常处理器

在`src/main/kotlin/com/example/demo/controller/`目录下创建`GlobalExceptionHandler.kt`文件，用于捕捉和处理各种异常：

```kotlin
package com.example.demo.controller

import com.example.demo.model.ResultInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.NoSuchElementException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException(ex: NoSuchElementException): ResponseEntity<ResultInfo> {
        return ResponseEntity(ResultInfo(ResultInfo.ERROR_CODE, ex.message ?: "Not Found"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ResultInfo> {
        return ResponseEntity(ResultInfo(ResultInfo.UNKNOWN_CODE, ex.message ?: "Unknown Error"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
```

这里我们定义了两个异常处理器方法：一个是处理`NoSuchElementException`，另一个是处理所有其他类型的`Exception`。

### 5. 创建一个简单的Controller

在`src/main/kotlin/com/example/demo/controller/`目录下创建`DemoController.kt`文件，提供一些基本的API接口：

```kotlin
package com.example.demo.controller

import com.example.demo.model.ResultInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class DemoController {

    @GetMapping("/hello")
    fun sayHello(): ResultInfo {
        return ResultInfo(ResultInfo.SUCCESS_CODE, "Hello, World!")
    }

    @GetMapping("/error")
    fun triggerError(): ResultInfo {
        throw NoSuchElementException("This is a sample error.")
    }
}
```

### 6. 运行项目

现在，你可以运行你的Spring Boot应用，并测试这些端点。例如，访问`/api/v1/hello`应该返回正常的问候信息，而访问`/api/v1/error`则会触发异常处理逻辑，返回错误信息。

以上就是使用Kotlin和Spring Boot 3实现全局异常处理的基本案例。希望这能帮助你理解和实现相关的功能。如果有任何疑问或者需要进一步的帮助，请随时告诉我！
