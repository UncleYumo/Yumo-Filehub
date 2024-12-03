package cn.uncleyumo.filehub.mainapplication.service.impl

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import cn.uncleyumo.filehub.mainapplication.service.FileService
import cn.uncleyumo.filehub.mainapplication.utils.FileLinkUtils
import cn.uncleyumo.filehub.mainapplication.utils.FileManipulationUtil
import cn.uncleyumo.filehub.mainapplication.utils.ThreadLocalUtil
import cn.uncleyumo.utils.ColorPrinter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

/**
 *@author uncle_yumo
 *@fileName FileServiceImpl
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/3
 *@updateTime 2024/12/3
 *@description
 **/

@Service
class FileServiceImpl: FileService {

    @Autowired
    private lateinit var fileManipulationUtil: FileManipulationUtil

    override fun uploadFile(
        file: MultipartFile,
        validTime: Int
    ): String {
        val claims: Map<String, *>? = ThreadLocalUtil.get()
        val accessKey: String = claims?.get("accessKey") as String
        val fileName = file.originalFilename?: "UnknownName"
        val uuidFileName = FileLinkUtils.generateUUIDFileName(FileLinkUtils.getFileType(fileName))
        val fileDTO = FileDTO(
            originalFileName = fileName,
            uuidFileName = uuidFileName,
            fileUrl = fileManipulationUtil.saveFile(accessKey, uuidFileName, file),
            fileSize = file.size.toInt(),
            createTime = LocalDateTime.now(),
            validTime = validTime
        )
        ColorPrinter.printlnCyanBlack("File uploaded successfully: \n$fileDTO")
        return fileDTO.fileUrl
    }


}