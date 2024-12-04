package cn.uncleyumo.filehub.mainapplication.interceptor

import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.utils.JwtUtil
import cn.uncleyumo.filehub.mainapplication.utils.ThreadLocalUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * @author uncle_yumo
 * @fileName VerifyInterceptor
 * @createDate 2024/12/4 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Component
class VerifyInterceptor: HandlerInterceptor {

    @Autowired
    lateinit var userRedisTemplate: RedisTemplate<String, UserDTO>

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.method == "OPTIONS") {
            val origin = request.getHeader("Origin")
            response.setHeader("Access-Control-Allow-Origin", origin)
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH")
            response.setHeader("Access-Control-Max-Age", "3600")
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, content-type")
            response.status = 200  // 允许跨域请求
            return false  // false表示请求不再继续，直接返回响应
        }

        val token = request.getHeader("Authorization")
        if (token == null) {
            response.status = 200
            response.contentType = "application/json"
            val responseBody = mapOf(
                "code" to 401,
                "message" to "No authorization token found",
                "data" to null
            )
            response.writer.write(jacksonObjectMapper().writeValueAsString(responseBody))
            return false
        }

        val claims: Map<String, Any>?
        return try {
            claims = JwtUtil.validateToken(token.removePrefix("Bearer "))
            // 检查claims所存accessKey是否有效
            val accessKey = claims["accessKey"] as String
            val userDTO: UserDTO = userRedisTemplate.opsForValue().get("access-key:$accessKey") ?: throw Exception("AccessKey is Out of Date")
            ThreadLocalUtil.set(claims)
            true
        } catch (e: Exception) {
            response.status = 200
            response.contentType = "application/json"
            val responseBody = mapOf(
                "code" to 401,
                "message" to "Invalid token",
                "data" to null
            )
            response.writer.write(jacksonObjectMapper().writeValueAsString(responseBody))
            false
        }
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: java.lang.Exception?
    ) {
        ThreadLocalUtil.remove()
    }
}