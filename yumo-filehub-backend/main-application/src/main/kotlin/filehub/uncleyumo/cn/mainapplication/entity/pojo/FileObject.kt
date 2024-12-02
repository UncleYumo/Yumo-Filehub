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

data class FileObject(
    var originalFileName: String = "",
    var fileName: String = "",
    var fileUrl: String = "",
    var fileSize: Long = 0,
    var createTime: LocalDateTime,
    var remainingTime: Int = 12,
)
