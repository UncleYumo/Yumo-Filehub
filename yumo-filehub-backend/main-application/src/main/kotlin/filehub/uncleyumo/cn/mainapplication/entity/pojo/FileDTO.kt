package filehub.uncleyumo.cn.mainapplication.entity.pojo

import java.time.LocalDateTime

/**
 *@author uncle_yumo
 *@fileName FileObject
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/2
 *@updateTime 2024/12/2
 *@description
 **/

data class FileDTO(
    var originalFileName: String,
    var fileName: String,
    var fileUrl: String,
    /*
     * 文件大小，单位：KB
     */
    var fileSize: Int,
    var createTime: LocalDateTime = LocalDateTime.now(),
    /*
     * 剩余有效时间，单位：分钟 - 默认6小时
     */
    var validTime: Int = 60 * 6,
)
