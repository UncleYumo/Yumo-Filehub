package cn.uncleyumo.filehub.mainapplication.controller

import cn.uncleyumo.filehub.mainapplication.entity.result.ResultInfo
import cn.uncleyumo.filehub.mainapplication.service.FileService
import cn.uncleyumo.utils.ColorPrinter
import jakarta.servlet.http.Part
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@RestController
@RequestMapping("/file")
class FileController {

    @Autowired
    private lateinit var fileService: FileService


    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") multipartFile: MultipartFile, @RequestParam("validTime") validTime: Int): ResultInfo {

        val fileURL: String =  fileService.uploadFile(multipartFile, validTime)

        return ResultInfo.success(data = fileURL)
    }

    // 其他方法...
}