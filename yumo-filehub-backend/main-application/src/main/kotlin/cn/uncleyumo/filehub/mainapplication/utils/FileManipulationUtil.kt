package cn.uncleyumo.filehub.mainapplication.utils

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

/**
 * 文件操作工具类
 *
 * @author uncle_yumo
 * @fileName FileManipulationUtils
 * @proName yumo-filehub-backend
 * @school Wuxi_University
 * @stuNumber 22344131
 * @createTime 2024/12/2
 * @updateTime 2024/12/2
 * @description 提供文件保存、读取、删除等操作
 */

@Component
class FileManipulationUtil {

    // 定义存储文件的根位置（使用ClassPathResource来获取资源文件目录）
    val rootLocation: File = ClassPathResource("yumo-filehub-store").file.also {
        if (!it.exists()) {
            it.mkdirs() // 如果不存在，则创建目录
        }
    }

    /**
     * 保存文件
     * @param accessKey 子文件夹名称
     * @param file 文件对象
     */
    fun saveFile(accessKey: String, uuidFileName: String, file: MultipartFile): String {

        // 防止文件名中有特殊字符
        if (file.originalFilename?.contains("/") == true) {
            throw IllegalArgumentException("File name cannot contain '/'")
        }

        if (file.size > 200 * 1024 * 1024) {
            throw IllegalArgumentException("File size cannot exceed 200MB")
        }

        // 创建子文件夹
        val directory = File(rootLocation, accessKey).also { if (!it.exists()) it.mkdirs() }

        // 构建目标文件路径
        val targetFile = File(directory, uuidFileName)

        // 将上传的文件保存到目标位置
        file.transferTo(targetFile)

        return FileLinkUtils.generateEncryptedFileLink(accessKey, uuidFileName)
    }

    /**
     * 获取文件
     * @param accessKey 子文件夹名称
     * @param uuidFileName 文件名
     * @return 文件对象
     */
    fun getFile(accessKey: String, uuidFileName: String): File? {
        return File(rootLocation, "$accessKey/$uuidFileName").takeIf { it.exists() }
    }

    /**
     * 删除文件
     * @param accessKey 子文件夹名称
     * @param uuidFileName 文件名
     */
    fun deleteFile(accessKey: String, uuidFileName: String) {
        File(rootLocation, "$accessKey/$uuidFileName").delete()
    }

    /**
     * 获取文件目录大小
     * @param accessKey 子文件夹名称
     * @return 目录大小
     */
    fun getFileDirectorySize(accessKey: String): Long {
        val directory = File(rootLocation, accessKey)
        return if (directory.isDirectory) directory.walk().sumOf { it.length() } else 0L
    }

    /**
     * 获取文件名列表
     * @param accessKey 子文件夹名称
     * @return 文件名列表
     */
    fun getAccessKeyUUIDFileNameList(accessKey: String): List<String>? {
        val directory = File(rootLocation, accessKey)
        return if (directory.isDirectory) directory.list()?.toList() else null
    }
}