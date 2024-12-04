package cn.uncleyumo.filehub.mainapplication.config

import cn.uncleyumo.filehub.mainapplication.interceptor.VerifyInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @author uncle_yumo
 * @fileName SecurityConfig
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Configuration
class SecurityConfig: WebMvcConfigurer {
    @Autowired
    lateinit var interceptor: VerifyInterceptor
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor)
            .excludePathPatterns(
                "/user/verify", "/user/test", "/user/addUser", "/file/download"
            )
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")  // allow all origins
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600)
    }
}