package cn.uncleyumo.filehub.mainapplication.entity.pojo

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

/**
 * @author uncle_yumo
 * @fileName User
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

data class UserDTO(
    /*
     * accessKey, 访问密钥，用于身份验证
     */
    val accessKey: String,

    /*
     * 用户创建时间，精确到秒 - LocalDateTime.now()
     */
    val createTime: LocalDateTime = LocalDateTime.now(),

    /*
     * 有效时间，单位：分钟
     */
    val validTime: Int = 5,

    /*
     * 用户剩余容量
     */
    var availableSpace: Int = 2 * 1024 * 1024, // 单位：KB 默认2G
)
