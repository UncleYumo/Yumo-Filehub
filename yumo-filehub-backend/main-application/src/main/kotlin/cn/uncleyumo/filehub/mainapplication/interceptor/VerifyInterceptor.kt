package cn.uncleyumo.filehub.mainapplication.interceptor

import cn.uncleyumo.filehub.mainapplication.utils.JwtUtil
import cn.uncleyumo.filehub.mainapplication.utils.ThreadLocalUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
            response.status = 401  // 未登录
            return false
        }

        val claims: Map<String, Any>?
        return try {
            claims = JwtUtil.validateToken(token.removePrefix("Bearer "))
            ThreadLocalUtil.set(claims)
            true
        } catch (e: Exception) {
            response.status = 401  // 未登录
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