package filehub.uncleyumo.cn.mainapplication.controller

import cn.uncleyumo.utils.ColorPrinter
import filehub.uncleyumo.cn.mainapplication.entity.result.ResultInfo
import filehub.uncleyumo.cn.mainapplication.service.FileService
import filehub.uncleyumo.cn.mainapplication.utils.FileManipulationUtil
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
        ColorPrinter.printlnCyanBlack("uploadFile: $multipartFile, $validTime")

        val fileURL: String =  fileService.uploadFile(multipartFile, validTime)

        return ResultInfo.success(data = fileURL)
    }

    // 其他方法...
}