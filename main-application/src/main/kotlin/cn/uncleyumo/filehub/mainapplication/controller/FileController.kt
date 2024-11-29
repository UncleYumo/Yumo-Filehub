package cn.uncleyumo.filehub.mainapplication.controller

import cn.uncleyumo.filehub.mainapplication.entity.Result
import cn.uncleyumo.filehub.mainapplication.vo.FileInfo
import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.File
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.util.UUID

/**
 * @author uncle_yumo
 * @fileName FileController
 * @createDate 2024/11/28 November
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@RestController
class FileController {

    @Value("\${filehub.server-config.url}")
    lateinit var serverURL: String

    // 处理文件上传的接口
    @PostMapping("file/upload")
    fun fileUpload(
        @RequestParam("file") file: MultipartFile, @RequestHeader("Access-Key") accessKey: String
    ): Result {

        if (accessKey != "uncleyumo") {
            return Result.unauthorized("Invalid access key")
        }

        // 获取原始文件名
        val originalFileName = file.originalFilename ?: "unknown"
        val contentType = file.contentType
        val sizeMB = file.size / 1024 / 1024.0

        // 检查文件拓展名，如果没有，就使用默认拓展名
        val extension = originalFileName.substringAfterLast(".", "")
        val fileNameUUID = "${
            UUID.randomUUID().toString().replace("-", "").substring(0, 10)
        }.${extension.ifEmpty { "unknown" }}"

        val uploadDir = Paths.get("main-application/src/main/resources/static/tempfile", accessKey)

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        val filePath = uploadDir.resolve(fileNameUUID)
        val absolutePath = filePath.toAbsolutePath()
        file.transferTo(absolutePath.toFile())

        return Result.success(data = "${serverURL}/file/download?access-key=$accessKey&fileName=$fileNameUUID")
    }


    // 根据accessKey获取文件列表
    @GetMapping("file/list")
    fun fileList(@RequestHeader("Access-Key") accessKey: String): Result {
        val fileDir = Paths.get("main-application/src/main/resources/static/tempfile", accessKey)
        val fileList = Files.list(fileDir)

        val decimalFormat = DecimalFormat("#.####") // 保留4位小数
        val validDurationMillis = 12 * 60 * 60 * 1000 // 12小时的毫秒数
        val currentTimeMillis = System.currentTimeMillis()

        val fileInfoList = fileList.map { file ->
            val fileName = file.fileName.toString()
            val fileSize: String = decimalFormat.format(Files.size(file) / 1024 / 1024.0) + "MB" // 格式化文件大小
            val uploadTime: String = Files.getAttribute(file, "creationTime").toString()
            val uploadTimeMillis = ZonedDateTime.parse(uploadTime).toInstant().toEpochMilli()

            // 计算剩余有效时间
            val remainingLifeMillis = validDurationMillis - (currentTimeMillis - uploadTimeMillis)

            // 仅包含有效时间在12小时以内的文件
            if (remainingLifeMillis > 0) {
                val remainingLifeHours = remainingLifeMillis / (1000 * 60 * 60) // 转换为小时
                val downloadURL: String = "${serverURL}/file/download?access-key=$accessKey&fileName=$fileName"
                FileInfo(fileName, fileSize, downloadURL, uploadTime, remainingLifeHours.toString())
            } else {
                null // 过期文件排除在外
            }
        }.toList()

//        LogPrinter.info("File list: \n$fileInfoList")
        return Result.success(data = fileInfoList)
    }


    // 文件下载接口
    @GetMapping("file/download")
    fun fileDownload(
        @RequestParam("access-key") accessKey: String,
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<*> {
        val fileDir = Paths.get("main-application/src/main/resources/static/tempfile", accessKey)
        val filePath = fileDir.resolve(fileName)

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error("File not found."))
        }

        val resource = File(filePath.toUri())
        val contentType: String = Files.probeContentType(filePath) ?: "application/octet-stream"
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType(contentType ?: "application/octet-stream"))
            .body(resource.readBytes())
    }

    @GetMapping("test")
    fun test(): Result {
        return Result.success()
    }
}