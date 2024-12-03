package filehub.uncleyumo.cn.mainapplication.controller

import filehub.uncleyumo.cn.mainapplication.utils.FileManipulationUtil
import jakarta.servlet.http.Part
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@RestController
@RequestMapping("/files")
class FileController(@Autowired private val fileManipulationUtil: FileManipulationUtil) {

    @PostMapping("/{accessKey}")
    @Throws(IOException::class)
    fun uploadFile(@PathVariable accessKey: String, @RequestParam("file") multipartFile: MultipartFile) {
        // 将MultipartFile转换为Part
        val part: Part = multipartFile as Part
        // 创建文件输出流
        val fileOutputStream = FileOutputStream(File(fileManipulationUtil.rootLocation, "$accessKey/${part.submittedFileName}"))
        // 保存文件
        fileManipulationUtil.saveFile(accessKey, fileOutputStream)
    }

    // 其他方法...
}