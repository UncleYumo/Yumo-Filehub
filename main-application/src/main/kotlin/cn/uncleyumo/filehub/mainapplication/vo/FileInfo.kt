package cn.uncleyumo.filehub.mainapplication.vo

/**
 * @author uncle_yumo
 * @fileName FileInfo
 * @createDate 2024/11/28 November
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

data class FileInfo(
    val fileName: String,
    val fileSize: String,
    val downloadURL: String,
    val uploadTime: String,
    val remainingLife: String
)
