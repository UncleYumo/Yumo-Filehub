好的，根据你的要求，我会提供一个完整的文件操作工具类，并且包含详细的中文注释。此外，我还会说明如何处理`MultipartFile`类型的文件，以便能够将它转换成`File`类型并传入到工具方法中。

首先，我们来创建一个文件操作工具类`FileManipulationUtil`，并且使用相对路径来进行文件操作。在Spring Boot项目中，我们可以利用`ResourceLoader`来获取资源文件的路径，这样可以确保在不同环境下都能正确地找到文件存储位置。

### 1. 文件操作工具类

```kotlin
package filehub.uncleyumo.cn.mainapplication.utils

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

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
```

### 2. 处理`MultipartFile`并调用工具方法

当从请求中接收到`MultipartFile`时，你可以通过以下方式将其转换为`File`：

```kotlin
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun MultipartFile.convertToFile(): File {
    val file = File(this.originalFilename!!)
    val inputStream: InputStream = this.inputStream
    val outputStream = FileOutputStream(file)
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return file
}
```

然后你可以在控制器中使用这个扩展函数来处理上传的文件，并调用`FileManipulationUtil`中的方法。

### 3. 控制器示例

这里是一个简单的控制器示例，用于演示如何接收`MultipartFile`并调用`FileManipulationUtil`的方法：

```kotlin
import filehub.uncleyumo.cn.mainapplication.utils.FileManipulationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/files")
class FileController(@Autowired private val fileManipulationUtil: FileManipulationUtil) {

    @PostMapping("/{accessKey}")
    fun uploadFile(@PathVariable accessKey: String, @RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val convertedFile = file.convertToFile()
        fileManipulationUtil.saveFile(accessKey, convertedFile)
        return ResponseEntity.ok("File uploaded successfully.")
    }

    @GetMapping("/{accessKey}/{fileName}")
    fun downloadFile(@PathVariable accessKey: String, @PathVariable fileName: String, response: HttpServletResponse) {
        val file = fileManipulationUtil.getFile(accessKey, fileName) ?: return
        response.setHeader("Content-Disposition", "attachment; filename=\"${file.name}\"")
        response.contentType = "application/octet-stream"
        file.inputStream().copyTo(response.outputStream)
    }

    // 其他方法如删除文件和列出文件名可以类似地实现
}
```

以上代码提供了完整的文件操作流程，包括文件上传、下载以及相关辅助功能。请确保在实际部署时，调整文件存储路径以适应不同的服务器环境。如果你有任何疑问或需要进一步的帮助，请随时告诉我！