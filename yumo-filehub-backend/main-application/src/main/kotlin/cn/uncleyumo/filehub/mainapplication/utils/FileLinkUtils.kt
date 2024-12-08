package cn.uncleyumo.filehub.mainapplication.utils

import java.net.URI
import java.util.*

/**
 * @author uncle_yumo
 * @fileName FileLinkUtils
 * @createDate 2024/12/4 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

class FileLinkUtils {
    companion object {
        private const val BASE_URL = "http://139.224.195.43/filehub/file/download"
        fun generateEncryptedFileLink(accessKey: String, uuidFileName: String): String {
            val encryptedAccessKey = AccessKeyUtil.encrypt(accessKey)
            return "$BASE_URL?accessKey=$encryptedAccessKey&uuidFileName=$uuidFileName"
        }

        fun getAccessKeyAndUuidFileName(fileLink: String): Map<String, String> {
            // 创建一个不可变的映射，用于存储解密后的accessKey和uuidFileName
            val map = mutableMapOf<String, String>()

            // 使用 URI 类解析传入的文件链接
            val uri = URI(fileLink)

            // 获取查询参数部分
            val queryParams = uri.query

            // 将查询参数根据&分割成键值对
            val pairs = queryParams.split("&")

            // 遍历每一个键值对
            for (pair in pairs) {
                // 根据=分割，键为参数名，值为参数值
                val (key, value) = pair.split("=")

                // 如果参数名是accessKey，将其解密并放入映射中
                if (key == "accessKey") {
                    val decryptedAccessKey = AccessKeyUtil.decrypt(value) // 解密accessKey
                    map["accessKey"] = decryptedAccessKey // 存入映射
                }

                // 如果参数名是uuidFileName，直接放入映射中
                if (key == "uuidFileName") {
                    map["uuidFileName"] = value // 存入映射
                }
            }

            // 返回存储了解密后的accessKey和uuidFileName的映射
            return map
        }

        fun generateUUIDFileName(fileType: String): String {
            return (UUID.randomUUID().toString()) + "." + fileType
        }

        fun getFileType(fileName: String): String {
            return if (fileName.contains(".")) {
                fileName.substringAfterLast(".")
            } else {
                "unknown"
            }
        }

    }
}