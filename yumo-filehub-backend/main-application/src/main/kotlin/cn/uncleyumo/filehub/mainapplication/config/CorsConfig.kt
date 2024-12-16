package cn.uncleyumo.filehub.mainapplication.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.Collections

/**
 * @author uncle_yumo
 * @fileName CorsConfig
 * @createDate 2024/12/7 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfiguration = CorsConfiguration()
        // 允许所有来源
        corsConfiguration.allowedOriginPatterns = Collections.singletonList("*")
        // 允许所有请求头
        corsConfiguration.allowedHeaders = Collections.singletonList(CorsConfiguration.ALL)
        // 允许所有请求方法
        corsConfiguration.allowedMethods = Collections.singletonList(CorsConfiguration.ALL)
        // 允许携带cookie
        corsConfiguration.allowCredentials = false
        // 设置预检请求的有效时间
        corsConfiguration.maxAge = 3600

        val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return CorsFilter(source)
    }
}