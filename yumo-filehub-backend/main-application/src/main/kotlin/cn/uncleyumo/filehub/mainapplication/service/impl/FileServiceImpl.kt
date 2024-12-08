package cn.uncleyumo.filehub.mainapplication.service.impl

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.service.FileService
import cn.uncleyumo.filehub.mainapplication.utils.AccessKeyUtil
import cn.uncleyumo.filehub.mainapplication.utils.FileLinkUtils
import cn.uncleyumo.filehub.mainapplication.utils.FileManipulationUtil
import cn.uncleyumo.filehub.mainapplication.utils.ThreadLocalUtil
import cn.uncleyumo.utils.ColorPrinter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

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
class FileServiceImpl : FileService {

    @Autowired
    private lateinit var fileManipulationUtil: FileManipulationUtil

    @Autowired
    private lateinit var fileRedisTemplate: RedisTemplate<String, FileDTO>

    @Autowired
    private lateinit var userRedisTemplate: RedisTemplate<String, UserDTO>

    @Autowired
    private lateinit var stringRedisTemplate: StringRedisTemplate

    override fun uploadFile(
        file: MultipartFile,
        validTime: Int
    ): String {
        // 如果文件大小超过200MB 则直接返回错误信息
        if (file.size > 200 * 1024 * 1024) throw IllegalArgumentException("File size must be less than 200MB")

        // 获取当前用户信息
        val claims: Map<String, *>? = ThreadLocalUtil.get()
        val accessKey: String = claims?.get("accessKey") as String

        // 从Redis中获取用户的账户有效期剩余时间（秒）
        val accountValidSeconds: Long = userRedisTemplate.getExpire("access-key:${accessKey}", TimeUnit.SECONDS)

        // 将账户有效期剩余时间转换为分钟并舍去秒的精度
        val accountValidMinutes = (accountValidSeconds / 60).toInt()

        // 检查用户账户有效时间与文件有效时间是否冲突 如文件有效期大于用户有效期，则抛出异常
        if ((accountValidSeconds != -1L && validTime > accountValidMinutes && validTime != -1) || (validTime == -1 && accountValidSeconds != -1L)) {
            throw IllegalArgumentException("File valid time cannot exceed account valid time")
        }

        // 检查当前用户文件容量是否超出限制(小于1GB)  单位：KB
        val fileDirectorySize: Int = fileManipulationUtil.getFileDirectorySize(accessKey)
//        ColorPrinter.printlnCyanRed("Current file directory size: $fileDirectorySize KB")

        val userDTO: UserDTO = userRedisTemplate.opsForValue().get("access-key:${accessKey}")
            ?: throw IllegalArgumentException("Invalid access key")

        if (fileDirectorySize + (file.size / 1024) > userDTO.availableSpace)
            throw IllegalArgumentException("Your current file size exceeds the limit of ${userDTO.availableSpace - fileDirectorySize} KB")

        val fileName = file.originalFilename ?: "UnknownName"
        val uuidFileName = FileLinkUtils.generateUUIDFileName(FileLinkUtils.getFileType(fileName))
        val fileDTO = FileDTO(
            originalFileName = fileName,
            uuidFileName = uuidFileName,
            fileUrl = fileManipulationUtil.saveFile(accessKey, uuidFileName, file),
            fileSize = file.size.toInt() / 1024,  // 单位KB
            createTime = LocalDateTime.now(),
            validTime = validTime
        )

        // 保存文件信息到redis数据库 并设置过期时间 -1 表示永久保存
        if (validTime == -1) fileRedisTemplate.opsForValue().set("file:$accessKey:$uuidFileName", fileDTO)
        else if (validTime > 0) fileRedisTemplate.opsForValue()
            .set("file:$accessKey:$uuidFileName", fileDTO, validTime.toLong() * 60, TimeUnit.SECONDS)
        else throw IllegalArgumentException("Valid time must be greater than 0 or -1")
        return fileDTO.fileUrl
    }

    override fun downloadFile(encryptedAccessKey: String, uuidFileName: String): File? {
        val accessKey: String = AccessKeyUtil.decrypt(encryptedAccessKey)
        // 查看Redis数据库中是否有该文件记录以判断是否过期
        val fileDTO: FileDTO = fileRedisTemplate.opsForValue().get("file:$accessKey:$uuidFileName")
            ?: throw IllegalArgumentException("File not found or expired")
        return fileManipulationUtil.getFile(accessKey, uuidFileName)
    }

    override fun getFileList(): List<FileDTO> {
        val claims: Map<String, *>? = ThreadLocalUtil.get()
        val accessKey: String = claims?.get("accessKey") as String
        val fileList = fileRedisTemplate.keys("file:$accessKey:*").mapNotNull {
            fileRedisTemplate.opsForValue().get(it)
        }
        return fileList
    }

    override fun deleteFile(uuidFileName: String) {
        val claims: Map<String, *>? = ThreadLocalUtil.get()
        val accessKey: String = claims?.get("accessKey") as String
        val fileDTO = fileRedisTemplate.opsForValue().get("file:$accessKey:$uuidFileName")
        if (fileDTO != null) {
            fileManipulationUtil.deleteFile(accessKey, uuidFileName)
            fileRedisTemplate.delete("file:$accessKey:$uuidFileName")
        } else {
            throw IllegalArgumentException("File not found")
        }
    }

    override fun markdownInstruction(): String {
        val instruction = stringRedisTemplate.opsForValue().get("markdown:instruction")
        return instruction ?: throw IllegalArgumentException("Instruction not found")
    }
}