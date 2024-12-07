package cn.uncleyumo.filehub.mainapplication.utils

import cn.uncleyumo.utils.ColorPrinter
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

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


    private val rootLocation: File = createTempDirectory()

    private fun createTempDirectory(): File {
        val tempDir = Files.createTempDirectory("yumo-filehub-store").toFile()
        ColorPrinter.printlnCyanRed("创建临时文件目录: ${tempDir.absolutePath}")
        tempDir.deleteOnExit() // JAR 运行结束时自动删除临时文件
        return tempDir
    }

    private fun getRealFilePath(accessKey: String, uuidFileName: String): File {
        val directory = File(rootLocation, accessKey)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, uuidFileName)
    }

    /**
     * 保存文件
     * @param accessKey 子文件夹名称
     * @param file 文件对象
     */
    fun saveFile(accessKey: String, uuidFileName: String, file: MultipartFile): String {
        if (file.originalFilename?.contains("/") == true) {
            throw IllegalArgumentException("File name cannot contain '/'")
        }

        if (file.size > 200 * 1024 * 1024) {
            throw IllegalArgumentException("File size cannot exceed 200MB")
        }

        val targetFile = getRealFilePath(accessKey, uuidFileName)
        file.inputStream.use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return FileLinkUtils.generateEncryptedFileLink(accessKey, uuidFileName)
    }

    /**
     * 获取文件
     * @param accessKey 子文件夹名称
     * @param uuidFileName 文件名
     * @return 文件对象
     */
    fun getFile(accessKey: String, uuidFileName: String): File? {
        val file = getRealFilePath(accessKey, uuidFileName)
        return if (file.exists()) file else null
    }

    /**
     * 删除文件
     * @param accessKey 子文件夹名称
     * @param uuidFileName 文件名
     */
    fun deleteFile(accessKey: String, uuidFileName: String) {
        val file = getRealFilePath(accessKey, uuidFileName)
        file.delete()
    }

    /**
     * 获取文件目录大小
     * @param accessKey 子文件夹名称
     * @return 目录大小 单位：KB
     */
    fun getFileDirectorySize(accessKey: String): Int {
        val directory = File(rootLocation, accessKey)

        // 如果目录不存在，尝试创建它
        if (!directory.exists() && !directory.mkdirs()) {
            throw IllegalArgumentException("Failed to create directory: $accessKey")
        }

        return if (directory.isDirectory) {
            try {
                directory.walk().sumOf { it.length().toInt() } / 1024
            } catch (e: Exception) {
                throw RuntimeException("Error occurred while calculating directory size: ${e.message}", e)
            }
        } else {
            throw IllegalArgumentException("Path is not a directory: $accessKey")
        }
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

    /**
     * 获取本地文件列表
     * @param accessKey 子文件夹名称
     * @return 文件名列表
     */
    fun getLocalUUIDFileNameList(accessKey: String): List<String> {
        val directory = File(rootLocation, accessKey)

        // 如果目录不存在，尝试创建它
        if (!directory.exists() && !directory.mkdirs()) {
            return emptyList()
        }

        return if (directory.isDirectory) directory.list()?.toList() ?: emptyList() else emptyList()
    }

    fun getLocalAccessKeyList(): List<String> {
        return rootLocation.listFiles()
            ?.filter { it.isDirectory }
            ?.map { it.name }
            ?: emptyList()
    }

    fun deleteDirectory(accessKey: String) {
        val directory = File(rootLocation, accessKey)
        if (directory.exists()) {
            directory.deleteRecursively()
        }
    }
}
