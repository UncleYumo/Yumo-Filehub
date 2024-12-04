package cn.uncleyumo.filehub.mainapplication.service.impl

import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.service.UserService
import cn.uncleyumo.filehub.mainapplication.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 *@author uncle_yumo
 *@fileName UserServiceImpl
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/2
 *@updateTime 2024/12/2
 *@description
 **/

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRedisTemplate: RedisTemplate<String, UserDTO>

    /*
     * 验证accessKey是否有效 返回token
     */
    override fun verify(accessKey: String): String {
        val user: UserDTO = userRedisTemplate.opsForValue().get("access-key:$accessKey") ?: throw Exception("Invalid accessKey")
        val map = mapOf<String, Any>("accessKey" to accessKey)
        return JwtUtil.generateToken(map)
    }

    override fun addUser(user: UserDTO) {

        if (user.validTime == -1) {
            userRedisTemplate.opsForValue().set(
                "access-key:${user.accessKey}", user  // 设置过期时间为永久
            )
            return
        }

        if (user.validTime > 0) {
            val expireTime: Long = user.validTime.toLong() * 60  // 过期时间单位为秒 传入的单位为分钟
            userRedisTemplate.opsForValue().set(
                "access-key:${user.accessKey}",
                user,
                expireTime,
                TimeUnit.SECONDS
            )
        }

    }
}
