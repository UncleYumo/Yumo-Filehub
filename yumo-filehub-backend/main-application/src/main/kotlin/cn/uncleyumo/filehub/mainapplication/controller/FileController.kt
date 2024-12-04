package cn.uncleyumo.filehub.mainapplication.controller

import cn.uncleyumo.filehub.mainapplication.entity.result.ResultInfo
import cn.uncleyumo.filehub.mainapplication.service.FileService
import cn.uncleyumo.utils.ColorPrinter
import jakarta.servlet.http.Part
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files

@RestController
@RequestMapping("/file")
class FileController {

    @Autowired
    private lateinit var fileService: FileService


    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") multipartFile: MultipartFile,
        @RequestParam("validTime") validTime: Int
    ): ResultInfo {
        ColorPrinter.printlnCyanRed("uploadFile: $multipartFile, $validTime")
        val fileURL: String = fileService.uploadFile(multipartFile, validTime)

        return ResultInfo.success(data = fileURL)
    }

    @GetMapping("/download")
    fun downloadFile(
        @RequestParam("accessKey") encryptedAccessKey: String,
        @RequestParam("uuidFileName") uuidFileName: String
    ): ResponseEntity<ByteArray> {
        // 调用文件服务下载文件，返回文件对象，如果文件不存在，则返回404响应
        val file: File = fileService.downloadFile(encryptedAccessKey, uuidFileName) ?:
        return ResponseEntity.notFound().build()

        return try {
            // 将文件转换为字节数组
            val fileContent: ByteArray = Files.readAllBytes(file.toPath())

            // 设置响应头，指明内容类型和文件名
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("attachment", file.name)

            // 返回包含文件内容的响应实体，状态码为200
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } catch (e: IOException) {
            // 捕获IO异常，并返回500错误
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/fileList")
    fun fileList(): ResultInfo {
        return ResultInfo.success(data = fileService.getFileList())
    }

    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("uuidFileName") uuidFileName: String): ResultInfo {
        fileService.deleteFile(uuidFileName)
        return ResultInfo.success()
    }

}