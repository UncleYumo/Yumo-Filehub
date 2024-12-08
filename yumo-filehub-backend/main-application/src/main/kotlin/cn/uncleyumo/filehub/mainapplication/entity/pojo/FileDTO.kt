package cn.uncleyumo.filehub.mainapplication.entity.pojo

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
    var uuidFileName: String,
    var fileUrl: String,
    /*
     * 文件大小，单位：KB
     */
    var fileSize: Int,
    var createTime: LocalDateTime,
    /*
     * 剩余有效时间，单位：分钟
     */
    var validTime: Int
)
