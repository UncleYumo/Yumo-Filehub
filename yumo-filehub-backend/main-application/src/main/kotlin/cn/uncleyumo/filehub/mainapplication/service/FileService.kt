package cn.uncleyumo.filehub.mainapplication.service

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

/**
 *@author uncle_yumo
 *@fileName FileService
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/3
 *@updateTime 2024/12/3
 *@description
 **/

@Service
interface FileService {

    fun uploadFile(file: MultipartFile, validTime: Int): String

    fun downloadFile(encryptedAccessKey: String, uuidFileName: String): File?

    fun getFileList(): List<FileDTO>

    fun deleteFile(uuidFileName: String)

    fun markdownInstruction(): String
}