package filehub.uncleyumo.cn.mainapplication.service.impl

import cn.uncleyumo.utils.LogPrinter
import filehub.uncleyumo.cn.mainapplication.entity.pojo.UserDTO
import filehub.uncleyumo.cn.mainapplication.service.UserService
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

        val user = userRedisTemplate.opsForValue().get("access-key:$accessKey")



        return user?.let { "Pretend to be a token" } ?: "-1"

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
