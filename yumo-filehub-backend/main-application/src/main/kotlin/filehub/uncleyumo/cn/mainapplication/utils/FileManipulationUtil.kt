package filehub.uncleyumo.cn.mainapplication.utils

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.isDirectory

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
 * @description 提供文件保存、读取、删除等基本操作
 */

@Component
class FileManipulationUtil(private val resourceLoader: ResourceLoader) {
    // 文件存储根目录
    private val rootLocation = "yumo-filehub-store"

    init {
        // 初始化时检查根目录是否存在，如果不存在则创建
        Files.createDirectories(Paths.get(resourceLoader.getResource(rootLocation).file.toURI()))
    }

    /**
     * 保存文件
     *
     * @param accessKey 子文件夹名称
     * @param file 上传的文件
     */
    fun saveFile(accessKey: String, file: File) {
        // 获取目标文件夹路径
        val targetDir = Paths.get(resourceLoader.getResource("$rootLocation/$accessKey").file.toURI())
        if (!Files.exists(targetDir)) {
            Files.createDirectory(targetDir)
        }
        // 将文件复制到目标文件夹
        Files.copy(file.toPath(), targetDir.resolve(file.name))
    }

    /**
     * 读取文件
     *
     * @param accessKey 子文件夹名称
     * @param content 文件名
     * @return 文件对象
     */
    fun getFile(accessKey: String, content: String): File? {
        return File(resourceLoader.getResource("$rootLocation/$accessKey/$content").file.toURI()).takeIf { it.exists() }
    }

    /**
     * 删除文件
     *
     * @param accessKey 子文件夹名称
     * @param fileName 文件名
     */
    fun deleteFile(accessKey: String, fileName: String) {
        val fileToDelete = File(resourceLoader.getResource("$rootLocation/$accessKey/$fileName").file.toURI())
        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
    }

    /**
     * 获取指定子文件夹下的文件总大小
     *
     * @param accessKey 子文件夹名称
     * @return 文件总大小（字节）
     */
    fun getFileDirectorySize(accessKey: String): Long {
        val directory = Paths.get(resourceLoader.getResource("$rootLocation/$accessKey").file.toURI())
        return Files.walk(directory)
            .filter { p -> !p.isDirectory() }
            .mapToLong { p -> p.toFile().length() }
            .sum()
    }

    /**
     * 获取指定子文件夹下的文件名列表
     *
     * @param accessKey 子文件夹名称
     * @return 文件名列表
     */
    fun getAccessKeyFileNameList(accessKey: String): List<String>? {
        val directory = Paths.get(resourceLoader.getResource("$rootLocation/$accessKey").file.toURI())
        return if (Files.exists(directory)) {
            Files.list(directory)
                .map { path -> path.fileName.toString() }
                .collect(Collectors.toList())
        } else {
            null
        }
    }
}