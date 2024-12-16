为了帮助您在Spring Boot 3项目中实现JWT（JSON Web Token）的生成、配发以及接口的Token校验，我将提供一个完整的案例。这个案例将包括必要的配置、业务逻辑和服务层代码。我们将使用Kotlin语言和Gradle构建工具来完成此任务。

### 1. 准备工作

首先，请确保您的开发环境已经安装了JDK 1.8或更高版本，以及IntelliJ IDEA或者任何其他支持Kotlin的IDE。接着，创建一个新的Spring Boot项目，并选择Kotlin作为主要编程语言。

### 2. 添加依赖

打开`build.gradle.kts`文件，添加以下依赖项：

```kotlin
plugins {
    id("org.springframework.boot") version "3.0.0" // 使用最新稳定版
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}
```

这里我们引入了Spring Security和JWT相关的库。

### 3. 配置Spring Security

接下来，在`src/main/kotlin`下创建一个名为`SecurityConfig.kt`的安全配置类：

```kotlin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()  // 禁用CSRF保护
            .authorizeRequests()
            .antMatchers("/authenticate").permitAll()  // 允许所有人访问认证接口
            .anyRequest().authenticated()  // 所有请求都需要认证
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 使用无状态会话
            .and()
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
```

### 4. 创建JWT相关服务

现在，我们需要创建一些服务来处理JWT的生成与验证。在`src/main/kotlin`目录下创建一个名为`JwtUtil.kt`的文件：

```kotlin
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class JwtUtil(val secret: String) {

    companion object {
        const val HEADER_STRING = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
    }

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10小时过期
            .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
            .compact()
    }

    fun getUsernameFromToken(token: String?): String? {
        return try {
            getClaimsFromToken(token)?.subject
        } catch (e: Exception) {
            null
        }
    }

    private fun getClaimsFromToken(token: String?): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret.toByteArray()).parseClaimsJws(token).body
        } catch (e: Exception) {
            null
        }
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String?): Boolean {
        return try {
            val expiration = getClaimsFromToken(token)?.expiration
            expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HEADER_STRING)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7, bearerToken.length)
        }
        return null
    }
}
```

### 5. 实现JWT过滤器

在相同的包里创建`JwtRequestFilter.kt`，用于检查每个HTTP请求中的JWT令牌：

```kotlin
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtUtil.getTokenFromRequest(request)
        if (token != null && jwtUtil.validateToken(token, userDetailsService.loadUserByUsername(jwtUtil.getUsernameFromToken(token)!!))) {
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(jwtUtil.getUsernameFromToken(token)!!)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }
}
```

### 6. 用户服务

为了演示登录功能，我们需要定义用户服务。创建`UserService.kt`:

```kotlin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        return User.withUsername(user.username).password(user.password).authorities(user.roles).build()
    }
}
```

### 7. 认证控制器

最后，创建一个简单的控制器来处理登录请求：

```kotlin
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) {

    @PostMapping("/authenticate")
    fun createAuthenticationToken(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<Any> {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password))
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(authRequest.username)
            val token = jwtUtil.generateToken(userDetails.username)
            return ResponseEntity.ok(mapOf("token" to "$TOKEN_PREFIX$token"))
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(401).body("Invalid username or password")
        }
    }
}

data class AuthenticationRequest(
    val username: String,
    val password: String
)
```

### 8. 运行项目

现在，您可以运行项目并测试通过`/authenticate`端点进行登录的功能。如果一切设置正确，您应该能够成功获取到JWT令牌，并且该令牌可用于后续的API调用以验证身份。

### 总结

以上步骤涵盖了从配置到实现JWT认证的基本流程。通过这种方式，您可以为您的Spring Boot应用增加安全特性，同时保持架构的简洁性。希望这份指南对您有所帮助！如果您有任何问题或需要进一步的帮助，请随时告诉我。