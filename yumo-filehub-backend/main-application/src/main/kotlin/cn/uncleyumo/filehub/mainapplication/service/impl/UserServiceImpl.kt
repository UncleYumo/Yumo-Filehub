package cn.uncleyumo.filehub.mainapplication.service.impl

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.service.UserService
import cn.uncleyumo.filehub.mainapplication.utils.FileManipulationUtil
import cn.uncleyumo.filehub.mainapplication.utils.JwtUtil
import cn.uncleyumo.filehub.mainapplication.utils.ThreadLocalUtil
import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
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

    @Autowired
    lateinit var fileRedisTemplate: RedisTemplate<String, FileDTO>

    @Autowired
    private lateinit var fileManipulationUtil: FileManipulationUtil

    /*
     * 验证accessKey是否有效 返回token
     */
    override fun verify(accessKey: String): String {
        val user: UserDTO =
            userRedisTemplate.opsForValue().get("access-key:$accessKey") ?: throw Exception("Invalid accessKey")
        val map = mapOf<String, Any>("accessKey" to accessKey)
        return JwtUtil.generateToken(map)
    }

    override fun addUser(user: UserDTO) {

        if (user.validTime == -1) {
            userRedisTemplate.opsForValue().set(
                "access-key:${user.accessKey}", user  // 设置过期时间为永久
            )
            return
        } else if (user.validTime > 0) {
            val expireTime: Long = user.validTime.toLong() * 60  // 过期时间单位为秒 传入的单位为分钟
            userRedisTemplate.opsForValue().set(
                "access-key:${user.accessKey}",
                user,
                expireTime,
                TimeUnit.SECONDS
            )
        } else {
            throw Exception("Invalid validTime must be greater than 0 or -1")
        }

    }

    override fun getUserList(): List<UserDTO> {
        return userRedisTemplate.keys("access-key:*").mapNotNull {
            userRedisTemplate.opsForValue().get(it)
        }
    }

    override fun getAvailableSpace(): Int {
        // 获取当前用户信息
        val claims: Map<String, *>? = ThreadLocalUtil.get()
        val accessKey: String = claims?.get("accessKey") as String

        // 获取当前用户信息下的目录已使用空间(KB)
        val fileDirectorySize: Int = fileManipulationUtil.getFileDirectorySize(accessKey)

        ColorPrinter.printlnCyanRed("Current file directory size: $fileDirectorySize KB")

        val userDTO: UserDTO = userRedisTemplate.opsForValue().get("access-key:${accessKey}")
            ?: throw IllegalArgumentException("Invalid access key")

        // 返回剩余空间(KB)
        return userDTO.availableSpace - fileDirectorySize
    }

    override fun deleteUser(accessKey: String) {
        // 删除Redis中用户信息
        userRedisTemplate.delete("access-key:$accessKey")

        // 删除用户文件目录
        fileManipulationUtil.deleteDirectory(accessKey)

        // 删除Redis中用户文件记录
        val fileList: List<FileDTO> = fileRedisTemplate.keys("file:${accessKey}:*").mapNotNull {
            fileRedisTemplate.opsForValue().get(it)
        }

        fileList.forEach {
            fileRedisTemplate.delete("file:${accessKey}:${it.uuidFileName}")
            LogPrinter.info("Delete file: 'file:${accessKey}:${it.uuidFileName}' successfully")
        }

        LogPrinter.error("Delete user: $accessKey and its files successfully")
    }

}
